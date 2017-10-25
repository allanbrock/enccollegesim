package com.endicott.edu.simulators;
import com.endicott.edu.datalayer.CollegeDao;
import com.endicott.edu.datalayer.StudentDao;
import com.endicott.edu.models.CollegeModel;
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

    public void handleTimeChange(String runId, int hoursAlive) {
        students = dao.getStudents(runId);
        addNewStudents(runId, hoursAlive);
        runningTuitionOfStudent(runId, hoursAlive);
        removeStudents(runId, hoursAlive);
        dao.saveAllStudents(runId, students);

        //get students into student body
        StudentsModel studentBody = new StudentsModel();
        List<StudentModel> studentList = dao.getStudents(runId);
        studentBody.setStudentList(studentList);

        //get college
        CollegeModel college = new CollegeModel();
        CollegeDao collegeDao = new CollegeDao();
        college = collegeDao.getCollege(runId);

        //calculate happiness and set it in the college
        college.setStudentBodyHappiness(calculateStudentsStats(studentBody));

        //save the college
        collegeDao.saveCollege(college);

    }

    private void runningTuitionOfStudent(String runId, int hoursAlive) {
        int tuitionSum = StudentModel.getTuitionCost() * students.size();
        Accountant.studentIncome(runId, tuitionSum);
        NewsManager.createNews(runId, hoursAlive, "Received $" + tuitionSum + " from student tuition");
    }

    private void addNewStudents(String runId, int hoursAlive) {
        int numNewStudents = rand.nextInt(3);
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
            student.setSick(false);
            student.setRunId(runId);
            students.add(student);
        }
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

    public int calculateStudentsStats(StudentsModel studentBody) {
        int happinessSum = 0;
        students =  studentBody.getStudentList();
        int happinessLevel;
        for (int i = 0; i < students.size() ; i++) {
            happinessSum += students.get(i).getHappinessLevel();
        }
        happinessLevel = happinessSum / students.size();

        return happinessLevel;

    }

}