package com.endicott.edu.simulators;

import com.endicott.edu.datalayer.NewsFeedDao;
import com.endicott.edu.datalayer.SportsDao;
import com.endicott.edu.models.NewsFeedItemModel;
import com.endicott.edu.models.NewsType;
import com.endicott.edu.models.SportModel;
import com.endicott.edu.models.SportsModel;

import java.util.List;

public class SportManager {
    SportsDao dao = new SportsDao();

    public void handleTimeChange(String runId, int hoursAlive) {
        List<SportModel> sports = dao.getSports(runId);

        for (SportModel sport : sports) {
            billRunningCostofSport(runId, hoursAlive, sport);
            sport.setHourLastUpdated(hoursAlive);
        }

        dao.saveAllSports(runId, sports);
    }

    private void billRunningCostofSport(String runId, int hoursAlive, SportModel sport) {
        int newCharge = (hoursAlive - sport.getHourLastUpdated()) * sport.getCostPerHour();
        Accountant.payBill(runId, newCharge);
        NewsManager.createNews(runId, hoursAlive, "Charge for " + sport.getName() + " $" + newCharge);
    }

    static public void sellSport(String runId) {
        SportsDao sportsDao = new SportsDao();
        NewsFeedDao noteDao = new NewsFeedDao();

        sportsDao.deleteSports(runId);
        noteDao.deleteNotes(runId);
    }
}
