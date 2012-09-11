package com.infor.cloudsuite.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.infor.cloudsuite.service.StringDefs;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"name","product_id"})})
public class ProductVersion extends DatedDataEntity  {

	private Long id;
	private String name;
	private String description;
    private Boolean ieOnly = false;
    private Set<AmiDescriptor> amiDescriptors;
    private String accessKey;
    private String secretKey;
    private Region region;
    
    private Product product;
    
	
    @ManyToOne
    public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	@GenericGenerator(name = "sequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "ProductVersion_SEQ"),
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
    



    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getIeOnly() {
        return ieOnly;
    }

    public void setIeOnly(Boolean ieOnly) {
        this.ieOnly = ieOnly;
    }
    

    @JsonIgnore
    @Column(length = 200)
    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    @JsonIgnore
    @Column(length = 255)
    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnore
	public Set<AmiDescriptor> getAmiDescriptors() {
		if (amiDescriptors==null) {
			amiDescriptors=new HashSet<>();
		}
		return amiDescriptors;
	}

	public void setAmiDescriptors(Set<AmiDescriptor> amiDescriptors) {
		this.amiDescriptors = amiDescriptors;
	}

    @Override
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    public Date getCreatedAt() {
        return super.createdAt;
    }

    @Override
    @Temporal(TemporalType.TIMESTAMP)
    public Date getUpdatedAt() {
        return super.updatedAt;
    }

    @ManyToOne
    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }
    
}
