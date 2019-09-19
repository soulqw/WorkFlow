package com.qw.workflow;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "WorkFlow";

    private WorkFlow workFlow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testWorkFlow();
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
                        Log.d(TAG, "this is node " + current.getId() + " executed");
                        current.onCompleted();
                    }
                }))
                .withNode(WorkNode.build(2, new Worker() {
                    @Override
                    public void doWork(Node current) {
                        Log.d(TAG, "this is node " + current.getId() + " executed");
                        try {
                            Thread.sleep(3000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        current.onCompleted();
                    }
                }))
                .withNode(WorkNode.build(4, new Worker() {
                    @Override
                    public void doWork(Node current) {
                        Log.d(TAG, "this is node " + current.getId() + " executed");
                        current.onCompleted();
                    }
                }))
                .withNode(WorkNode.build(3, new Worker() {
                    @Override
                    public void doWork(Node current) {
                        Log.d(TAG, "this is node " + current.getId() + " executed");
                        new AlertDialog.Builder(MainActivity.this)
                                .setMessage("选择接下来做什么")
                                .setNegativeButton("停止", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        workFlow.dispose();
                                    }
                                }).setNeutralButton("执行节点 5", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                workFlow.startWithNode(5);
                            }
                        }).setPositiveButton("继续执行下一个", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                workFlow.continueWork();
                            }
                        }).create().show();
                    }
                }))
                .withNode(WorkNode.build(5, new Worker() {
                    @Override
                    public void doWork(final Node current) {
                        Log.d(TAG, "this is node " + current.getId() + " executed");
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
                        Log.d(TAG, "this is node " + current.getId() + " executed");
                    }
                }))
                .create();
        workFlow.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
