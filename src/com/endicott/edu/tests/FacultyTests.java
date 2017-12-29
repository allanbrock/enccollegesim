package com.endicott.edu.tests;

import com.endicott.edu.models.FacultyModel;
import com.endicott.edu.datalayer.FacultyDao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.prism.impl.BaseMesh;
import org.glassfish.jersey.client.ClientConfig;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mazlin Higbee
 * 10-10-17
 * mhigb411@mail.endicott.edu
 * Testing class for Faculty Operations
 */
public class FacultyTests {
    private Client client;
    private static final String PASS = "pass";
    private static final String FAIL = "fail";
    private static final String COLLEGE_TEST_ID = "faculty_test_01";
    private String serviceUrl;
    private List<FacultyModel> facultyList;


    private void init(){
        this.client = ClientBuilder.newClient(new ClientConfig());

    }

    private void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    static void runTests(String serviceUrl){
        FacultyTests tester = new FacultyTests();

        tester.init();
        tester.setServiceUrl(serviceUrl);
        tester.testCreateCollege(COLLEGE_TEST_ID);
    }


    private void testCreateCollege(String runId){
        String result = PASS;
        FacultyModel[] member;
        System.out.print("Test case name: testCreateCollegeFaculty...");
        WebTarget webTarget = client.target(serviceUrl + "college/" + runId);
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        // Create college
        Response response = invocationBuilder.post(null);

        Client client = ClientBuilder.newClient(new ClientConfig());
        //check that the college we just created has faculty.
        webTarget = client.target(serviceUrl + "faculty/" + runId);
        invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);

        response = invocationBuilder.get();
        if(response.getStatus() != 200) {
            System.out.println("    Got bad college response or contents:" + response.getStatus());
            System.out.println(" Result: FAIL");
            result = FAIL;
            return;
        }

        String responseAsString = response.readEntity(String.class);
        Gson gson = new GsonBuilder().create();

        try {
            member = gson.fromJson(responseAsString, FacultyModel[].class);
        } catch (Exception e) {
            System.out.println("    Didn't understand response of " + responseAsString);
            System.out.println(" Result: FAIL");
            result = FAIL;
            return;
        }

        //when we create the college we expect there to be faculty.
        if(member == null || member.length <= 0){
            System.out.println("Didn't find faculty in new college.");
            result = FAIL;
            return;
        }
        FacultyDao fao = new FacultyDao();
        facultyList = fao.getFaculty(runId);
        if(facultyList.size() != 10){
            System.out.println("FacultyList size expected 10 got other...");
            System.out.println(facultyList.size() + " :SIZE");
            result = FAIL;
            return;
        }

        System.out.println(" Result: " + result );
    }



}
