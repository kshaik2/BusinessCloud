package com.infor.cloudsuite.dto;

import java.util.HashSet;
import java.util.Set;

import com.infor.cloudsuite.entity.AmiDescriptor;
import com.infor.cloudsuite.entity.ProductVersion;



public class ProductVersionDto {

    private Set<String> amiDescriptors;
    private String accessKey;
    private String secretKey;
    private Long id;
    private String description;
    private Boolean ieOnly;
    private String name;
    private Long productId;
    private ProductDto product;
    private Long regionId;

    public ProductVersionDto() {

    }
    public ProductVersionDto(ProductVersion productVersion) {

        accessKey = productVersion.getAccessKey();
        secretKey=productVersion.getSecretKey();
        id=productVersion.getId();
        description=productVersion.getDescription();
        ieOnly=productVersion.getIeOnly();
        name=productVersion.getName();
        productId=productVersion.getProduct().getId();
        if(productVersion.getRegion() != null) {
            regionId=productVersion.getRegion().getId();
        }
        for (AmiDescriptor amiDescriptor : productVersion.getAmiDescriptors()) {
            getAmiDescriptors().add(amiDescriptor.getName());

        }
        this.product=new ProductDto(productVersion.getProduct());

    }
    public Set<String> getAmiDescriptors() {
        if (amiDescriptors==null) {
            return new HashSet<>();
        }
        return amiDescriptors;
    }
    public void setAmiDescriptors(Set<String> amiDescriptors) {
        this.amiDescriptors = amiDescriptors;
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
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
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
    public Long getProductId() {
        return productId;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    public ProductDto getProduct() {
        return product;
    }
    public void setProduct(ProductDto product) {
        this.product = product;
    }
    public Long getRegion_id() {
        return regionId;
    }
    public void setRegion_id(Long region_id) {
        this.regionId = region_id;
    }

}
