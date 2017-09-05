package com.endicott.edu.simulators;

import com.endicott.edu.datalayer.DormitoryDao;
import com.endicott.edu.models.DormitoryModel;

import java.util.List;

/**
 * Created by abrocken on 7/29/2017.
 */
public class DormManager {
    DormitoryDao dao = new DormitoryDao();

    public void handleTimeChange(String runId, int hoursAlive) {
        List<DormitoryModel> dorms = dao.getDorms(runId);

        for (DormitoryModel dorm : dorms) {
            int newCharge = (hoursAlive - dorm.getHourLastUpdated()) * dorm.getCostPerHour();
            Accountant.payBill(runId, newCharge);
            dorm.setHourLastUpdated(hoursAlive);
        }

        dao.saveAllDorms(runId, dorms);
    }
}
