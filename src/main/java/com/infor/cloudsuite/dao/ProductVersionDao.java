package com.infor.cloudsuite.dao;

import java.util.List;

import com.infor.cloudsuite.entity.Product;
import com.infor.cloudsuite.entity.ProductVersion;
import com.infor.cloudsuite.platform.jpa.ExtJpaRepository;

public interface ProductVersionDao extends ExtJpaRepository<ProductVersion, Long> {

	public ProductVersion findByName(String name);
	public ProductVersion findByProductAndName(Product product, String name);
	public List<ProductVersion> findByProduct(Product product);
	
	public ProductVersion findByProduct_ShortNameAndName(String productShortName, String name);
}