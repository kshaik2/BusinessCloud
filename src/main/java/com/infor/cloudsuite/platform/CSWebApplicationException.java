package com.infor.cloudsuite.platform;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * User: bcrow
 * Date: 11/1/11 1:35 PM
 */
public class CSWebApplicationException extends WebApplicationException {
    public CSWebApplicationException() {
        super();
    }

    public CSWebApplicationException(Response response) {
        super(response);
    }

    public CSWebApplicationException(int status) {
        super(status);
    }

    public CSWebApplicationException(int status, Object entity) {
        super(Response.status(status).entity(entity).build());
    }

    public CSWebApplicationException(Response.Status status) {
        super(status);
    }

    public CSWebApplicationException(Response.Status status, Object entity) {
        super(Response.status(status).entity(entity).build());
    }

    public CSWebApplicationException(Throwable cause) {
        super(cause);
    }

    public CSWebApplicationException(Throwable cause, Response response) {
        super(cause, response);
    }

    public CSWebApplicationException(Throwable cause, int status) {
        super(cause, status);
    }

    public CSWebApplicationException(Throwable cause, int status, Object entity) {
        super(cause, Response.status(status).entity(entity).build());
    }

    public CSWebApplicationException(Throwable cause, Response.Status status) {
        super(cause, status);
    }

    public CSWebApplicationException(Throwable cause, Response.Status status, Object entity) {
        super(cause, Response.status(status).entity(entity).build());
    }
}
