# WorkFlow

 [![Hex.pm](https://img.shields.io/badge/download-0.0.4-green)](https://www.apache.org/licenses/LICENSE-2.0)
 [![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)]()

#### 简化顺序调用方法之间的嵌套和耦合：

- 轻松替换相互嵌套的方法之间的调用顺序
- 增删改方便,提高代码简洁性和可读性、降低维护成本

## Installation：

```java
dependencies {
     implementation 'com.github.soulqw:WorkFlow:0.0.4'
}

```

## Usage：

#### 场景示例1
依次顺序的展示Toast，Dialog，SnackBar：

```java
    private static final int STEP_TOAST = 1;

    private static final int STEP_DIALOG = 2;

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
```

效果如图：

![image](https://upload-images.jianshu.io/upload_images/11595074-2001727ab83da1e5.gif?imageMogr2/auto-orient/strip)

假如现在需要让Dialog和SnackBar，替换位置，只需要让Dialog定义值比SnackBar大即可：

```java
    private static final int STEP_TOAST = 1;

    private static final int STEP_DIALOG = 20;

    private static final int STEP_SNACK_BAR = 3;
```

效果如下：

![image](https://upload-images.jianshu.io/upload_images/11595074-6e2c294a0d584ccc.gif?imageMogr2/auto-orient/strip)

#### 场景示例2
进入APP首页按顺序请求3个接口,第一个接口和第三个接口成功后启动对话框,第二个接口跳转到Web页面

```java
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
                .create();
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

```
###  效果：

![image](https://img-blog.csdnimg.cn/20190930234247563.gif)

### 流程：

![image](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAxOS8xMC80LzE2ZDk3MjgyZmM5OTE5ZDQ?x-oss-process=image/format,png)

### 功能介绍:

- 每个节点只关注自己是否完成:

  ```java
   private WorkNode getFirstAdNode() {
          return WorkNode.build(NODE_FIRST_AD, new Worker() {
              @Override
              public void doWork(final Node current) {
                  //仅仅只需关心自己是否完成，下一个节点会自动执行
                  current.onCompleted();
              }
          });
      }
  ```

- 可以操作WorkFlow对象来控制全局节点

  ```java
  public class WorkFlow {

      /**
       * 标记为处理完成,调用后不再能执行任何开启的操作
       */
      public void dispose()

      /**
       * 给workflow添加一个工作节点
       *
       * @param workNode 节点
       */
      public void addNode(WorkNode workNode)

      /**
       * 开始工作，默认从第一个节点
       */
      public void start()

      /**
       * 基于某个节点Id 开始工作
       *
       * @param startNodeId 节点id
       */
      public void startWithNode(int startNodeId)

      /**
       * 让当前工作流继续工作
       * 效果等同于当前节点调用 Node.onCompleted
       */
      public void continueWork()

      /**
       * 回退
       * 基于最近节点回退至上一节点
       */
      public void revert()

  }
  ```

更多代码可参考demo

#### [工作原理和最佳示例](https://blog.csdn.net/u014626094/article/details/101697305)



