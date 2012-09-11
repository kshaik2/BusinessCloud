package com.infor.cloudsuite.platform.jpa.hibernate;

import org.hibernate.dialect.HSQLDialect;

/**
 * The base HSQLDialect generates errors in the new Hibernate version when
 * dealing with an in-memory hsql database. This extenstion will fix those
 * issues.
 * User: bcrow
 * Date: 8/20/12 9:24 AM
 */
public class ExtHSQLDialect extends HSQLDialect {

    @Override
    public String getDropSequenceString(String sequenceName) {
        // Adding the "if exists" clause to avoid warnings
        return "drop sequence if exists " + sequenceName;
    }

    @Override
    public boolean dropConstraints() {
        // We don't need to drop constraints before dropping tables, that just leads to error
        // messages about missing tables when we don't have a schema in the database
        return false;
    }


}
