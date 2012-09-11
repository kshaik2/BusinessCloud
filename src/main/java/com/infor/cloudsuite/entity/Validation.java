package com.infor.cloudsuite.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.validator.constraints.Email;

import com.infor.cloudsuite.service.StringDefs;
import com.infor.cloudsuite.validation.DomainBlacklist;

/**
 * Validation entity used for storing registration validation keys and lost password reset keys.
 * User: bcrow
 * Date: 10/16/11 10:12 PM
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"email", "type"}))
public class Validation implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private String company;
    private String language;
    private String email;
    private ValidationType type;
    private Date createDate;
    private String validationKey;

    @GenericGenerator(name = "sequenceGenerator",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {
            @Parameter(name = "sequence_name", value = "Validation_SEQ"),
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

    @Column(length=50)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    @Column(length=50)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Column(length=100)
    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    @Column(length=5)
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Email(message = "Invalid email address")
    @DomainBlacklist
    @Column(nullable = false, updatable = false, length=100)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    @Enumerated(EnumType.STRING)
    public ValidationType getType() {
        return type;
    }

    public void setType(ValidationType type) {
        this.type = type;
    }

    @JsonIgnore
    @Column(nullable = false, updatable = false)
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @JsonIgnore
    @Column(nullable = false, updatable = false)
    public String getValidationKey() {
        return validationKey;
    }

    public void setValidationKey(String validationKey) {
        this.validationKey = validationKey;
    }
}
