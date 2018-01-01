package com.endicott.edu.simulators;
import com.endicott.edu.datalayer.*;
import com.endicott.edu.models.*;

import java.util.List;
import java.util.Random;

/**
 * Responsible for simulating students at the college.
 */
public class StudentManager {
    private StudentDao dao = new StudentDao();
    private CollegeDao collegeDao = new CollegeDao();
    private FacultyDao facultyDao = new FacultyDao();
    private DormManager dormManager = new DormManager();
    private CollegeModel college = new CollegeModel();
    private Random rand = new Random();

    /**
     * The college has just been created.  Add initial students and calculate
     * student statistics.
     *
     * @param runId
     */
    public void establishCollege(String runId) {
        admitStudents(runId, college.getCurrentDay()/24, true);
        calculateStatistics(runId);
    }

    /**
     * Simulate the changes in students and stutdent finances
     * due to passage of time at the college.
     *
     * @param runId
     * @param hoursAlive
     */
    public  void handleTimeChange(String runId, int hoursAlive) {
        admitStudents(runId, hoursAlive, false);
        receiveStudentTuition(runId);
        withdrawStudents(runId, hoursAlive);
        calculateStatistics(runId);
        updateStudentsTime(runId, hoursAlive);
    }

    /**
     * Receive student tuition.
     *
     * @param runId
     */
    private void receiveStudentTuition(String runId) {
        List<StudentModel> students = dao.getStudents(runId);
        college = collegeDao.getCollege(runId);
        int dailyTuitionSum = (college.getYearlyTuitionCost() / 365) * students.size();
        Accountant.receiveIncome(runId,"Student tuition received.",dailyTuitionSum);
    }

    /**
     * Admit new students to the college.
     *
     * @param runId
     * @param hoursAlive
     * @param initial
     */
    public void admitStudents(String runId, int hoursAlive, boolean initial) {
        int openBeds = dormManager.getOpenBeds(runId);
        int numNewStudents;
        List<StudentModel> students = dao.getStudents(runId);

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

    /**
     * Withdraw students from the college.
     *
     * @param runId
     * @param hoursAlive
     */
    private void withdrawStudents(String runId, int hoursAlive) {
        float scalingFactor = .0001f;
        List<StudentModel> students = dao.getStudents(runId);
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

        dao.saveAllStudents(runId, students);

        college = collegeDao.getCollege(runId);
        college.setNumberStudentsWithdrew(college.getNumberStudentsWithdrew() + studentsWithdrawn);
        collegeDao.saveCollege(college);

        // Don't create a news story if no students leave
        if ((currentSize - students.size()) > 0) {
            NewsManager.createNews(runId, hoursAlive, Integer.toString(currentSize - students.size()) + " students withdrew from college.", NewsType.COLLEGE_NEWS, NewsLevel.BAD_NEWS);
        }
    }

    /**
     * Advance the last updated time on the students.
     *
     * @param runId
     * @param hoursAlive
     */
    private void updateStudentsTime(String runId, int hoursAlive){
        List<StudentModel> students = dao.getStudents(runId);

        for(int i = 0; i < students.size(); i++){
            students.get(i).setHourLastUpdated(hoursAlive);
        }

        dao.saveAllStudents(runId, students);
    }

    /**
     * Recalculate all the statistics that are being maintained involving students.
     * @param runId
     */
    public void calculateStatistics(String runId) {
        calculateStudentHealthRating(runId);
        calculateStudentsHappiness(runId);
        calculateStudentFacultyRatio(runId);
        calculateStudentFacultyRatioRating(runId);
        calculateRetentionRate(runId);
    }

    private void calculateStudentHealthRating(String runId) {
        CollegeModel college = collegeDao.getCollege(runId);
        List<StudentModel> students = dao.getStudents(runId);

        int nSick = 0;

        for(int i = 0; i < students.size(); i++){
            if(students.get(i).getNumberHoursLeftBeingSick() > 0){
                nSick++;
            }
        }

        int percentageWell = 100 - (nSick * 100) / Math.max(students.size(), 1);
        int rating = SimulatorUtilities.getRatingZeroToOneHundred(50, 95, percentageWell);
        college.setStudentHealthRating(rating);

        collegeDao.saveCollege(college);
    }

    private void calculateStudentsHappiness(String runId) {
        CollegeModel college = collegeDao.getCollege(runId);
        List<StudentModel> students = dao.getStudents(runId);
        List<FacultyModel> faculty = facultyDao.getFaculty(runId);

        calculateIndividualStudentHappiness(college.getRunId(), college.getReputation(), faculty.size(), college.getYearlyTuitionCost());

        int happinessSum = 0;
        for (int i = 0; i < students.size(); i++) {
            happinessSum += students.get(i).getHappinessLevel();
        }

        int aveHappiness = Math.max(0,happinessSum / students.size());

        college.setStudentBodyHappiness(aveHappiness);
        collegeDao.saveCollege(college);
    }

    private void calculateIndividualStudentHappiness(String runId, int reputation, int numberOfFaculty, int tuitionCost){
        List<StudentModel> students = dao.getStudents(runId);
        CollegeModel college = collegeDao.getCollege(runId);

        // We're not using reputation at the moment because it's
        // hardcoded at 50%.  That leaves: one third for faculty, one third for tuition, one third for health.

        for(int i = 0; i < students.size(); i++){
            int healthRating = 100;
            if (students.get(i).getNumberHoursLeftBeingSick() > 0){
                healthRating = 0;
            }

            int happiness = college.getStudentFacultyRatioRating()/3 +
                            college.getYearlyTuitionRating()/3 +
                            healthRating/3;
            happiness = Math.min(happiness, 100);
            happiness = Math.max(happiness, 0);

            students.get(i).setHappinessLevel(happiness);
        }

        dao.saveAllStudents(runId, students);
    }

    private void calculateStudentFacultyRatio(String runId) {
        CollegeModel college = collegeDao.getCollege(runId);
        List<StudentModel> students = dao.getStudents(college.getRunId());
        List<FacultyModel> faculty = facultyDao.getFaculty(college.getRunId());

        college.setStudentFacultyRatio(students.size() / Math.max(faculty.size(),1));

        collegeDao.saveCollege(college);
    }

    private void calculateStudentFacultyRatioRating(String runId) {
        CollegeModel college = collegeDao.getCollege(runId);
        int rating = SimulatorUtilities.getRatingZeroToOneHundred(20, 5, college.getStudentFacultyRatio());
        college.setStudentFacultyRatioRating(rating);
        collegeDao.saveCollege(college);
    }

    /**
     * Calculate retention rate of students.  This is based on how many students
     * withdrew compare to students that remained.
     *
     * @param runId
     */
    private void calculateRetentionRate(String runId) {
        college = collegeDao.getCollege(runId);
        int retentionRate = 100;
        if (college.getNumberStudentsAdmitted() > 0) {
            retentionRate =
                    Math.max(((college.getNumberStudentsAdmitted() - college.getNumberStudentsWithdrew()) * 100)/
                            college.getNumberStudentsAdmitted(), 0);
        }
        college.setRetentionRate(retentionRate);

        collegeDao.saveCollege(college);
    }

    private boolean didItHappen(float oddsBetween0And1) {
        return (Math.random() < oddsBetween0And1);
    }

}











