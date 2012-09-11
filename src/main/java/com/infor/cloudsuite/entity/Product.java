package com.infor.cloudsuite.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.infor.cloudsuite.service.StringDefs;

/**
 * User: bcrow
 * Date: 10/24/11 10:37 AM
 */
@Cacheable
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = "name"),
        @UniqueConstraint(columnNames = "shortName")})
@NamedQueries(@NamedQuery(name = "Product.findProductUserProduct",
        query = "SELECT new com.infor.cloudsuite.dto.ProductUserProductDto" +
                " (prod.id, prod.name, prod.shortName, userProds.owned, prod.trialsAvailable, " +
                " prod.deploymentsAvailable, prod.ieOnly, prod.tileSize, prod.tileOrder, " +
                " userProds.trialAvailable, userProds.launchAvailable, userProds.id, "+
                " prod.displayName1, prod.displayName2, prod.displayName3)" +
                " FROM Product prod " +
                " LEFT OUTER JOIN prod.userProducts userProds WITH userProds.id.user.id = ?1 ",
        lockMode = LockModeType.READ))
public class Product extends DatedDataEntity {
    private Long id;
    private String name;
    private String shortName;
    private String displayName1;
    private String displayName2;
    private String displayName3;
    
    
    private TileSize tileSize = TileSize.small;
    private Integer tileOrder = 0;
    private List<UserProduct> userProducts;
    private Boolean trialsAvailable = false;
    private Boolean deploymentsAvailable = false;
    private Boolean ieOnly=false;
    
    private List<ProductVersion> productVersions;
    
    @GenericGenerator(name = "sequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "Product_SEQ"),
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

    @NotNull
    @Column(length = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotNull
    @Column(length = 50)
    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }


    @Enumerated(EnumType.STRING)
    public TileSize getTileSize() {
        return tileSize;
    }

    public void setTileSize(TileSize tileSize) {
        this.tileSize = tileSize;
    }

    public Integer getTileOrder() {
        return tileOrder;
    }

    public void setTileOrder(Integer tileOrder) {
        this.tileOrder = tileOrder;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "id.product", cascade = CascadeType.REMOVE)
    public List<UserProduct> getUserProducts() {
        if (userProducts == null) {
            userProducts = new ArrayList<>();
        }
        return userProducts;
    }

    public void setUserProducts(List<UserProduct> userProducts) {
        this.userProducts = userProducts;
    }

    public Boolean getTrialsAvailable() {
        return trialsAvailable;
    }

    public void setTrialsAvailable(Boolean trialsAvailable) {
        this.trialsAvailable = trialsAvailable;
    }

    public Boolean getDeploymentsAvailable() {
        return deploymentsAvailable;
    }

    public void setDeploymentsAvailable(Boolean deploymentsAvailable) {
        this.deploymentsAvailable = deploymentsAvailable;
    }
    
    @JsonIgnore
    @OneToMany
    public List<ProductVersion> getProductVersions() {
    	if (productVersions==null) {
    		productVersions=new ArrayList<>();
    	}
    	return productVersions;
    }
    
    
    public void setProductVersions(List<ProductVersion> productVersions) {
		this.productVersions = productVersions;
	}


    @Override
    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreatedAt() {
        return super.createdAt;
    }

    @Override
    @Temporal(TemporalType.TIMESTAMP)
    public Date getUpdatedAt() {
        return super.updatedAt;
    }
    
	public String getDisplayName1() {
		return displayName1;
	}

	public void setDisplayName1(String displayName1) {
		this.displayName1 = displayName1;
	}

	public String getDisplayName2() {
		return displayName2;
	}

	public void setDisplayName2(String displayName2) {
		this.displayName2 = displayName2;
	}

	public String getDisplayName3() {
		return displayName3;
	}

	public void setDisplayName3(String displayName3) {
		this.displayName3 = displayName3;
	}

	public Boolean getIeOnly() {
		return ieOnly;
	}

	public void setIeOnly(Boolean ieOnly) {
		this.ieOnly = ieOnly;
	}



    
}

