package com.infor.cloudsuite.dto;

import com.infor.cloudsuite.entity.CSLocale;
import com.infor.cloudsuite.entity.ProductDescKey;
import com.infor.cloudsuite.entity.ProductDescription;

public class ProductDescriptionDto {
    private Long id;
    private Long productId;
    private String localeLanguage;
    private String localeCountry;
    private String localeVariant;
    
    private String productDescKey;
    private String text;
    
    public ProductDescriptionDto(ProductDescKey descKey, CSLocale locale, String text) {
    	this.productId=null;
    	this.id=null;
    	
    	this.productDescKey=descKey.name();
    	this.text=text;
    	this.localeLanguage=locale.getLanguage();
    	this.localeCountry=locale.getCountry();
    	this.localeVariant=locale.getVariant();
    }
    public ProductDescriptionDto(Long id, Long productId, ProductDescKey descKey, CSLocale locale, String text) {
    	this.productId=productId;
    	this.productDescKey=descKey.name();
    	this.text=text;
    	this.localeLanguage=locale.getLanguage();
    	this.localeCountry=locale.getCountry();
    	this.localeVariant=locale.getVariant();
    	this.id=id;
    	
    }
    public ProductDescriptionDto(ProductDescription productDescription) {
    	this.id=productDescription.getId();
    	this.productId=productDescription.getProduct().getId();
    	this.localeLanguage=productDescription.getLocale().getLanguage();
    	this.localeCountry=productDescription.getLocale().getCountry();
    	this.localeVariant=productDescription.getLocale().getVariant();
    	this.productDescKey=productDescription.getDescKey().name();
    	this.text=productDescription.getText();
    	
    }
    
    public ProductDescriptionDto() {
    	
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public String getLocaleLanguage() {
		return localeLanguage;
	}

	public void setLocaleLanguage(String localeLanguage) {
		this.localeLanguage = localeLanguage;
	}

	public String getLocaleCountry() {
		return localeCountry;
	}

	public void setLocaleCountry(String localeCountry) {
		this.localeCountry = localeCountry;
	}

	public String getLocaleVariant() {
		return localeVariant;
	}

	public void setLocaleVariant(String localeVariant) {
		this.localeVariant = localeVariant;
	}

	public String getProductDescKey() {
		return productDescKey;
	}

	public void setProductDescKey(String productDescKey) {
		this.productDescKey = productDescKey;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
    
    
}
