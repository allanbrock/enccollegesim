package com.endicott.edu.tests;

import com.endicott.edu.service.ServiceUtils;

// Created by abrocken on 7/17/2017.

public class TestDriver {

    public static void main(String[] args){
        runTests(ServiceUtils.LOCAL_SERVICE_URL);
        //runTests(ServiceUtils.REMOTE_SERVICE_URL);
    }

    private static void runTests(String serviceUrl) {
        System.out.println("Testing service: " + serviceUrl);

        //CollegeTests.runTests(serviceUrl);
        DormTests.runTests(serviceUrl);
        SportsTests.runTests(serviceUrl);
        //FacultyTests.runTests(serviceUrl);
        StudentTests.runTests(serviceUrl);
        FloodTest.runTests(serviceUrl);
        PlagueTest.runTests(serviceUrl);
    }

}
