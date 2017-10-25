package com.endicott.edu.models;

import java.util.List;

// created by Cam Bleck 10/3/17
public class StudentsModel {
    private List<StudentModel> studentList;
    private int retentionRate;
    private int happinessLevel;
    private int graduationRate;

    public List<StudentModel> getStudentList() {return studentList; }

    public void setStudentList(List<StudentModel> studentList) { this.studentList = studentList; }

    public int getRetentionRate() {
        return retentionRate;
    }

    public void setRetentionRate(int retentionRate) {
        this.retentionRate = retentionRate;
    }

    public int getHappinessLevel() {
        return happinessLevel;
    }

    public void setHappinessLevel(int happinessLevel) {
        this.happinessLevel = happinessLevel;
    }

    public int getGraduationRate() {
        return graduationRate;
    }

    public void setGraduationRate(int graduationRate) {
        this.graduationRate = graduationRate;
    }

}
