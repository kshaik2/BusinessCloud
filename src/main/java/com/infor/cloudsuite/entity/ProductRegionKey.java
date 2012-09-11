package com.infor.cloudsuite.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

/**
 * User: bcrow
 * Date: 3/22/12 9:54 AM
 */
@Embeddable
public class ProductRegionKey implements Serializable {
    
    private Product product;
    private Region region;

    public ProductRegionKey() {
    }

    public ProductRegionKey(Product product, Region region) {
        this.product = product;
        this.region = region;
    }

    @ManyToOne
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @ManyToOne()
    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductRegionKey that = (ProductRegionKey) o;

        return product.getId().equals(that.product.getId()) &&
               region.getId().equals(that.region.getId());

    }

    @Override
    public int hashCode() {
        int result = product.getId().hashCode();
        result = 31 * result + region.getId().hashCode();
        return result;
    }
}
