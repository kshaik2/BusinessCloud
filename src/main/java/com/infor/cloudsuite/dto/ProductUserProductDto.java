package com.infor.cloudsuite.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.infor.cloudsuite.entity.TileSize;
import com.infor.cloudsuite.entity.UserProductKey;

/**
 * User: bcrow
 * Date: 10/26/11 1:18 PM
 */
public class ProductUserProductDto {

    private Long id;
    private String name;
    private String shortName;
    private String displayName1;
    private String displayName2;
    private String displayName3;
    private Boolean owned;
    private Boolean ieOnly;
    private String tileSize;
    private Integer tileOrder;
    private Map<String, String> descriptions;
    private UserProductKey userProductKey;
    private TrialDeployDto availability;
    private TrialDeployDto security;
    private List<ProductVersionDto> versions;
    
    public ProductUserProductDto() {

    }

    public ProductUserProductDto(Long id, String name, String shortName, Boolean owned, Boolean trialsAvail, Boolean deploysAvail, Boolean ieOnly,
                                 TileSize tileSize, Integer tileOrder, Boolean userTrialAvail, Boolean userLaunchAvail, UserProductKey userProductKey, 
                                 String displayName1, String displayName2, String displayName3) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.userProductKey = userProductKey;
        this.owned = owned;
        this.ieOnly = ieOnly == null ? false : ieOnly;
        this.tileSize = tileSize == null ? TileSize.small.name() : tileSize.name();
        this.tileOrder = tileOrder == null ? 0 : tileOrder;
        this.availability = new TrialDeployDto(trialsAvail, deploysAvail);
        this.security = new TrialDeployDto(userTrialAvail, userLaunchAvail);
        this.displayName1=displayName1;
        this.displayName2=displayName2;
        this.displayName3=displayName3;
    }


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

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Boolean getIeOnly() {
        return ieOnly;
    }

    public void setIeOnly(Boolean ieOnly) {
        this.ieOnly = ieOnly;
    }

    public String getTileSize() {
        return tileSize;
    }

    public void setTileSize(String tileSize) {
        this.tileSize = tileSize;
    }

    public Integer getTileOrder() {
        return tileOrder;
    }

    public void setTileOrder(Integer tileOrder) {
        this.tileOrder = tileOrder;
    }

    public Boolean getOwned() {
        return owned;
    }


    public void setOwned(Boolean owned) {
        this.owned = owned;
    }

    public Map<String, String> getDescriptions() {
        if (descriptions == null) {
            descriptions = new HashMap<>();
        }
        return descriptions;
    }

    public void setDescriptions(Map<String, String> descriptions) {
        this.descriptions = descriptions;
    }

    @JsonIgnore
    public UserProductKey getUserProductKey() {
        return userProductKey;
    }


    public void setUserProductKey(UserProductKey userProductKey) {
        this.userProductKey = userProductKey;
    }

    public TrialDeployDto getAvailability() {
        return availability;
    }

    public void setAvailability(TrialDeployDto availability) {
        this.availability = availability;
    }

    public TrialDeployDto getSecurity() {
        return security;
    }


    public void setSecurity(TrialDeployDto security) {
        this.security = security;
    }

	public List<ProductVersionDto> getVersions() {
		if (versions==null) {
			versions=new ArrayList<>();
		}
		return versions;
	}

	public void setVersions(List<ProductVersionDto> versions) {
		this.versions = versions;
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


}
