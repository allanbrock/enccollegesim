package com.endicott.edu.tests;

import com.endicott.edu.models.SportsModel;
import org.glassfish.jersey.client.ClientConfig;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

// Created by abrocken on 7/17/2017.

class SportTests {
    private Client client;
    private static final String PASS = "pass";
    private static final String FAIL = "fail";
    private static final String COLLEGE_TEST_ID = "testsport";
    private String serviceUrl;

    private void init(){
        this.client = ClientBuilder.newClient(new ClientConfig());
        //this.client = ClientBuilder.newClient(new ClientConfig().register(LoggingFilter.class));
    }

    private void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    static void runTests(String serviceUrl){
        SportTests tester = new SportTests();

        tester.init();
        tester.setServiceUrl(serviceUrl);
        tester.testCreateCollegeWithSport(COLLEGE_TEST_ID);

    }

    private void testCreateCollegeWithSport(String runId){
        String result = PASS;

        System.out.print("Test case name: testCreateCollegeWithSport...");

        WebTarget webTarget = client.target(serviceUrl + "college/" + runId);
        Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);

        // Create college
        Response response = invocationBuilder.post(null);

        //Response response = invocationBuilder.post(Entity.entity(college, MediaType.APPLICATION_JSON));
        if(response.getStatus() != 200) {
            System.out.println("    Got bad response creating college: " + response.getStatus());
            return;
        }

        // Get sport from newly created college.
        client.target(serviceUrl + "sports/" + runId);
        response = invocationBuilder.get();

        //List<DormitoryModel> dorms = response.readEntity(List.class);
        SportsModel sports = response.readEntity(SportsModel.class);

        if(response.getStatus() != 200 && sports.getSportList().size() != 1) {
            System.out.println("    Got bad college response or contents:" + response.getStatus());
            result = FAIL;
        }

        System.out.println(" Result: " + result );
    }
//    private void testGetCollege(String runId){
//        String result = PASS;
//
//        System.out.print("Test case name: testGetCollege...");
//
//        WebTarget webTarget = client.target(serviceUrl + "college/" + runId);
//        Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
//
//        Response response = invocationBuilder.get();
//        SportsModel sport = response.readEntity(SportsModel.class);
//
//        if(response.getStatus() != 200 || sport.getHoursAlive() != 1 || college.getAvailableCash() != CollegeManager.STARTUP_FUNDING) {
//            System.out.println("    Got bad college response or contents.");
//            result = FAIL;
//        }
//
//        System.out.println(" Result: " + result );
//    }



}
