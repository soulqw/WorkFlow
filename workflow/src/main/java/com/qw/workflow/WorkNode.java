package com.qw.workflow;


/**
 * 工作节点
 *
 * @author cd5160866
 */
public class WorkNode implements Node {

    /**
     * 节点id
     */
    private int nodeId;

    /**
     * 节点工作者
     */
    private Worker worker;

    private WorkCallBack callBack;

    public static WorkNode build(int nodeId, Worker worker) {
        return new WorkNode(nodeId, worker);
    }

    public WorkNode(int nodeId, Worker worker) {
        this.nodeId = nodeId;
        this.worker = worker;
    }

    void doWork(WorkCallBack callBack) {
        this.callBack = callBack;
        worker.doWork(this);
    }

    void removeCallBack() {
        this.callBack = null;
    }

    @Override
    public int getId() {
        return nodeId;
    }

    @Override
    public void onCompleted() {
        if (null != callBack) {
            callBack.onWorkCompleted();
        }
    }

    @Override
    public String toString() {
        return "nodeId : " + getId();
    }

    interface WorkCallBack {

        /**
         * 当前任务完成
         */
        void onWorkCompleted();

    }
}
