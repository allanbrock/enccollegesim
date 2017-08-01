package com.endicott.edu.models;

//import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

/**
 * Created by abrocken on 7/10/2017.
 */
//@XmlRootElement
public class CollegeModel implements Serializable {
    private int hoursAlive = 0;
    private int availableCash = 0;
    private String runId = "unknown";

    public int getAvailableCash() {
        return availableCash;
    }

    public void setAvailableCash(int availableCash) {
        this.availableCash = availableCash;
    }

    public String getRunId() {
        return runId;

    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public int getHoursAlive() {
        return hoursAlive;
    }

    public void setHoursAlive(int hoursAlive) {
        this.hoursAlive = hoursAlive;
    }

    public void advanceClock(int hours) {
        setHoursAlive(getHoursAlive() + hours);
    }

    public int getCurrentDay() {
        return hoursAlive / 24 + 1;
    }
}
