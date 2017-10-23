package com.endicott.edu.simulators;
import com.endicott.edu.datalayer.StudentDao;
import com.endicott.edu.models.StudentModel;
import java.util.List;
import java.util.Random;

public class StudentManager {
    StudentDao dao = new StudentDao();
    List<StudentModel> students;
    Random rand = new Random();

    public void handleTimeChange(String runId, int hoursAlive) {
        students = dao.getStudents(runId);
        addNewStudents(runId, hoursAlive);
        runningTuitionOfStudent(runId, hoursAlive);
        removeStudents(runId, hoursAlive);
        dao.saveAllStudents(runId, students);
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
        NewsManager.createNews(runId, hoursAlive,  Integer.toString(numNewStudents) + " students joined the college.");
    }

    private void removeStudents(String runId, int hoursAlive) {
        int currentSize = students.size();
        int numStudents = rand.nextInt(3);
        for(int i = 0; i < numStudents; i++){
            students.remove(rand.nextInt(students.size()));
        }
        if ((currentSize - students.size()) > 0){
            NewsManager.createNews(runId, hoursAlive, Integer.toString(currentSize - students.size()) + " students withdrew from college.");
        }
    }


}