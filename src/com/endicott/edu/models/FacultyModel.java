package com.endicott.edu.models;

import java.io.Serializable;


/**
 * Implemented 9-28-17 by Mazlin Higbee
 * mhigb411@mail.endicott.edu
 */
public class FacultyModel implements Serializable {
    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getConcentration() {
        return concentration;
    }

    public void setConcentration(String concentration) {
        this.concentration = concentration;
    }

    public long getSalary() {
        return salary;
    }

    public void setSalary(long salary) {
        this.salary = salary;
    }

    public String getOfficeLocation() {
        return officeLocation;
    }

    public void setOfficeLocation(String officeLocation) {
        this.officeLocation = officeLocation;
    }

    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

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
