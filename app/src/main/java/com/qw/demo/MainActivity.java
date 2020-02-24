package com.qw.demo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.qw.workflow.Node;
import com.qw.workflow.WorkFlow;
import com.qw.workflow.WorkNode;
import com.qw.workflow.Worker;

public class MainActivity extends AppCompatActivity {


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
        findViewById(R.id.btn_simple).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startWorkFlow();
            }
        });
    }

    private static final int STEP_TOAST = 1;

    private static final int STEP_DIALOG = 20;

    private static final int STEP_SNACK_BAR = 3;

    private void startWorkFlow() {
        new WorkFlow.Builder()
                .withNode(WorkNode.build(STEP_TOAST, new Worker() {
                    @Override
                    public void doWork(final Node current) {
                        //do any work you want
                        Toast.makeText(MainActivity.this, "step for toast", Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //call it when finish work
                                current.onCompleted();
                            }
                        }, 2000);

                    }
                }))
                .withNode(WorkNode.build(STEP_SNACK_BAR, new Worker() {
                    @Override
                    public void doWork(final Node current) {
                        Snackbar.make(findViewById(R.id.btn_after), "step for snack_bar", Snackbar.LENGTH_SHORT).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                current.onCompleted();
                            }
                        }, 2000);
                    }
                }))
                .withNode(WorkNode.build(STEP_DIALOG, new Worker() {
                    @Override
                    public void doWork(final Node current) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("step for show dialog")
                                .setPositiveButton("complete", null)
                                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialogInterface) {
                                        current.onCompleted();
                                    }
                                }).create().show();
                    }
                })).create()
                .start();
    }
}
