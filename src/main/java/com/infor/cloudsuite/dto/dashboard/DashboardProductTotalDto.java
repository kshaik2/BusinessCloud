package com.infor.cloudsuite.dto.dashboard;

public class DashboardProductTotalDto {

	private Long productId;
	private Long infor24=0L;
	private Long aws=0L;
	
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public Long getInfor24() {
		return infor24;
	}
	public void setInfor24(Long infor24) {
		this.infor24 = infor24;
	}
	public Long getAws() {
		return aws;
	}
	public void setAws(Long aws) {
		this.aws = aws;
	}
	
	
}
