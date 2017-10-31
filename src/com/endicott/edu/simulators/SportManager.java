package com.endicott.edu.simulators;

import com.endicott.edu.datalayer.NewsFeedDao;
import com.endicott.edu.datalayer.SportsDao;
import com.endicott.edu.models.NewsType;
import com.endicott.edu.models.SportModel;
import com.endicott.edu.models.SportsModel;


import java.util.ArrayList;
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
            checkIfGameDay(sport, hoursAlive, runId);
            System.out.println(hoursAlive + "this is the hours alive");
            System.out.println(sport.getHourLastUpdated() + "this is the hour last updated of " + sport.getName());
        }

        dao.saveAllSports(runId, sports);
    }

    private void billRunningCostofSport(String runId, int hoursAlive, SportModel sport) {
        int newCharge = ((hoursAlive - sport.getHourLastUpdated()) * sport.getCostPerHour()) / 24;
        if(newCharge > 0)
        {
            Accountant.payBill(runId, newCharge);
            NewsManager.createNews(runId, hoursAlive, "Charge for " + sport.getName() + " $" + newCharge, NewsType.FINANCIAL_NEWS);
        }
    }

    public static SportModel addNewTeam(String sportName, String runId){
        SportsDao newSportDao = new SportsDao();
        logger.info("Attempt to add sport: '" + sportName + "' to '" + runId + "'");
        SportModel result = null;

        if (sportName.equals("Men's Basketball")){
            result = new SportModel(12, 0, 20, 100, 0, 0, 0, 20, 50000, 0, 0, "Men's Basketball", runId, false, 48);
        }
        else if(sportName.equals("Women's Basketball")){
            result  = new SportModel(12, 0, 20, 100, 0,0,0,20,50000,0,0,"Women's Basketball", runId, false,48);
        }
        else if(sportName.equals("Baseball")){
            result  = new SportModel(16, 0, 25, 100, 0,0,0,20,75000,0,0,"Baseball", runId, false,48);
        }
        else if(sportName.equals("Softball")){
            result  = new SportModel(16, 0, 25, 100, 0,0,0,20,75000,0,0,"Women's Basketball", runId, false, 48);
        }
        else if(sportName.equals("Women's Soccer")){
            result  = new SportModel(15,0, 30, 10, 0, 0, 0 , 0 , 0, 14, 100, "Women's Soccer", runId, false,48 );
        }
        else if(sportName.equals("Men's Soccer")){
            result  = new SportModel(15,0, 30, 10, 0, 0, 0 , 0 , 0, 14, 100, "Men's Soccer", runId, false, 48 );
        } else {
            logger.severe("Could not add sport: '" + sportName + "'");
        }
        newSportDao.saveNewSport(runId, result);
        return result;
    }

    static public void sellSport(String runId) {
        SportsDao sportsDao = new SportsDao();
        NewsFeedDao noteDao = new NewsFeedDao();

        sportsDao.deleteSports(runId);
        noteDao.deleteNotes(runId);
    }

    public static ArrayList<String> checkAvailableSports(String runId) {
        ArrayList<String> avalibleSports = new ArrayList<>();
        SportsDao dao = new SportsDao();
            for (int i = 0; i < dao.getSports(runId).size(); i++) {
                for (int x = 0; x < dao.seeAllSportNames().size(); x++) {
                    if (dao.getSports(runId).get(i).getName().equals(dao.seeAllSportNames().get(i))) {
                        avalibleSports.add(dao.getSports(runId).get(i).getName());
                        System.out.println(dao.getSports(runId).get(i).getName() + "This is sports name");
                    }
                }
            }
        return avalibleSports;
    }

    public static void checkIfGameDay(SportModel sport, int hoursAlive,String runId ){
        if(sport.getHoursUntilNextGame() <= 0){
            NewsManager.createNews(runId, hoursAlive, sport.getName() + " Just payed a game.", NewsType.FINANCIAL_NEWS);
            sport.setHoursUntilNextGame(48);
        }else{
            sport.setHoursUntilNextGame(sport.getHoursUntilNextGame() -24);

        }
    }
}
