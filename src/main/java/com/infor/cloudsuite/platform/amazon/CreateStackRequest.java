package com.infor.cloudsuite.platform.amazon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.infor.cloudsuite.entity.AmiDescriptor;
import com.infor.cloudsuite.entity.DeploymentStack;

/**
* User: bcrow
* Date: 6/15/12 8:43 AM
*/
public class CreateStackRequest {
    private String regionName;
    private DeploymentStack deploymentStack;
    private Set<AmiDescriptor> amiDescriptors;
    private List<String> productNames;
    private List<String> destEmails;
    private Locale locale;

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public DeploymentStack getDeploymentStack() {
        return deploymentStack;
    }

    public void setDeploymentStack(DeploymentStack deploymentStack) {
        this.deploymentStack = deploymentStack;
    }

    public Set<AmiDescriptor> getAmiDescriptors() {
        if (amiDescriptors == null) amiDescriptors = new HashSet<>();
        return amiDescriptors;
    }

    public void setAmiDescriptors(Set<AmiDescriptor> amiDescriptors) {
        this.amiDescriptors = amiDescriptors;
    }

    public List<String> getProductNames() {
        if (productNames == null) productNames = new ArrayList<>();
        return productNames;
    }

    public void setProductNames(List<String> productNames) {
        this.productNames = productNames;
    }

    public List<String> getDestEmails() {
        if (destEmails == null) destEmails = new ArrayList<>();
        return destEmails;
    }

    public void setDestEmails(List<String> destEmails) {
        this.destEmails = destEmails;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
