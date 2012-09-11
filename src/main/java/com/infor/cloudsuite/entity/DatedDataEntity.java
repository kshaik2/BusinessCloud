package com.infor.cloudsuite.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * User: bcrow
 * Date: 10/12/11 2:28 PM
 */
public abstract class DatedDataEntity implements Serializable {
    protected Date createdAt;
    protected Date updatedAt;

    public abstract Date getCreatedAt();

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public abstract Date getUpdatedAt();

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
