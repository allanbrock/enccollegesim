package com.endicott.edu.service;

import com.endicott.edu.datalayer.SportsDao;
import com.endicott.edu.models.SportModel;
import com.endicott.edu.simulators.CollegeManager;
import com.endicott.edu.simulators.SportManager;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;


@Path("/sports")
public class SportService {
    private SportsDao dao = new SportsDao();
    Gson gson = new Gson();
    private SportManager sportManager = new SportManager();

    /**
     * Create a new dorm.
     * Notice that it consumes "text plain".  It really should be  APPLICATION_JSON
     * but having trouble getting this to work.
     *
     * @return college in JSON format
     */
    @POST
    @Path("/{runId}/{sportName}")
    @Produces(MediaType.APPLICATION_JSON)
    public SportModel addSport(@PathParam("runId") String runId,@PathParam("sportName") String sportName) {
        if (!CollegeManager.doesCollegeExist(runId)) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        // Create a sport
        SportsDao sportDao = new SportsDao();
        return(SportManager.addNewTeam(sportName,runId));
    }

    /**
     * Get a list of the sports that are in the college.
     *
     * @param runId simulation ID
     * @return JSON formatted list.
     */
    @GET
    @Path("/{runId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<SportModel> getSports(@PathParam("runId") String runId) {
        return dao.getSports(runId);
    }

    /**
     *  Delete a sports team.
     * @param runId
     * @return
     */
    @DELETE
    @Path("/{runId}/{sellSportName}")
    @Produces(MediaType.TEXT_PLAIN)
    public String deleteSport(@PathParam("runId") String runId, @PathParam("sellSportName") String sportName) {

        if (!CollegeManager.doesCollegeExist(runId)) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        sportManager.deleteSelectedSport(runId,sportName);
        return "Sport should have been deleted";
    }

    /**
     * Get a list of sports that can be added to the college.
     *
     * @param runId the unique id for the simulation run
     * @return a sport list of type string
     */
    @GET
    @Path("/{runId}/{command}")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<SportModel> getAvailableSports(@PathParam("runId") String runId, @PathParam("command") String command) {
        System.out.println("College command: " + command);
        SportManager sportManager = new SportManager();

        if (command.equalsIgnoreCase("available")) {
            return sportManager.checkAvailableSports(runId);
        } else {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

    }

}
