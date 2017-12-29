package com.endicott.edu.simulators;

import com.endicott.edu.datalayer.DormitoryDao;
import com.endicott.edu.datalayer.FloodDao;
import com.endicott.edu.models.*;

import java.util.List;
import java.util.Random;

/**
 * Responsible for simulating floods at the college.
 * NOTE: THERE CAN ONLY BE ONE FLOOD AT A TIME.
 */
public class FloodManager {
    private static final float PROBABILTY_OF_FLOOD_PER_HOUR = 0.05f;
    FloodDao floodDao = new FloodDao();
    DormitoryDao dormDao = new DormitoryDao();

    /**
     * Simulate changes in floods due to passage of time at college.
     *
     * @param runId
     * @param hoursAlive number of hours college has been active.
     */
    public void handleTimeChange(String runId, int hoursAlive) {
        List<FloodModel> floods = floodDao.getFloods(runId);
        List<DormitoryModel> dorms = dormDao.getDorms(runId);

        // If there are no floods, possibly start one.
        if (floods.size() <= 0) {
            possiblyStartFlood(runId, hoursAlive);
            return;
        }

        // Advance state of flood.
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

    /**
     * Possibly start a flood at one of the dorms at the college.
     * The dorm must be fully built.
     *
     * @param runId
     * @param hoursAlive
     */
    private void possiblyStartFlood(String runId, int hoursAlive) {
        List<DormitoryModel> dorms = dormDao.getDorms(runId);

        for (DormitoryModel dorm : dorms) {
            if (dorm.getHoursToComplete() <= 0) {
                if (didFloodStartAtThisDorm(runId, hoursAlive, dorm)) {
                    return;
                }
            }
        }
    }

    /**
     * Charge the college for flood cleanup costs.
     *
     * @param runId
     * @param hoursAlive
     * @param dorm
     */
    private void billCostOfFlood(String runId, int hoursAlive, DormitoryModel dorm) {
        Random rand = new Random();
        Accountant.payBill(runId,"Flood cleanup cost for dorm " + dorm.getName(), rand.nextInt(500) + 500);
    }

    /**
     * Possibly start a flood at the given dorm.
     *
     * @param runId
     * @param hoursAlive
     * @param dorm
     * @return true if fllod started.
     */
    private boolean didFloodStartAtThisDorm(String runId, int hoursAlive, DormitoryModel dorm) {
        float oddsOfFlood = (hoursAlive - dorm.getHourLastUpdated()) * PROBABILTY_OF_FLOOD_PER_HOUR;
        if (Math.random() <= oddsOfFlood) {
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

    /**
     * Take care of any initial flood set up when colege is first created.
     * @param runId
     */
    public static void establishCollege(String runId){
    }
}
