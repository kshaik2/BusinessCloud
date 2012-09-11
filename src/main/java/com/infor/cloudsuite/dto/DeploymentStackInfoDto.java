package com.infor.cloudsuite.dto;

import java.util.HashMap;
import java.util.Map;

public class DeploymentStackInfoDto {

	private Long totalCount;
	private Integer uniqueUserCount;
	private Integer uniqueCreatedByUserCount;
	private Map<Long,Long> countMap;
	private Map<Long,Long> createdByCountMap;	
	
	public DeploymentStackInfoDto() {
		
	}
	
	public Long getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}


	public Integer getUniqueUserCount() {
		return uniqueUserCount;
	}

	public void setUniqueUserCount(Integer uniqueUserCount) {
		this.uniqueUserCount = uniqueUserCount;
	}

	public Integer getUniqueCreatedByUserCount() {
		return uniqueCreatedByUserCount;
	}

	public void setUniqueCreatedByUserCount(Integer uniqueCreatedByUserCount) {
		this.uniqueCreatedByUserCount = uniqueCreatedByUserCount;
	}

	public Map<Long, Long> getCountMap() {
		if (countMap==null) {
			countMap=new HashMap<>();
		}
		return countMap;
	}

	public void setCountMap(Map<Long, Long> countMap) {
		this.countMap = countMap;
	}

	public Map<Long, Long> getCreatedByCountMap() {
		if (createdByCountMap==null) {
			createdByCountMap=new HashMap<>();
		}
		return createdByCountMap;
	}

	public void setCreatedByCountMap(Map<Long, Long> createdByCountMap) {
		this.createdByCountMap = createdByCountMap;
	}



	
}
