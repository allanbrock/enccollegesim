package com.endicott.edu.models;

import java.util.List;

// created by Cam Bleck 10/3/17
public class StudentsModel {
    static private List<StudentModel> studentList;
    static private int retentionRate;
    static private int happinessLevel;
    static private int graduationRate;

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


    public static void calculateStudentsStats(){
        int happinessSum = 0;
        for(int i = 0; i < studentList.size(); i++){
            happinessSum += studentList.get(i).getHappinessLevel();
        }
        happinessLevel = happinessSum/studentList.size();

        //need calculation for grad rate
        //  based off days alive and happiness + reputation


        //need calculation for retention rate
        //  based off happiness level


    }



}
