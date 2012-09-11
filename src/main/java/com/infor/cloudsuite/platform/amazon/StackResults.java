package com.infor.cloudsuite.platform.amazon;

/**
 * User: bcrow
 * Date: 6/15/12 1:22 PM
 */
public class StackResults {
    private boolean complete;
    private boolean rollback;
    private String exception = "--";
    private String rollbackException = "--";

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public boolean isRollback() {
        return rollback;
    }

    public void setRollback(boolean rollback) {
        this.rollback = rollback;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getRollbackException() {
        return rollbackException;
    }

    public void setRollbackException(String rollbackException) {
        this.rollbackException = rollbackException;
    }
}
