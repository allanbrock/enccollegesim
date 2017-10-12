package com.endicott.edu.simulators;

import com.endicott.edu.datalayer.*;
import com.endicott.edu.models.*;

import java.util.Random;
import java.util.logging.Logger;

// Created by abrocken on 7/24/2017.

public class CollegeManager {
    static public final int STARTUP_FUNDING = 100000;

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
        NewsManager.createNews(runId, college.getCurrentDay(),"The college was established today.");
        // Creating students
        createInitialStudents(runId, college.getCurrentDay());

        // Create a dorm
        // We need to add the students to the dorm.
        logger.info("Creating dorm");
        DormitoryModel dorm = new DormitoryModel(100, 10, "Hampshire Hall", 120,"none", 5, "none", 60);
        DormitoryDao dormDao = new DormitoryDao();
        dormDao.saveNewDorm(runId, dorm);
        NewsManager.createNews(runId, college.getCurrentDay(),"Dorm " + dorm.getName() + " has opened.");

        SportManager sportManager = new SportManager();
        sportManager.addNewTeam("Men's Soccer", runId);
        sportManager.addNewTeam("Men's Basketball", runId);

        logger.info("Done creating college");
        return college;
    }

    static private void createInitialFaculty(String runId){
        Logger logger = Logger.getLogger("CollegeManager");

        logger.info("Creating Initial Faculty..");

        FacultyModel member = new FacultyModel("Dr. Jake Test","Dean","Science","LSB",runId);
        FacultyDao fao = new FacultyDao();
        fao.saveNewFaculty(runId,member);
        logger.info("Created new faculty member ID: " + member.getFacultyID());

    }

    static private void createInitialStudents(String runId, int currentDay) {
        StudentModel student = new StudentModel();
        StudentDao studentDao = new StudentDao();
        Random rand = new Random();
        int numStudents = 2 + rand.nextInt(3);

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
            student.setSick(false);
            student.setRunId(runId);
            studentDao.saveNewStudent(runId, student);
        }

        NewsManager.createNews(runId, currentDay,Integer.toString(numStudents) + " students have enrolled.");
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
        DormManager dormManager = new DormManager();
        dormManager.handleTimeChange(runId, hoursAlive);

        SportManager sportManager = new SportManager();
        sportManager.handleTimeChange(runId, hoursAlive);

        FloodManager floodManager = new FloodManager();
        floodManager.handleTimeChange(runId, hoursAlive);

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
