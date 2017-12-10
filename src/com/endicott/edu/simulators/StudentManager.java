package com.endicott.edu.simulators;
import com.endicott.edu.datalayer.*;
import com.endicott.edu.models.*;

import java.util.List;
import java.util.Random;

public class StudentManager {
    private StudentDao dao = new StudentDao();
    private CollegeDao collegeDao = new CollegeDao();
    private FacultyDao facultyDao = new FacultyDao();
    private DormManager dormManager = new DormManager();
    private CollegeModel college = new CollegeModel();
    private List<StudentModel> students;
    private List<FacultyModel> faculty;
    private Random rand = new Random();

    public void establishCollege(String runId) {
        addNewStudents(runId, college.getCurrentDay()/24, true);
        recalculateStudentStatistics(runId);
    }

    public  void handleTimeChange(String runId, int hoursAlive) {
        students = dao.getStudents(runId);
        addNewStudents(runId, hoursAlive, false);
        runningTuitionOfStudent(runId);
        removeStudents(runId, hoursAlive);
        updateStudentsTime(hoursAlive);
        dao.saveAllStudents(runId, students);

        recalculateStudentStatistics(runId);
    }

    public void recalculateStudentStatistics(String runId) {
        faculty = facultyDao.getFaculty(runId);
        college = collegeDao.getCollege(runId);
        college.setStudentBodyHappiness(calculateStudentsHappiness(college, faculty));
        college.setStudentFacultyRatio(updateStudentFacultyRatio());
        college.setCollegeScore(calculateCollegeScore());

        int retentionRate = 100;
        if (college.getNumberStudentsAdmitted() > 0) {
            retentionRate =
                    Math.max(((college.getNumberStudentsAdmitted() - college.getNumberStudentsWithdrew()) * 100)/
                            college.getNumberStudentsAdmitted(), 0);
        }
        college.setRetentionRate(retentionRate);
        collegeDao.saveCollege(college);
    }

    private void runningTuitionOfStudent(String runId) {
        college = collegeDao.getCollege(runId);
        int dailyTuitionSum = (college.getYearlyTuitionCost() / 365) * students.size();
        Accountant.studentIncome(runId,"Student tuition received.",dailyTuitionSum);
    }

    public void addNewStudents(String runId, int hoursAlive, boolean initial) {
        int openBeds = dormManager.getOpenBeds(runId);
        int numNewStudents;
        students = dao.getStudents(runId);

        // Are we fully booked?
        if (openBeds <= 0) {
            return;
        }

        numNewStudents = rand.nextInt(openBeds);

        for (int i = 0; i < numNewStudents; i++) {
            StudentModel student = new StudentModel();
            if(rand.nextInt(10) + 1 > 5){
                student.setName(NameGenDao.generateName(false));
                student.setGender("Male");
            } else {
                student.setName(NameGenDao.generateName(true));
                student.setGender("Female");
            }
            student.setIdNumber(IdNumberGenDao.getID(runId));
            student.setHappinessLevel(70);
            student.setAthleticAbility(rand.nextInt(10));
            if(student.getAthleticAbility() > 6) {
                student.setAthlete(true);
            }
            else {
                student.setAthlete(false);
            }
            student.setTeam("");
            student.setDorm(dormManager.assignDorm(runId));
            student.setRunId(runId);
            students.add(student);
            dao.saveAllStudents(runId, students);
        }

        college = collegeDao.getCollege(runId);
        college.setNumberStudentsAdmitted(college.getNumberStudentsAdmitted() + numNewStudents);
        collegeDao.saveCollege(college);

        if (numNewStudents > 0) {
            NewsManager.createNews(runId, hoursAlive, Integer.toString(numNewStudents) + " students joined the college.", NewsType.COLLEGE_NEWS, NewsLevel.GOOD_NEWS);
        }
    }

    private void removeStudents(String runId, int hoursAlive) {
        float scalingFactor = .0001f;
        int currentSize = students.size();

        // scroll through students list and remove student based upon a probability determined by their happiness level
        // the lower the happiness the greater the chance they have of withdrawing
        int studentsWithdrawn = 0;

        for (int i = 0; i < students.size(); i++){
            int h = students.get(i).getHappinessLevel();
            float odds = (100f - h) * scalingFactor;
            if (didItHappen(odds)) {
                dormManager.removeStudent(runId, students.get(i).getDorm());
                students.remove(i);
                studentsWithdrawn++;
            }
        }

        college = collegeDao.getCollege(runId);
        college.setNumberStudentsWithdrew(college.getNumberStudentsWithdrew() + studentsWithdrawn);
        collegeDao.saveCollege(college);

        // Don't create a news story if no students leave
        if ((currentSize - students.size()) > 0) {
            NewsManager.createNews(runId, hoursAlive, Integer.toString(currentSize - students.size()) + " students withdrew from college.", NewsType.COLLEGE_NEWS, NewsLevel.BAD_NEWS);
        }
    }

    private boolean didItHappen(float oddsBetween0And1) {
        return (Math.random() < oddsBetween0And1);
    }

    private int calculateStudentsHappiness(CollegeModel college, List <FacultyModel> faculty) {
        //calculate affects of college stats on individual student happiness
        calculateCollegeAffect(college.getReputation(), faculty.size(), college.getYearlyTuitionCost());

        int happinessSum = 0;
        int happinessLevel;
        for (int i = 0; i < students.size(); i++) {
            happinessSum += students.get(i).getHappinessLevel();
        }

        // Make sure happiness is never less than 0.
        happinessLevel = Math.max(0,happinessSum / students.size());

        return happinessLevel;
    }

    private void calculateCollegeAffect(int reputation, int numberOfFaculty, int tuitionCost){
        //dividing values below are for scaling
        int reputationAffect = (reputation - 60)/10;
        int ratioAffect = -((students.size() / numberOfFaculty) - 13)/5;
        int tuitionAffect = -(tuitionCost - 40000)/1000;
        int sicknessAffect = 0;

        for(int i = 0; i < students.size(); i++){

            if(students.get(i).getNumberHoursLeftBeingSick() > 0){
                sicknessAffect = (int) -(0.5 * students.get(i).getHappinessLevel());
            }

            students.get(i).setHappinessLevel(
                    Math.min(100,students.get(i).getHappinessLevel() + reputationAffect + ratioAffect + tuitionAffect + sicknessAffect));
        }
    }

    private void updateStudentsTime(int hoursAlive){
        for(int i = 0; i < students.size(); i++){
            students.get(i).setHourLastUpdated(hoursAlive);
        }

    }

    private int updateStudentFacultyRatio() {
        return students.size() / faculty.size();
    }

    private float calculateCollegeScore(){
        float collegeScore = college.getStudentBodyHappiness(); // temporary college score rating
        return collegeScore;
    }
}











