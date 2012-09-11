package com.infor.cloudsuite.dto;

/**
 * User: bcrow
 * Date: 3/28/12 10:11 AM
 */
public class ProductInfoDto extends ProductDto{

    private TrialDeployDto availability;
    private Integer tileOrder;
    
    public ProductInfoDto() {
           
    }

    public ProductInfoDto(Long id, String shortName, String longName, Boolean trialAvail, Boolean deployAvail, Integer tileOrder) {
        super(id, shortName, longName);
        this.availability = new TrialDeployDto(trialAvail, deployAvail);
        this.tileOrder = tileOrder;
    }

    public TrialDeployDto getAvailability() {
        return availability;
    }

    public void setAvailability(TrialDeployDto availability) {
        this.availability = availability;
    }

    public Integer getTileOrder() {
        return tileOrder;
    }

    public void setTileOrder(Integer tileOrder) {
        this.tileOrder = tileOrder;
    }
}
