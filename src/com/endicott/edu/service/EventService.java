package com.endicott.edu.service;


import com.endicott.edu.datalayer.EventsDao;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Mazlin Higbee
 * 12-4-2017
 * mhigb411@mail.endicott.edu
 * A class to maintain the services for events.
 * Hosts all REST requests for events.
 */

@Path("/event")
public class EventService {
    private EventsDao dao = new EventsDao();
    private Gson gson = new Gson();


    /**
     * Return all events as json object
     * @param runId simulation id
     * @return list of events
     */
    @GET
    @Path("/{runId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getFaculty(@PathParam("runId") String runId) {
        return gson.toJson(dao.getEvents(runId));
    }

}
