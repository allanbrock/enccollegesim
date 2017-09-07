package com.endicott.edu.simulators;

import com.endicott.edu.datalayer.DormitoryDao;
import com.endicott.edu.datalayer.NewsFeedDao;
import com.endicott.edu.models.DormitoryModel;
import com.endicott.edu.models.NewsFeedItemModel;
import com.endicott.edu.models.NewsType;

import java.util.List;

/**
 * Created by abrocken on 7/29/2017.
 */
public class DormManager {
    private static final float PROBABILTY_OF_FLOOD = 0.005f;
    DormitoryDao dao = new DormitoryDao();

    public void handleTimeChange(String runId, int hoursAlive) {
        List<DormitoryModel> dorms = dao.getDorms(runId);

        for (DormitoryModel dorm : dorms) {

            billRunningCostOfDorm(runId, hoursAlive, dorm);
            checkForEnvironmentalDisaster(runId, hoursAlive, dorm);

            dorm.setHourLastUpdated(hoursAlive);
        }

        dao.saveAllDorms(runId, dorms);
    }

    private void billRunningCostOfDorm(String runId, int hoursAlive, DormitoryModel dorm) {
        int newCharge = (hoursAlive - dorm.getHourLastUpdated()) * dorm.getCostPerHour();
        Accountant.payBill(runId, newCharge);
    }

    private void checkForEnvironmentalDisaster(String runId, int hoursAlive, DormitoryModel dorm) {
        float oddsThatBurnedDown = (hoursAlive - dorm.getHourLastUpdated()) * PROBABILTY_OF_FLOOD;
        if (didItHappen(oddsThatBurnedDown)) {
            NewsFeedItemModel note = new NewsFeedItemModel();
            note.setHour(hoursAlive);
            note.setMessage("Dorm " + dorm.getName() + " has flooded. Students successfully evacuated.\n");
            note.setNoteType(NewsType.GENERAL_NOTE);
            NewsFeedDao noteDao = new NewsFeedDao();
            noteDao.saveNote(runId, note);
        }
    }

    private boolean didItHappen(float oddsBetween0And1
    ) {
        return (Math.random() < oddsBetween0And1);
    }
}
