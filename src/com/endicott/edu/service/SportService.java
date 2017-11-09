package com.endicott.edu.service;

import com.endicott.edu.datalayer.SportsDao;
import com.endicott.edu.models.SportModel;
import com.endicott.edu.models.SportsModel;
import com.endicott.edu.simulators.CollegeManager;
import com.endicott.edu.simulators.SportManager;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

// The Java class will be hosted at the URI path "/finances"
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


    @POST
    @Path("/delete")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteSport(String sportJson) {
        SportModel sport = gson.fromJson(sportJson,SportModel.class);
        String runId = sport.getRunId();

        if (!CollegeManager.doesCollegeExist(runId)) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }


        sportManager.deleteSelectedSport(runId,sport);
        return gson.toJson(sport);
    }

    /**
     * Update an existing simulation run.
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

//    @GET
//    @Produces("text/plain")
//    //@Produces(MediaType.APPLICATION_XML)
//    public DormitoryModel getById() {
//        DormitoryModel acct = dao.getFinances("8");
//        return acct;
//    }
//    @GET
//    @Path("/{id}")
//    @Produces(MediaType.APPLICATION_XML)
//    public DormitoryModel getById(final @PathParam("id") String id) {
//        DormitoryModel acct = dao.getFinances(id);
//        return acct;
//    }
//    @GET
//    @Produces(MediaType.TEXT_PLAIN)
//    public String getFinances() {
//        DormitoryModel bankAccount = dao.getFinances("8");
//        return bankAccount.toJson();
//    }
//- works 7/19 returns xml displayable in the browser
//    @GET
//    @Produces(MediaType.APPLICATION_XML)
//    public DormitoryModel getFinances() {
//        DormitoryModel bankAccount = dao.getFinances("8");
//        return bankAccount;
//    }
