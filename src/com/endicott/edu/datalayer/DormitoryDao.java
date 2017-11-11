package com.endicott.edu.datalayer;

import com.endicott.edu.models.DormitoryModel;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


// Created by abrocken on 7/17/2017.

public class DormitoryDao {
    private static String getFilePath(String runId) {
        return DaoUtils.getFilePathPrefix(runId) +  "dormitory.dat";
    }
    private Logger logger = Logger.getLogger("DormitoryDao");

    public List<DormitoryModel> getDorms(String runId) {
        ArrayList<DormitoryModel> dorms = new ArrayList<>();
        DormitoryModel dormModel = null;
        try {
            File file = new File(getFilePath(runId));

            if (!file.exists()) {
                return dorms;  // There are no dorms yet.
            }
            else{
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                dorms = (ArrayList<DormitoryModel>) ois.readObject();
                ois.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return dorms;
    }

    public void saveAllDorms(String runId, List<DormitoryModel> notes){
        logger.info("Saving all dorm...");
        try {
            File file = new File(getFilePath(runId));
            file.createNewFile();
            FileOutputStream fos;
            fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(notes);
            oos.close();
        } catch (FileNotFoundException e) {
            logger.info("Got file not found when attempting to create: " + getFilePath(runId));
            e.printStackTrace();
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            logger.info("Got io exceptionfound when attempting to create: " + getFilePath(runId));
            e.printStackTrace();
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        logger.info("Saved dorms...");
    }

    public void saveNewDorm(String runId, DormitoryModel dorm) {
        logger.info("Saving new dorm...");
        List<DormitoryModel> dorms = getDorms(runId);
        dorm.setRunId(runId);
        dorms.add(dorm);
        saveAllDorms(runId, dorms);

    }

    public static void deleteDorms(String runId) {
        File file = new File(getFilePath(runId));
        file.delete();
    }

    public static void main(String[] args) {
        testNotes();
    }


    private static void testNotes() {
        final String runId = "testdorm001";
        DormitoryDao dao = new DormitoryDao();

        DormitoryModel m1 = new DormitoryModel(100, 10, "Hampshire Hall", 50,
                "none", 3, "none", 50);
        DormitoryModel m2 = new DormitoryModel(200, 10, "Vermont House", 70,
                "none", 7, "none", 100);
        ArrayList<DormitoryModel> dorms = new ArrayList<>();
        dorms.add(m1);
        dorms.add(m2);
        dao.saveAllDorms(runId, dorms);

        List<DormitoryModel> outMsgs = dao.getDorms(runId);

        assert(outMsgs.size() == 2);
        assert(outMsgs.get(1).getCapacity() == 100);

        DormitoryModel m3 = new DormitoryModel(300, 10, "Maine Manor", 250,
                "flood", 8, "none",100);
        dao.saveNewDorm(runId, m3);
        outMsgs = dao.getDorms(runId);
        assert(outMsgs.size() == 3);

        System.out.println("Test case name: testDorms, Result: pass");
    }
}
