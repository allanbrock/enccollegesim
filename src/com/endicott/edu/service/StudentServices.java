package com.endicott.edu.service;

import com.endicott.edu.datalayer.StudentDao;
import com.endicott.edu.models.StudentModel;
import com.endicott.edu.simulators.CollegeManager;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

//Created by Connor Frazier on 9/28/17


// The Java class will be hosted at the URI path "/students"
@Path("/students")
public class StudentServices {
    private StudentDao dao = new StudentDao();

    /**
     * Create a new student.
     * Notice that it consumes "text plain".  It really should be  APPLICATION_JSON
     * but having trouble getting this to work.
     *
     * @return college in JSON format
     */
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public StudentModel postStudent(String studentJsonString) {
        Gson g = new Gson();
        StudentModel student = g.fromJson(studentJsonString, StudentModel.class);

        // What if we already have a student with the same name?
        // We should return an error.

        // Make sure the college exists, return error if not.
        String runId = student.getRunId();
        if (!CollegeManager.doesCollegeExist(runId)) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        // Override some fields
        //dorm.setHourLastUpdated(0);

        // Create a student
        StudentDao studentDao = new StudentDao();
        studentDao.saveNewStudent(runId, student);
        student.setNote("created student.");
        return student;
    }

    /**
     * Get a list of the students that are in the college.
     *
     * @param runId simulation ID
     * @return JSON formatted list.
     */
    @GET
    @Path("/{runId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StudentModel> getStudents(@PathParam("runId") String runId) {
        return dao.getStudents(runId);
    }

    /**
     * Get a list of the students that are on a specific sports team.
     *
     * @param runId simulation ID
     * @param teamName for specifiing a team.
     * @return JSON formatted list.
     */
    @GET
    @Path("/{runId}/{command}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StudentModel> getStudentOnSport(@PathParam("runId") String runId, @PathParam("command") String teamName) { return dao.getStudentsOnSport(runId,teamName); }

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
