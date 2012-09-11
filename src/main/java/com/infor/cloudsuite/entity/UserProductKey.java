package com.infor.cloudsuite.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

/**
 * User: bcrow
 * Date: 10/25/11 3:16 PM
 */
@Embeddable
public class UserProductKey implements Serializable{
    private User user;
    private Product product;

    public UserProductKey() {
        //Empty constructor
    }

    public UserProductKey(User user, Product product) {
        this.user = user;
        this.product = product;
    }

    @ManyToOne
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserProductKey that = (UserProductKey) o;

        return product.getId().equals(that.product.getId()) &&
               user.getId().equals(that.user.getId());

    }

    @Override
    public int hashCode() {
        int result = user.hashCode();
        result = 31 * result + product.hashCode();
        return result;
    }
}
