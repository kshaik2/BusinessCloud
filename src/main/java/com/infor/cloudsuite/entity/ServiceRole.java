package com.infor.cloudsuite.entity;

import java.util.EnumSet;

import com.infor.cloudsuite.service.StringDefs;

public enum ServiceRole {
    SERVICEROLE_BASIC_EDGE(StringDefs.SERVICEROLE_BASIC_EDGE);

    public static final EnumSet ALLOWED_ROLES = EnumSet.of(SERVICEROLE_BASIC_EDGE);

    private String value;

    ServiceRole(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

}
