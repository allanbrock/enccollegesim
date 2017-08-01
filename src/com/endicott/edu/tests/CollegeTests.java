package com.endicott.edu.tests;

import com.endicott.edu.models.CollegeModel;
import com.endicott.edu.service.ServiceUtils;
import com.endicott.edu.simulators.CollegeManager;
import org.glassfish.jersey.client.ClientConfig;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by abrocken on 7/17/2017.
 */

public class CollegeTests {
    private Client client;
    private static final String PASS = "pass";
    private static final String FAIL = "fail";

    private void init(){
        this.client = ClientBuilder.newClient(new ClientConfig());
        //this.client = ClientBuilder.newClient(new ClientConfig().register(LoggingFilter.class));
    }

    public static void runTests(){
        CollegeTests tester = new CollegeTests();
        //initialize the tester
        tester.init();
        //tests get all users Web Service Method
        tester.testEstablishCollege();
    }

    private void testEstablishCollege(){
        String runId = "test007";
        String result = PASS;

        System.out.println("Test case name: testEstablishCollege\n");

        WebTarget webTarget = client.target(ServiceUtils.SERVICE_URL + "college/" + runId);
        Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);

        // Delete the college (if it already exists)
        //Response response = invocationBuilder.delete();

        // Create college
        Response response = invocationBuilder.post(null);

        //Response response = invocationBuilder.post(Entity.entity(college, MediaType.APPLICATION_JSON));
        if(response.getStatus() != 200) {
            System.out.println("    Got bad response creating college: " + response.getStatus());
            result = FAIL;
            return;
        }

        // Get newly created college.
        response = invocationBuilder.get();
        CollegeModel college = response.readEntity(CollegeModel.class);

        if(response.getStatus() != 200 || college.getHoursAlive() != 1 || college.getAvailableCash() != CollegeManager.STARTUP_FUNDING) {
            System.out.println("    Got bad college response or contents.");
            result = FAIL;
        }

        System.out.println("Test case name: testEstablishCollege, Result: " + result );
    }

}
