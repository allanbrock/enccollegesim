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
    private List<StudentModel> listOfStudentsSick;


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
        makeMoreStudentsSick(runId, hoursAlive);
        extendStudentSickTime(runId, hoursAlive);
    }

    private void makeMoreStudentsSick(String runId, int currentDay) {
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
        int probabilityOfStudentsGettingSick = (studentSickCount/numOfStudents)*100;
        //use probability to make more students sick
        Random rand = new Random();
        int x = rand.nextInt(1) + 10;
        if(x == 7){
            makeStudentSick(runId, currentDay);
        }
//        if(probabilityOfStudentsGettingSick >= 50){
//            makeStudentSick(runId, currentDay);
//        } else if(probabilityOfStudentsGettingSick >= 30 && probabilityOfStudentsGettingSick < 50){
//            makeStudentSick(runId, currentDay);
//        } else if(probabilityOfStudentsGettingSick >= 0 && probabilityOfStudentsGettingSick <30){
//            makeStudentSick2(runId, currentDay);
//        }

        dao.saveAllStudents(runId, students);
    }

    private void extendStudentSickTime(String runId, int currentDay){
        StudentDao dao = new StudentDao();
        List<StudentModel> students = dao.getStudents(runId);

        Random rand = new Random();
        int x = rand.nextInt(1) + 6;

        for(int i = 0; i < students.size(); i++){
            StudentModel student = students.get(i);
            int sickTime = student.getNumberHoursLeftBeingSick();
            if(sickTime > 0) {
                if(x == 4){
                    student.setNumberHoursLeftBeingSick(sickTime + 24);
                    NewsManager.createNews(runId,currentDay, student.getName() + " is sicker", NewsType.COLLEGE_NEWS, NewsLevel.BAD_NEWS);

                }
            }
        }
        dao.saveAllStudents(runId, students);
    }

    private void displayNoLongerSick(int int1, int int2, String runId, int currentDay) {
        if(int1 == int2){
            //students are not sick
            NewsManager.createNews(runId,currentDay, "Students are no longer sick", NewsType.COLLEGE_NEWS, NewsLevel.GOOD_NEWS);
        }
    }

    private void makeStudentsBetter(String runId, int hoursAlive) {

        StudentDao dao = new StudentDao();
        List<StudentModel> students = dao.getStudents(runId);
        for(int i = 0; i < students.size(); i++){
            StudentModel student = students.get(i);
            if(students.get(i).getNumberHoursLeftBeingSick() > 0){
                int studentLastUpdated = students.get(i).getHourLastUpdated();
                int timeChange = hoursAlive - studentLastUpdated;
                int sickTime = students.get(i).getNumberHoursLeftBeingSick() - timeChange;
                sickTime = Math.max(0,sickTime);
                students.get(i).setNumberHoursLeftBeingSick(sickTime);
                //if their sicktime <= 0 then increase athletic ability
                //display students better here

            }
        }

        dao.saveAllStudents(runId, students);

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
    private static void makeStudentSick2(String runId, int currentDay) {
        Random rand = new Random();

        StudentDao dao = new StudentDao();
        List<StudentModel> students = dao.getStudents(runId);
        for(int i = 0; i < students.size(); i++){
            StudentModel student = students.get(i);
            if(rand.nextInt(13) + 1 > 9){
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
