package com.endicott.edu.service;

import com.endicott.edu.datalayer.DormitoryDao;
import com.endicott.edu.models.DormitoryModel;
import com.endicott.edu.simulators.CollegeManager;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

// The Java class will be hosted at the URI path "/finances"
@Path("/dorms")
public class DormServices {
    private DormitoryDao dao = new DormitoryDao();

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
    public DormitoryModel postDorm(String dormJsonString) {
        Gson g = new Gson();
        DormitoryModel dorm = g.fromJson(dormJsonString, DormitoryModel.class);

        // This is not very good error handling.  We should return HTTP error.
        if (dorm == null) {
            DormitoryModel badDorm = new DormitoryModel();
            badDorm.setNote("Didn't get a dorm");
            return badDorm;
        }

        // What if we already have a dorm with the same name?
        // We should return an error.

        // Make sure the college exists, return error if not.
        String runId = dorm.getRunId();
        if (!CollegeManager.doesCollegeExist(runId)) {
            //throw new DataNotFoundException("No such college.");
            dorm.setNote("College not found: " + runId + " Dorm: " + dorm.getName());
            return dorm;
        }

        // Override some fields
        dorm.setHourLastTimeBillPaid(0);

        // Create a dorm
        DormitoryDao dormDao = new DormitoryDao();
        dormDao.saveNewDorm(runId, dorm);
        dorm.setNote("created dorm.");
        return dorm;
    }

    /**
     * Get a list of the dorms that are in the college.
     *
     * @param runId simulation ID
     * @return JSON formatted list.
     */
    @GET
    @Path("/{runId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DormitoryModel> getDorms(@PathParam("runId") String runId) {
        return dao.getDorms(runId);
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
