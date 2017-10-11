package com.endicott.edu.datalayer;

import com.endicott.edu.models.FloodModel;
import com.sun.scenario.effect.Flood;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


// Created by cseidl on 7/17/2017.

public class FloodDao {
    private String getFilePath(String runId) {
        return DaoUtils.getFilePathPrefix(runId) +  "flood.dat";
    }
    private Logger logger = Logger.getLogger("FloodDao");

    public List<FloodModel> getFloods(String runId) {
        ArrayList<FloodModel> floods = new ArrayList<>();
        FloodModel floodModel = null;
        try {
            File file = new File(getFilePath(runId));

            if (!file.exists()) {
                return floods;  // There are no floods yet.
            }
            else{
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                floods = (ArrayList<FloodModel>) ois.readObject();
                ois.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return floods;
    }

    public void saveAllFloods(String runId, List<FloodModel> notes){
        logger.info("Saving flood...");
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

        logger.info("Saved flood...");
    }

    public void saveNewFlood(String runId, FloodModel flood) {
        logger.info("Saving new flood...");
        List<FloodModel> floods = getFloods(runId);
        flood.setRunId(runId);
        floods.add(flood);
        saveAllFloods(runId, floods);
    }

    public void deleteFloods(String runId) {
        File file = new File(getFilePath(runId));
        file.delete();
    }

    public static void main(String[] args) {
        testNotes();
    }

    private static void testNotes() {
        final String runId = "testflood001";
        FloodDao dao = new FloodDao();
        FloodModel m1 = new FloodModel(100, 10, "Hampshire Hall", runId );
        FloodModel m2 = new FloodModel(200, 20,"Vermont House", runId);
        ArrayList<FloodModel> floods = new ArrayList<>();
        floods.add(m1);
        floods.add(m2);
        dao.saveAllFloods(runId, floods);

        List<FloodModel> outMsgs = dao.getFloods(runId);

        assert(outMsgs.size() == 2);

        FloodModel m3 = new FloodModel(300, 10,"Maine Manor", runId);
        dao.saveNewFlood(runId, m3);
        outMsgs = dao.getFloods(runId);
        assert(outMsgs.size() == 3);

        System.out.println("Test case name: testFloods, Result: pass");
    }
}
