package com.endicott.edu.service;

// Created by abrocken on 7/8/2017.

import com.endicott.edu.datalayer.CollegeDao;
import com.endicott.edu.models.CollegeModel;
import com.endicott.edu.simulators.CollegeManager;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

// The Java class will be hosted at the URI path "/helloworld"
@Path("/college")
public class CollegeService {
    private CollegeDao collegeDao = new CollegeDao();

    /**
     * Retrieve a college simulation that already exists.
     *
     * @param runId the unique id for the simulation run
     * @return college in JSON format
     */
    @GET
    @Path("/{runId}")
    @Produces(MediaType.APPLICATION_JSON)
    public CollegeModel getCollege(@PathParam("runId") String runId) {
        return collegeDao.getCollege(runId);
    }

    /**
     * Create a new college simulation run.
     * The body of the post is ignored.
     * The server determines the initial settings.
     *
     * @param runId the unique id for the simulation run
     * @return college in JSON format
     */
    @POST
    @Path("/{runId}")
    @Produces(MediaType.APPLICATION_JSON)
    public CollegeModel postCollege(@PathParam("runId") String runId) {
        return CollegeManager.establishCollege(runId);
    }

    /**
     * Delete an existing simulation run.
     *
     * @param runId the unique id for the simulation run
     * @return a useless plain text message
     */
    @DELETE
    @Path("/{runId}")
    @Produces(MediaType.TEXT_PLAIN)
    public String deleteCollege(@PathParam("runId") String runId) {
        CollegeManager president = new CollegeManager();
        CollegeManager.sellCollege(runId);
        return "College might have been deleted.\n";
    }

    /**
     * Update an existing simulation run.
     *
     * @param runId the unique id for the simulation run
     * @return college in JSON format
     */
    @PUT
    @Path("/{runId}/{command}")
    @Produces(MediaType.APPLICATION_JSON)
    public CollegeModel putCollege(@PathParam("runId") String runId, @PathParam("command") String command) {
        System.out.println("College command: " + command);

        if (command.equalsIgnoreCase("nextDay")) {
            return CollegeManager.nextDay(runId);
        } else {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
    }

    /**
     * A simple test service.
     *
     * @return A success message.
     */
    @GET
    @Path("/test")
    @Produces(MediaType.TEXT_HTML)
    public String getTest() {
        StringBuilder sb = new StringBuilder();

        sb.append("<body>\n");
        sb.append("<p>Success!</p>");
        sb.append("<p></p>");
        sb.append("<p><h1>Contributors</h1></p>");
        sb.append("<p><h2>Fall 2017</h2></p>");
        sb.append("<li>Allan Brockenbrough</l1>");
        sb.append("<li>Connor Frazier</li>");
        sb.append("<li>Jeff Thor</li>");
        sb.append("<li>Nick Scrivani</li>");
        sb.append("</body>\n");
        return(sb.toString());
    }
}
