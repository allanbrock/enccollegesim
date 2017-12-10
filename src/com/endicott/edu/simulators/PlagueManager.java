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

        int hoursLeftInPlague = 0;
        // This is a loop, but we only have one plague at most.
        for (PlagueModel plague : plagues) {
            int timePassed = hoursAlive - plague.getHourLastUpdated();
            plague.setHourLastUpdated(hoursAlive);
            hoursLeftInPlague = Math.max(0,plague.getNumberOfHoursLeftInPlague() - timePassed);
            plague.setNumberOfHoursLeftInPlague(hoursLeftInPlague);
        }

        makeStudentsBetter(runId, hoursAlive);
        extendStudentSickTime(runId, hoursAlive);

        if (hoursLeftInPlague > 0) {
            plagueSpreadsThroughStudents(runId, hoursAlive, hoursLeftInPlague, getNumberSick(runId));
        } else {
            // Decide if there should be a new plague.
            Random rand = new Random();
            if (rand.nextInt(10) >= 8) {
                refreshPlague(plagues);
            }
        }

        dao.saveAllPlagues(runId, plagues);
    }

    private void refreshPlague(List<PlagueModel> plagues) {
        Random rand = new Random();
        int plagueLengthInHours = rand.nextInt(72) + 72;

        for (PlagueModel plague : plagues) {
            plague.setNumberOfHoursLeftInPlague(plagueLengthInHours);
        }
    }

    private int getNumberSick(String runId) {
        StudentDao dao = new StudentDao();
        List<StudentModel> students = dao.getStudents(runId);
        int studentSickCount = 0;
        for(int i = 0; i < students.size(); i++){
            StudentModel student = students.get(i);
            if(students.get(i).getNumberHoursLeftBeingSick() > 0){
                studentSickCount++;
            }
        }

        return studentSickCount;
    }

    static public void establishCollege(String runId){
        int hoursInPlague = createPlaque(runId);
        plagueSpreadsThroughStudents(runId, 0, hoursInPlague, 0);
    }

    static private int createPlaque(String runId) {
        Random rand = new Random();
        int plagueLengthInHours = rand.nextInt(48) + 36;
        PlagueModel plague = new PlagueModel( 0, 0, "Hampshire Hall",
                "none", 0, 0, 1000, plagueLengthInHours, 0);
        PlagueDao plagueDao = new PlagueDao();
        plagueDao.saveNewPlague(runId, plague);
        return plagueLengthInHours;
    }

    private static void plagueSpreadsThroughStudents(String runId, int currentHour, int hoursLeftInPlague, int studentSickCount) {
        StudentDao dao = new StudentDao();
        List<StudentModel> students = dao.getStudents(runId);
        int nSick = 0;
        String someoneSick = "";

        if (students.size() <= 0) {
            return;
        }

        int probCatchesFromOthers = (studentSickCount * 100)/students.size();
        int probCatchesFromOutside = 10; // out of 100
        int totalProb = Math.min(100,probCatchesFromOthers + probCatchesFromOutside);
        Random rand = new Random();

        for(int i = 0; i < students.size(); i++){
            StudentModel student = students.get(i);
             if(rand.nextInt(100) <= totalProb){
                student.setNumberHoursLeftBeingSick(hoursLeftInPlague);
                nSick++;
                someoneSick = student.getName();
            } else {
                student.setNumberHoursLeftBeingSick(0);
            }
        }

        if (nSick == 1) {
            NewsManager.createNews(runId,currentHour, "Student " + someoneSick + " has fallen ill.", NewsType.COLLEGE_NEWS, NewsLevel.BAD_NEWS);
        } else if (nSick > 1) {
            NewsManager.createNews(runId,currentHour, "Student " + someoneSick + " and " + (nSick-1) + " others have fallen ill.", NewsType.COLLEGE_NEWS, NewsLevel.BAD_NEWS);
        }

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
}
