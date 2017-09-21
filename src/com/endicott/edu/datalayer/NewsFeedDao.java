package com.endicott.edu.datalayer;

import com.endicott.edu.models.NewsFeedItemModel;
import com.endicott.edu.models.NewsType;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


// Created by abrocken on 7/17/2017.

public class NewsFeedDao {
    private Logger logger = Logger.getLogger("NewFeedDao");
    private String getFilePath(String runId) {
        return DaoUtils.getFilePathPrefix(runId) +  "newsfeed.dat";
    }

    public List<NewsFeedItemModel> getAllNotes(String runId) {
        ArrayList<NewsFeedItemModel> notes = new ArrayList<>();
        NewsFeedItemModel noteModel = null;
        try {
            File file = new File(getFilePath(runId));

            if (!file.exists()) {
                return notes;
            }
            else{
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                notes = (ArrayList<NewsFeedItemModel>) ois.readObject();
                ois.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return notes;
    }

    public List<NewsFeedItemModel> getNotes(String runId, int dayNumber, NewsType noteType) {
        List<NewsFeedItemModel> noteModels = new ArrayList<>();
        noteModels = getAllNotes(runId);

        ArrayList<NewsFeedItemModel> outNotes = new ArrayList<>();
        for (NewsFeedItemModel entry : noteModels) {
            if (entry.getNoteType() == noteType &&
                    entry.getHour() == dayNumber) {
                outNotes.add(entry);
            }
        }

        return outNotes;
    }

    private void saveAllNotes(String runId, List<NewsFeedItemModel> notes){
        try {
            File file = new File(getFilePath(runId));
            file.createNewFile();
            FileOutputStream fos;
            fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(notes);
            oos.close();
        } catch (IOException e) {
            logger.info("Cannot create file: " + getFilePath(runId));
            e.printStackTrace();
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    public void saveNote(String runId, NewsFeedItemModel note) {
        List<NewsFeedItemModel> allNotes = getAllNotes(runId);
        note.setRunId(runId);
        allNotes.add(note);
        saveAllNotes(runId, allNotes);
    }

    public void deleteNotes(String runId) {
        File file = new File(getFilePath(runId));
        file.delete();
    }

    public static void main(String[] args) {
        testNotes();
    }

    private static void testNotes() {
        NewsFeedDao dao = new NewsFeedDao();
        NewsFeedItemModel m1 = new NewsFeedItemModel(1, "Day One - msg 1", NewsType.GENERAL_NOTE, "000");
        NewsFeedItemModel m2 = new NewsFeedItemModel(1, "Day One - msg 2", NewsType.GENERAL_NOTE, "000");
        ArrayList<NewsFeedItemModel> notes = new ArrayList<>();
        notes.add(m1);
        notes.add(m2);
        dao.saveAllNotes("000", notes);

        List<NewsFeedItemModel> outMsgs = dao.getAllNotes("000");

        assert(outMsgs.size() == 2);
        assert(outMsgs.get(1).getHour() == 1);

        NewsFeedItemModel m3 = new NewsFeedItemModel(2, "Day Two", NewsType.GENERAL_NOTE, "000");
        dao.saveNote("000", m3);
        outMsgs = dao.getAllNotes("000");
        assert(outMsgs.size() == 3);

        System.out.println("Test case name: testNotes, Result: pass");
    }
}
