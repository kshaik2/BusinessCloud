package com.infor.cloudsuite.dto.db;

import java.util.Date;

public class ProductCountDto {

	private Date searched;
	private Long productId;
	private Long count;
	
	public ProductCountDto() {
		
	}
	public ProductCountDto(Long productId, Long count) {
		this.productId=productId;
		this.count=count;
	}
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}
	public Date getSearched() {
		return searched;
	}
	public void setSearched(Date searched) {
		this.searched = searched;
	}
	
	
}
