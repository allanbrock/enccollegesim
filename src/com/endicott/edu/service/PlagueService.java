package com.endicott.edu.service;

import com.endicott.edu.datalayer.PlagueDao;
import com.endicott.edu.models.PlagueModel;
import com.endicott.edu.simulators.CollegeManager;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

// The Java class will be hosted at the URI path "/plagues"
@Path("/plagues")
public class PlagueService {
    private PlagueDao dao = new PlagueDao();

    /**
     * Create a new plague.
     * Notice that it consumes "text plain".  It really should be  APPLICATION_JSON
     * but having trouble getting this to work.
     *
     * @return college in JSON format
     */
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public PlagueModel postDorm(String dormJsonString) {
        Gson g = new Gson();
        PlagueModel plague = g.fromJson(dormJsonString, PlagueModel.class);

        // If we already have a plague in progress
        // We should return an error.

        // Make sure the college exists, return error if not.
        String runId = plague.getRunId();
        if (!CollegeManager.doesCollegeExist(runId)) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        // Override some fields
        plague.setHourLastUpdated(0);

        // Create a plague
        PlagueDao dormDao = new PlagueDao();
        dormDao.saveNewPlague(runId, plague);
        plague.setNote("Plague created");
        return plague;
    }

    /**
     * Get a list of the plagues
     *
     * @param runId simulation ID
     * @return JSON formatted list.
     */
    @GET
    @Path("/{runId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PlagueModel> getPlagues(@PathParam("runId") String runId) {
        return dao.getPlagues(runId);
    }

}
