package com.endicott.edu.simulators;

import com.endicott.edu.datalayer.CollegeDao;
import com.endicott.edu.datalayer.DormitoryDao;
import com.endicott.edu.datalayer.StudentDao;
import com.endicott.edu.models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DormManager {
    static private DormitoryDao dao = new DormitoryDao();
    static private Logger logger = Logger.getLogger("DormManager");
    CollegeModel college = new CollegeModel();
    static private StudentDao studentDao = new StudentDao();

    public void handleTimeChange(String runId, int hoursAlive) {
        List<DormitoryModel> dorms = dao.getDorms(runId);

        for (DormitoryModel dorm : dorms) {
            billRunningCostOfDorm(runId, hoursAlive, dorm);
            dorm.setHourLastUpdated(hoursAlive);
            if(dorm.getHoursToComplete() > 0){
                dorm.setHoursToComplete(24, runId);
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
        if (dormType.equals("Small")) {
            temp.setDormType(1);
        } else if (dormType.equals("Medium")) {
            temp.setDormType(2);
        } else if (dormType.equals("Large")) {
            temp.setDormType(3);
        }

        setDormAttributesByDormType(temp);
        temp.setHourLastUpdated(0);
        temp.setReputation(5);
        temp.setCurDisaster("none");
        temp.setMaintenanceCostPerDay(temp.getNumRooms());
        Accountant.payBill(runId, "Charge of new dorm", temp.getTotalBuildCost());
        NewsManager.createNews(runId, hoursAlive, "Construction of " + dormName +" dorm has started! ", NewsType.RES_LIFE_NEWS, NewsLevel.GOOD_NEWS);
        DormitoryDao dormDao = new DormitoryDao();
        temp.setNote("A new dorm has been created.");

        dormDao.saveNewDorm(runId, temp);
        return temp;


    }

    private void billRunningCostOfDorm(String runId, int hoursAlive, DormitoryModel dorm) {
        int newCharge = (hoursAlive - dorm.getHourLastUpdated()) * dorm.getMaintenanceCostPerDay();
//        int newCharge = (hoursAlive - dorm.getHourLastUpdated()) * dorm.getMaintenanceCostPerHour();
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

    public static int getOpenBeds(String collegeId) {
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
        List<StudentModel> students = studentDao.getStudents(runId);
        String name = "";
        int totalBuildCost = 0;
        int refund = 0;
        for(DormitoryModel d : dorms){
            name = d.getName();
            totalBuildCost = d.getTotalBuildCost();

            //takes 20% of the build cost to refund back to the college.
            refund = (int)(totalBuildCost/20);
            if(name.equals(dormName)){
                if(students.size() < (getOpenBeds(runId) - d.getCapacity())){
                    dorms.remove(d);
                    dormitoryDao.saveAllDorms(runId, dorms);
                    Accountant.studentIncome(runId, dormName + "has been sold.", refund);
                    return;
                }
                else{
                    logger.info("Not enough open beds...");
                }

            }
        }

    }

    public List<DormitoryModel> checkAvailableDorms(String runId){
        CollegeDao collegeDao = new CollegeDao();
        CollegeModel college = collegeDao.getCollege(runId);
        int availableCash = college.getAvailableCash();
        List<DormitoryModel> availableDormTypes = null;
        DormitoryModel smallDorm = new DormitoryModel();
        smallDorm.setDormType(1);
        DormitoryModel mediumDorm = new DormitoryModel();
        mediumDorm.setDormType(2);
        DormitoryModel largeDorm = new DormitoryModel();
        largeDorm.setDormType(3);

        if(availableCash >= 250000){
            //can build dorm type all
            availableDormTypes.add(smallDorm);
            availableDormTypes.add(mediumDorm);
            availableDormTypes.add(largeDorm);
            return availableDormTypes;
        }
        else if(availableCash >= 175000){
            //can build small and medium sized dorms
            availableDormTypes.add(smallDorm);
            availableDormTypes.add(mediumDorm);
            return availableDormTypes;
        }
        else if(availableCash >=100000){
            //can build small
            availableDormTypes.add(smallDorm);

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
        DormitoryModel dorm = new DormitoryModel(200, 10, "Hampshire Hall",
                0, "none", 5, "none", 100);
        dorm.setHoursToComplete(300, runId);
        dorm.setMaintenanceCostPerDay(60);
        dorm.setTotalBuildCost(100);
        DormitoryDao dormDao = new DormitoryDao();
        dormDao.saveNewDorm(runId, dorm);
        NewsManager.createNews(runId, college.getCurrentDay(), "Dorm " + dorm.getName() + " has opened.", NewsType.RES_LIFE_NEWS, NewsLevel.GOOD_NEWS);
    }
    static public List<DormitoryModel> getDorms(String runId){
        String dormName = "";
        List<DormitoryModel> dorms = dao.getDorms(runId);
        for(DormitoryModel d : dorms){
            d.setNumStudents(0);
        }
        List<StudentModel> students = studentDao.getStudents(runId);
        for(StudentModel s : students){
            dormName = s.getDorm();
            for (DormitoryModel d : dorms) {
                if (dormName.equals(d.getName())) {
                    d.incrementNumStudents(1);
                } else {
                    logger.info("Dorm was not found.");
                }
            }
        }
        return dorms;
    }
}







