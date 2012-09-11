package com.infor.cloudsuite.dto.export.v2;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONObject;
import com.infor.cloudsuite.dto.export.AbstractExportDto;
import com.infor.cloudsuite.dto.export.v3.ExportProductVersionDto;
import com.infor.cloudsuite.entity.Product;
import com.infor.cloudsuite.entity.ProductVersion;

public class ExportProductDto extends AbstractExportDto{
	private static final SimpleDateFormat SDF=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz");

	private String shortName;
	private String name;
	private String displayName1;
	private String displayName2;
	private String displayName3;
	
	private Date createdAt;
	private String createdAtString;
	private Date updatedAt;
	private String updatedAtString;
	private Boolean deploymentsAvailable;
	private Integer tileOrder;
	private String tileSize;
	private Boolean trialsAvailable;
	private Boolean ieOnly;
	private List<ExportProductVersionDto> productVersions;
	
	public ExportProductDto() {
		
	}
	
	public ExportProductDto(Product product) {
		this.shortName=product.getShortName();
		this.name=product.getName();
		this.displayName1=product.getDisplayName1();
		this.displayName2=product.getDisplayName2();
		this.displayName3=product.getDisplayName3();
		this.createdAt=product.getCreatedAt();
		if (this.createdAt != null) {
			this.createdAtString=SDF.format(this.createdAt);
		}
		
		this.updatedAt=product.getUpdatedAt();
		if (this.updatedAt != null) {
			this.updatedAtString=SDF.format(this.updatedAt);
		}
		
		this.deploymentsAvailable=product.getDeploymentsAvailable();
		this.tileOrder=product.getTileOrder();
		this.tileSize=product.getTileSize().name();
		this.trialsAvailable=product.getTrialsAvailable();
		this.ieOnly=product.getIeOnly();
		for (ProductVersion productVersion : product.getProductVersions()) {
			getProductVersions().add(new ExportProductVersionDto(productVersion));
		}
		
	}
	
	public ExportProductDto(JSONObject jsonObject) {
		this.createdAt=getDate(jsonObject,"createdAtString","createdAt");
		this.createdAtString=jsonObject.optString("createdAtString");
		this.updatedAt=getDate(jsonObject,"updatedAtString","updatedAt");
		this.updatedAtString=jsonObject.optString("updatedAtString");
		this.shortName=jsonObject.optString("shortName");
		this.displayName1=jsonObject.optString("displayName1");
		this.displayName2=jsonObject.optString("displayName2");
		this.displayName3=jsonObject.optString("displayName3");
		
		this.name=jsonObject.optString("name");


		this.deploymentsAvailable=jsonObject.optBoolean("deploymentsAvailable");
		this.tileOrder=jsonObject.optInt("tileOrder");
		this.tileSize=jsonObject.optString("tileSize");
		this.trialsAvailable=jsonObject.optBoolean("trialsAvailable");
		this.ieOnly=jsonObject.optBoolean("ieOnly");
		
		JSONArray prodVersArray=jsonObject.optJSONArray("productVersions");
		if (prodVersArray != null) {
			for (int i=0; i<prodVersArray.length();i++) {
				JSONObject productVersion = prodVersArray.optJSONObject(i);
				if (productVersion != null) {
					getProductVersions().add(new ExportProductVersionDto(productVersion));
					
				}
				
			}
			
		}
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getCreatedAtString() {
		return createdAtString;
	}

	public void setCreatedAtString(String createdAtString) {
		this.createdAtString = createdAtString;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getUpdatedAtString() {
		return updatedAtString;
	}

	public void setUpdatedAtString(String updatedAtString) {
		this.updatedAtString = updatedAtString;
	}

	public Boolean getDeploymentsAvailable() {
		return deploymentsAvailable;
	}

	public void setDeploymentsAvailable(Boolean deploymentsAvailable) {
		this.deploymentsAvailable = deploymentsAvailable;
	}

	public Integer getTileOrder() {
		return tileOrder;
	}

	public void setTileOrder(Integer tileOrder) {
		this.tileOrder = tileOrder;
	}

	public String getTileSize() {
		return tileSize;
	}

	public void setTileSize(String tileSize) {
		this.tileSize = tileSize;
	}

	public Boolean getTrialsAvailable() {
		return trialsAvailable;
	}

	public void setTrialsAvailable(Boolean trialsAvailable) {
		this.trialsAvailable = trialsAvailable;
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

	public List<ExportProductVersionDto> getProductVersions() {
		if (productVersions==null) {
			productVersions=new ArrayList<>();
		}
		return productVersions;
	}

	public void setProductVersions(List<ExportProductVersionDto> productVersions) {
		this.productVersions = productVersions;
	}

	public Boolean getIeOnly() {
		return ieOnly;
	}

	public void setIeOnly(Boolean ieOnly) {
		this.ieOnly = ieOnly;
	}

	
}
