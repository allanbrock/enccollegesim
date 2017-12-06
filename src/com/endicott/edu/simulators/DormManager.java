package com.endicott.edu.simulators;

import com.endicott.edu.datalayer.CollegeDao;
import com.endicott.edu.datalayer.DormitoryDao;
import com.endicott.edu.models.CollegeModel;
import com.endicott.edu.models.DormitoryModel;
import com.endicott.edu.models.NewsLevel;
import com.endicott.edu.models.NewsType;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.endicott.edu.models.NewsType.RES_LIFE_NEWS;

public class DormManager {
    DormitoryDao dao = new DormitoryDao();
    static private Logger logger = Logger.getLogger("DormManager");
    CollegeDao collegeDao = new CollegeDao();
    CollegeModel college = new CollegeModel();
    List<DormitoryModel> dorms = dao.getDorms(college.getRunId());

    public void handleTimeChange(String runId, int hoursAlive) {
        List<DormitoryModel> dorms = dao.getDorms(runId);

        for (DormitoryModel dorm : dorms) {
            billRunningCostOfDorm(runId, hoursAlive, dorm);
            dorm.setHourLastUpdated(hoursAlive);
            if(dorm.getHoursToComplete() > 0){
                dorm.setHoursToComplete(24);
            }
        }

        dao.saveAllDorms(runId, dorms);
    }

    private static void setDormAttributesByDormType(DormitoryModel temp) {
        //small size
        if (temp.getDormType() == 1) {
            temp.setCapacity(200);
            temp.setNumRooms(100);
            temp.setTotalBuildCost(100);
        }
        //normal size
        else if (temp.getDormType() == 2) {
            temp.setCapacity(350);
            temp.setNumRooms(175);
            temp.setTotalBuildCost(175);
        }
        //large size
        else if (temp.getDormType() == 3) {
            temp.setCapacity(500);
            temp.setNumRooms(250);
            temp.setTotalBuildCost(250);

        } else {
            //not a type only 3 types of dorms
            logger.severe("Could not add dorm: '" + temp.getName() + "'");
        }
    }

    public static DormitoryModel createDorm(String runId, String dormName, String dormType, int hoursAlive) {
        DormitoryModel temp = new DormitoryModel();
        temp.setName(dormName);
        if (dormType == "Small") {
            temp.setDormType(1);
        } else if (dormType == "Medium") {
            temp.setDormType(2);
        } else if (dormType == "Large") {
            temp.setDormType(3);
        }

        setDormAttributesByDormType(temp);
        temp.setHourLastUpdated(0);
        temp.setReputation(5);
        temp.setCurDisaster("none");
        temp.setMaintenanceCostPerHour(temp.getNumRooms());
        Accountant.payBill(runId, "Charge of new dorm", temp.getTotalBuildCost());
        NewsManager.createNews(runId, hoursAlive, dormName +" dorm has been created! ", NewsType.RES_LIFE_NEWS, NewsLevel.GOOD_NEWS);
        DormitoryDao dormDao = new DormitoryDao();
        temp.setNote("A new dorm has been created.");

        dormDao.saveNewDorm(runId, temp);
        return temp;


    }

    private void billRunningCostOfDorm(String runId, int hoursAlive, DormitoryModel dorm) {
        int newCharge = (hoursAlive - dorm.getHourLastUpdated()) * dorm.getMaintenanceCostPerHour();
        Accountant.payBill(runId, "Maintenance of dorm " + dorm.getName(), (int) (newCharge));
    }

    /*Takes in the length of the flood, the dorm dormName affected by the flood, and the runId of the college. */
    public void floodAlert(int lengthOfFlood, String dormName, String collegeId) {
        List<DormitoryModel> dorms = dao.getDorms(collegeId);
        for (DormitoryModel d : dorms) {
            if (d.getName() == dormName) {
                d.setCurDisaster("flood");
                d.setLengthOfDisaster(lengthOfFlood);
            }
        }
        dao.saveAllDorms(collegeId, dorms);
        //when lengthOfFlood number of hours is completed change curDisaster back to "none".
    }

    /*Handles one student being admitted to the college at a time:
    Takes in the runId of the college (String)
    returns the name of the dorm (String) that student was placed in.*/
    public String assignDorm(String collegeId) {
        List<DormitoryModel> dorms = dao.getDorms(collegeId);
        String dormName = "";
        for (DormitoryModel d : dorms) {
            int s = d.getNumStudents();
            int c = d.getCapacity();
            dormName = d.getName();
            if (s < c) {
                d.setNumStudents(s + 1);
                dao.saveAllDorms(collegeId, dorms);
                return dormName;
            }
        }

        // We don't expect to get here.
        // This means we didn't find room for the student!
        return "Commuter";
    }

    /*Handles one student leaving the college at a time:
    Takes in the runId of the college (String), and the name of the dorm the student is in (String)
    returns nothing.*/
    public void removeStudent(String collegeId, String dormName) {
        List<DormitoryModel> dorms = dao.getDorms(collegeId);
        Boolean removed = false;
        for (DormitoryModel d : dorms) {
            String name = d.getName();
            int s = d.getNumStudents();
            if (name.equals(dormName)) {
                d.setNumStudents(s - 1);
            }
        }
        dao.saveAllDorms(collegeId, dorms);
    }

    public int getOpenBeds(String collegeId) {
        List<DormitoryModel> dorms = dao.getDorms(collegeId);
        int openBeds = 0;
        for (DormitoryModel d : dorms) {
            if(d.getHoursToComplete() == 0) {
                int numStudents = d.getNumStudents();
                int capacity = d.getCapacity();
                openBeds += capacity - numStudents;
            }
        }
        return openBeds;
    }



    //takes the runId of the dorm and the dorm name to be removed
    public static void sellDorm(String runId, String dormName) {
        DormitoryDao dormitoryDao = new DormitoryDao();
        List<DormitoryModel> dorms = dormitoryDao.getDorms(runId);
        String name = "";
        int totalBuildCost = 0;
        int refund = 0;
        for(DormitoryModel d : dorms){
            name = d.getName();
            totalBuildCost = d.getTotalBuildCost();
            //takes 20% of the build cost to refund back to the college.
            refund = (int)(totalBuildCost/20);
            if(name.equals(dormName)){
                dorms.remove(d);
                Accountant.studentIncome(runId, dormName + "has been sold.", refund);
            }
        }
        dormitoryDao.saveAllDorms(runId, dorms);

    }

    public ArrayList checkAvailableDorms(String runId){
        CollegeDao collegeDao = new CollegeDao();
        CollegeModel college = collegeDao.getCollege(runId);
        int availableCash = college.getAvailableCash();
        ArrayList<String> availableDormTypes = new ArrayList<>();
        if(availableCash >= 100 ){
            //can build dorm type 1 (small)
            availableDormTypes.add("Small");
            return availableDormTypes;
        }
        else if(availableCash >= 175){
            //can build small and medium sized dorms
            availableDormTypes.add("Small");
            availableDormTypes.add("Medium");
            return availableDormTypes;
        }
        else if(availableCash >= 250){
            //can build small, medium, and large sized dorms
            availableDormTypes.add("Small");
            availableDormTypes.add("Medium");
            availableDormTypes.add("Large");
            return availableDormTypes;
        }
        else{
            //you do not have enough money to build a dorm
            return availableDormTypes;
        }
    }

    public void chanceOfEventDuringConstruction(String runId) {
        String dormName = "";
        double chance = Math.random();
        if (chance < 0.25) {
            //25% chance of gaining $1000 dollars
            Accountant.studentIncome(runId, "Donation received for building " + dormName, 1000);
        } else if (chance < 0.35) {
            //35% chance of losing $500 dollars
            Accountant.studentIncome(runId, "Ran into unexpected construction costs building " + dormName, 500);
        } else {
            //40% chance of nothing happening
        }
    }

    static public void establishCollege(String runId, CollegeModel college) {
        logger.info("Creating dorm");
        DormitoryModel dorm = new DormitoryModel(100, 10, "Hampshire Hall",
                0, "none", 5, "none", 60);
        dorm.setHoursToComplete(300);
        dorm.setMaintenanceCostPerHour(60);
        dorm.setTotalBuildCost(60);
        DormitoryDao dormDao = new DormitoryDao();
        dormDao.saveNewDorm(runId, dorm);
        NewsManager.createNews(runId, college.getCurrentDay(), "Dorm " + dorm.getName() + " has opened.", NewsType.RES_LIFE_NEWS, NewsLevel.GOOD_NEWS);
    }
}







