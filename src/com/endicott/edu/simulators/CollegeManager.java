package com.endicott.edu.simulators;

import com.endicott.edu.datalayer.*;
import com.endicott.edu.models.*;

import java.util.logging.Logger;

/**
 * The CollegeManager is responsible for simulating all overall college functions,
 * such as creating a college, deleting the college, and is responsible for
 * providing college information (retention rate, current tuition, etc.)
 */

public class CollegeManager {
    static public final int STARTUP_FUNDING = 200000;  // Amount of money initially in college bank account.

    /**
     * Sets college yearly tuition.
     *
     * @param runId college name
     * @return the college
     */
    public static CollegeModel updateCollegeTuition(String runId, int amount){
        CollegeDao cao = new CollegeDao();
        CollegeModel college = cao.getCollege(runId); //get the college for this runID
        college.setYearlyTuitionCost(amount); //set the amount via setter
        cao.saveCollege(college); //write to disk
        NewsManager.createNews(runId, college.getHoursAlive(),"Tuition Updated to: $" + amount, NewsType.FINANCIAL_NEWS,NewsLevel.GOOD_NEWS);

        StudentManager studentManager = new StudentManager();
        studentManager.recalculateStudentStatistics(runId);

        return college;
    }

    /**
     * Creates a new college.
     *
     * @param runId college name
     * @return the college
     */
    static public CollegeModel establishCollege(String runId) {
        CollegeDao collegeDao = new CollegeDao();
        Logger logger = Logger.getLogger("CollegeManager");
        logger.info("Establishing the college");

        // See if there already is a college for this run.
        // We don't expect this, but if so, just return it.
        try {
            logger.info("College exists.");
            return collegeDao.getCollege(runId);
        } catch (Exception ignored) {
        }

        // Create the college.
        CollegeModel college = new CollegeModel();
        college.setRunId(runId);
        college.setHoursAlive(1);
        college.setAvailableCash(STARTUP_FUNDING);
        collegeDao.saveCollege(college);

        NewsManager.createNews(runId, college.getCurrentDay(),"The college was established today.", NewsType.COLLEGE_NEWS, NewsLevel.GOOD_NEWS);

        DormManager.establishCollege(runId, college);
        FacultyManager.establishCollege(runId);

        StudentManager studentManager = new StudentManager();
        studentManager.establishCollege(runId);

        PlagueManager.establishCollege(runId);
        FloodManager.establishCollege(runId);

        EventManager.establishCollege(runId);

        return college;
    }

    /**
     * Deletes a college.  Removes all storage associated with the college.
     *
     * @param runId college name
     */
    static public void sellCollege(String runId) {
        CollegeDao.deleteCollege(runId);
        DormitoryDao.deleteDorm(runId);
        FacultyDao.removeAllFaculty(runId);
        FloodDao.deleteFloods(runId);
        NewsFeedDao.deleteNotes(runId);
        PlagueDao.deletePlagues(runId);
        SportsDao.deleteSports(runId);
        StudentDao.deleteStudents(runId);
        IdNumberGenDao.deleteIDs(runId);
    }

    /**
     * Advance the clock one day.  Simulate all changes occurring during that day.
     *
     * @param runId college name
     */
    static public CollegeModel nextDay(String runId) {
        CollegeDao collegeDao = new CollegeDao();

        // Get the college
        CollegeModel college = collegeDao.getCollege(runId);

        // Advance time college has been alive.
        college.setHoursAlive(college.getHoursAlive() + 24);
        collegeDao.saveCollege(college);
        int hoursAlive = college.getHoursAlive();

        // Tell all the simulators about the time change.

        PlagueManager plagueManager = new PlagueManager();
        plagueManager.handleTimeChange(runId, hoursAlive);

        DormManager dormManager = new DormManager();
        dormManager.handleTimeChange(runId, hoursAlive);

        SportManager sportManager = new SportManager();
        sportManager.handleTimeChange(runId, hoursAlive);

        StudentManager studentManager = new StudentManager();
        studentManager.handleTimeChange(runId, hoursAlive);

        FloodManager floodManager = new FloodManager();
        floodManager.handleTimeChange(runId, hoursAlive);

        FacultyManager.handleTimeChange(runId,hoursAlive);

        return college;
    }

    /**
     * Return true if the given college exists.
     *
     * @param runId college name
     * @return true if exists.
     */
    static public boolean doesCollegeExist(String runId) {
        CollegeDao collegeDao = new CollegeDao();

        try {
            collegeDao.getCollege(runId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}