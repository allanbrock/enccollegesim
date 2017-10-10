package com.endicott.edu.tests;

import com.endicott.edu.models.DormitoryModel;
import com.endicott.edu.models.SportModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.glassfish.jersey.client.ClientConfig;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

// Created by abrocken on 7/17/2017.

class SportsTests {
    private Client client;
    private static final String PASS = "pass";
    private static final String FAIL = "fail";
    private static final String COLLEGE_TEST_ID = "testsports";
    private String serviceUrl;

    private void init(){
        this.client = ClientBuilder.newClient(new ClientConfig());
        //this.client = ClientBuilder.newClient(new ClientConfig().register(LoggingFilter.class));
    }

    private void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    static void runTests(String serviceUrl){
        SportsTests tester = new SportsTests();

        tester.init();
        tester.setServiceUrl(serviceUrl);
        tester.testCreateCollege(COLLEGE_TEST_ID);
        tester.testSports(COLLEGE_TEST_ID);
        tester.testDeleteCollege(COLLEGE_TEST_ID);
    }

    private boolean testCreateCollege(String runId) {
        String result = PASS;

        System.out.print("Test case name: testCreateCollegeWithSports...");

        WebTarget webTarget = client.target(serviceUrl + "college/" + runId);
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        // Create college
        Response response = invocationBuilder.post(null);

        //Response response = invocationBuilder.post(Entity.entity(college, MediaType.APPLICATION_JSON));
        if (response.getStatus() != 200) {
            System.out.println("    Got bad response creating college: " + response.getStatus());
            return false;
        }

        return true;
    }

    private void testSports(String runId){
        String result = PASS;
        SportModel[] sports;
        Client client = ClientBuilder.newClient(new ClientConfig());
        WebTarget webTarget = client.target(serviceUrl + "sports/" + runId);
        Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);

        Response response = invocationBuilder.get();
        if(response.getStatus() != 200) {
            System.out.println("    Got bad college response or contents:" + response.getStatus());
            System.out.println(" Result: FAIL");
            return;
        }

        String responseAsString = response.readEntity(String.class);
        Gson gson = new GsonBuilder().create();

        try {
            sports = gson.fromJson(responseAsString, SportModel[].class);
        } catch (Exception e) {
            System.out.println("    Didn't understand response of " + responseAsString);
            System.out.println(" Result: FAIL");
            return;
        }

        if(sports.length != 1) {
            System.out.println("    Thought there would be 1 sport team.  Saw: " + sports.length);
            System.out.println(" Result: FAIL");
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
