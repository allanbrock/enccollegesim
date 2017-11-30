package com.endicott.edu.simulators;

import com.endicott.edu.datalayer.DormitoryDao;
import com.endicott.edu.datalayer.FloodDao;
import com.endicott.edu.datalayer.NewsFeedDao;
import com.endicott.edu.models.*;

import java.util.List;

/**
 * Created by abrocken on 7/29/2017.
 */
public class FloodManager {
    private static final float PROBABILTY_OF_FLOOD = 0.003f;
    FloodDao dao = new FloodDao();
    DormitoryDao dormDao = new DormitoryDao();

    public void handleTimeChange(String runId, int hoursAlive) {
        List<FloodModel> floods = dao.getFloods(runId);
        List<DormitoryModel> dorms = dormDao.getDorms(runId);

        if(floods.size() <= 0) {
            for (DormitoryModel dorm : dorms) {
                checkForFlood(runId, hoursAlive, dorm);
            }
        }else{
            for (DormitoryModel dorm : dorms) {
                billCostOfFlood(runId, hoursAlive, dorm);
            }
            for (FloodModel flood : floods){
                int elapsedTime = hoursAlive - flood.getHourLastUpdated();
                int timeLeft = Math.max(0,flood.getHoursLeftInFlood() - elapsedTime);
                if(timeLeft <= 0){
                    dao.deleteFloods(runId);
                }else{
                    flood.setHoursLeftInFlood(timeLeft);
                    dao.saveAllFloods(runId, floods);
                }

            }
        }
        dao.saveAllFloods(runId, floods);
    }


    // Charges the college for the dorm flooding
    private void billCostOfFlood(String runId, int hoursAlive, DormitoryModel dorm) {

        // Comment out and using a fixed value for the time being (change later)
        // int newCharge = (hoursAlive - dorm.getHourLastUpdated()) * dorm.getMaintenanceCostPerHour();
        Accountant.payBill(runId,"Flood cost for dorm " + dorm.getName() + " Cost is $ " + 1000, 1000);

    }


    // Checks to see if a flood happened
    private void checkForFlood(String runId, int hoursAlive, DormitoryModel dorm) {
        float oddsThatBurnedDown = (hoursAlive - dorm.getHourLastUpdated()) * PROBABILTY_OF_FLOOD;
        if (didItHappen(oddsThatBurnedDown)) {
            DormManager dormMan = new DormManager();
            NewsManager.createNews(runId, hoursAlive, "Dorm " + dorm.getName() + " has flooded.\n", NewsType.COLLEGE_NEWS, NewsLevel.EMERGENCY_NEWS);

            FloodModel flood = new FloodModel(1000,72, 72, dorm.getHourLastUpdated(), dorm.getName(), runId);
            NewsManager.createNews(runId, hoursAlive, "Dorm " + flood.getDormName() + " has flooded.\n", NewsType.COLLEGE_NEWS,NewsLevel.EMERGENCY_NEWS);
            Accountant.payBill(runId, "Flood cost for dorm " + dorm.getName() + " is $" + flood.getCostOfFlood(), flood.getCostOfFlood());
            dormMan.floodAlert(hoursAlive , dorm.getName(), runId);

        }
    }

    private boolean didItHappen(float oddsBetween0And1
    ) {
        return (Math.random() < oddsBetween0And1);
    }

    public static void establishCollege(String runId){
        FloodModel flood = new FloodModel(0 ,0,  0, 0, "none", runId);
        FloodDao floodDao = new FloodDao();
        floodDao.saveNewFlood(runId, flood);
    }
}
