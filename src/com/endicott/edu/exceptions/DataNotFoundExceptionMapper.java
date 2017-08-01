package com.endicott.edu.exceptions;

import com.endicott.edu.models.CollegeErrorMessage;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by abrocken on 7/25/2017.
 */
@Provider
public class DataNotFoundExceptionMapper implements ExceptionMapper<DataNotFoundException> {
    @Override
    public Response toResponse(DataNotFoundException e) {
        CollegeErrorMessage errMsg = new CollegeErrorMessage(e.getMessage(), 404, "www.endicott.edu");
        return Response.status(Response.Status.NOT_FOUND).build();
        // The above isn't quite right.  We really want to return errMsg, and return that as a
        // JSON response.  I'm having trouble getting that to work.
        // Should be something like:  return Response.status(Response.Status.NOT_FOUND).entity(errMsg).build();
    }
}
