package com.infor.cloudsuite.dto;

import com.infor.cloudsuite.entity.Product;

/**
 * User: bcrow
 * Date: 12/2/11 3:49 PM
 */
public class ProductDto {
    private Long id;
    private String shortName;
    private String longName;
    private String displayName1;
    private String displayName2;
    private String displayName3;

    public ProductDto() {
    }
    
    public ProductDto(Product product) {
    	//this(product.getId(),product.getShortName(),product.getName());
    	this.id=product.getId();
    	this.shortName=product.getShortName();
    	this.longName=product.getName();
    	this.displayName1=product.getDisplayName1();
    	this.displayName2=product.getDisplayName2();
    	this.displayName3=product.getDisplayName3();
    }

    public ProductDto(Long id, String shortName, String longName) {
        this.id = id;
        this.shortName = shortName;
        this.longName = longName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

	public String getDisplayName1() {
		return displayName1;
	}

	public void setDisplayName1(String displayName1) {
		this.displayName1 = displayName1;
	}

	public String getDisplayName2() {
		return displayName2;
	}

	public void setDisplayName2(String displayName2) {
		this.displayName2 = displayName2;
	}

	public String getDisplayName3() {
		return displayName3;
	}

	public void setDisplayName3(String displayName3) {
		this.displayName3 = displayName3;
	}
    
    
}
