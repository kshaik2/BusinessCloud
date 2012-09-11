package com.infor.cloudsuite.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.infor.cloudsuite.service.StringDefs;

/**
 * User: bcrow
 * Date: 3/23/12 10:25 AM
 */
@Entity
public class TrialProductChild {

    private Long id;
    private Region region;
    private ProductVersion parentVersion;
    private ProductVersion childVersion;

    public TrialProductChild() {
    }

    public TrialProductChild(Region region, ProductVersion parent, ProductVersion child) {
        this.region = region;
        this.parentVersion = parent;
        this.childVersion = child;
    }

    @GenericGenerator(name = "sequenceGenerator",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {
            @Parameter(name = "sequence_name", value = "TrialProductChild_SEQ"),
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
    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    @ManyToOne
    public ProductVersion getParentVersion() {
        return parentVersion;
    }

    public void setParentVersion(ProductVersion parent) {
        this.parentVersion = parent;
    }

    @ManyToOne
    public ProductVersion getChildVersion() {
        return childVersion;
    }

    public void setChildVersion(ProductVersion child) {
        this.childVersion = child;
    }
}
