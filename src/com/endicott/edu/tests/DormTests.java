package com.endicott.edu.tests;

import com.endicott.edu.models.DormitoriesModel;
import org.glassfish.jersey.client.ClientConfig;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

// Created by abrocken on 7/17/2017.

class DormTests {
    private Client client;
    private static final String PASS = "pass";
    private static final String FAIL = "fail";
    private static final String COLLEGE_TEST_ID = "testdorm";
    private String serviceUrl;

    private void init(){
        this.client = ClientBuilder.newClient(new ClientConfig());
        //this.client = ClientBuilder.newClient(new ClientConfig().register(LoggingFilter.class));
    }

    private void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    static void runTests(String serviceUrl){
        DormTests tester = new DormTests();

        tester.init();
        tester.setServiceUrl(serviceUrl);
        tester.testCreateCollegeWithDorm(COLLEGE_TEST_ID);
        tester.testDeleteCollege(COLLEGE_TEST_ID);
    }

    private void testCreateCollegeWithDorm(String runId){
        String result = PASS;

        System.out.print("Test case name: testCreateCollegeWithDorm...");

        WebTarget webTarget = client.target(serviceUrl + "college/" + runId);
        Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);

        // Create college
        Response response = invocationBuilder.post(null);

        //Response response = invocationBuilder.post(Entity.entity(college, MediaType.APPLICATION_JSON));
        if(response.getStatus() != 200) {
            System.out.println("    Got bad response creating college: " + response.getStatus());
            return;
        }

        // Get dorm from newly created college.
        client.target(serviceUrl + "dorms/" + runId);
        response = invocationBuilder.get();

        //List<DormitoryModel> dorms = response.readEntity(List.class);
        DormitoriesModel dorms = response.readEntity(DormitoriesModel.class);

        if(response.getStatus() != 200 && dorms.getDormList().size() != 1) {
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
