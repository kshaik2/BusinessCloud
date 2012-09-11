package com.infor.cloudsuite.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.infor.cloudsuite.service.StringDefs;

/**
 * User: bcrow
 * Date: 3/19/12 10:51 AM
 */

@Entity
public class TrialRequest extends DatedDataEntity {

    private Long id;
    private User user;
    private List<ProductVersion> trialProductVersions;
    private Region region;
    private String language;
    private String requestKey;
    private String comment;
    

    @GenericGenerator(name = "sequenceGenerator",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {
            @Parameter(name = "sequence_name", value = "TrialRequest_SEQ"),
            @Parameter(name = "initial_value", value = StringDefs.SEQ_INITIAL_VALUE),
            @Parameter(name = "optimizer", value = StringDefs.SEQ_OPTIMIZER),
            @Parameter(name = "increment_size", value = StringDefs.SEQ_INCREMENT) })
    @Id
    @GeneratedValue(generator = "sequenceGenerator")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    public List<ProductVersion> getProductVersions() {
        if (trialProductVersions == null) {
            trialProductVersions = new ArrayList<>();
        }
        return trialProductVersions;
    }
    
    public void setProductVersions(List<ProductVersion> trialProductVersions) {
        this.trialProductVersions = trialProductVersions;
    }

    @ManyToOne
    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    @Column(length = 10)
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Column(length = 100)
    public String getRequestKey() {
        return requestKey;
    }

    public void setRequestKey(String requestKey) {
        this.requestKey = requestKey;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public Date getCreatedAt() {
        return super.createdAt;
    }

    @Override
    public Date getUpdatedAt() {
        return super.updatedAt;
    }



    
    
}
