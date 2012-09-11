package com.infor.cloudsuite.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import com.infor.cloudsuite.service.StringDefs;

/**
 * User: bcrow
 * Date: 1/9/12 9:46 AM
 */
@Entity
public class UserTracking {
    private Long id;
    private User user;
    private TrackingType trackingType;
    private Long targetObject;
    private String otherData;
    private Date timestamp;

    public UserTracking() {
    }

    public UserTracking(User user, TrackingType trackingType) {
        this.user = user;
        this.trackingType = trackingType;
        this.timestamp = new Date();
    }

    public UserTracking(User user, TrackingType trackingType, Long targetObject) {
        this.user = user;
        this.trackingType = trackingType;
        this.targetObject = targetObject;
        this.timestamp = new Date();
    }

    public UserTracking(User user, TrackingType trackingType, Long targetObject, String otherData) {
        this.user = user;
        this.trackingType = trackingType;
        this.targetObject = targetObject;
        this.otherData = otherData;
        this.timestamp = new Date();
    }

    @GenericGenerator(name = "sequenceGenerator",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {
            @Parameter(name = "sequence_name", value = "UserTracking_SEQ"),
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

    @ManyToOne(optional = false)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Index(name = "TrackingType")
    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    public TrackingType getTrackingType() {
        return trackingType;
    }

    public void setTrackingType(TrackingType trackingType) {
        this.trackingType = trackingType;
    }

    public Long getTargetObject() {
        return targetObject;
    }

    public void setTargetObject(Long targetObject) {
        this.targetObject = targetObject;
    }

    public String getOtherData() {
        return otherData;
    }

    public void setOtherData(String otherData) {
        this.otherData = otherData;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
