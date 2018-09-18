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
     * Update the college based on a command
     *Currently supports:
     *  updateTuition -> this updates the amount that the college costs
     * @param runId the unique id for the simulation run
     * @return college in JSON format
     */
    @PUT
    @Path("/{runId}/{command}/{arguments}")
    @Produces(MediaType.APPLICATION_JSON)
    public CollegeModel updateCollege(@PathParam("runId") String runId, @PathParam("command") String command,@PathParam("arguments") String args) {
        System.out.println("College command: " + command);

        if (command.equalsIgnoreCase("tuition")) { //this changes the tuition of the college.
            return CollegeManager.updateCollegeTuition(runId,Integer.valueOf(args)) ;
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
        sb.append("<p></p>");
        sb.append("<p><h1>Contributors</h1></p>");
        sb.append("<p><h2>2nd Generation: Fall 2018</h2></p>");
        sb.append("<li>CJ Mustone</l1>");
        sb.append("<li>Stephen Hoadley</l1>");
        sb.append("<li>Steven Suchcicki</l1>");
        sb.append("<li>Joseph Moss</l1>");
        sb.append("<li>Tyler Ouellette</l1>");
        String word = "string";
        sb.append("<li>Allan B.</l1>");
        sb.append("<p><h2>Founders: Fall 2017</h2></p>");
        sb.append("<li>Mazlin Higbee</l1>");
        sb.append("<li>Nick Dos Santos</l1>");
        sb.append("<li>Jeremy Doski</l1>");
        sb.append("<li>Cam Bleck</l1>");
        sb.append("<li>Connor Frazier</l1>");
        sb.append("<li>Allison Flood</l1>");
        sb.append("<li>Karen Litwinczyk</l1>");
        sb.append("<li>Nick Scrivani</l1>");
        sb.append("<li>Chris Seidl</l1>");
        sb.append("<li>Jeff Thor</l1>");
        sb.append("<li>Derek Yannone</l1>");
        sb.append("<li>a Cool Cat</l1>");
        sb.append("</body>\n");
        return(sb.toString());
    }
}