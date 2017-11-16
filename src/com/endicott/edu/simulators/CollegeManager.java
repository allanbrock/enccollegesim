package com.endicott.edu.simulators;

import com.endicott.edu.datalayer.*;
import com.endicott.edu.models.*;

import java.util.Random;
import java.util.logging.Logger;

// Created by abrocken on 7/24/2017.

public class CollegeManager {
    static public final int STARTUP_FUNDING = 1000000;
    /**
     * This functions updates the amount that the college costs per year
     * @param runId id of college instance
     * @return an instance of a college model
     */
    public static CollegeModel updateCollegeTuition(String runId, int amount){
        CollegeDao cao = new CollegeDao();
        CollegeModel college = cao.getCollege(runId); //get the college for this runID
        college.setYearlyTuitionCost(amount); //set the amount via setter
        cao.saveCollege(college); //write to disk
        NewsManager.createNews(runId, college.getCurrentDay(),"Tuition Updated to: $" + amount, NewsType.FINANCIAL_NEWS);
        return college;
    }



    static public CollegeModel establishCollege(String runId) {
        CollegeDao collegeDao = new CollegeDao();

        Logger logger = Logger.getLogger("CollegeManager");
        logger.info("Establishing the college");

        // See if there already is a college for this run.
        // We don't expect this, but if so, just return it.
        logger.info("Checking if college exists.");
        try {
            logger.info("College exists.");
            return collegeDao.getCollege(runId);
        } catch (Exception ignored) {
        }

        // Create the college.
        logger.info("Creating college");
        CollegeModel college = new CollegeModel();
        college.setRunId(runId);
        college.setHoursAlive(1);
        college.setAvailableCash(STARTUP_FUNDING);
        collegeDao.saveCollege(college);
        NewsManager.createNews(runId, college.getCurrentDay(),"The college was established today.", NewsType.GENERAL_NOTE);

        // Create a dorm
        // We need to add the students to the dorm.
        DormManager dormManager = new DormManager();
        dormManager.establishCollege(runId, college);

        // Creating students
        StudentManager studentManager = new StudentManager();
        studentManager.addNewStudents(runId, college.getCurrentDay()/24, true);

        // Create a plague
        PlagueManager plague = new PlagueManager();
        plague.createInitialPlague(runId);

        //save new flood
        FloodManager.initFloodOnCollegeCreate(runId);
        FacultyManager.createInitFaculty(runId); //create init faculty
        logger.info("Done creating college");

        return college;
    }

    private static void makeStudentSick(StudentModel student, String runId, int currentDay) {
        Random rand = new Random();

        if(rand.nextInt(10) + 1 > 9){
            student.setNumberHoursLeftBeingSick(72);
            NewsManager.createNews(runId,currentDay, student.getName() + " is sick", NewsType.GENERAL_NOTE);
        } else {
            student.setNumberHoursLeftBeingSick(0);
        }
    }

    static public void sellCollege(String runId) {
        CollegeDao.deleteCollege(runId);
        DormitoryDao.deleteDorms(runId);
        FacultyDao.removeAllFaculty(runId);
        FloodDao.deleteFloods(runId);
        NewsFeedDao.deleteNotes(runId);
        PlagueDao.deletePlagues(runId);
        SportsDao.deleteSports(runId);
        StudentDao.deleteStudents(runId);
        IdNumberGenDao.deleteIDs(runId);
    }

    static public CollegeModel nextDay(String runId) {
        CollegeDao collegeDao = new CollegeDao();

        // Get the college
        CollegeModel college = collegeDao.getCollege(runId);

        // Advance time
        college.advanceClock(24);
        collegeDao.saveCollege(college);
        int hoursAlive = college.getHoursAlive();

        // Tell everyone about the time change.
        FloodManager floodManager = new FloodManager();
        floodManager.handleTimeChange(runId, hoursAlive);

        //Plague time change
        PlagueManager plagueManager = new PlagueManager();
        plagueManager.handleTimeChange(runId, hoursAlive);

        DormManager dormManager = new DormManager();
        dormManager.handleTimeChange(runId, hoursAlive);

        SportManager sportManager = new SportManager();
        sportManager.handleTimeChange(runId, hoursAlive);

        StudentManager studentManager = new StudentManager();
        studentManager.handleTimeChange(runId, hoursAlive);

        FacultyManager.handleTimeChange(runId,hoursAlive);

        return college;
    }

    static public boolean doesCollegeExist(String runId) {
        CollegeDao collegeDao = new CollegeDao();

        // See if there already is a college for this run.
        // We don't expect this, but if so, just return it.
        try {
            CollegeModel college = collegeDao.getCollege(runId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
