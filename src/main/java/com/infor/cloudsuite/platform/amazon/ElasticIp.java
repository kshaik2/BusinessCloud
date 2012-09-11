package com.infor.cloudsuite.platform.amazon;

/**
* User: bcrow
* Date: 6/12/12 3:30 PM
*/
class ElasticIp {
    private String allocationId;
    private String publicIp;

    ElasticIp(String allocationId, String publicIp) {
        this.allocationId = allocationId;
        this.publicIp = publicIp;
    }

    public String getAllocationId() {
        return allocationId;
    }

    public void setAllocationId(String allocationId) {
        this.allocationId = allocationId;
    }

    public String getPublicIp() {
        return publicIp;
    }

    public void setPublicIp(String publicIp) {
        this.publicIp = publicIp;
    }
}
