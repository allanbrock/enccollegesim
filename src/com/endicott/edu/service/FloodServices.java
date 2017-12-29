package com.endicott.edu.service;

import com.endicott.edu.datalayer.FloodDao;
import com.endicott.edu.models.FloodModel;
import com.endicott.edu.simulators.CollegeManager;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

// The Java class will be hosted at the URI path "/floods"
@Path("/floods")
public class FloodServices {
    private FloodDao dao = new FloodDao();

    /**
     * Create a new flood.
     * Notice that it consumes "text plain".  It really should be  APPLICATION_JSON
     * but having trouble getting this to work.
     *
     * @return college in JSON format
     */
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public FloodModel postDorm(String dormJsonString) {
        Gson g = new Gson();
        FloodModel flood = g.fromJson(dormJsonString, FloodModel.class);

        // If we already have a flood in progress
        // We should return an error.

        // Make sure the college exists, return error if not.
        String runId = flood.getRunId();
        if (!CollegeManager.doesCollegeExist(runId)) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        // Override some fields
        flood.setHourLastUpdated(0);

        // Create a flood
        FloodDao dormDao = new FloodDao();
        dormDao.saveNewFlood(runId, flood);
        flood.setNote("Flood created");
        return flood;
    }

    /**
     * Get a list of the floods
     *
     * @param runId simulation ID
     * @return JSON formatted list.
     */
    @GET
    @Path("/{runId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<FloodModel> getFloods(@PathParam("runId") String runId) {
        return dao.getFloods(runId);
    }

}

