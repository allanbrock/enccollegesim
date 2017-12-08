package com.endicott.edu.simulators;


import com.endicott.edu.datalayer.PlagueDao;
import com.endicott.edu.datalayer.StudentDao;
import com.endicott.edu.models.NewsLevel;
import com.endicott.edu.models.PlagueModel;
import com.endicott.edu.models.NewsType;
import com.endicott.edu.models.StudentModel;

import java.util.List;
import java.util.Random;

/**
 * Created by dyannone on 7/29/2017.
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
        makeStudentsBetter(runId, hoursAlive);
        checkAndDisplayStudentsSicknessStatus(runId, hoursAlive);
    }

    private void checkAndDisplayStudentsSicknessStatus(String runId, int currentDay) {
        StudentDao dao = new StudentDao();
        List<StudentModel> students = dao.getStudents(runId);
        int studentGoodCount = 0;
        int studentSickCount = 0;
        for(int i = 0; i < students.size(); i++){
            StudentModel student = students.get(i);
            if(students.get(i).getNumberHoursLeftBeingSick() == 0){
                studentGoodCount++;
            } else {
                studentSickCount++;
            }
        }
        int numOfStudents = students.size();
        if(studentGoodCount == numOfStudents){
            //students are not sick
            NewsManager.createNews(runId,currentDay, "Students are no longer sick", NewsType.COLLEGE_NEWS, NewsLevel.GOOD_NEWS);
        }

        dao.saveAllStudents(runId, students);
    }

    private void makeStudentsBetter(String runId, int hoursAlive) {

        StudentDao dao = new StudentDao();
        List<StudentModel> students = dao.getStudents(runId);
        for(int i = 0; i < students.size(); i++){
            StudentModel student = students.get(i);
            int happiness = student.getHappinessLevel();
            if(students.get(i).getNumberHoursLeftBeingSick() > 0){
                student.setHappinessLevel(happiness - 10);
                int studentLastUpdated = students.get(i).getHourLastUpdated();
                int timeChange = hoursAlive - studentLastUpdated;
                int sickTime = students.get(i).getNumberHoursLeftBeingSick() - timeChange;
                sickTime = Math.max(0,sickTime);
                students.get(i).setNumberHoursLeftBeingSick(sickTime);
            }
        }

        dao.saveAllStudents(runId, students);

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
        makeStudentSick(runId, 0);

    }

    private static void makeStudentSick(String runId, int currentDay) {
        Random rand = new Random();

        StudentDao dao = new StudentDao();
        List<StudentModel> students = dao.getStudents(runId);
        for(int i = 0; i < students.size(); i++){
            StudentModel student = students.get(i);
            if(rand.nextInt(10) + 1 > 9){
                student.setNumberHoursLeftBeingSick(72);
                NewsManager.createNews(runId,currentDay, student.getName() + " is sick", NewsType.COLLEGE_NEWS, NewsLevel.BAD_NEWS);
            } else {
                student.setNumberHoursLeftBeingSick(0);
            }
        }

        dao.saveAllStudents(runId, students);

    }

    private boolean didItHappen(float oddsBetween0And1) {
        return (Math.random() < oddsBetween0And1);
    }
}
