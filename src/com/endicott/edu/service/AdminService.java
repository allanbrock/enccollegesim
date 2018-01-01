package com.endicott.edu.service;

import com.endicott.edu.datalayer.CollegeDao;
import com.endicott.edu.models.CollegeModel;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by Mazlin Higbee
 * 10-20-2017
 * mhigb411@mail.endicott.edu
 */
@Path("/admin")
public class AdminService {

    /**
     * Retrieve all colleges that exist
     * @return ALL colleges that exist
     */
    @GET
    @Path("/getColleges")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<CollegeModel> getColleges() {
        ArrayList<CollegeModel> list = new ArrayList<>(); //list to contain all the colleges
        CollegeDao collegeDao = new CollegeDao();
        String collegeDir = System.getenv("SystemDrive")+ File.separator +"collegesim"; //we must know where they are stored
        File folder = new File(collegeDir);
        File[] listOfFiles = folder.listFiles();//list of all files in directory

        for(int i = 0; i < listOfFiles.length; i++){
            //if the name of the file ends in college.dat we know it contains a college object
            if(listOfFiles[i].getName().endsWith("college.dat")){
                //get the name and chop off the file ending to get the runID
                String tmp = listOfFiles[i].getName();
                tmp = tmp.substring(0,tmp.length() - 11);
                list.add(collegeDao.getCollege(tmp)); //grab the college with the runId we just discovered and add it to the list
            }
        }
        return list;
    }
}
