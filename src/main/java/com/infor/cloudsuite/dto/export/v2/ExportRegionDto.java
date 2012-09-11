package com.infor.cloudsuite.dto.export.v2;

import com.amazonaws.util.json.JSONObject;
import com.infor.cloudsuite.dto.export.AbstractExportDto;
import com.infor.cloudsuite.entity.Region;

public class ExportRegionDto extends AbstractExportDto{

	
	private Long id;
	private String name;
	private String shortName;
	private String cloudAlias;
	private String endPoint;
	private String regionType;
	
	public ExportRegionDto() {
		
	}
	
	public ExportRegionDto(Region region) {
		this.id=region.getId();
		this.name=region.getName();
		this.shortName=region.getShortName();
		this.cloudAlias=region.getCloudAlias();
		this.endPoint=region.getEndPoint();
		this.regionType=region.getRegionType().name();
	}
	
	public ExportRegionDto(JSONObject jsonObject) {
		this.name=jsonObject.optString("name");
		this.shortName=jsonObject.optString("shortName");
		this.id=jsonObject.optLong("id");
		this.cloudAlias=jsonObject.optString("cloudAlias");
		this.endPoint=jsonObject.optString("endPoint");
		this.regionType=jsonObject.optString("regionType");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCloudAlias() {
		return cloudAlias;
	}

	public void setCloudAlias(String cloudAlias) {
		this.cloudAlias = cloudAlias;
	}

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getRegionType() {
        return regionType;
    }

    public void setRegionType(String regionType) {
        this.regionType = regionType;
    }
	
	
	
}
