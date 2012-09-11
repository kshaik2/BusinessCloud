package com.infor.cloudsuite.entity;

import java.util.EnumSet;

import com.infor.cloudsuite.service.StringDefs;

/**
 * User: bcrow
 * Date: 11/7/11 10:39 AM
 */
public enum Role {
    ROLE_ADMIN(StringDefs.ROLE_ADMIN),
    ROLE_I24_ADMIN(StringDefs.ROLE_I24_ADMIN),
    ROLE_SALES(StringDefs.ROLE_SALES),
    ROLE_EXTERNAL(StringDefs.ROLE_EXTERNAL),
    ROLE_VALIDATED(StringDefs.ROLE_VALIDATED),
    ROLE_SUPERADMIN(StringDefs.ROLE_SUPERADMIN);

    public static final EnumSet<Role> ALLOWED_ROLES = EnumSet.of(ROLE_ADMIN, ROLE_EXTERNAL,ROLE_SALES);
    public static final EnumSet<Role> ADMIN_ROLES = EnumSet.of(ROLE_ADMIN, ROLE_SUPERADMIN);
    public static final EnumSet<Role> INFOR24_ROLES = EnumSet.of(ROLE_ADMIN, ROLE_SUPERADMIN, ROLE_I24_ADMIN);
    public static final EnumSet<Role> SALES_ROLES = EnumSet.of(ROLE_SALES);
    public static final EnumSet<Role> USER_ROLES = EnumSet.of(ROLE_EXTERNAL);
 //   public static EnumSet<Role> HIGHLEVEL_ADMIN_ROLES=EnumSet.of(ROLE_SUPERADMIN,ROLE_ADMIN);
//    public static EnumSet<Role> SUPERADMIN_ROLES=EnumSet.of(ROLE_SUPERADMIN,ROLE_ADMIN);

    private final String value;
    
    Role(String value) {
        this.value = value;
    }
    

    @Override
    public String toString() {
        return value;
    }

    
}



