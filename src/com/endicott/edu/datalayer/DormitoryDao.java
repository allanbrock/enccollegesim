package com.endicott.edu.datalayer;

import com.endicott.edu.models.DormitoryModel;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by abrocken on 7/17/2017.
 */
public class DormitoryDao {
    private String getFilePath(String runId) {
        return DaoUtils.getFilePathPrefix(runId) +  "dormitory.dat";
    }

    public List<DormitoryModel> getDorms(String runId) {
        ArrayList<DormitoryModel> dorms = new ArrayList<>();
        DormitoryModel dormModel = null;
        try {
            File file = new File(getFilePath(runId));

            if (!file.exists()) {
                return dorms;
            }
            else{
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                dorms = (ArrayList<DormitoryModel>) ois.readObject();
                ois.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return dorms;
    }

    public void saveAllDorms(String runId, List<DormitoryModel> notes){
        try {
            File file = new File(getFilePath(runId));
            file.createNewFile();
            FileOutputStream fos;
            fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(notes);
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveNewDorm(String runId, DormitoryModel dorm) {
        List<DormitoryModel> dorms = getDorms(runId);
        dorm.setRunId(runId);
        dorms.add(dorm);
        saveAllDorms(runId, dorms);
    }

    public void deleteDorms(String runId) {
        File file = new File(getFilePath(runId));
        file.delete();
    }

    public static void main(String[] args) {
        testNotes();
    }

    private static void testNotes() {
        final String runId = "testdorm001";
        DormitoryDao dao = new DormitoryDao();
        DormitoryModel m1 = new DormitoryModel(100, 10, 0, "Frates", runId );
        DormitoryModel m2 = new DormitoryModel(200, 10, 0, "Hamilton", runId);
        ArrayList<DormitoryModel> dorms = new ArrayList<DormitoryModel>();
        dorms.add(m1);
        dorms.add(m2);
        dao.saveAllDorms(runId, dorms);

        List<DormitoryModel> outMsgs = dao.getDorms(runId);

        assert(outMsgs.size() == 2);
        assert(outMsgs.get(1).getCapacity() == 100);

        DormitoryModel m3 = new DormitoryModel(300, 10, 0, "Rockport", runId);
        dao.saveNewDorm(runId, m3);
        outMsgs = dao.getDorms(runId);
        assert(outMsgs.size() == 3);

        System.out.println("Test case name: testDorms, Result: pass");
    }
}
