package com.infor.cloudsuite.entity;

/**
 * Created with IntelliJ IDEA.
 * User: briancrow
 * Date: 5/23/12
 * Time: 8:21 AM
 */

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.infor.cloudsuite.service.StringDefs;

@Entity
public class Company implements Serializable {

    private Long id;
    private String name;
    private String notes;
    private String inforId;
    private Industry industry;

    @GenericGenerator(name = "sequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "Company_SEQ"),
                    @Parameter(name = "initial_value", value = StringDefs.SEQ_INITIAL_VALUE),
                    @Parameter(name = "optimizer", value = StringDefs.SEQ_OPTIMIZER),
                    @Parameter(name = "increment_size", value = StringDefs.SEQ_INCREMENT)})
    @Id
    @GeneratedValue(generator = "sequenceGenerator")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(length = 200)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Column(length = 50)
    public String getInforId() {
		return inforId;
	}

	public void setInforId(String inforId) {
		this.inforId = inforId;
	}
    
    @ManyToOne(fetch = FetchType.EAGER)
    public Industry getIndustry() {
        return industry;
    }

   

	public void setIndustry(Industry industry) {
        this.industry = industry;
    }
}
