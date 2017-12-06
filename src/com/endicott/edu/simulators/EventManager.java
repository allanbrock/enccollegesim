package com.endicott.edu.simulators;

import com.endicott.edu.datalayer.EventsDao;
import com.endicott.edu.models.EventsModel;

public class EventManager {
    private static EventsDao dao = new EventsDao();

    /**
     * when a college is created this function is called to create the first event
     * this will be called home coming and really just serves as a place holder
     *
     * @param runId sim id
     */
    public static void establishCollege(String runId){
        EventsModel event = new EventsModel(runId,"Homecoming","This is the first event a college has",0,0);
        dao.saveNewEvent(runId,event);
    }

}
