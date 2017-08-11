package com.endicott.edu.service; /**
 * Created by abrocken on 7/8/2017.
 */

import com.endicott.edu.datalayer.NewsFeedDao;
import com.endicott.edu.models.NewsFeedItemModel;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

// The Java class will be hosted at the URI path "/helloworld"
@Path("/newsfeed")
public class NewsFeedService {
    @GET
    @Path("/{runId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<NewsFeedItemModel> getNewsFeed(@PathParam("runId") String runId) {
        NewsFeedDao dao = new NewsFeedDao();
        return dao.getAllNotes(runId);
    }

//    @POST
//    @Path("/{runId}")
//    @Produces(MediaType.TEXT_HTML)
//    public String post(@PathParam("runId") String runId) {
//        return "Hello added.";
//    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_HTML)
    public String post(NewsFeedItemModel news) {
        // This has an issue.  Not receiving the news that was sent.
        // Getting an empty NewsFeedItemModel 
        return news.getMessage();
    }
    /**
     * A simple test service.
     *
     * @return A success message.
     */
    @GET
    @Path("/test")
    @Produces(MediaType.TEXT_PLAIN)
    public String getTest() {
        return "Success.\n";
    }
}
