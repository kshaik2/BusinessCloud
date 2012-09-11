package com.infor.cloudsuite.dto.export.v3;

import java.util.HashSet;
import java.util.Set;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONObject;
import com.infor.cloudsuite.entity.AmiDescriptor;
import com.infor.cloudsuite.entity.ProductVersion;

public class ExportProductVersionDto {

	private Set<String> amiDescriptors;
	private Boolean ieOnly;
	private String name;
	private String lookupName;
	private String description;
	private String accessKey;
	private String secretKey;
	private String regionId;
	
	public ExportProductVersionDto(ProductVersion productVersion) {
		
		for (AmiDescriptor amiDescriptor : productVersion.getAmiDescriptors()) {
			getAmiDescriptors().add(amiDescriptor.getName());
		}
		
		this.accessKey=productVersion.getAccessKey();
		this.secretKey=productVersion.getSecretKey();
		this.description=productVersion.getDescription();
		this.name=productVersion.getName();
		this.ieOnly=productVersion.getIeOnly();
		if(productVersion.getRegion() != null) {
		    this.regionId=productVersion.getRegion().getId().toString();
		}
		
		
		
	}
	public ExportProductVersionDto(JSONObject jsonObject) {
		JSONArray amiDescriptorArray=jsonObject.optJSONArray("amiDescriptors");
		if (amiDescriptorArray != null) {
			for (int i=0; i<amiDescriptorArray.length();i++) {
				String amiDescriptor=amiDescriptorArray.optString(i);
				if (amiDescriptor != null) {
					this.getAmiDescriptors().add(amiDescriptor);
				}
			}
		}
		
		this.accessKey=jsonObject.optString("accessKey");
		this.secretKey=jsonObject.optString("secretKey");
		this.description=jsonObject.optString("description");
		this.ieOnly=jsonObject.optBoolean("ieOnly");
		this.name=jsonObject.optString("name");
		this.lookupName=jsonObject.optString("lookupName");
		this.regionId=jsonObject.optString("regionId");

	}


	public Set<String> getAmiDescriptors() {
		if (amiDescriptors==null) {
			amiDescriptors=new HashSet<>();
		}
		return amiDescriptors;
	}


	public void setAmiDescriptors(Set<String> amiDescriptors) {
		this.amiDescriptors = amiDescriptors;
	}
	public Boolean getIeOnly() {
		return ieOnly;
	}
	public void setIeOnly(Boolean ieOnly) {
		this.ieOnly = ieOnly;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAccessKey() {
		return accessKey;
	}
	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}
	public String getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	public String getLookupName() {
		return lookupName;
	}
	public void setLookupName(String lookupName) {
		this.lookupName = lookupName;
	}
    public String getRegionId() {
        return regionId;
    }
    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }
	
}
