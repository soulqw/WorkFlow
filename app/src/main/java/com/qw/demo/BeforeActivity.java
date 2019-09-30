package com.qw.demo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.qw.demo.utils.HttpCallBack;
import com.qw.demo.utils.Utils;

/**
 * @author cd5160866
 * @date 2019-09-29
 */
public class BeforeActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_H5 = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);
        ((TextView) findViewById(R.id.tv_desc)).setText("使用前");
        checkFirstDialogIfNeed();
    }

    /**
     * step 1
     */
    private void checkFirstDialogIfNeed() {
        Utils.fakeRequest("http://www.api1.com", new HttpCallBack() {
            @Override
            public void onOk() {
                showADialog();
            }

            @Override
            public void onFailure() {
//                //请求失败直接跳过，请求注册协议
//                checkRegisterAgreement();
                checkNeedShowH5();
            }
        });
    }

    private void showADialog() {
        new AlertDialog.Builder(this)
                .setTitle("这是一条有态度的广告")
                .setPositiveButton("我看完了", null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        //	//弹框结束后请求注册协议
//                        checkRegisterAgreement();
                        //现在产品要插入一个H5页面优先请求
                        checkNeedShowH5();

                    }
                }).create().show();
    }

    /**
     * step 2
     */
    private void checkRegisterAgreement() {
        Utils.fakeRequest("http://www.api2.com", new HttpCallBack() {
            @Override
            public void onOk() {
                showBDialog();
            }

            @Override
            public void onFailure() {
                //do nothing
            }
        });
    }

    private void showBDialog() {
        new AlertDialog.Builder(this)
                .setTitle("这是注册协议")
                .setPositiveButton("我看完了", null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        //do nothing
                    }
                }).create().show();
    }

    //step 3
    private void checkNeedShowH5() {
        Utils.fakeRequest("http://www.api3.com", new HttpCallBack() {
            @Override
            public void onOk() {
                toH5Page();
            }

            @Override
            public void onFailure() {
                checkRegisterAgreement();
            }
        });
    }

    private void toH5Page() {
        startActivityForResult(new Intent(this, TestH5Activity.class), REQUEST_CODE_H5);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_H5:
                checkRegisterAgreement();
                break;
            default:
                break;
        }
    }
}
