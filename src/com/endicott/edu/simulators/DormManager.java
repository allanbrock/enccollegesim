package com.endicott.edu.simulators;

import com.endicott.edu.datalayer.CollegeDao;
import com.endicott.edu.datalayer.DormitoryDao;
import com.endicott.edu.models.DormitoryModel;
import com.endicott.edu.models.NewsType;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class DormManager {
    private static final float PROBABILTY_OF_FLOOD = 0.005f;
    //how much the college loses everytime a dorm floods.
    private static final int COST_OF_FLOOD = 500;
    DormitoryDao dao = new DormitoryDao();
    static private Logger logger = Logger.getLogger("DormManager");

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
    private static void setDormAttributesByDormType(DormitoryModel temp){


        //small size
        if(temp.getDormType() == 1){
//          int capacity, int hourLastUpdated, String dormName, int numStudents,
            //String curDisaster, int reputation, String runId, int numRooms,
            //float maintenanceCost
            temp.setCapacity(700);
            temp.setNumRooms(350);

        }
        //normal size
        else if(temp.getDormType() == 2){
            temp.setCapacity(1000);
            temp.setNumRooms(500);

        }
        //large size
        else if(temp.getDormType() == 3){
            temp.setCapacity(1500);
            temp.setNumRooms(750);

        }
        else{
            //not a type only 3 types of dorms
            logger.severe("Could not add dorm: '" + temp.getName() + "'");
        }



    }

    public static DormitoryModel createDorm(String runId, String dormName, String dormType){
        //need to get "dorm"
        DormitoryModel temp = new DormitoryModel();
        temp.setName(dormName);
        if(dormType == "Small"){
            temp.setDormType(1);
        }
        else if(dormType == "Medium"){
            temp.setDormType(2);
        }
        else if(dormType == "Large"){
            temp.setDormType(3);
        }

        setDormAttributesByDormType(temp);
        temp.setHourLastUpdated(0);
        temp.setReputation(5);
        temp.setCurDisaster("none");
        temp.setMaintenanceCostPerHour(temp.getNumRooms());
        DormitoryDao dormDao = new DormitoryDao();
        temp.setNote("A new dorm has been created.");

        dormDao.saveNewDorm(runId, temp);
        return temp;
    }

    private void billRunningCostOfDorm(String runId, int hoursAlive, DormitoryModel dorm) {
        float newCharge = (hoursAlive - dorm.getHourLastUpdated()) * dorm.getMaintenanceCostPerHour();
        Accountant.payBill(runId, (int) (newCharge));
        NewsManager.createNews(runId, hoursAlive, "Charge for " + dorm.getName() + " $" + newCharge, NewsType.FINANCIAL_NEWS);
    }



    //takes in the length of the flood, the dorm dormName affected by the flood, and the runId of the college.
    public void floodAlert(int lengthOfFlood, String dormName, String collegeId){
        List<DormitoryModel> dorms = dao.getDorms(collegeId);
        for(DormitoryModel d : dorms){
            if(d.getName() == dormName){
                d.setCurDisaster("flood");
                d.setLengthOfDisaster(lengthOfFlood);
            }
        }
        dao.saveAllDorms(collegeId, dorms);
        //when lengthOfFlood number of hours is completed change curDisaster back to "none".
    }

    //handles one student being admitted to the college at a time:
    //takes in the runId of the college (String)
    //returns the dormName of the dorm (String) that student was placed in.
    public String assignDorm(String collegeId){
        List<DormitoryModel> dorms = dao.getDorms(collegeId);
        String dormName = "";
        for(DormitoryModel d : dorms){
            int s = d.getNumStudents();
            int c = d.getCapacity();
            dormName = d.getName();
            if(s < c){
                d.setNumStudents(s + 1);
                break;
            }
        }
        dao.saveAllDorms(collegeId, dorms);
        return dormName;
    }

    //handles one student leaving the college at a time:
    //takes in the runId of the college (String), and the dormName of the dorm the student is in (String)
    //returns nothing.
    public void removeStudent(String collegeId, String dormName){
        List<DormitoryModel> dorms = dao.getDorms(collegeId);
        for(DormitoryModel d : dorms){
            int s = d.getNumStudents();
            if(d.getName() == dormName){
                d.setNumStudents(s - 1);
                break;
            }
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
        for (DormitoryModel d : dorms){
            int numStudents = d.getNumStudents();
            int capacity = d.getCapacity();
            openBeds += capacity - numStudents;
        }
        return openBeds;
    }












    }







