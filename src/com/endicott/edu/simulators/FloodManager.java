package com.endicott.edu.simulators;

import com.endicott.edu.datalayer.DormitoryDao;
import com.endicott.edu.datalayer.FloodDao;
import com.endicott.edu.models.*;

import java.util.List;
import java.util.Random;

/**
 * Created by abrocken on 7/29/2017.
 */
public class FloodManager {
    private static final float PROBABILTY_OF_FLOOD = 0.05f;
    FloodDao floodDao = new FloodDao();
    DormitoryDao dormDao = new DormitoryDao();

    public void handleTimeChange(String runId, int hoursAlive) {
        List<FloodModel> floods = floodDao.getFloods(runId);
        List<DormitoryModel> dorms = dormDao.getDorms(runId);

        if (floods.size() <= 0) {
            for (DormitoryModel dorm : dorms) {
                if (dorm.getHoursToComplete() <= 0) {
                    boolean didFloodStart = possiblyStartFloodInDorm(runId, hoursAlive, dorm);
                    if (didFloodStart) {
                        return;  // only one flood at a time.
                    }
                }
            }
            return;
        }


        for (FloodModel flood : floods) {
            String floodedDorm = flood.getDormName();
            for (DormitoryModel dorm : dorms) {
                if (dorm.getName().compareTo(floodedDorm) == 0)
                    billCostOfFlood(runId, hoursAlive, dorm);
            }

            int elapsedTime = hoursAlive - flood.getHourLastUpdated();
            int timeLeft = Math.max(0, flood.getHoursLeftInFlood() - elapsedTime);
            if (timeLeft <= 0) {
                floodDao.deleteFloods(runId);
                return;
            } else {
                flood.setHoursLeftInFlood(timeLeft);
            }

        }
        floodDao.saveAllFloods(runId, floods);

    }


    // Charges the college for the dorm flooding
    private void billCostOfFlood(String runId, int hoursAlive, DormitoryModel dorm) {
        Random rand = new Random();
        Accountant.payBill(runId,"Flood cleanup cost for dorm " + dorm.getName(), rand.nextInt(500) + 500);

    }


    // Checks to see if a flood happened
    private boolean possiblyStartFloodInDorm(String runId, int hoursAlive, DormitoryModel dorm) {
        float oddsOfFlood = (hoursAlive - dorm.getHourLastUpdated()) * PROBABILTY_OF_FLOOD;
        if (didItHappen(oddsOfFlood)) {
            DormManager dormMan = new DormManager();
            int randomCost = (int)(Math.random()*1500) + 1000 ;
            int randomLength = (int) (Math.random() * 72) + 24;

            FloodModel flood = new FloodModel(randomCost, randomLength, randomLength, dorm.getHourLastUpdated(), dorm.getName(), runId);
            FloodDao floodDao = new FloodDao();
            floodDao.saveNewFlood(runId, flood);

            NewsManager.createNews(runId, hoursAlive, "Flooding detected at " + flood.getDormName(), NewsType.COLLEGE_NEWS, NewsLevel.BAD_NEWS);
            Accountant.payBill(runId, "Flood cost for dorm " + dorm.getName(), flood.getCostOfFlood());

            dormMan.floodAlert(hoursAlive , dorm.getName(), runId);
            return true;
        }

        return false;
    }

    private boolean didItHappen(float oddsBetween0And1
    ) {
        return (Math.random() < oddsBetween0And1);
    }

    public static void establishCollege(String runId){
       // FloodModel flood = new FloodModel(0 ,0,  0, 0, "none", runId);
        //FloodDao floodDao = new FloodDao();
        //floodDao.saveNewFlood(runId, flood);
    }
}
