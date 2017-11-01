package com.endicott.edu.datalayer;


import java.io.*;
import java.util.Scanner;
import java.util.logging.Logger;
import com.google.gson.Gson;


/**
 * Created by Mazlin Higbee
 * 10-12-17
 * mhigb411@mail.endicott.edu
 *
 * This class will store and retreive a the last used ID number.
 * Starting at 1 it will be incremented when needed.
 * All ID's will be Unique as this object will be used to create
 * any id that is needed in the given college.
 */
public class IdNumberGenDao {
    //glorified struct for gson
    private static class ID {
        int id;

        public ID(int id) {
            this.id = id;
        }
    }
    private static Logger logger = Logger.getLogger("IdNumberGenDao");
    private static String getFilePath(String runId) {
        return DaoUtils.getFilePathPrefix(runId) +  "IdGen.json";
    }

    /**
     * Creates the first id and writes it to the disk
     * @param runId College instance id
     * @return unique id#
     */
    public static int firstWrite(String runId){
        ID id = new ID(1000);
        Gson gson = new Gson();
        FileWriter fw;
        try {
            fw = new FileWriter(getFilePath(runId));
            fw.write(gson.toJson(id));
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return id.id;
    }
    /**
     * Lookup the most recently used ID number
     * return an id increased by one and update the record
     *
     * @param runId college instance ID
     * @return unique id
     */
    public static int getID(String runId){
       // logger.info("Fetching ID...");
        Gson gson = new Gson();
        ID id = new ID(-1);
        try  {
            BufferedReader br = new BufferedReader(new FileReader(getFilePath(runId)));
            id = gson.fromJson(br,ID.class);
           // ID tmpId = new ID(id.id);
            id.id++;
            FileWriter fw = new FileWriter(getFilePath(runId));
            fw.write(gson.toJson(id));
            fw.close();

        } catch (FileNotFoundException e) {
            return firstWrite(runId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return id.id; //return the original
    }


    public void deleteIDs(String runId) {
        File file = new File(getFilePath(runId));
        file.delete();
    }

    public static void main(String[] args) {
        final String runId = "testIdGen2";
        IdNumberGenDao idGen = new IdNumberGenDao();
        idGen.deleteIDs(runId);
        if (getID(runId) == 0) {
            System.out.println("PASS");
        } else {
            System.out.println("FAIL");
        }
        if (getID(runId) == 1) {
            System.out.println("PASS");
        } else {
            System.out.println("FAIL");
        }
    }

}
