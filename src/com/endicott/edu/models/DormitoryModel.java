package com.endicott.edu.models;

import java.io.Serializable;



public class DormitoryModel implements Serializable {
    private int capacity = 0;
    private int costPerHour = 0;
    private int hourLastUpdated = 0;
    private String name = "unknown";
    private String runId = "unknown";
    private String note = "no note";

    public DormitoryModel() {
    }

    public DormitoryModel(int capacity, int costPerHour, int hourLastUpdated, String name, String runId) {
        this.capacity = capacity;
        this.costPerHour = costPerHour;
        this.hourLastUpdated = hourLastUpdated;
        this.name = name;
        this.runId = runId;
    }


    public DormitoryModel(int capacity, int hourLastUpdated, String name, int numStudents,
                          String curDisaster, int reputation, String runId, int numRooms){
        this.capacity=capacity;
        this.hourLastUpdated=hourLastUpdated;
        this.name=name;
        //this.numStudents=numStudents;
        //this.curDisaster=curDisaster;
        //this.reputation=reputation;
        this.runId=runId;
        //this.numRooms=numRooms;
//this.squareFeet = 250 * numRooms;
//this.maintenanceCostPerHour = (int)(squareFeet * 2)/(365*24);
//this.hoursToComplete = squareFeet * 2;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCostPerHour() {
        return costPerHour;
    }

    public void setCostPerHour(int costPerHour) {
        this.costPerHour = costPerHour;
    }

    public int getHourLastUpdated() {
        return hourLastUpdated;
    }

    public void setHourLastUpdated(int hourLastUpdated) {
        this.hourLastUpdated = hourLastUpdated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getMaintenanceCostPerHour() {
        return 0;
    }
}