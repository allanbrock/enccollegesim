package com.endicott.edu.simulators;

import com.endicott.edu.datalayer.FacultyDao;
import com.endicott.edu.datalayer.IdNumberGenDao;
import com.endicott.edu.datalayer.NameGenDao;
import com.endicott.edu.models.FacultyModel;

import java.util.List;

/**
 * Responsible for simulating faculty at the college.
 */
public class FacultyManager {

    /**
     * Simulate changes in faculty based on the passage of time at the college.
     *
     * @param runId
     * @param hoursAlive number of hours college has existed.
     */
    public static void handleTimeChange(String runId, int hoursAlive){
            payFaculty(runId,hoursAlive);
    }

    /**
     * Pay the faculty based on the number of hours that have passed at the
     * college since the faculty was last paid.
     *
     * @param runId
     * @param hoursAlive  number of hours college has existed.
     */
   private static void payFaculty(String runId, int hoursAlive){
        FacultyDao fao = new FacultyDao();
        List<FacultyModel> facultyList = fao.getFaculty(runId);
        int total = 0;
        for(FacultyModel member : facultyList){
            int yearlySalary = member.getSalary();
            int paycheck = (int) ((hoursAlive/24f)*(yearlySalary/365f)) ;
            total += paycheck;
        }

        Accountant.payBill(runId,"Faculty has been paid",total);
   }

    /**
     * Create the initial faculty at the new college.
     *
     * @param runId instance of the simulation
     */
   public static void establishCollege(String runId){
       for (int i=0; i<10; i++) {
           addFaculty(runId);
       }
   }

    /**
     * Add new faculty to the college.
     */
    public static FacultyModel addFaculty(String runId) {
        FacultyModel member;
        FacultyDao fao = new FacultyDao();

        member = new FacultyModel("Dr. " + NameGenDao.generateName(false), "Dean", "Science", "LSB", runId);
        member.setFacultyID(IdNumberGenDao.getID(runId));
        fao.saveNewFaculty(runId, member);
        return member;
    }
}
