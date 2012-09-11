package com.infor.cloudsuite.dto.dashboard;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DashboardRollingPeriodDto {

	private String name;
	private Long aws=0L;
	private Long infor24=0L;
	private List<DashboardProductTotalDto> products;
	private Date periodStart;
	private Date periodEnd;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Long getAws() {
		return aws;
	}
	public void setAws(Long aws) {
		this.aws = aws;
	}
	public Long getInfor24() {
		return infor24;
	}
	public void setInfor24(Long infor24) {
		this.infor24 = infor24;
	}
	public List<DashboardProductTotalDto> getProducts() {
		if (products==null) {
			products=new ArrayList<>();
		}
		return products;
	}
	public void setProducts(List<DashboardProductTotalDto> products) {
		this.products = products;
	}
	public Date getPeriodStart() {
		return periodStart;
	}
	public void setPeriodStart(Date periodStart) {
		this.periodStart = periodStart;
	}
	public Date getPeriodEnd() {
		return periodEnd;
	}
	public void setPeriodEnd(Date periodEnd) {
		this.periodEnd = periodEnd;
	}
	
}
