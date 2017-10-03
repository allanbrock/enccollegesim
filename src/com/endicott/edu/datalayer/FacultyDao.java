package com.endicott.edu.datalayer;

import com.endicott.edu.models.FacultyModel;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Mazlin Higbee 10/02/17
 * mhigb411@mail.endicott.edu
 * This class is intended to handle to data access for faculty objects
 *
 */
public class FacultyDao {
    private String getFilePath(String runId) {
        return DaoUtils.getFilePathPrefix(runId) +  "faculty.dat";
    }

    private Logger logger = Logger.getLogger("FacultyDao");

    /**
     * This function returns a list of all the faculty for a college
     * The college is defined by its runId
     * @param runId
     * @return ArrayList<FacultyModel> faculty
     */
    public List<FacultyModel> getFaculty(String runId) {
        ArrayList<FacultyModel> faculty = new ArrayList<>();
        FacultyModel facultyModel = null; //not sure why this is defined and not used.....
        try {
            File file = new File(getFilePath(runId));

            if (!file.exists()) {
                return faculty;  // No faculty exist
            }
            else{ //faculty exist lets return the objects....
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                faculty = (ArrayList<FacultyModel>) ois.readObject();
                ois.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return faculty;
    }


    /**
     * This creates a new faculty member and then saves them to the master list
     * @param runId
     * @param member
     */
    public void saveNewFaculty(String runId, FacultyModel member) {
        logger.info("Saving new dorm...");
        List<FacultyModel> faculty = getFaculty(runId);
        member.setRunId(runId);
        faculty.add(member);
        saveAllFaculty(runId, faculty);
    }

    /**
     * This function writes a list of faculty objects to the disk...
     * @param runId
     * @param faculty
     */
    private void saveAllFaculty(String runId, List<FacultyModel> faculty) {
        logger.info("Saving all dorm...");
        try {
            File file = new File(getFilePath(runId));
            file.createNewFile();
            FileOutputStream fos;
            fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(faculty);
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

    /**
     * Deletes the faculty file
     * @param runId
     */
    public void removeAllFaculty(String runId){
        File file = new File(getFilePath(runId));
        file.delete();
    }






    public static void main(String[] args) {
        testNotes();
    }

    private static void testNotes(){
        final String runId = "testFaculty01";
        FacultyDao fao = new FacultyDao();
        List<FacultyModel> faculty = new ArrayList<>();

        FacultyModel f1 = new FacultyModel("Dr. Test","Title","Comp","LSB",runId);
        FacultyModel f2 = new FacultyModel("Dr. Test2","LesserTitle","Programming",125000,"LSB",runId);

        faculty.add(f1);
        faculty.add(f2);

        fao.saveAllFaculty(runId,faculty);

        List<FacultyModel> outMsgs = fao.getFaculty(runId);

        assert(outMsgs.size() == 2);
        assert (outMsgs.get(0).getFacultyName().equals("Dr. Test"));

        FacultyModel f3 = new FacultyModel("Dr. Test23","LesserTitle","Programming",125000,"LSB",runId);

        fao.saveNewFaculty(runId,f3);
        outMsgs = fao.getFaculty(runId);

        assert(outMsgs.size() == 3);
        System.out.println("Adding a new Faculty member...");

        faculty.clear();
        System.out.println("Clearing list... ");

        System.out.println("Loading in faculty from list");
        faculty = fao.getFaculty(runId);


        assert (faculty.size() == 3);
        assert (faculty.get(2).getFacultyName().equals("Dr. Test23"));
        System.out.println("Lets remove all the faculty.... ");
        fao.removeAllFaculty(runId);

        File file = new File(fao.getFilePath(runId));
        assert (!file.exists());

        System.out.println("End of testing faculty");
    }




}
