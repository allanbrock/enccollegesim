package com.endicott.edu.datalayer;

import com.endicott.edu.models.EventsModel;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class EventsDao {
    private static String getFilePath(String runId) {
        return DaoUtils.getFilePathPrefix(runId) +  "event.dat";
    }

    private static Logger logger = Logger.getLogger("EventsDao");

    /**
     * This function returns a list of all the events
     * The college is defined by its runId
     * @param runId sim id
     * @return ArrayList<EventsModel> events
     */
    public List<EventsModel> getEvents(String runId) {
        ArrayList<EventsModel> events = new ArrayList<>();
        EventsModel eventsModel = null; //not sure why this is defined and not used.....
        try {
            File file = new File(getFilePath(runId));

            if (!file.exists()) {
                return events;  // No events exist
            }
            else{ //events exist lets return the objects....
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                events = (ArrayList<EventsModel>) ois.readObject();
                ois.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.warning("IO exception in retrieving events.. ");
            e.printStackTrace();
        }

        return events;
    }


    /**
     * This creates a new event
     * After assigning them an ID
     * @param runId sim id
     * @param event event object
     */
    public void saveNewEvent(String runId, EventsModel event) {
        // logger.info("Saving new event...");
        List<EventsModel> events = getEvents(runId);
        event.setRunId(runId);
        event.setEventId(IdNumberGenDao.getID(runId));

        //logger.info("Creating event with ID: " + event.getEventId());
        events.add(event);
        saveAllEvents(runId, events);
    }

    /**
     * This function writes a list of events objects to the disk...
     * @param runId sim id
     * @param events list of events
     */
    private void saveAllEvents(String runId, List<EventsModel> events) {
        logger.info("Saving all events...");
        try {
            File file = new File(getFilePath(runId));
            file.createNewFile();
            FileOutputStream fos;
            fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(events);
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

        logger.info("Saved events...");

    }

    /**
     * Deletes the events file
     * @param runId sim id
     */
    public static void removeAllEvents(String runId){
        File file = new File(getFilePath(runId));
        file.delete();
    }

    /**
     * Loads all of the events, and searches through the list until it finds a matching event id to the event passed
     * as argument. Then removes that from the list and saves the new list.
     * @param runId sim id
     * @param event instance of an event to be deleted.
     */
    public void removeSingleEvent(String runId,EventsModel event){
        logger.info("Removing event..");
        int tmp = event.getEventId();
        String name = event.getEventName();

        List<EventsModel> events = getEvents(runId);
        for( int i = 0; i < events.size(); i++){
            if(event.getEventId() == events.get(i).getEventId()){
                logger.info("removing " + events.get(i).getEventName());
                events.remove(i);
                break;
            }

        }
        saveAllEvents(runId,events);

        logger.info(name + " removed.. ID: " + tmp );


    }

    /**
     * returns the number of events in the list for the college
     * @param runId sim id
     * @return number of events
     */
    public int numberOfEvents(String runId){
        return getEvents(runId).size();

    }


    public static void main(String[] args) {
        final String runId = "testEventsDao";
        EventsDao dao = new EventsDao();
        List<EventsModel> events = new ArrayList<>();

        EventsModel event1 = new EventsModel(runId,"test1","desc1",100,200);
        dao.saveNewEvent(runId,event1);
        assert (dao.numberOfEvents(runId) == 1);
        EventsModel event2 = new EventsModel(runId,"test2","desc2",100,200);
        events = dao.getEvents(runId);
        assert(events.size() == 1);
        events.add(event2);
        dao.saveAllEvents(runId,events);
        assert(dao.numberOfEvents(runId) == 2);
        dao.removeSingleEvent(runId,event1);
        assert(dao.numberOfEvents(runId) == 1);
        EventsDao.removeAllEvents(runId);
        assert(dao.numberOfEvents(runId) == 0);
        
    }

}