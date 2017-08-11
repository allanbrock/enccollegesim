package com.endicott.edu.tests;

import com.endicott.edu.service.ServiceUtils;

/**
 * Created by abrocken on 7/17/2017.
 */

public class TestDriver {

    public static void main(String[] args){
        CollegeTests collegeTests = new CollegeTests();
        collegeTests.runTests(ServiceUtils.LOCAL_SERVICE_URL);
        collegeTests.runTests(ServiceUtils.REMOTE_SERVICE_URL);
    }

}
