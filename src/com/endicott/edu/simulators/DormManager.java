package com.endicott.edu.simulators;

import com.endicott.edu.datalayer.CollegeDao;
import com.endicott.edu.datalayer.DormitoryDao;
import com.endicott.edu.models.DormitoryModel;
import com.endicott.edu.models.NewsType;
import java.util.List;
import java.util.logging.Logger;

public class DormManager {
    DormitoryDao dao = new DormitoryDao();
    static private Logger logger = Logger.getLogger("DormManager");

    CollegeDao collegeDao = new CollegeDao();



    public void handleTimeChange(String runId, int hoursAlive) {
        List<DormitoryModel> dorms = dao.getDorms(runId);

        for (DormitoryModel dorm : dorms) {

            billRunningCostOfDorm(runId, hoursAlive, dorm);

            dorm.setHourLastUpdated(hoursAlive);
        }

        dao.saveAllDorms(runId, dorms);
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
        Accountant.payBill(runId,"Maintenance of dorm " + dorm.getName() + " $ " + newCharge,(int) (newCharge));
    }



    /*Takes in the length of the flood, the dorm dormName affected by the flood, and the runId of the college. */
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

    /*Handles one student being admitted to the college at a time:
    Takes in the runId of the college (String)
    returns the name of the dorm (String) that student was placed in.*/
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

    /*Handles one student leaving the college at a time:
    Takes in the runId of the college (String), and the name of the dorm the student is in (String)
    returns nothing.*/
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







