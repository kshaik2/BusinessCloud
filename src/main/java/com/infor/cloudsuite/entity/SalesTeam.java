package com.infor.cloudsuite.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;

import com.infor.cloudsuite.service.StringDefs;

/**
 * Created with IntelliJ IDEA.
 * User: Brian Crow
 * Date: 5/22/12
 * Time: 2:09 PM
 */
@Entity
public class SalesTeam {

    private Long id;
    private User lead;
    private User salesRep;

    @GenericGenerator(name = "sequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "SalesTeam_SEQ"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = StringDefs.SEQ_INITIAL_VALUE),
                    @org.hibernate.annotations.Parameter(name = "optimizer", value = StringDefs.SEQ_OPTIMIZER),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = StringDefs.SEQ_INCREMENT)})
    @Id
    @GeneratedValue(generator = "sequenceGenerator")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public User getLead() {
        return lead;
    }

    public void setLead(User lead) {
        this.lead = lead;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public User getSalesRep() {
        return salesRep;
    }

    public void setSalesRep(User salesRep) {
        this.salesRep = salesRep;
    }
}
