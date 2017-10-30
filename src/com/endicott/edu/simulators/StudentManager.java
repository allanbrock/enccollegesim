package com.endicott.edu.simulators;
import com.endicott.edu.datalayer.CollegeDao;
import com.endicott.edu.datalayer.FacultyDao;
import com.endicott.edu.datalayer.StudentDao;
import com.endicott.edu.models.CollegeModel;
import com.endicott.edu.models.FacultyModel;
import com.endicott.edu.models.StudentModel;
import com.endicott.edu.models.StudentsModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StudentManager {
    StudentDao dao = new StudentDao();
    List<StudentModel> students;
    Random rand = new Random();
    DormManager dormManager = new DormManager();
    FacultyDao facultyDao = new FacultyDao();
    List<FacultyModel> faculty;

    public void handleTimeChange(String runId, int hoursAlive) {
        students = dao.getStudents(runId);
        addNewStudents(runId, hoursAlive);
        runningTuitionOfStudent(runId, hoursAlive);
        removeStudents(runId, hoursAlive);
        updateStudentsTime(hoursAlive, students);
        dao.saveAllStudents(runId, students);
        faculty = facultyDao.getFaculty(runId);
        //get students into student body
        StudentsModel studentBody = new StudentsModel();
        studentBody.setStudentList(students);

        //get college
        CollegeModel college = new CollegeModel();
        CollegeDao collegeDao = new CollegeDao();
        college = collegeDao.getCollege(runId);

        //calculate happiness and set it in the college
        college.setStudentBodyHappiness(calculateStudentsHappiness(studentBody, college, faculty));

        //save the college
        collegeDao.saveCollege(college);

    }

    private void runningTuitionOfStudent(String runId, int hoursAlive) {
        int dailyTuitionSum = (CollegeModel.getYearlyTuitionCost() / 365) * students.size();
        Accountant.studentIncome(runId, dailyTuitionSum);
        NewsManager.createNews(runId, hoursAlive, "Received $" + dailyTuitionSum + " from student tuition");
    }

    private void addNewStudents(String runId, int hoursAlive) {
        DormManager dormManager = new DormManager();
        int openBeds = dormManager.getOpenBeds(runId);
        int numNewStudents = rand.nextInt(openBeds);
        for (int i = 0; i < numNewStudents; i++) {
            StudentModel student = new StudentModel();
            student.setIdNumber(100000 + rand.nextInt(900000));
            student.setHappinessLevel(rand.nextInt(100));
            student.setAthlete(false);
            student.setAthleticAbility(rand.nextInt(100));
            student.setTeam("");
            student.setDorm("");
            if (rand.nextInt(1) == 1) {
                student.setGender("Male");
            } else {
                student.setGender("Female");
            }
            student.setRunId(runId);
            students.add(student);
        }
        dao.saveAllStudents(runId, students);
        NewsManager.createNews(runId, hoursAlive, Integer.toString(numNewStudents) + " students joined the college.");
    }

    private void removeStudents(String runId, int hoursAlive) {
        int currentSize = students.size();
        int numStudents = rand.nextInt(3);
        for (int i = 0; i < numStudents; i++) {
            students.remove(rand.nextInt(students.size()));
        }
        if ((currentSize - students.size()) > 0) {
            NewsManager.createNews(runId, hoursAlive, Integer.toString(currentSize - students.size()) + " students withdrew from college.");
        }
    }

    private int calculateStudentsHappiness(StudentsModel studentBody, CollegeModel college, List <FacultyModel> faculty) {
        //calculate affects of college stats on individual student happiness
        calculateCollegeAffect(studentBody, college.getReputation(), faculty.size(), college.getYearlyTuitionCost());

        int happinessSum = 0;
        students =  studentBody.getStudentList();
        int happinessLevel;
        for (int i = 0; i < students.size(); i++) {
            happinessSum += students.get(i).getHappinessLevel();
        }
        happinessLevel = happinessSum / students.size();

        return happinessLevel;

    }

    private void calculateCollegeAffect(StudentsModel studentBody, int reputation, int numberOfFaculty, int tuitionCost){
        students = studentBody.getStudentList();
        int reputationAffect = (reputation - 60)/10;
        int ratioAffect = -((students.size() / numberOfFaculty) - 13)/5;
        int tuitionAffect = -(tuitionCost - 40000)/1000;

        for(int i = 0; i < students.size(); i++){
            students.get(i).setHappinessLevel(students.get(i).getHappinessLevel() + reputationAffect + ratioAffect + tuitionAffect);
        }
    }

    private void updateStudentsTime(int hoursAlive, List<StudentModel> students){
        for(int i = 0; i < students.size(); i++){
            students.get(i).setHourLastUpdated(hoursAlive);
        }

    }







}