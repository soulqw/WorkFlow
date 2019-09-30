package com.qw.demo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.qw.workflow.Node;
import com.qw.workflow.WorkFlow;
import com.qw.workflow.WorkNode;
import com.qw.workflow.Worker;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "WorkFlow";

    private WorkFlow workFlow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_before).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, BeforeActivity.class));
            }
        });
        findViewById(R.id.btn_after).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AfterActivity.class));
            }
        });
    }

    private void testWorkFlow() {
        if (null != workFlow) {
            workFlow.dispose();
        }
        workFlow = new WorkFlow.Builder()
                .withNode(WorkNode.build(1, new Worker() {
                    @Override
                    public void doWork(Node current) {
                        Log.d(TAG, "this is node " + current.getId() + " executed. thread:" + Thread.currentThread().getName());
                        try {
                            Thread.sleep(3000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // workflow will execute the next node when called onCompleted
                        current.onCompleted();
                    }
                }))
                .withNode(WorkNode.build(2, new Worker() {
                    @Override
                    public void doWork(Node current) {
                        Log.d(TAG, "this is node " + current.getId() + " executed. thread:" + Thread.currentThread().getName());
                        current.onCompleted();
                    }
                }))
                .withNode(WorkNode.build(4, new Worker() {
                    @Override
                    public void doWork(Node current) {
                        Log.d(TAG, "this is node " + current.getId() + " executed. thread:" + Thread.currentThread().getName());
                        current.onCompleted();
                    }
                }))
                .withNode(WorkNode.build(3, new Worker() {
                    @Override
                    public void doWork(Node current) {
                        Log.d(TAG, "this is node " + current.getId() + " executed. thread:" + Thread.currentThread().getName());
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("选择接下来做什么")
                                .setCancelable(false)
                                .setItems(new String[]{
                                        "停止",
                                        "执行节点 5",
                                        "继续执行下一个",
                                        "回退",
                                }, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        switch (i) {
                                            case 0:
                                                workFlow.dispose();
                                                break;
                                            case 1:
                                                workFlow.startWithNode(5);
                                                break;
                                            case 2:
                                                workFlow.continueWork();
                                                break;
                                            case 3:
                                                workFlow.revert();
                                                break;
                                        }
                                        dialogInterface.dismiss();
                                    }
                                }).create().show();
                    }
                }))
                .withNode(WorkNode.build(5, new Worker() {
                    @Override
                    public void doWork(final Node current) {
                        Log.d(TAG, "this is node " + current.getId() + " executed. thread:" + Thread.currentThread().getName());
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                current.onCompleted();
                            }
                        }).start();
                    }
                }))
                .withNode(WorkNode.build(6, new Worker() {
                    @Override
                    public void doWork(final Node current) {
                        Log.d(TAG, "this is node " + current.getId() + " executed. thread:" + Thread.currentThread().getName());
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "this is node " + current.getId() + " executed", Toast.LENGTH_SHORT).show();
                                current.onCompleted();
                            }
                        });
                    }
                }))
                .create();
        workFlow.start();
    }
}
