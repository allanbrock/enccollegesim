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

        for (DormitoryModel dorm : dorms) {

            checkForFlood(runId, hoursAlive, dorm);
            billCostOfFlood(runId, hoursAlive, dorm);

            dorm.setHourLastUpdated(hoursAlive);
        }

        dao.saveAllFloods(runId, floods);
    }


    // Charges the college for the dorm flooding
    private void billCostOfFlood(String runId, int hoursAlive, DormitoryModel dorm) {
        float newCharge = (hoursAlive - dorm.getHourLastUpdated()) * dorm.getMaintenanceCostPerHour();
        Accountant.payBill(runId, (int) (newCharge));
        if(newCharge > 0){
            NewsManager.createNews(runId, hoursAlive, "Charge for " + dorm.getName() + " flooding is $" + newCharge, NewsType.FINANCIAL_NEWS);

        }
        }


    // Checks to see if a flood happened
    private void checkForFlood(String runId, int hoursAlive, DormitoryModel dorm) {
        float oddsThatBurnedDown = (hoursAlive - dorm.getHourLastUpdated()) * PROBABILTY_OF_FLOOD;
        if (didItHappen(oddsThatBurnedDown)) {
            NewsManager.createNews(runId, hoursAlive, "Dorm " + dorm.getName() + " has flooded.\n", NewsType.GENERAL_NOTE);

        }
    }

    private boolean didItHappen(float oddsBetween0And1
    ) {
        return (Math.random() < oddsBetween0And1);
    }
}
