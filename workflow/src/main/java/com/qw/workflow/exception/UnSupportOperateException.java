package com.qw.workflow.exception;

/**
 * @author: george
 */
public class UnSupportOperateException extends IllegalStateException {
    public UnSupportOperateException() {
        super("you can not operate a disposed workflow");
    }
}
