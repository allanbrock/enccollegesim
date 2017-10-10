package com.endicott.edu.simulators;

//import com.endicott.edu.datalayer.DormitoryDao;
import com.endicott.edu.datalayer.NewsFeedDao;
//import com.endicott.edu.models.DormitoryModel;
import com.endicott.edu.datalayer.SportsDao;
import com.endicott.edu.models.NewsFeedItemModel;
import com.endicott.edu.models.NewsType;
import com.endicott.edu.models.SportModel;
import com.endicott.edu.models.SportsModel;

import java.util.List;

/**
 * Created by abrocken on 7/29/2017.
 */
public class SportManager {
    private static final float PROBABILTY_OF_FLOOD = 0.005f;
    SportsDao dao = new SportsDao();

    public void handleTimeChange(String runId, int hoursAlive) {
        List<SportModel> sports = dao.getSports(runId);

        for (SportModel sport : sports) {

            billRunningCostofSport(runId, hoursAlive, sport);
            checkForEnvironmentalDisaster(runId, hoursAlive, sport);

            sport.setHourLastUpdated(hoursAlive);
        }

        dao.saveAllSports(runId, sports);
    }

    private void billRunningCostofSport(String runId, int hoursAlive, SportModel dorm) {
        int newCharge = (hoursAlive - dorm.getHourLastUpdated()) * dorm.getCostPerHour();
        Accountant.payBill(runId, newCharge);
    }

    private void checkForEnvironmentalDisaster(String runId, int hoursAlive, SportModel sport) {
        float oddsThatBurnedDown = (hoursAlive - sport.getHourLastUpdated()) * PROBABILTY_OF_FLOOD;
        if (didItHappen(oddsThatBurnedDown)) {
            NewsFeedItemModel note = new NewsFeedItemModel();
            note.setHour(hoursAlive);
            note.setMessage("Sport " + sport.getName() + " has flooded. Students successfully evacuated.\n");
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
