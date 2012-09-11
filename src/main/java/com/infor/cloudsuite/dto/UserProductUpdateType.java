package com.infor.cloudsuite.dto;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

/**
 * User: bcrow
 * Date: 11/8/11 9:29 AM
 */
public enum UserProductUpdateType {
    TRIAL_TYPE("trialType"),
    DEPLOY_TYPE("deployType"),
    OWNED_TYPE("ownedType");

    private String value;

    UserProductUpdateType(String value) {
        this.value = value;
    }

    @JsonValue
    @Override
    public String toString() {
        return value;
    }

    @JsonCreator
    public static UserProductUpdateType fromEnum(String value) {
        for (UserProductUpdateType userProductUpdateType : values()) {
            if (userProductUpdateType.value.equals(value)) {
                return userProductUpdateType;
            }
        }
        return null;
    }
}
