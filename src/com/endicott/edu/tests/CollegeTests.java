package com.endicott.edu.tests;

import com.endicott.edu.models.CollegeModel;
import com.endicott.edu.simulators.CollegeManager;
import org.glassfish.jersey.client.ClientConfig;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

// Created by abrocken on 7/17/2017.

class CollegeTests {
    private Client client;
    private static final String PASS = "pass";
    private static final String FAIL = "fail";
    private static final String TEST_COLLEGE_ID = "test_automated_001";
    private static final String TEST_COLLEGE_ID_MISSING = "test_automated_xyz";
    private String serviceUrl;

    private void init(){
        this.client = ClientBuilder.newClient(new ClientConfig());
        //this.client = ClientBuilder.newClient(new ClientConfig().register(LoggingFilter.class));
    }

    private void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    static void runTests(String serviceUrl){
        CollegeTests tester = new CollegeTests();

        tester.init();
        tester.setServiceUrl(serviceUrl);
        tester.testFetchMissingCollege(TEST_COLLEGE_ID_MISSING);
        tester.testCreateCollege(TEST_COLLEGE_ID);
        tester.testGetCollege(TEST_COLLEGE_ID);
        tester.testDeleteCollege(TEST_COLLEGE_ID);
    }

    private void testFetchMissingCollege(String runId) {
        String result = PASS;

        System.out.print("Test case name: testFetchMissingCollege...");

        WebTarget webTarget = client.target(serviceUrl + "college/" + runId);
        Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);

        Response response = invocationBuilder.get();
        if(response.getStatus() != 404) {
            System.out.println("    Got unexpected response college.");
            result = FAIL;
        }

        System.out.println(" Result: " + result );
    }

    private void testGetCollege(String runId){
        String result = PASS;

        System.out.print("Test case name: testGetCollege...");

        WebTarget webTarget = client.target(serviceUrl + "college/" + runId);
        Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);

        Response response = invocationBuilder.get();
        CollegeModel college = response.readEntity(CollegeModel.class);

        if(response.getStatus() != 200 || college.getHoursAlive() != 1 || college.getAvailableCash() != CollegeManager.STARTUP_FUNDING) {
            System.out.println("    Got bad college response or contents.");
            result = FAIL;
        }

        System.out.println(" Result: " + result );
    }


    private void testCreateCollege(String runId){
        String result = PASS;

        System.out.print("Test case name: testCreateCollege...");

        WebTarget webTarget = client.target(serviceUrl + "college/" + runId);
        Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);

        // Delete the college (if it already exists)
        //Response response = invocationBuilder.delete();

        // Create college
        Response response = invocationBuilder.post(null);

        //Response response = invocationBuilder.post(Entity.entity(college, MediaType.APPLICATION_JSON));
        if(response.getStatus() != 200) {
            System.out.println("    Got bad response creating college: " + response.getStatus());
            return;
        }

        // Get newly created college.
        response = invocationBuilder.get();
        CollegeModel college = response.readEntity(CollegeModel.class);

        if(response.getStatus() != 200 || college.getHoursAlive() != 1 || college.getAvailableCash() != CollegeManager.STARTUP_FUNDING) {
            System.out.println("    Got bad college response or contents:" + response.getStatus());
            result = FAIL;
        }

        System.out.println(" Result: " + result );
    }


    private void testDeleteCollege(String runId) {
        String result = PASS;

        System.out.print("Test case name: testDeleteCollege...");

        WebTarget webTarget = client.target(serviceUrl + "college/" + runId);
        Invocation.Builder invocationBuilder =  webTarget.request();

        Response response = invocationBuilder.delete();
        if(response.getStatus() != 200) {
            System.out.println("    Got unexpected delete response college." + response.getStatus());
            result = FAIL;
        }

        response = invocationBuilder.get();
        if(response.getStatus() != 404) {
            System.out.println("    Got unexpected get response college: " + response.getStatus());
            result = FAIL;
        }

        System.out.println(" Result: " + result );
    }


}
