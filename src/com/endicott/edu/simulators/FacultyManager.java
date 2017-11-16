package com.endicott.edu.simulators;

import com.endicott.edu.datalayer.FacultyDao;
import com.endicott.edu.models.FacultyModel;
import com.endicott.edu.models.NewsType;


import java.util.List;

public class FacultyManager {

    public static void handleTimeChange(String runId, int hoursAlive){
            payFacultySingleDay(runId,hoursAlive);
    }

   private static void payFacultySingleDay(String runId,int hoursAlive){
        //pay scale is based on this
        //https://www.opm.gov/policy-data-oversight/pay-leave/pay-administration/fact-sheets/computing-hourly-rates-of-pay-using-the-2087-hour-divisor/
        //average person works 260 days a year
        //salary/260 = each day at the college
        //this can be refined if we find that it is too costly
        FacultyDao fao = new FacultyDao();
        List<FacultyModel> facultyList = fao.getFaculty(runId);
        int total = 0;
        for(FacultyModel member : facultyList){
            int tmp = member.getSalary();
            tmp = tmp/260;
            total += tmp;
        }
        Accountant.payBill(runId,"Faculty has been paid  $ " + total,total);
   }

    /**
     * This function creates the init faculty for the college
     * This is the dean in this case
     * @param runId instance of the simulation
     */
   public static void createInitFaculty(String runId){
       FacultyModel member = new FacultyModel("Dr. Jake Test","Dean","Science","LSB",runId);
       member.setFacultyID(-1); //set the id to -1 so we know this is the first id we set
       FacultyDao fao = new FacultyDao();
       fao.saveNewFaculty(runId,member);
   }
}
