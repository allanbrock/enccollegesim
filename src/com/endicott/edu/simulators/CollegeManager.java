package com.endicott.edu.simulators;

import com.endicott.edu.datalayer.*;
import com.endicott.edu.models.*;

import java.util.Random;
import java.util.logging.Logger;

// Created by abrocken on 7/24/2017.

public class CollegeManager {
    static public final int STARTUP_FUNDING = 10000;

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
        // Creating students
        createInitialStudents(runId, college.getCurrentDay());

        // Create a dorm
        // We need to add the students to the dorm.
        logger.info("Creating dorm");
        DormitoryModel dorm = new DormitoryModel(100, 10, "Hampshire Hall",
                120,"none", 5, "none", 60);
        dorm.setCostPerHour(450);
        DormitoryDao dormDao = new DormitoryDao();
        dormDao.saveNewDorm(runId, dorm);
        NewsManager.createNews(runId, college.getCurrentDay(),"Dorm " + dorm.getName() + " has opened.", NewsType.GENERAL_NOTE);

        // Create a plague
        // Make students sick.
        logger.info("Generating Plague");
        PlagueModel plague = new PlagueModel( 0, 0, "Hampshire Hall","none", 5, 0, 1000, 72, 0);
        PlagueDao plagueDao = new PlagueDao();
        plagueDao.saveNewPlague(runId, plague);
        int sickStudents = plague.getStudentSick();
        NewsManager.createNews(runId, college.getCurrentDay(),"Dorm " + dorm.getName() + " has been infected. 5 students are sick.", NewsType.GENERAL_NOTE);

        SportManager sportManager = new SportManager();
        sportManager.addNewTeam("Men's Soccer", runId);
        sportManager.addNewTeam("Men's Basketball", runId);

        logger.info("Done creating college");
        createInitialFaculty(runId);
        return college;
    }

    static private void createInitialFaculty(String runId){
        Logger logger = Logger.getLogger("CollegeManager");
        logger.info("Creating Initial Faculty..");
        FacultyModel member = new FacultyModel("Dr. Jake Test","Dean","Science","LSB",runId);
        member.setFacultyID(-1); //set the id to -1 so we know this is the first id we set
        FacultyDao fao = new FacultyDao();
        fao.saveNewFaculty(runId,member);
        logger.info("Created new faculty member ID: " + member.getFacultyID());

    }

    static private void createInitialStudents(String runId, int currentDay) {
        StudentModel student = new StudentModel();
        StudentDao studentDao = new StudentDao();
        Random rand = new Random();
        int numStudents = 100;

        for(int i = 0; i < numStudents; i++) {
            student.setIdNumber(100000 + rand.nextInt(900000));
            student.setHappinessLevel(rand.nextInt(100));
            student.setAthlete(false);
            student.setAthleticAbility(rand.nextInt(100));
            student.setTeam("");
            student.setDorm("");
            if (rand.nextInt(1) == 1) {
                student.setGender("Male");
            } else {
                student.setGender("Female");
            }
            student.setRunId(runId);
            studentDao.saveNewStudent(runId, student);
        }

        NewsManager.createNews(runId, currentDay,Integer.toString(numStudents) + " students have enrolled.", NewsType.GENERAL_NOTE);
    }

    static public void sellCollege(String runId) {
        CollegeDao collegeDao = new CollegeDao();
        DormitoryDao dormitoryDao = new DormitoryDao();
        NewsFeedDao noteDao = new NewsFeedDao();
        SportsDao sportsDao = new SportsDao();

        collegeDao.deleteCollege(runId);
        dormitoryDao.deleteDorms(runId);
        sportsDao.deleteSports(runId);
        noteDao.deleteNotes(runId);
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
