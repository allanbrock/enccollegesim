package com.endicott.edu.simulators;


import com.endicott.edu.datalayer.PlagueDao;
import com.endicott.edu.models.PlagueModel;
import com.endicott.edu.models.NewsType;

import java.util.List;

/**
 * Created by abrocken on 7/29/2017.
 */
public class PlagueManager {
    PlagueDao dao = new PlagueDao();


    public void handleTimeChange(String runId, int hoursAlive) {
        List<PlagueModel> plagues = dao.getPlagues(runId);

        for (PlagueModel plague : plagues) {
            int timePassed = hoursAlive - plague.getHourLastUpdated();
            plague.setHourLastUpdated(hoursAlive);
            int hoursLeftInPlague = plague.getNumberOfHoursLeftInPlague() - timePassed;
            plague.setNumberOfHoursLeftInPlague(hoursLeftInPlague);
        }

        dao.saveAllPlagues(runId, plagues);
    }

    //look into this
    public void handleStudentsSick(int numOfStudentsSick, int hoursLeft){
        if(numOfStudentsSick > 5 && hoursLeft >=72){
            //do nothing
            numOfStudentsSick = numOfStudentsSick;
        }else if(numOfStudentsSick == 2 && hoursLeft <=0){
            numOfStudentsSick = 0;
            //announce students are not sick
        }else if(numOfStudentsSick <= 5 && hoursLeft < 71){
            numOfStudentsSick = numOfStudentsSick - 1;
        }

    }

    private boolean didItHappen(float oddsBetween0And1
    ) {
        return (Math.random() < oddsBetween0And1);
    }
}
