package com.endicott.edu.simulators;

import com.endicott.edu.datalayer.CollegeDao;
import com.endicott.edu.datalayer.NewsFeedDao;
import com.endicott.edu.datalayer.SportsDao;
import com.endicott.edu.models.*;
import com.endicott.edu.datalayer.StudentDao;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class SportManager {
    SportsDao dao = new SportsDao();
    static private Logger logger = Logger.getLogger("SportManager");
    private int overallRep = 0;

    public void handleTimeChange(String runId, int hoursAlive) {
        List<SportModel> sports = dao.getSports(runId);

        for (SportModel sport : sports) {
            countPlayers(runId, sport);
            changeStatus(runId, sport);
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
            result = new SportModel(12, 0, 20, 100, 0, 0, 0, 20, 50000, 50, 0, "Men's Basketball", runId, false, 48, "Male");
            Accountant.payBill(runId, "Men's Basketball start up fee", result.getStartupCost());
        }
        else if(sportName.equals("Women's Basketball")){
            result  = new SportModel(12, 0, 20, 100, 0,0,0,20,50000,50,0,"Women's Basketball", runId, false,48, "Female");
            Accountant.payBill(runId, "Women's Basketball start up fee", result.getStartupCost());
        }
        else if(sportName.equals("Baseball")){
            result  = new SportModel(16, 0, 25, 100, 0,0,0,20,75000,50,0,"Baseball", runId, false,48, "Male");
            Accountant.payBill(runId, "Baseball start up fee", result.getStartupCost());
        }
        else if(sportName.equals("Softball")){
            result  = new SportModel(16, 0, 25, 100, 0,0,0,20,75000,50,0,"Softball", runId, false, 48,"Female");
            Accountant.payBill(runId, "Softball start up fee", result.getStartupCost());
        }
        else if(sportName.equals("Women's Soccer")){
            result  = new SportModel(15,0, 25, 100, 0, 0, 0 , 20 , 50000, 50, 0, "Women's Soccer", runId, false,48, "Female" );
            Accountant.payBill(runId, "Women's Soccer start up fee", result.getStartupCost());
        }
        else if(sportName.equals("Men's Soccer")){
            result  = new SportModel(15,0, 25, 100, 0, 0, 0 , 20 , 50000, 50, 0, "Men's Soccer", runId, false, 48,"Male" );
            Accountant.payBill(runId, "Men's Soccer start up fee", result.getStartupCost());
        } else {
            logger.severe("Could not add sport: '" + sportName + "'");
        }

        addPlayers(runId, result);
        newSportDao.saveNewSport(runId, result);
        countPlayers(runId, result);
        changeStatus(runId, result);
        return result;
    }
    public static void countPlayers(String runId, SportModel sport){
        StudentDao dao = new StudentDao();
        List<StudentModel> students = dao.getStudents(runId);
        for(int i = 0; i < students.size(); i++) {
            if(students.get(i).getTeam().equals(sport)){
                sport.setCurrentPlayers(sport.getCurrentPlayers() + 1);
            }
        }
    }

    public static void changeStatus(String runId, SportModel sport){
        if (sport.getCurrentPlayers() < sport.getMinPlayers()){
            sport.setActive(false);
        }
        else {
            sport.setActive(true);
        }
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
        ArrayList<String> availableSportsNames = new ArrayList<>();
        for (int i = 0; i < dao.seeAllSportNames().size(); i++ ){
            availableSportsNames.add(dao.seeAllSportNames().get(i));
        }

        // Compares the currents sports names w the names in the availbleSportsNames array and takes out any sports that are already created
        for(int x = 0; x < dao.getSports(runId).size(); x++){
            for(int y = 0; y < availableSportsNames.size(); y++){
                if( availableSportsNames.get(y).equals(dao.getSports(runId).get(x).getName())){
                    availableSportsNames.remove(y);
                }
            }
        }

        // Takes the modified availbleSportsNames array and converts/creates objects of sport model with the left...
        // over names in availblesportsnames and stores them in abvaibleSports
        ArrayList<SportModel> availableSports = new ArrayList<>();
        for(int yz = 0; yz < availableSportsNames.size(); yz++){

            // TODO: we should check if the college has enough money to startup the sport.

            System.out.println(availableSportsNames.get(yz) + "This is a check");
            logger.info("list of the names after the check " + availableSportsNames.get(yz));
            SportModel tempSport = new SportModel();
            tempSport.setName(availableSportsNames.get(yz));
            availableSports.add(tempSport);
        }
        return availableSports;
    }

    public static void checkIfGameDay(SportModel sport, int hoursAlive,String runId ){
        if(sport.getHoursUntilNextGame() <= 0){
            simulateGame(sport, hoursAlive, runId);
        }else{
            sport.setHoursUntilNextGame(hoursAlive - sport.getHourLastUpdated());
        }
    }
    public static void deleteSelectedSport(String runId, String sportName){
        SportsDao dao = new SportsDao();
        List<SportModel> sports = dao.getSports(runId);
        for(int i =0; i < sports.size(); i++){
            if(sportName.equals(sports.get(i).getName())){
                sports.remove(i);
            }
        }
        dao.saveAllSports(runId,sports);

    }
    public static void simulateGame(SportModel sport, int hoursAlive,String runId){
        StudentDao stuDao = new StudentDao();
        List<StudentModel> students = stuDao.getStudentsOnSport(runId,sport.getName());
        int numOfPlayers = sport.getCurrentPlayers();
        int totalAthleticAbility = 0;

        for(StudentModel student : students){
            totalAthleticAbility = totalAthleticAbility + student.getAthleticAbility();
        }

        int teamAverage = totalAthleticAbility/numOfPlayers;
        Random rand = new Random();
        int random_integer = rand.nextInt(5) + 5;

        if(random_integer > teamAverage){
            sport.setGamesLost(sport.getGamesLost() +1);
            NewsManager.createNews(runId, hoursAlive, sport.getName() + " just lost a game.", NewsType.SPORTS_NEWS, NewsLevel.BAD_NEWS);
        }else{
            sport.setGamesWon(sport.getGamesWon() +1);
            NewsManager.createNews(runId, hoursAlive, sport.getName() + " just WON a game!", NewsType.SPORTS_NEWS, NewsLevel.GOOD_NEWS);
        }
        SportManager rep = new SportManager();
        rep.sportRep(sport, runId);
        sport.setHoursUntilNextGame(48);

    }
    public int calcRep(String runId){
        List<SportModel> sports = dao.getSports(runId);
        int averageRep = 0;
        for(SportModel sport: sports){
            if (sport.getActive()) {
                averageRep = sport.getReputation() + averageRep;
            }
        }
        return (averageRep/sports.size());
    }

    public int sportRep(SportModel sport, String runId){
        if(sport.getReputation() >= 100){
            sport.setReputation(100);
        }
        else if (sport.getReputation() <= 0) {
            sport.setReputation(0);
        }
        else if (sport.getGamesWon() > sport.getGamesLost()){
            sport.setReputation(sport.getReputation() + 5);
        }
        else if (sport.getGamesWon() < sport.getGamesLost()) {
            sport.setReputation(sport.getReputation() - 5);
        }
        return overallRep = calcRep(runId);

    }
}
