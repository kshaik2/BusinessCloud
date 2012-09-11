package com.infor.cloudsuite.platform.amazon;

import com.amazonaws.AmazonClientException;

public class DummyFailPointException extends AmazonClientException {

	private DummyFailPoint dummyFailPoint;
	
	public DummyFailPointException(DummyFailPoint dummyFailPoint) {
		super(dummyFailPoint.name());
		this.dummyFailPoint=dummyFailPoint;
	}
	
	public DummyFailPoint getDummyFailPoint() {
		return dummyFailPoint;
	}
	
}
