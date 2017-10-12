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
            logger.warning("IO exception in retrieving faculty.. ");
            e.printStackTrace();
        }

        return faculty;
    }


    /**
     * This creates a new faculty member and then saves them to the master list
     * After assigning them an ID
     * THIS NEEDS TO BE USED TO CREATE NEW MEMBERS
     * todo: change how we assign ID
     * @param runId sim id
     * @param member faculty object
     */
    public void saveNewFaculty(String runId, FacultyModel member) {
        logger.info("Saving new faculty...");
        List<FacultyModel> faculty = getFaculty(runId);
        member.setRunId(runId);
        //sets the faculty id number to the number of faculty in the list +1
        if(member.getFacultyID() ==  -1){
            member.setFacultyID(numberOfFaculty(runId) + 1);
        }
        logger.info("Creating faculty with ID: " + member.getFacultyID());
        faculty.add(member);

        saveAllFaculty(runId, faculty);
    }

    /**
     * This function writes a list of faculty objects to the disk...
     * @param runId
     * @param faculty
     */
    private void saveAllFaculty(String runId, List<FacultyModel> faculty) {
        logger.info("Saving all faculty...");
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

        logger.info("Saved faculty...");

    }

    /**
     * Deletes the faculty file
     * @param runId
     */
    public void removeAllFaculty(String runId){
        File file = new File(getFilePath(runId));
        logger.info("Removing all faculty from: " + getFilePath(runId));
        file.delete();
    }

    public void removeSingleFaculty(String runId,FacultyModel member){
        logger.info("Removing faculty member..");
        String tmp = member.getFacultyName();

        List<FacultyModel> faculty = getFaculty(runId);
        for( int i = 0; i < faculty.size(); i++){
            if(member.getFacultyID() == faculty.get(i).getFacultyID()){
                logger.info("removing " + faculty.get(i).getFacultyName());
                faculty.remove(i);
            }

        }
        saveAllFaculty(runId,faculty);

        logger.info("Faculty member removed: " + tmp);


    }

    /**
     * returns the number of faculty in the list for the college
     * @param runId
     * @return
     */
    public int numberOfFaculty(String runId){
        return getFaculty(runId).size();

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
        assert(fao.numberOfFaculty(runId) == 2);
        System.out.println(fao.numberOfFaculty(runId));

        List<FacultyModel> outMsgs = fao.getFaculty(runId);

        assert(outMsgs.size() == 2);
        assert (outMsgs.get(0).getFacultyName().equals("Dr. Test"));

        FacultyModel f3 = new FacultyModel("Dr. Test23","LesserTitle","Programming",125000,"LSB",runId);

        fao.saveNewFaculty(runId,f3);
        outMsgs = fao.getFaculty(runId);
        System.out.println("Adding a new Faculty member..." + "ID: " + outMsgs.get(2).getFacultyID());
        assert(outMsgs.size() == 3);

        System.out.println("Removing object:  " + f3.getFacultyName() + "ID: " + f3.getFacultyID());
        fao.removeSingleFaculty(runId,f3);
        outMsgs = fao.getFaculty(runId);
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

        System.out.println("End of testing faculty successful.");
    }




}
