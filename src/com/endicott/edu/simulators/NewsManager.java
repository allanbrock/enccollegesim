package com.endicott.edu.simulators;
// Created by abrocken on 10/9/2017.

import com.endicott.edu.datalayer.CollegeDao;
import com.endicott.edu.datalayer.NewsFeedDao;
import com.endicott.edu.models.CollegeModel;
import com.endicott.edu.models.NewsFeedItemModel;
import com.endicott.edu.models.NewsType;

public class NewsManager {

    public static void createNews(String collegeId, String message) {
        CollegeDao dao = new CollegeDao();
        CollegeModel college = dao.getCollege(collegeId);
        if (college == null) {
            return;
        }

        createNews(collegeId, college.getCurrentDay(), message, NewsType.GENERAL_NOTE);
    }

    public static void createNews(String collegeId, int newsHour, String message, NewsType newsType) {
        NewsFeedItemModel note = new NewsFeedItemModel();
        note.setHour(newsHour);
        note.setMessage(message);
        note.setNoteType(newsType);
        NewsFeedDao noteDao = new NewsFeedDao();
        noteDao.saveNote(collegeId, note);
    }
    public static void createFinancialNews(String collegeId, int newsHour, String message, int amount) {
        NewsFeedItemModel note = new NewsFeedItemModel();
        note.setHour(newsHour);
        note.setMessage(message);
        note.setAmount(amount);
        note.setNoteType(NewsType.FINANCIAL_NEWS);
        NewsFeedDao noteDao = new NewsFeedDao();
        noteDao.saveNote(collegeId, note);
    }


}
