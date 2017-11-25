package com.endicott.edu.simulators;


import com.endicott.edu.datalayer.PlagueDao;
import com.endicott.edu.datalayer.StudentDao;
import com.endicott.edu.models.PlagueModel;
import com.endicott.edu.models.NewsType;
import com.endicott.edu.models.StudentModel;

import java.util.List;
import java.util.Random;

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

//    private void updateStudentsTime(int hoursAlive){
//        StudentDao dao = new StudentDao();
//        List<StudentModel> students = dao.getStudents();
//        for(int i = 0; i < students.size(); i++){
//            students.get(i).setHourLastUpdated(hoursAlive);
//        }
//
//    }

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

    static public void establishCollege(String runId){
        // Create a plague
        // Make students sick.
        PlagueModel plague = new PlagueModel( 0, 0, "Hampshire Hall","none", 0, 0, 1000, 72, 0);
        PlagueDao plagueDao = new PlagueDao();
        plagueDao.saveNewPlague(runId, plague);
    }

    private static void makeStudentSick(StudentModel student, String runId, int currentDay) {
        Random rand = new Random();

        if(rand.nextInt(10) + 1 > 9){
            student.setNumberHoursLeftBeingSick(72);
            NewsManager.createNews(runId,currentDay, student.getName() + " is sick", NewsType.COLLEGE_NEWS);
        } else {
            student.setNumberHoursLeftBeingSick(0);
        }
    }

    private boolean didItHappen(float oddsBetween0And1) {
        return (Math.random() < oddsBetween0And1);
    }
}
