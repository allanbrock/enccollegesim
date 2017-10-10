package com.endicott.edu.simulators;

import com.endicott.edu.datalayer.DormitoryDao;
import com.endicott.edu.datalayer.NewsFeedDao;
import com.endicott.edu.models.DormitoryModel;
import com.endicott.edu.models.NewsFeedItemModel;
import com.endicott.edu.models.NewsType;

import java.util.List;

/**
 * Created by abrocken on 7/29/2017.
 */
public class DormManager {
    private static final float PROBABILTY_OF_FLOOD = 0.005f;
    DormitoryDao dao = new DormitoryDao();

    public void handleTimeChange(String runId, int hoursAlive) {
        List<DormitoryModel> dorms = dao.getDorms(runId);

        for (DormitoryModel dorm : dorms) {

            billRunningCostOfDorm(runId, hoursAlive, dorm);
            checkForEnvironmentalDisaster(runId, hoursAlive, dorm);

            dorm.setHourLastUpdated(hoursAlive);
        }

        dao.saveAllDorms(runId, dorms);
    }
    public void generateNewDorm(int dormType){

        //small size
        if(dormType == 1){
//          int capacity, int hourLastUpdated, String name, int numStudents,
            //String curDisaster, int reputation, String runId, int numRooms,
            //float maintenanceCost
            DormitoryModel temp = new DormitoryModel(700, 1, "take in from user 1", 0, "none",
                   5, " ",  350, 0);
        }
        //normal size
        else if(dormType == 2){
            DormitoryModel temp = new DormitoryModel(1000, 1, "take in from user 2", 0, "none",
                    5, " ",  500, 0);
        }
        //large size
        else if(dormType == 3){
            DormitoryModel temp = new DormitoryModel(1500, 1, "take in from user 3", 0, "none",
                    5, " ",  750, 0);
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
        float newCharge = (hoursAlive - dorm.getHourLastUpdated()) * dorm.getCostPerHour();
        Accountant.payBill(runId, (int) (newCharge));
        NewsManager.createNews(runId, hoursAlive, "Charge for " + dorm.getName() + " $" + newCharge);
    }

    private void checkForEnvironmentalDisaster(String runId, int hoursAlive, DormitoryModel dorm) {
        float oddsThatBurnedDown = (hoursAlive - dorm.getHourLastUpdated()) * PROBABILTY_OF_FLOOD;
        if (didItHappen(oddsThatBurnedDown)) {
            NewsFeedItemModel note = new NewsFeedItemModel();
            note.setHour(hoursAlive);
            note.setMessage("Dorm " + dorm.getName() + " has flooded. Students successfully evacuated.\n");
            note.setNoteType(NewsType.GENERAL_NOTE);
            NewsFeedDao noteDao = new NewsFeedDao();
            noteDao.saveNote(runId, note);
        }
    }

    private boolean didItHappen(float oddsBetween0And1
    ) {
        return (Math.random() < oddsBetween0And1);
    }
}
