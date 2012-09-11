package com.infor.cloudsuite.dto.export.v2;

import com.amazonaws.util.json.JSONObject;
import com.infor.cloudsuite.dto.export.AbstractExportDto;
import com.infor.cloudsuite.entity.CSLocale;
import com.infor.cloudsuite.entity.ProductDescription;

public class ExportProductDescriptionDto extends AbstractExportDto{

	private String descKey;
	private String csLocaleVariant;
	private String csLocaleCountry;
	private String csLocaleLanguage;
	private String productShortName;
	private String text;

	public ExportProductDescriptionDto() {
		
	}
	
	public ExportProductDescriptionDto(ProductDescription productDescription) {
	
		this.descKey=productDescription.getDescKey().name();
		CSLocale csLocale=productDescription.getLocale();
		this.csLocaleVariant=csLocale.getVariant();
		this.csLocaleCountry=csLocale.getCountry();
		this.csLocaleLanguage=csLocale.getLanguage();
		this.productShortName=productDescription.getProduct().getShortName();
		this.text=productDescription.getText();
	}
	
	public ExportProductDescriptionDto(JSONObject jsonObject) {
		this.descKey=jsonObject.optString("descKey");
		this.csLocaleVariant=jsonObject.optString("csLocaleVariant");
		this.csLocaleCountry=jsonObject.optString("csLocaleCountry");
		this.csLocaleLanguage=jsonObject.optString("csLocaleLanguage");
		this.productShortName=jsonObject.optString("productShortName");
		this.text=jsonObject.optString("text");
	}

	public String getDescKey() {
		return descKey;
	}

	public void setDescKey(String descKey) {
		this.descKey = descKey;
	}

	public String getCsLocaleVariant() {
		return csLocaleVariant;
	}

	public void setCsLocaleVariant(String csLocaleVariant) {
		this.csLocaleVariant = csLocaleVariant;
	}

	public String getCsLocaleCountry() {
		return csLocaleCountry;
	}

	public void setCsLocaleCountry(String csLocaleCountry) {
		this.csLocaleCountry = csLocaleCountry;
	}

	public String getCsLocaleLanguage() {
		return csLocaleLanguage;
	}

	public void setCsLocaleLanguage(String csLocaleLanguage) {
		this.csLocaleLanguage = csLocaleLanguage;
	}

	public String getProductShortName() {
		return productShortName;
	}

	public void setProductShortName(String productShortName) {
		this.productShortName = productShortName;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	
}
