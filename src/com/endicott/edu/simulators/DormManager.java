package com.endicott.edu.simulators;

import com.endicott.edu.datalayer.CollegeDao;
import com.endicott.edu.datalayer.DormitoryDao;
import com.endicott.edu.datalayer.NewsFeedDao;
import com.endicott.edu.models.CollegeModel;
import com.endicott.edu.models.DormitoryModel;
import com.endicott.edu.models.NewsFeedItemModel;
import com.endicott.edu.models.NewsType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abrocken on 7/29/2017.
 */
public class DormManager {
    private static final float PROBABILTY_OF_FLOOD = 0.005f;
    //how much the college loses everytime a dorm floods.
    private static final int COST_OF_FLOOD = 500;
    DormitoryDao dao = new DormitoryDao();

    CollegeDao collegeDao = new CollegeDao();



    public void handleTimeChange(String runId, int hoursAlive) {
        List<DormitoryModel> dorms = dao.getDorms(runId);

        for (DormitoryModel dorm : dorms) {

            billRunningCostOfDorm(runId, hoursAlive, dorm);
//            checkForEnvironmentalDisaster(runId, hoursAlive, dorm);

            dorm.setHourLastUpdated(hoursAlive);
        }

        dao.saveAllDorms(runId, dorms);
//        // Get the college
//        CollegeModel college = collegeDao.getCollege(runId);
//        collegeRunId = college.getRunId();

    }
    public void generateNewDorm(int dormType){

        //small size
        if(dormType == 1){
//          int capacity, int hourLastUpdated, String name, int numStudents,
            //String curDisaster, int reputation, String runId, int numRooms,
            //float maintenanceCost
            DormitoryModel temp = new DormitoryModel(700, 1, "take in from user 1", 0, "none",
                   5, " ",  350);
        }
        //normal size
        else if(dormType == 2){
            DormitoryModel temp = new DormitoryModel(1000, 1, "take in from user 2", 0, "none",
                    5, " ",  500);
        }
        //large size
        else if(dormType == 3){
            DormitoryModel temp = new DormitoryModel(1500, 1, "take in from user 3", 0, "none",
                    5, " ",  750);
        }
        else{
            //not a type only 3 types of dorms
        }

    }
//    private void setDormAttributes(DormitoryModel temp){
//
//        if(dormType == 1){
//            this.buildCost = 1500000;
//            this.squareFeet = 12000;
//            this.maintenanceCost = 2 * this.squareFeet;
//        }
//
//
//    }


    private void billRunningCostOfDorm(String runId, int hoursAlive, DormitoryModel dorm) {
        float newCharge = (hoursAlive - dorm.getHourLastUpdated()) * dorm.getMaintenanceCostPerHour();
        Accountant.payBill(runId, (int) (newCharge));
        NewsManager.createNews(runId, hoursAlive, "Charge for " + dorm.getName() + " $" + newCharge);
    }



    //takes in the length of the flood, the dorm name affected by the flood, and the runId of the college.
    public void floodAlert(int lengthOfFlood, String dormName, String collegeId){
        List<DormitoryModel> dorms = dao.getDorms(collegeId);
        for(DormitoryModel d : dorms){
            if(d.getName() == dormName){
                //d.setCurDisaster("flood");
                //d.setLengthOfDisaster(lengthOfFlood);
            }
        }
        dao.saveAllDorms(collegeId, dorms);
        //when lengthOfFlood number of hours is completed change curDisaster back to "none".
    }

    //handles one student being admitted to the college at a time:
    //takes in the runId of the college (String)
    //returns the name of the dorm (String) that student was placed in.
    public String assignDorm(String collegeId){
        List<DormitoryModel> dorms = dao.getDorms(collegeId);
        String dormName = "";
        for(DormitoryModel d : dorms){
//            int s = d.getNumStudents();
//            int c = d.getCapacity();
//            dormName = d.getName();
//            if(s < c){
//                d.setNumStudents(s + 1);
//                break;
//            }
        }
        dao.saveAllDorms(collegeId, dorms);
        return dormName;
    }

    //handles one student leaving the college at a time:
    //takes in the runId of the college (String), and the name of the dorm the student is in (String)
    //returns nothing.
    public void removeStudent(String collegeId, String dormName){
        List<DormitoryModel> dorms = dao.getDorms(collegeId);
        for(DormitoryModel d : dorms){
//            int s = d.getNumStudents();
//            if(d.name == dormName){
//                d.setNumStudents(s - 1);
//                break;
//            }
        }
        dao.saveAllDorms(collegeId, dorms);

    }

    //takes in the runId of the college, and returns a table of the
    //name of each dorm and its corresponding reputation level (0-10).
//    public String[][] getDormReputation(String collegeId){
//        List<DormitoryModel> dorms = dao.getDorms(collegeId);
//        String [][] dormReputation = new String[dorms.size()][2];
//        int i = 0;
//        int j = 1;
//        for(DormitoryModel d : dorms){
//            int r = d.getReputation();
//            dormReputation[i][i] = d.getName();
//            dormReputation[i][j] = String.valueOf(r);
//
//            i++;
//            j++;
//        }
//
//        return dormReputation;
//    }

    //takes in the runId of the college, and returns the number of open beds throughout
    //of the dorms.
    public int getOpenBeds(String collegeId){
        List<DormitoryModel> dorms = dao.getDorms(collegeId);
        int openBeds = 0;
//        for (DormitoryModel d : dorms){
//            int numStudents = d.getNumStudents();
//            int capacity = d.getCapacity();
//            openBeds += capacity - numStudents;
//        }
        return openBeds;
    }












    }







