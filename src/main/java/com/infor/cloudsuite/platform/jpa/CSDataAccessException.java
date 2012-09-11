package com.infor.cloudsuite.platform.jpa;

import org.springframework.dao.DataAccessException;

/**
 * User: bcrow
 * Date: 10/13/11 1:48 PM
 */
public class CSDataAccessException extends DataAccessException {
    public CSDataAccessException(String msg) {
        super(msg);
    }

    public CSDataAccessException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public CSDataAccessException(Throwable cause) {
        super("CloudSuite Data Access Exception", cause);
    }
}
