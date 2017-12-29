package com.endicott.edu.simulators;

import com.endicott.edu.datalayer.CollegeDao;
import com.endicott.edu.models.CollegeModel;

/**
 * Responsible for handling finances at the college.
 */
public class Accountant {

    /**
     * Pay the given bill deducting the money from available
     * cash.
     *
     * @param runId  college name
     * @param message description of bill
     * @param amount bill amount
     */
    static public void payBill(String runId, String message, int amount) {
        CollegeDao collegeDao = new CollegeDao();

        CollegeModel college = collegeDao.getCollege(runId);
        college.setAvailableCash(college.getAvailableCash() - amount);
        collegeDao.saveCollege(college);
        NewsManager.createFinancialNews(runId,college.getHoursAlive(), message, - amount);
    }

    /**
     * Increase the available cash at the college.
     *
     * @param runId
     * @param message description of why money was received.
     * @param amount
     */
    static public void receiveIncome(String runId, String message, int amount) {
        CollegeDao collegeDao = new CollegeDao();

        CollegeModel college = collegeDao.getCollege(runId);
        college.setAvailableCash(college.getAvailableCash() + amount);
        collegeDao.saveCollege(college);
        NewsManager.createFinancialNews(runId, college.getHoursAlive(),message, + amount);
    }

    /**
     * Get the balance at the college.
     *
     * @param runId
     * @return
     */
    public static int getAvailableCash(String runId) {
        CollegeDao collegeDao = new CollegeDao();
        CollegeModel college = collegeDao.getCollege(runId);
        return college.getAvailableCash();
    }
}

