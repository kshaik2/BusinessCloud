package com.infor.cloudsuite.dto;

import java.util.ArrayList;
import java.util.List;

import com.infor.cloudsuite.entity.Product;
import com.infor.cloudsuite.entity.ProductVersion;
import com.infor.cloudsuite.entity.TileSize;

public class ProductWithDescriptionDto {

	    private Long id;
	    private String name;
	    private String shortName;
	    private String displayName1;
	    private String displayName2;
	    private String displayName3;
	    
	    
	    private Boolean trialsAvailable = false;
	    private Boolean deploymentsAvailable = false;
	    private String tileSize = TileSize.small.name();
	    private Integer tileOrder = 0;
	    private Boolean ieOnly;
	    
	    private List<ProductDescriptionDto> productDescriptionDtos;
	    private List<ProductVersionDto> productVersions;
	    
	public ProductWithDescriptionDto() {
		
	}
	
	public ProductWithDescriptionDto(Product product, List<ProductDescriptionDto> productDescriptionDtos) {
		this(product,productDescriptionDtos,false);
	}
	public ProductWithDescriptionDto(Product product, List<ProductDescriptionDto> productDescriptionDtos, boolean ignoreIds) {
		
		if (!ignoreIds){
			this.id=product.getId();
		}
		
		
		this.name=product.getName();
		this.shortName=product.getShortName();
		
		this.trialsAvailable=product.getTrialsAvailable();
		this.deploymentsAvailable=product.getDeploymentsAvailable();
		
		this.tileSize=product.getTileSize().name();
		this.tileOrder=product.getTileOrder();
		this.displayName1=product.getDisplayName1();
		this.displayName2=product.getDisplayName2();
		this.displayName3=product.getDisplayName3();
		this.ieOnly=product.getIeOnly();
		
		if (productDescriptionDtos != null) {
			getProductDescriptionDtos().addAll(productDescriptionDtos);
		}

		for (ProductVersion productVersion : product.getProductVersions()) {
			getProductVersions().add(new ProductVersionDto(productVersion));
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Boolean getTrialsAvailable() {
		return trialsAvailable;
	}

	public void setTrialsAvailable(Boolean trialsAvailable) {
		this.trialsAvailable = trialsAvailable;
	}

	public Boolean getDeploymentsAvailable() {
		return deploymentsAvailable;
	}

	public void setDeploymentsAvailable(Boolean deploymentsAvailable) {
		this.deploymentsAvailable = deploymentsAvailable;
	}


	public String getTileSize() {
		return tileSize;
	}

	public void setTileSize(TileSize tileSize) {
		this.tileSize = tileSize.name();
	}
	
	public void setTileSize(String tileSize) {
		this.tileSize=tileSize;
		
	}

	public Integer getTileOrder() {
		return tileOrder;
	}

	public void setTileOrder(Integer tileOrder) {
		this.tileOrder = tileOrder;
	}

	public List<ProductDescriptionDto> getProductDescriptionDtos() {
		if (this.productDescriptionDtos == null) {
			this.productDescriptionDtos=new ArrayList<>();
			
		}
		return productDescriptionDtos;
	}

	public void setProductDescriptionDtos(List<ProductDescriptionDto> productDescriptionDtos) {
		this.productDescriptionDtos = productDescriptionDtos;
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

	public List<ProductVersionDto> getProductVersions() {
		if (productVersions == null) {
			productVersions=new ArrayList<>();
		}
		return productVersions;
	}

	public void setProductVersions(List<ProductVersionDto> productVersions) {
		this.productVersions = productVersions;
	}

	
	public Boolean getIeOnly() {
		return ieOnly;
	}

	public void setIeOnly(Boolean ieOnly) {
		this.ieOnly = ieOnly;
	}

	public StringBuilder forDebug() {
		StringBuilder builder=new StringBuilder();
		builder.append("id:").append(id).append("\n");
		builder.append("name:").append(name).append("\n");
		builder.append("shortName:").append(shortName).append("\n");
		builder.append("tileSize:").append(tileSize).append("\n");
	
		
		return builder;
	}
	
}