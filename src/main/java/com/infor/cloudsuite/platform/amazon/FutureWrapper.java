package com.infor.cloudsuite.platform.amazon;

import java.util.concurrent.Future;

/**
 * User: bcrow
 * Date: 6/14/12 8:28 AM
 */
public class FutureWrapper<A extends Future<?>, W> {
    private A asyncResult;
    private W wrappedValue;

    public FutureWrapper(A asyncResult, W wrappedValue) {
        this.asyncResult = asyncResult;
        this.wrappedValue = wrappedValue;
    }

    public A getAsyncResult() {
        return asyncResult;
    }

    public W getWrappedValue() {
        return wrappedValue;
    }
}
