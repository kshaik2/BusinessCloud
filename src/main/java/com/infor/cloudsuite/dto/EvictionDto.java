package com.infor.cloudsuite.dto;

import java.io.Serializable;

public class EvictionDto {

	private String className;
	private Serializable primaryKey;
	
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public Serializable getPrimaryKey() {
		return primaryKey;
	}
	public void setPrimaryKey(Serializable primaryKey) {
		this.primaryKey = primaryKey;
	}
	
	
	
}
