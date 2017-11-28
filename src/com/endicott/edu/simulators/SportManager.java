package com.endicott.edu.simulators;

import com.endicott.edu.datalayer.CollegeDao;
import com.endicott.edu.datalayer.NewsFeedDao;
import com.endicott.edu.datalayer.SportsDao;
import com.endicott.edu.models.*;
import com.endicott.edu.datalayer.StudentDao;


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
            Accountant.payBill(runId,"Charge for " + sport.getName(), newCharge);
        }
    }

    public static SportModel addNewTeam(String sportName, String runId){
        SportsDao newSportDao = new SportsDao();
        logger.info("Attempt to add sport: '" + sportName + "' to '" + runId + "'");
        SportModel sport = new SportModel();
        SportModel result = null;

        if (sportName.equals("Men's Basketball")){
            result = new SportModel(12, 0, 20, 100, 0, 0, 0, 20, 50000, 0, 0, "Men's Basketball", runId, false, 48, "Male");
            Accountant.payBill(runId, "Men's Basketball start up fee", result.getStartupCost());
        }
        else if(sportName.equals("Women's Basketball")){
            result  = new SportModel(12, 0, 20, 100, 0,0,0,20,50000,0,0,"Women's Basketball", runId, false,48, "Female");
            Accountant.payBill(runId, "Women's Basketball start up fee", result.getStartupCost());
        }
        else if(sportName.equals("Baseball")){
            result  = new SportModel(16, 0, 25, 100, 0,0,0,20,75000,0,0,"Baseball", runId, false,48, "Male");
            Accountant.payBill(runId, "Baseball start up fee", result.getStartupCost());
        }
        else if(sportName.equals("Softball")){
            result  = new SportModel(16, 0, 25, 100, 0,0,0,20,75000,0,0,"Softball", runId, false, 48,"Female");
            Accountant.payBill(runId, "Softball start up fee", result.getStartupCost());
        }
        else if(sportName.equals("Women's Soccer")){
            result  = new SportModel(15,0, 25, 100, 0, 0, 0 , 20 , 50000, 0, 0, "Women's Soccer", runId, false,48, "Female" );
            Accountant.payBill(runId, "Women's Soccer start up fee", result.getStartupCost());
        }
        else if(sportName.equals("Men's Soccer")){
            result  = new SportModel(15,0, 25, 100, 0, 0, 0 , 20 , 50000, 0, 0, "Men's Soccer", runId, false, 48,"Male" );
            Accountant.payBill(runId, "Men's Soccer start up fee", result.getStartupCost());
        } else {
            logger.severe("Could not add sport: '" + sportName + "'");
        }

        addPlayers(runId, result);
        newSportDao.saveNewSport(runId, result);
        return result;
    }

    public static SportModel addPlayers(String runId, SportModel sport){
        StudentDao dao = new StudentDao();
        List<StudentModel> students = dao.getStudents(runId);
        for(int i = 0; i < students.size(); i++) {
            if (students.get(i).isAthlete() && ((students.get(i).getTeam().equals("")) || students.get(i).getTeam().equals("unknown"))) {
                if (students.get(i).getGender().equals(sport.getGender())) {
                    if (sport.getCurrentPlayers() < sport.getMaxPlayers()) {
                        students.get(i).setTeam(sport.getSportName());
                        sport.setCurrentPlayers((sport.getCurrentPlayers() + 1));
                    }
                }
            }
        }

        dao.saveAllStudents(runId, students);
        return sport;
    }

    static public void sellSport(String runId) {
        SportsDao sportsDao = new SportsDao();
        NewsFeedDao noteDao = new NewsFeedDao();

        sportsDao.deleteSports(runId);
        noteDao.deleteNotes(runId);
    }

    public static ArrayList<SportModel> checkAvailableSports(String runId) {
        SportsDao dao = new SportsDao();
        CollegeDao cao = new CollegeDao();
        CollegeModel college = cao.getCollege(runId);
        int collegeFunds = college.getAvailableCash();

        // Creates a list called availbleSportNames of all sports names a college can make
        ArrayList<String> avalibleSportsNames = new ArrayList<>();
        for (int i = 0; i < dao.seeAllSportNames().size(); i++ ){
            avalibleSportsNames.add(dao.seeAllSportNames().get(i));
        }

        // Compares the currents sports names w the names in the availbleSportsNames array and takes out any sports that are already created
        for(int x = 0; x < dao.getSports(runId).size(); x++){
            for(int y = 0; y < avalibleSportsNames.size(); y++){
                if( avalibleSportsNames.get(y).equals(dao.getSports(runId).get(x).getName())){
                    avalibleSportsNames.remove(y);
                }
            }
        }

        // Takes the modified availbleSportsNames array and converts/creates objects of sport model with the left...
        // over names in availblesportsnames and stores them in abvaibleSports
        ArrayList<SportModel> availableSports = new ArrayList<>();
        for(int yz = 0; yz < avalibleSportsNames.size(); yz++){

            // TODO: we should check if the college has enough money to startup the sport.

            System.out.println(avalibleSportsNames.get(yz) + "This is a check");
            logger.info("list of the names after the check " + avalibleSportsNames.get(yz));
            SportModel tempSport = new SportModel();
            tempSport.setName(avalibleSportsNames.get(yz));
            availableSports.add(tempSport);
        }
        return availableSports;
    }

    public static void checkIfGameDay(SportModel sport, int hoursAlive,String runId ){
        if(sport.getHoursUntilNextGame() <= 0){
            NewsManager.createNews(runId, hoursAlive, sport.getName() + " Just payed a game.", NewsType.SPORTS_NEWS);
            sport.setHoursUntilNextGame(48);
        }else{
            sport.setHoursUntilNextGame(hoursAlive - sport.getHourLastUpdated());
        }
    }

    public static void deleteSelectedSport(String runId, SportModel sport){
        SportsDao dao = new SportsDao();
        List<SportModel> sports = dao.getSports(runId);
        for(int i =0; i < sports.size(); i++){
            if(sport.getName().equals(sports.get(i).getName())){
                sports.remove(i);
            }
        }
        dao.saveAllSports(runId,sports);
    }
}
