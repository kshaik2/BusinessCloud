package com.infor.cloudsuite.dto.export.v3;

import com.amazonaws.util.json.JSONObject;
import com.infor.cloudsuite.entity.AmiDescriptor;

public class ExportAmiDescriptorDto {

    private String ami;
    private String awsKey;
    private String awsSecretKey;
    private String description;
    private Boolean eipNeeded;
    private String ipAddress;
    private String name;
    private String size;
    private String tagName;
    private String version;
    private String regionId;
	
	public ExportAmiDescriptorDto() {
		
	}
	
	public ExportAmiDescriptorDto(AmiDescriptor amiDescriptor) {
		ami = amiDescriptor.getAmi();
		awsKey=amiDescriptor.getAwsKey();
		awsSecretKey=amiDescriptor.getAwsSecretKey();
		description=amiDescriptor.getDescription();
		eipNeeded=amiDescriptor.getEipNeeded();
		ipAddress=amiDescriptor.getIpAddress();
		name=amiDescriptor.getName();
		size=amiDescriptor.getSize();
		tagName=amiDescriptor.getTagName();
		version=amiDescriptor.getVersion();
		regionId=amiDescriptor.getRegion().getId().toString();
		
	}
	
	public ExportAmiDescriptorDto(JSONObject jsonObject) {
		this.ami=jsonObject.optString("ami");
		this.awsKey=jsonObject.optString("awsKey");
		this.awsSecretKey=jsonObject.optString("awsSecretKey");
		this.description=jsonObject.optString("description");
		this.eipNeeded=jsonObject.optBoolean("eipNeeded");
		this.ipAddress=jsonObject.optString("ipAddress");
		this.name=jsonObject.optString("name");
		this.size=jsonObject.optString("size");
		this.tagName=jsonObject.optString("tagName");
		this.version=jsonObject.optString("version");
		this.regionId=jsonObject.optString("regionId");
		
		
	}

	public String getAmi() {
		return ami;
	}

	public void setAmi(String ami) {
		this.ami = ami;
	}


	public String getAwsKey() {
		return awsKey;
	}

	public void setAwsKey(String awsKey) {
		this.awsKey = awsKey;
	}

	public String getAwsSecretKey() {
		return awsSecretKey;
	}

	public void setAwsSecretKey(String awsSecretKey) {
		this.awsSecretKey = awsSecretKey;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getEipNeeded() {
		return eipNeeded;
	}

	public void setEipNeeded(Boolean eipNeeded) {
		this.eipNeeded = eipNeeded;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }
	
	
}
