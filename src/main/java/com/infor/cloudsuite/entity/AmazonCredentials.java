package com.infor.cloudsuite.entity;

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
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id",
		"name" }) })
public class AmazonCredentials implements Serializable {

	private String name;
	private String awsKey;
	private String secretKey;
	private User user;
	private Long id;

	@GenericGenerator(name = "sequenceGenerator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "AmazonCredentials_SEQ"),
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

	@Column(nullable=false, length = 50)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAwsKey() {
		return awsKey;
	}

	public void setAwsKey(String awsKey) {
		this.awsKey=awsKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	@ManyToOne(fetch = FetchType.LAZY,optional=false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
