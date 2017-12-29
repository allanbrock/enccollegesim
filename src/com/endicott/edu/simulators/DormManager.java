package com.endicott.edu.simulators;

import com.endicott.edu.datalayer.DormitoryDao;
import com.endicott.edu.datalayer.StudentDao;
import com.endicott.edu.models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * DormManager is responsible for simulating all dorm activity and for
 * providing functions for manipulation and retrieving information
 * about dorms.
 */
public class DormManager {
    static private DormitoryDao dao = new DormitoryDao();
    static private Logger logger = Logger.getLogger("DormManager");
    static private StudentDao studentDao = new StudentDao();

    /**
     * Simulate all changes in dorms caused by advancing the hours the college
     * has been alive to the given value.
     *
     * @param runId college name
     * @param hoursAlive number of hours college has been alive
     */
    public void handleTimeChange(String runId, int hoursAlive) {
        List<DormitoryModel> dorms = dao.getDorms(runId);

        for (DormitoryModel dorm : dorms) {
            billRunningCostOfDorm(runId, hoursAlive, dorm);
            workOnDorm(dorm, 24, runId);
            dorm.setHourLastUpdated(hoursAlive);
        }

        dao.saveAllDorms(runId, dorms);
    }

    /**
     * Perform any construction work needed on dorm.
     *
     * @param dorm
     * @param hoursWorkingOnDorm
     * @param runId
     */
    public void workOnDorm(DormitoryModel dorm, int hoursWorkingOnDorm, String runId){
        if (dorm.getHourLastUpdated() > 0) {
            int left = dorm.getHoursToComplete() - hoursWorkingOnDorm;
            left = Math.max(0, left);
            dorm.setHoursToComplete(left);

            surpriseEventDuringConstruction(runId, hoursWorkingOnDorm);
        }
    }

    /**
     * Given a dorm type, set attributes of the dorm.
     *
     * @param dorm
     */
    private static void setDormAttributesByDormType(DormitoryModel dorm) {
        DormType dormType = DormType.valueOf(dorm.getDormType());

        switch(dormType) {
            case SMALL:
                dorm.setCapacity(200);
                dorm.setNumRooms(100);
                dorm.setTotalBuildCost(100);
                break;
            case MEDIUM:
                dorm.setCapacity(350);
                dorm.setNumRooms(175);
                dorm.setTotalBuildCost(175);
                break;
            case LARGE:
                dorm.setCapacity(500);
                dorm.setNumRooms(250);
                dorm.setTotalBuildCost(250);
                break;
            default:
                logger.severe("Could not add dorm: '" + dorm.getName() + "'");
        }
    }

    /**
     * Create a dorm.
     *
     * @param runId college name
     * @param dormName name of new dorm
     * @param dormType type of dorm (see DormType)
     * @param hoursAlive number of hours college has existed
     * @return
     */
    public static DormitoryModel createDorm(String runId, String dormName, int dormType, int hoursAlive) {

        // Create dorm
        DormitoryModel newDorm = new DormitoryModel();
        newDorm.setName(dormName);
        newDorm.setDormType(dormType);
        setDormAttributesByDormType(newDorm);
        newDorm.setHourLastUpdated(0);
        newDorm.setReputation(5);
        newDorm.setCurDisaster("none");
        newDorm.setMaintenanceCostPerDay(newDorm.getNumRooms());

        // Pay for dorm
        if (newDorm.getTotalBuildCost() >= Accountant.getAvailableCash(runId)) {
            newDorm.setNote("Not enough money to build it.");
            return newDorm;
        }

        Accountant.payBill(runId, "Charge of new dorm", newDorm.getTotalBuildCost());
        NewsManager.createNews(runId, hoursAlive, "Construction of " + dormName +" dorm has started! ", NewsType.RES_LIFE_NEWS, NewsLevel.GOOD_NEWS);
        newDorm.setNote("A new dorm has been created.");

        // Save dorm
        DormitoryDao dormDao = new DormitoryDao();
        dormDao.saveNewDorm(runId, newDorm);
        return newDorm;
    }

    /**
     * Charge the college the cost of running the dorm.
     *
     * @param runId college name
     * @param hoursAlive number of hours the college has existed
     * @param dorm the dorm that's causing the cost
     */
    private void billRunningCostOfDorm(String runId, int hoursAlive, DormitoryModel dorm) {
        int newCharge = (hoursAlive - dorm.getHourLastUpdated()) * dorm.getMaintenanceCostPerDay();
        Accountant.payBill(runId, "Maintenance of dorm " + dorm.getName(), (int) (newCharge));
    }

    /**
     * Set the dorm as having a flood disaster and set the number of hours left in the flooding.
     *
     * @param lengthOfFlood
     * @param dormName
     * @param collegeId
     */
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
    }

    /**
     * Return the name of a dorm that has an open spot for a student
     * and reserve this spot by increasing the dorm occupancy by 1.
     * It's the responsibility of the caller to store the dorm name
     * under the student.
     *
     * @param collegeId college name
     * @return the name of the dorm where the student as placed.  If no space available, return commuter.
     */
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

        // This means we didn't find room for the student!
        return "Commuter";
    }

    /**
     * Increase the occupancy of the given dorm.
     * It's the responsibility of the caller to clear the dorm name
     * stored under the student.
     *
     * @param collegeId
     * @param dormName
     */
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

    /**
     * Return the number of open beds in the college.
     *
     * @param collegeId
     * @return
     */
    public static int getOpenBeds(String collegeId) {
        List<DormitoryModel> dorms = dao.getDorms(collegeId);
        int openBeds = 0;
        for (DormitoryModel d : dorms) {
            if(d.getHoursToComplete() <= 0) {
                int numStudents = d.getNumStudents();
                int capacity = d.getCapacity();
                openBeds += capacity - numStudents;
            }
        }
        return openBeds;
    }

    /**
     * Return the number of beds under construction.
     *
     * @param collegeId
     * @return
     */
    public static int getBedsUnderConstruction(String collegeId) {
        List<DormitoryModel> dorms = dao.getDorms(collegeId);
        int openBeds = 0;
        for (DormitoryModel d : dorms) {
            if(d.getHoursToComplete() > 0) {
                int numStudents = d.getNumStudents();
                int capacity = d.getCapacity();
                openBeds += capacity - numStudents;
            }
        }
        return openBeds;
    }

    /**
     * Sell a dorm.
     *
     * @param runId
     * @param dormName
     */
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
                    Accountant.receiveIncome(runId, dormName + "has been sold.", refund);
                    return;
                }
                else{
                    logger.info("Not enough open beds...");
                }

            }
        }

    }

    /**
     * Get a list of the types of the dorms that can be built.
     * Returns a list of dorms where the dorm type is indicating
     * which dorms can be built.
     *
     * @param runId
     * @return
     */
    public List<DormitoryModel> getWhatTypesOfDormsCanBeBuilt(String runId){
        ArrayList<DormitoryModel> availableDormTypes = new ArrayList<>();
        DormitoryModel smallDorm = new DormitoryModel();
        smallDorm.setDormType(1);
        DormitoryModel mediumDorm = new DormitoryModel();
        mediumDorm.setDormType(2);
        DormitoryModel largeDorm = new DormitoryModel();
        largeDorm.setDormType(3);

        int availableCash = Accountant.getAvailableCash(runId);

        if(availableCash >= 250000){
            //can build all dorm types
            availableDormTypes.add(smallDorm);
            availableDormTypes.add(mediumDorm);
            availableDormTypes.add(largeDorm);
        }
        else if(availableCash >= 175000){
            availableDormTypes.add(smallDorm);
            availableDormTypes.add(mediumDorm);
        }
        else if(availableCash >=100000){
            availableDormTypes.add(smallDorm);
        }

        return availableDormTypes;
    }

    /**
     * See if a surprise event has occurred during construction.
     * If so, report it.
     *
     * @param runId
     * @param hoursWorkingOnDorm
     */
    public void surpriseEventDuringConstruction(String runId, int hoursWorkingOnDorm) {
        String dormName = "";
        double chance = Math.random();
        if (chance < 0.25 * 24f/hoursWorkingOnDorm) {
            //25% chance of gaining $1000 dollars every 24 hours working on dorm.
            Accountant.receiveIncome(runId, "Donation received for building " + dormName, 1000);
        } else if (chance < 0.35 * 24/hoursWorkingOnDorm) {
            Accountant.receiveIncome(runId, "Ran into unexpected construction costs building " + dormName, 500);
        }
    }

    /**
     * A new college has just been built.
     * Take care of any initial dorm construction.
     *
     * @param runId
     * @param college
     */
    static public void establishCollege(String runId, CollegeModel college) {
        DormitoryModel dorm = new DormitoryModel(200, 10, "Hampshire Hall",
                0, "none", 5, "none", 100);
        dorm.setHoursToComplete(0);
        dorm.setMaintenanceCostPerDay(60);
        dorm.setTotalBuildCost(100);
        DormitoryDao dormDao = new DormitoryDao();
        dormDao.saveNewDorm(runId, dorm);
        NewsManager.createNews(runId, college.getCurrentDay(), "Dorm " + dorm.getName() + " has opened.", NewsType.RES_LIFE_NEWS, NewsLevel.GOOD_NEWS);
    }

    /**
     * Get the dorms at the college.
     *
     * @param runId
     * @return
     */
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







