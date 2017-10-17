package com.endicott.edu.simulators;

import com.endicott.edu.datalayer.NewsFeedDao;
import com.endicott.edu.datalayer.SportsDao;
import com.endicott.edu.models.NewsFeedItemModel;
import com.endicott.edu.models.NewsType;
import com.endicott.edu.models.SportModel;
import com.endicott.edu.models.SportsModel;

import java.util.List;
import java.util.logging.Logger;

public class SportManager {
    SportsDao dao = new SportsDao();
    static private Logger logger = Logger.getLogger("SportManager");

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
        if(newCharge > 0)
        {
            Accountant.payBill(runId, newCharge);
            NewsManager.createNews(runId, hoursAlive, "Charge for " + sport.getName() + " $" + newCharge);
        }
    }

    public static SportModel addNewTeam(String sportName, String runId){
        logger.info("Attempt to add sport: '" + sportName + "' to '" + runId + "'");
        SportModel result = null;

        if (sportName.equals("Men's Basketball")){
            result = new SportModel(12, 0, 20, 100, 0, 0, 0, 20, 50000, 0, 0, "Men's Basketball", runId, false);
            SportsDao sportMBasketballDao = new SportsDao();
            sportMBasketballDao.saveNewSport(runId, result);
        }
        else if(sportName.equals("Women's Basketball")){
            result  = new SportModel(12, 0, 20, 100, 0,0,0,20,50000,0,0,"Women's Basketball", runId, false);
            SportsDao sportWBasketballDao = new SportsDao();
            sportWBasketballDao.saveNewSport(runId, result );
        }
        else if(sportName.equals("Baseball")){
            result  = new SportModel(16, 0, 25, 100, 0,0,0,20,75000,0,0,"Baseball", runId, false);
            SportsDao sportBaseballDao = new SportsDao();
            sportBaseballDao.saveNewSport(runId, result );
        }
        else if(sportName.equals("Softball")){
            result  = new SportModel(16, 0, 25, 100, 0,0,0,20,75000,0,0,"Women's Basketball", runId, false);
            SportsDao sportSoftballDao = new SportsDao();
            sportSoftballDao.saveNewSport(runId, result );
        }
        else if(sportName.equals("Women's Soccer")){
            result  = new SportModel(15,0, 30, 10, 0, 0, 0 , 0 , 0, 14, 100, "Women's Soccer", runId, false );
            SportsDao sportWSoccerDao = new SportsDao();
            sportWSoccerDao.saveNewSport(runId, result );
        }
        else if(sportName.equals("Men's Soccer")){
            result  = new SportModel(15,0, 30, 10, 0, 0, 0 , 0 , 0, 14, 100, "Men's Soccer", runId, false );
            SportsDao sportDao = new SportsDao();
            sportDao.saveNewSport(runId, result );
        } else {
            logger.severe("Could not add sport: '" + sportName + "'");
        }

        return result;
    }

    static public void sellSport(String runId) {
        SportsDao sportsDao = new SportsDao();
        NewsFeedDao noteDao = new NewsFeedDao();

        sportsDao.deleteSports(runId);
        noteDao.deleteNotes(runId);
    }
}
