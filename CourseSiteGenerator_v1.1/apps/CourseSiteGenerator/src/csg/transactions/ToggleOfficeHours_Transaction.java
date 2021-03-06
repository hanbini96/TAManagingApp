package csg.transactions;

import jtps.jTPS_Transaction;
import csg.data.OfficeHoursData;
import csg.data.TeachingAssistantPrototype;
import csg.data.TimeSlot;
import csg.data.TimeSlot.DayOfWeek;

/**
 *
 * @author McKillaGorilla
 */
public class ToggleOfficeHours_Transaction implements jTPS_Transaction {
    OfficeHoursData data;
    TimeSlot timeSlot;
    DayOfWeek dow;
    TeachingAssistantPrototype ta;
    
    public ToggleOfficeHours_Transaction(   OfficeHoursData initData, 
                                            TimeSlot initTimeSlot,
                                            DayOfWeek initDOW,
                                            TeachingAssistantPrototype initTA) {
        data = initData;
        timeSlot = initTimeSlot;
        dow = initDOW;
        ta = initTA;
    }

    @Override
    public void doTransaction() {
        timeSlot.toggleTA(dow, ta);
    }

    @Override
    public void undoTransaction() {
        timeSlot.toggleTA(dow, ta);
    }
}