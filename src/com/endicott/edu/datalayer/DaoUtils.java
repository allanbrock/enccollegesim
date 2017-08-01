package com.endicott.edu.datalayer;

/**
 * Created by abrocken on 7/20/2017.
 */
public class DaoUtils {
    private String REST_SERVICE_URL = "http://localhost:8080/College_sim3_war_exploded/finances";

    static public String getFilePathPrefix(String runId) {
        return System.getenv("SystemDrive")+"\\Users\\"+System.getProperty("user.name")+"\\collegesim_" + runId + "_";
    }
}
