package com.endicott.edu.models;

import java.io.Serializable;


/**
 * Implemented 9-28-17 by Mazlin Higbee
 * mhigb411@mail.endicott.edu
 */
public class FacultyModel implements Serializable {
    private String facultyName;
    private String title;
    private String concentration;
    private long salary = 115000;
    private String officeLocation;
    private String runId;

    public FacultyModel(String facultyName, String title, String concentration, long salary, String officeLocation, String runId) {
        this.facultyName = facultyName;
        this.title = title;
        this.concentration = concentration;
        this.salary = salary;
        this.officeLocation = officeLocation;
        this.runId = runId;
    }

    public FacultyModel(String facultyName, String title, String concentration, String officeLocation, String runId) {
        this.facultyName = facultyName;
        this.title = title;
        this.concentration = concentration;
        this.officeLocation = officeLocation;
        this.runId = runId;
    }


}
