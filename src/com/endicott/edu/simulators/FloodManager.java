package com.endicott.edu.simulators;

import com.endicott.edu.datalayer.DormitoryDao;
import com.endicott.edu.datalayer.FloodDao;
import com.endicott.edu.datalayer.NewsFeedDao;
import com.endicott.edu.models.FloodModel;
import com.endicott.edu.models.DormitoryModel;
import com.endicott.edu.models.NewsFeedItemModel;
import com.endicott.edu.models.NewsType;

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

        int newCharge = (hoursAlive - dorm.getHourLastUpdated()) * dorm.getMaintenanceCostPerHour();
        Accountant.payBill(runId,"Flooding cost for dorm " + dorm.getName() + " Costs $ " + 100, 100);

    }


    // Checks to see if a flood happened
    private void checkForFlood(String runId, int hoursAlive, DormitoryModel dorm) {
        float oddsThatBurnedDown = (hoursAlive - dorm.getHourLastUpdated()) * PROBABILTY_OF_FLOOD;
        if (didItHappen(oddsThatBurnedDown)) {

            NewsManager.createNews(runId, hoursAlive, "Dorm " + dorm.getName() + " has flooded.\n", NewsType.GENERAL_NOTE);

            FloodModel flood = new FloodModel(72, dorm.getHourLastUpdated(), dorm.getName(), runId);
            NewsManager.createNews(runId, hoursAlive, "Dorm " + flood.getDormName() + " has flooded.\n", NewsType.GENERAL_NOTE);


        }
    }

    private boolean didItHappen(float oddsBetween0And1
    ) {
        return (Math.random() < oddsBetween0And1);
    }
}
