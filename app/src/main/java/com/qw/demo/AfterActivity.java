package com.qw.demo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.qw.demo.utils.HttpCallBack;
import com.qw.demo.utils.Utils;
import com.qw.workflow.Node;
import com.qw.workflow.WorkFlow;
import com.qw.workflow.WorkNode;
import com.qw.workflow.Worker;

/**
 * @author cd5160866
 * @date 2019-09-29
 */
public class AfterActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_H5 = 1;

    /**
     * 初次广告弹框
     */
    private static final int NODE_FIRST_AD = 10;

    /**
     * 初次进入h5页
     */
    private static final int NODE_CHECK_H5 = 20;

    /**
     * 初次进入的注册协议
     */
    private static final int NODE_REGISTER_AGREEMENT = 30;

    private WorkFlow workFlow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);
        ((TextView) findViewById(R.id.tv_desc)).setText("使用后");
        startWorkFlow();
    }

    private void startWorkFlow() {
        workFlow = new WorkFlow.Builder()
                .withNode(getFirstAdNode())
                .withNode(getShowRegisterAgreementNode())
                .withNode(getShowH5Node())
                //设置回调监听
                .setCallBack(new WorkFlow.FlowCallBack() {
                    @Override
                    public void onNodeChanged(int nodeId) {
                        Toast.makeText(AfterActivity.this, "当前节点：" + nodeId, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFlowFinish() {
                        Toast.makeText(AfterActivity.this, "所有节点执行完成", Toast.LENGTH_SHORT).show();
                    }
                }).create();
        workFlow.start();
    }

    private WorkNode getFirstAdNode() {
        return WorkNode.build(NODE_FIRST_AD, new Worker() {
            @Override
            public void doWork(final Node current) {
                Utils.fakeRequest("http://www.api1.com", new HttpCallBack() {
                    @Override
                    public void onOk() {
                        new AlertDialog.Builder(AfterActivity.this)
                                .setTitle("这是一条有态度的广告")
                                .setPositiveButton("我看完了", null)
                                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialogInterface) {
                                        //仅仅只需关心自己是否完成，下一个节点会自动执行
                                        current.onCompleted();
                                    }
                                }).create().show();
                    }

                    @Override
                    public void onFailure() {
                        //仅仅只需关心自己是否完成，下一个节点会自动执行
                        current.onCompleted();
                    }
                });
            }
        });
    }

    private WorkNode getShowRegisterAgreementNode() {
        return WorkNode.build(NODE_REGISTER_AGREEMENT, new Worker() {
            @Override
            public void doWork(final Node current) {
                Utils.fakeRequest("http://www.api2.com", new HttpCallBack() {
                    @Override
                    public void onOk() {
                        new AlertDialog.Builder(AfterActivity.this)
                                .setTitle("这是注册协议")
                                .setPositiveButton("我看完了", null)
                                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialogInterface) {
                                        current.onCompleted();
                                    }
                                }).create().show();
                    }

                    @Override
                    public void onFailure() {
                        current.onCompleted();
                    }
                });
            }
        });
    }

    private WorkNode getShowH5Node() {
        return (WorkNode.build(NODE_CHECK_H5, new Worker() {
            @Override
            public void doWork(final Node current) {
                Utils.fakeRequest("http://www.api3.com", new HttpCallBack() {
                    @Override
                    public void onOk() {
                        startActivityForResult(new Intent(AfterActivity.this, TestH5Activity.class), REQUEST_CODE_H5);
                    }

                    @Override
                    public void onFailure() {
                        current.onCompleted();
                    }
                });
            }
        }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_H5:
                workFlow.continueWork();
                break;
            default:
                break;
        }
    }
}
