package com.endicott.edu.simulators;
import com.endicott.edu.datalayer.StudentDao;
import com.endicott.edu.models.StudentModel;
import java.util.List;
import java.util.Random;

public class StudentManager {
    StudentDao dao = new StudentDao();
    List<StudentModel> students;

    public void handleTimeChange(String runId, int hoursAlive) {
        students = dao.getStudents(runId);
        addNewStudents(runId, hoursAlive);
        runningTuitionOfStudent(runId, hoursAlive);
        dao.saveAllStudents(runId, students);
    }

    private void runningTuitionOfStudent(String runId, int hoursAlive) {
        int tuitionSum = StudentModel.getTuitionCost() * students.size();
        Accountant.studentIncome(runId, tuitionSum);
        NewsManager.createNews(runId, hoursAlive, "+$" + tuitionSum + " for student tuition");
    }

    private void addNewStudents(String runId, int hoursAlive) {

        Random rand = new Random();
        int numStudents = 2 + rand.nextInt(3);
        for (int i = 0; i < numStudents; i++) {
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
        NewsManager.createNews(runId, hoursAlive, Integer.toString(numStudents) + " new students have enrolled.");
    }
    

}