package com.infor.cloudsuite.entity;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * User: bcrow
 * Date: 10/25/11 3:08 PM
 */
@Entity
@Cacheable
public class UserProduct {
    private UserProductKey id;
    private Boolean trialAvailable = false;
    private Boolean launchAvailable = false;
    private Boolean owned=false;

    protected UserProduct() {
    }


    public UserProduct(User user, Product product) {
        UserProductKey key = new UserProductKey();
        key.setUser(user);
        key.setProduct(product);
        id = key;
    }

    @Transient
    public User getUser() {
        return id.getUser();
    }

    @Transient
    public Product getProduct() {
        return id.getProduct();
    }

    @Id
    public UserProductKey getId() {
        return id;
    }

    public void setId(UserProductKey id) {
        this.id = id;
    }

    public Boolean getTrialAvailable() {
        return trialAvailable;
    }

    public void setTrialAvailable(Boolean trialAvailable) {
        this.trialAvailable = trialAvailable;
    }

    public Boolean getLaunchAvailable() {
        return launchAvailable;
    }

    public void setLaunchAvailable(Boolean launchAvailable) {
        this.launchAvailable = launchAvailable;
    }


	public Boolean getOwned() {
		return owned;
	}


	public void setOwned(Boolean owned) {
		this.owned = owned;
	}
    
    
}
