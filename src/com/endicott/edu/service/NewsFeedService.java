package com.endicott.edu.service; /**
 * Created by abrocken on 7/8/2017.
 */

import com.endicott.edu.datalayer.NewsFeedDao;
import com.endicott.edu.models.NewsFeedItemModel;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
}
