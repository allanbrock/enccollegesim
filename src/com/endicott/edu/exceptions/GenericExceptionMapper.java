package com.endicott.edu.exceptions;

import com.endicott.edu.models.CollegeErrorMessage;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by abrocken on 7/25/2017.
 */
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable e) {
        CollegeErrorMessage errMsg = new CollegeErrorMessage(e.getMessage(), 500, "http://www.endicott.edu");
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errMsg)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
