package com.endicott.edu.datalayer;

import com.endicott.edu.models.PlagueModel;
import com.endicott.edu.models.PlagueModel;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


// Created by dyannone on 10/10/2017.

public class PlagueDao {
    private static String getFilePath(String runId) {
        return DaoUtils.getFilePathPrefix(runId) +  "plague.dat";
    }
    private Logger logger = Logger.getLogger("PlagueDao");

    public List<PlagueModel> getPlagues(String runId) {
        ArrayList<PlagueModel> plagues = new ArrayList<>();
        PlagueModel plagueModel = null;
        try {
            File file = new File(getFilePath(runId));

            if (!file.exists()) {
                return plagues;  // There are no plagues yet.
            }
            else{
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                plagues = (ArrayList<PlagueModel>) ois.readObject();
                ois.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return plagues;
    }

    public void saveAllPlagues(String runId, List<PlagueModel> notes){
        logger.info("Saving plague...");
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

        logger.info("Saved plague...");
    }

    public void saveNewPlague(String runId, PlagueModel plague) {
        logger.info("Saving new plague...");
        List<PlagueModel> plagues = getPlagues(runId);
        plague.setRunId(runId);
        plagues.add(plague);
        saveAllPlagues(runId, plagues);
    }

    public static void deletePlagues(String runId) {
        File file = new File(getFilePath(runId));
        file.delete();
    }

    public static void main(String[] args) {
        testNotes();
    }

    private static void testNotes() {
        final String runId = "testplague001";
        PlagueDao dao = new PlagueDao();
        PlagueModel m1 = new PlagueModel(100, 10, "Hampshire Hall", runId, 5, 0, 1000, 72, 0 );
        PlagueModel m2 = new PlagueModel(200, 20,"Vermont House", runId, 5, 0, 1000, 72, 0);
        ArrayList<PlagueModel> plagues = new ArrayList<>();
        plagues.add(m1);
        plagues.add(m2);
        dao.saveAllPlagues(runId, plagues);

        List<PlagueModel> outMsgs = dao.getPlagues(runId);

        assert(outMsgs.size() == 2);

        PlagueModel m3 = new PlagueModel(300, 10,"Maine Manor", runId, 5, 0, 0, 72, 0);
        dao.saveNewPlague(runId, m3);
        outMsgs = dao.getPlagues(runId);
        assert(outMsgs.size() == 3);

        System.out.println("Test case name: tests, Result: pass");
    }
}
