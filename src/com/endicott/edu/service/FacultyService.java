package com.endicott.edu.service;

import com.endicott.edu.models.FacultyModel;
import com.endicott.edu.datalayer.FacultyDao;
import com.endicott.edu.simulators.CollegeManager;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mazlin Higbee
 * @4:30pm 10-9-17
 * mhigb411@mail.endicott.edu
 */
// The Java class will be hosted at the URI path "/faculty"
@Path("/faculty")
public class FacultyService {
    private FacultyDao fao = new FacultyDao();
    Gson gson = new Gson();

    /**
     * Return all faculty as json object
     * ERROR RECEIVED WHEN USING List<> type (essageBodyWriter not found for media type=application/json, type=class java.util.ArrayList, genericType=java.util.List<com.endicott.edu.models.FacultyModel>)
     * @param runId simulation id
     * @return list of faculty
     */
    @GET
    @Path("/{runId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getFaculty(@PathParam("runId") String runId) {
        return gson.toJson(fao.getFaculty(runId));
    }

    /**
     *add a new member of faculty
     * @param facultyJson json object of faculty as plain text
     * @return the faculty member we created as json
     */
    @POST
    @Path("/add")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public String postFaculty(String facultyJson) {
        FacultyModel member = gson.fromJson(facultyJson,FacultyModel.class);

        //make sure the college were trying to use exists
        String runId = member.getRunId();

        if (!CollegeManager.doesCollegeExist(runId)) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        //make sure the dao layer nows this is a new member by setting id to -1
        member.setFacultyID(-1);
        fao.saveNewFaculty(runId,member);

        return gson.toJson(member);
    }

    /**
     * remove a faculty member
     * @param facultyJson json string of faculty object
     * @return removed member of faculty json object
     */
    @POST
    @Path("/delete")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteFaculty(String facultyJson) {
        FacultyModel member = gson.fromJson(facultyJson,FacultyModel.class);

        //make sure the college were trying to use exsists
        String runId = member.getRunId();

        if (!CollegeManager.doesCollegeExist(runId)) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }


        fao.removeSingleFaculty(runId,member);
        return gson.toJson(member);
    }

}
