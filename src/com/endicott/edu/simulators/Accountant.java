package com.endicott.edu.simulators;

import com.endicott.edu.datalayer.CollegeDao;
import com.endicott.edu.models.CollegeModel;

/**
 * Created by abrocken on 7/29/2017.
 */
public class Accountant {
    static public void payBill(String runId, int amount) {
        CollegeDao collegeDao = new CollegeDao();

        CollegeModel college = collegeDao.getCollege(runId);
        college.setAvailableCash(college.getAvailableCash() - amount);
        collegeDao.saveCollege(college);
    }

    static public void studentIncome(String runId, int amount) {
        CollegeDao collegeDao = new CollegeDao();

        CollegeModel college = collegeDao.getCollege(runId);
        college.setAvailableCash(college.getAvailableCash() + amount);
        collegeDao.saveCollege(college);
    }
}

