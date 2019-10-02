package com.qw.workflow;

import android.util.SparseArray;

/**
 * 工作流
 *
 * @author cd5160866
 * @date 2019-09-03
 */
public class WorkFlow {

    private final String TAG = WorkFlow.class.getSimpleName();

    private SparseArray<WorkNode> flowNodes;

    private WorkNode recentNode;

    private boolean isDisposed = false;

    public WorkFlow(SparseArray<WorkNode> flowNodes) {
        this.flowNodes = flowNodes;
    }

    /**
     * 标记为处理完成,调用后不再能执行任何开启的操作
     */
    public void dispose() {
        reset();
        if (null != flowNodes) {
            flowNodes.clear();
            flowNodes = null;
            recentNode = null;
        }
        this.isDisposed = true;
    }

    /**
     * 给workflow添加一个工作节点
     *
     * @param workNode 节点
     */
    public void addNode(WorkNode workNode) {
        if (isDisposed) {
            throw new IllegalStateException("you can not operate a disposed workflow");
        }
        flowNodes.append(workNode.getId(), workNode);
    }

    /**
     * 开始工作，默认从第一个节点
     */
    public void start() {
        if (isDisposed) {
            throw new IllegalStateException("you can not operate a disposed workflow");
        }
        startWithNode(flowNodes.keyAt(0));
    }

    /**
     * 基于某个节点Id 开始工作
     *
     * @param startNodeId 节点id
     */
    public void startWithNode(int startNodeId) {
        if (isDisposed) {
            throw new IllegalStateException("you can not operate a disposed workflow");
        }
        if (flowNodes.indexOfKey(startNodeId) < 0 || flowNodes.size() == 0) {
            return;
        }
        reset();
        final int startIndex = flowNodes.indexOfKey(startNodeId);
        WorkNode startNode = flowNodes.valueAt(startIndex);
        this.recentNode = startNode;
        startNode.doWork(new WorkNode.WorkCallBack() {
            @Override
            public void onWorkCompleted() {
                findAndExecuteNextNodeIfExist(startIndex);
            }
        });
    }

    /**
     * 让当前工作流继续工作
     * 效果等同于当前节点调用 Node.onCompleted
     */
    public void continueWork() {
        if (isDisposed) {
            throw new IllegalStateException("you can not operate a disposed workflow");
        }
        if (null != recentNode) {
            recentNode.onCompleted();
        }
    }

    /**
     * 回退
     * 基于最近节点回退至上一节点
     */
    public void revert() {
        if (isDisposed) {
            throw new IllegalStateException("you can not operate a disposed workflow");
        }
        if (null != recentNode && null != flowNodes) {
            int recentIndex = flowNodes.indexOfValue(recentNode);
            int targetId = flowNodes.keyAt(recentIndex - 1);
            if (targetId >= 0) {
                startWithNode(targetId);
            }
        }
    }

    public boolean isDisposed() {
        return isDisposed;
    }

    /**
     * 获得最近工作节点id
     *
     * @return 如果存在返回该节点id 否则返回-1
     */
    public int getRecentNodeId() {
        if (null == recentNode) {
            return -1;
        }
        return recentNode.getId();
    }

    private void findAndExecuteNextNodeIfExist(int startIndex) {
        final int nextIndex = startIndex + 1;
        final WorkNode nextNode = flowNodes.valueAt(nextIndex);
        if (null != nextNode) {
            this.recentNode = nextNode;
            nextNode.doWork(new WorkNode.WorkCallBack() {
                @Override
                public void onWorkCompleted() {
                    findAndExecuteNextNodeIfExist(nextIndex);
                }
            });
        }
    }

    private void reset() {
        if (null != flowNodes) {
            for (int i = 0; i < flowNodes.size(); i++) {
                flowNodes.valueAt(i).removeCallBack();
            }
        }
    }

    public static class Builder {

        private SparseArray<WorkNode> f;

        public Builder() {
            this.f = new SparseArray<>();
        }

        public Builder withNode(WorkNode node) {
            this.f.append(node.getId(), node);
            return this;
        }

        public WorkFlow create() {
            return new WorkFlow(f);
        }
    }

}

