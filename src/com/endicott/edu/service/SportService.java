package com.endicott.edu.service;

import com.endicott.edu.datalayer.DormitoryDao;
import com.endicott.edu.datalayer.SportsDao;
import com.endicott.edu.models.DormitoryModel;
import com.endicott.edu.models.SportModel;
import com.endicott.edu.models.SportsModel;
import com.endicott.edu.simulators.CollegeManager;
import com.endicott.edu.simulators.SportManager;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

// The Java class will be hosted at the URI path "/finances"
@Path("/sports")
public class SportService {
    private SportsDao dao = new SportsDao();

    /**
     * Create a new dorm.
     * Notice that it consumes "text plain".  It really should be  APPLICATION_JSON
     * but having trouble getting this to work.
     *
     * @return college in JSON format
     */
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public SportModel addSport(String sportJsonString) {
        Gson g = new Gson();
        SportModel sport = g.fromJson(sportJsonString, SportModel.class);

        // What if we already have a dorm with the same name?
        // We should return an error.

        // Make sure the college exists, return error if not.
        String runId = sport.getRunId();
        if (!CollegeManager.doesCollegeExist(runId)) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        // Override some fields
        sport.setHourLastUpdated(0);

        // Create a sport
        SportsDao sportDao = new SportsDao();
        sportDao.saveNewSport(runId, sport);
        sport.setNote("created sport.");
        return sport;
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


    @DELETE
    @Path("/{runId}")
    @Produces(MediaType.TEXT_PLAIN)
    public String deleteSport(@PathParam("runId") String runId) {
        SportManager president = new SportManager();
        SportManager.sellSport(runId);
        return "Sport has been deleted.\n";
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
