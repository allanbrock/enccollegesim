package com.endicott.edu.datalayer;

import com.endicott.edu.models.SportModel;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


// Created by Nick DosSantos on 10/2/17.

public class SportsDao {
    private String getFilePath(String runId) {
        return DaoUtils.getFilePathPrefix(runId) +  "sports.dat";
    }
    private Logger logger = Logger.getLogger("SportsDao");

    public List<SportModel> getSports(String runId) {
        ArrayList<SportModel> sports = new ArrayList<>();
        SportModel sportModel = null;
        try {
            File file = new File(getFilePath(runId));

            if (!file.exists()) {
                return sports;  // There are no sports yet.
            }
            else{
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                sports = (ArrayList<SportModel>) ois.readObject();
                ois.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return sports;
    }

    public void saveAllSports(String runId, List<SportModel> notes){
        logger.info("Saving all sport...");

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

        logger.info("Saved sports...");
    }

    public void saveNewSport(String runId, SportModel sport) {
        logger.info("Saving new sport...");
        List<SportModel> sports = getSports(runId);
        sport.setRunId(runId);
        sports.add(sport);
        saveAllSports(runId, sports);
    }

    public void deleteSports(String runId) {
        File file = new File(getFilePath(runId));
        file.delete();
    }

    public static void main(String[] args) {
        testNotes();
    }

    private static void testNotes() {
        final String runId = "testsport001";
        SportsDao dao = new SportsDao();
        SportModel m1 = new SportModel(18, 0, 20, 100, 0, 0, 10, 20, 200, 2, 0, "Soccer", runId,false , 48);
        SportModel m2 = new SportModel(20, 0, 30, 500, 0, 0, 10, 30, 1500, 3, 0, "Hockey", runId, false, 48 );
        ArrayList<SportModel> sports = new ArrayList<>();
        sports.add(m1);
        sports.add(m2);
        dao.saveAllSports(runId, sports);

        List<SportModel> outMsgs = dao.getSports(runId);

        assert(outMsgs.size() == 2);
        assert(outMsgs.get(1).getCapacity() == 100);

        SportModel m3 = new SportModel(10,0, 20, 100, 0, 0, 10, 20, 200, 2, 0, "Test Team", runId,false, 48  );
        dao.saveNewSport(runId, m3);
        outMsgs = dao.getSports(runId);
        assert(outMsgs.size() == 3);

        System.out.println("Test case name: testSports, Result: pass");

    }
    public ArrayList<String> seeAllSportNames(){
        ArrayList<String> sportNames = new ArrayList<>();

        sportNames.add("Men's Basketball");
        sportNames.add("Women's Basketball");
        sportNames.add("Women's Basketball");
        sportNames.add("Baseball");
        sportNames.add("Women's Basketball");
        sportNames.add("Softball");
        sportNames.add("Women's Soccer");
        sportNames.add("Women's Basketball");
        sportNames.add("Men's Soccer");
        return sportNames;
    }
}
