package com.endicott.edu.datalayer;

import com.endicott.edu.exceptions.DataNotFoundException;
import com.endicott.edu.models.CollegeModel;

import java.io.*;

/**
 * Created by abrocken on 7/17/2017.
 */
public class CollegeDao {
    private String getFilePath(String runId) {
        return DaoUtils.getFilePathPrefix(runId) +  "college.dat";
    }

    public CollegeModel getCollege(String runId) {
        CollegeModel college = null;
        try {
            college = new CollegeModel();
            college.setRunId(runId);

            File file = new File(getFilePath(runId));

            if (!file.exists()) {
                throw new DataNotFoundException("Couldn't find college for runId " + runId);
            }
            else{
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                college = (CollegeModel) ois.readObject();
                ois.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return college;
    }

    public void deleteCollege(String runId) {
        File file = new File(getFilePath(runId));
        System.out.println("About to delete file: " + getFilePath(runId));
        boolean result = file.delete();
        System.out.println("Result = " + result);
    }

    public void saveCollege(CollegeModel college){
        try {
            college.setNote(getFilePath(college.getRunId()));
            File file = new File(getFilePath(college.getRunId()));
            file.createNewFile();
            FileOutputStream fos;
            fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(college);
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        testCollegeDao();
    }

    private static void testCollegeDao() {
        String runId = "000";
        CollegeDao dao = new CollegeDao();
        CollegeModel college = new CollegeModel();
        college.setAvailableCash(100);
        college.setRunId(runId);
        dao.saveCollege(college);

        college = dao.getCollege(runId);
        assert(college.getAvailableCash() == 100);
        dao.deleteCollege(runId);
    }

}
