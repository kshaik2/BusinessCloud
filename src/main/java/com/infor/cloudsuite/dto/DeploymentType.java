package com.infor.cloudsuite.dto;

public enum DeploymentType {
	INFOR24("INFOR24"),
	AWS("AWS");
	
    private String name;

    DeploymentType(String name){
        this.name=name;
    }
    public String toString(){
        return name;
    }
}
