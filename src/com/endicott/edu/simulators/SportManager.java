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

    public static void addNewTeam(String sportName, String runId){
        if (sportName.equals("Men's Basketball")){
            SportModel sportMBasketball = new SportModel(12, 0, 20, 100, 0, 0, 0, 20, 50000, 0, 0, "Men's Basketball", runId, false);
            SportsDao sportMBasketballDao = new SportsDao();
            sportMBasketballDao.saveNewSport(runId, sportMBasketball);
        }
        else if(sportName.equals("Women's Basketball")){
            SportModel sportWBasketball = new SportModel(12, 0, 20, 100, 0,0,0,20,50000,0,0,"Women's Basketball", runId, false);
            SportsDao sportWBasketballDao = new SportsDao();
            sportWBasketballDao.saveNewSport(runId, sportWBasketball);
        }
        else if(sportName.equals("Baseball")){
            SportModel sportBaseball = new SportModel(16, 0, 25, 100, 0,0,0,20,75000,0,0,"Baseball", runId, false);
            SportsDao sportBaseballDao = new SportsDao();
            sportBaseballDao.saveNewSport(runId, sportBaseball);
        }
        else if(sportName.equals("Softball")){
            SportModel sportSoftball = new SportModel(16, 0, 25, 100, 0,0,0,20,75000,0,0,"Women's Basketball", runId, false);
            SportsDao sportSoftballDao = new SportsDao();
            sportSoftballDao.saveNewSport(runId, sportSoftball);
        }
        else if(sportName.equals("Women's Soccer")){
            SportModel sportWSoccer = new SportModel(15,0, 30, 10, 0, 0, 0 , 0 , 0, 14, 100, "Women's Soccer", runId, false );
            SportsDao sportWSoccerDao = new SportsDao();
            sportWSoccerDao.saveNewSport(runId, sportWSoccer);
        }
        else if(sportName.equals("Men's Soccer")){
            SportModel sport = new SportModel(15,0, 30, 10, 0, 0, 0 , 0 , 0, 14, 100, "Men's Soccer", runId, false );
            SportsDao sportDao = new SportsDao();
            sportDao.saveNewSport(runId, sport);
        }
    }

    static public void sellSport(String runId) {
        SportsDao sportsDao = new SportsDao();
        NewsFeedDao noteDao = new NewsFeedDao();

        sportsDao.deleteSports(runId);
        noteDao.deleteNotes(runId);
    }
}
