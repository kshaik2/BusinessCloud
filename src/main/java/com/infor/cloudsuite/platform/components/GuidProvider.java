package com.infor.cloudsuite.platform.components;

import java.util.UUID;

import org.springframework.stereotype.Component;

/**
 * User: bcrow
 * Date: 1/6/12 12:28 PM
 */
@Component
public class GuidProvider {
    
    public String generateGuid() {
        return UUID.randomUUID().toString();
    }
}
