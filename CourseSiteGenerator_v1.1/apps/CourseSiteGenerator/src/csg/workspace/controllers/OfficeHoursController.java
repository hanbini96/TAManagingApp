package csg.workspace.controllers;

import djf.modules.AppGUIModule;
import djf.ui.dialogs.AppDialogsFacade;
import javafx.collections.ObservableList;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import csg.CSGApp;
import static csg.CSGPropertyType.OH_EMAIL_TEXT_FIELD;
import static csg.CSGPropertyType.OH_FOOLPROOF_SETTINGS;
import static csg.CSGPropertyType.OH_NAME_TEXT_FIELD;
import static csg.CSGPropertyType.OH_NO_TA_SELECTED_CONTENT;
import static csg.CSGPropertyType.OH_NO_TA_SELECTED_TITLE;
import static csg.CSGPropertyType.OH_OFFICE_HOURS_TABLE_VIEW;
import static csg.CSGPropertyType.OH_TAS_TABLE_VIEW;
import static csg.CSGPropertyType.OH_TA_EDIT_DIALOG;
import csg.data.CSGData;
import csg.data.OfficeHoursData;
import csg.data.TAType;
import csg.data.TeachingAssistantPrototype;
import csg.data.TimeSlot;
import csg.data.TimeSlot.DayOfWeek;
import csg.transactions.AddTA_Transaction;
import csg.transactions.EditTA_Transaction;
import csg.transactions.RemoveTA_Transaction;
import csg.transactions.ToggleOfficeHours_Transaction;
import csg.workspace.dialogs.TADialog;

/**
 *
 * @author McKillaGorilla
 */
public class OfficeHoursController {

    CSGApp app;

    public OfficeHoursController(CSGApp initApp) {
        app = initApp;
    }

    public void processAddTA() {
        AppGUIModule gui = app.getGUIModule();
        TextField nameTF = (TextField) gui.getGUINode(OH_NAME_TEXT_FIELD);
        String name = nameTF.getText();
        TextField emailTF = (TextField) gui.getGUINode(OH_EMAIL_TEXT_FIELD);
        String email = emailTF.getText();
        OfficeHoursData data = ((CSGData)app.getDataComponent()).getOHData();
        TAType type = data.getSelectedType();
        if (data.isLegalNewTA(name, email)) {
            TeachingAssistantPrototype ta = new TeachingAssistantPrototype(name.trim(), email.trim(), type);
            AddTA_Transaction addTATransaction = new AddTA_Transaction(data, ta);
            app.processTransaction(addTATransaction);

            // NOW CLEAR THE TEXT FIELDS
            nameTF.setText("");
            emailTF.setText("");
            nameTF.requestFocus();
        }
        app.getFoolproofModule().updateControls(OH_FOOLPROOF_SETTINGS);
    }

    public void processRemoveTA() {
        AppGUIModule gui = app.getGUIModule();
        OfficeHoursData data = ((CSGData)app.getDataComponent()).getOHData();
        TableView<TeachingAssistantPrototype> taTableView = (TableView)gui.getGUINode(OH_TAS_TABLE_VIEW);
        TeachingAssistantPrototype ta = taTableView.getSelectionModel().getSelectedItem();
        RemoveTA_Transaction removeTATransaction = new RemoveTA_Transaction(app, ta, data.getTATimeSlots(ta));
        app.processTransaction(removeTATransaction);
        app.getFoolproofModule().updateControls(OH_FOOLPROOF_SETTINGS);
    }
    
    public void processVerifyTA() {

    }

    public void processToggleOfficeHours() {
        AppGUIModule gui = app.getGUIModule();
        TableView<TimeSlot> officeHoursTableView = (TableView) gui.getGUINode(OH_OFFICE_HOURS_TABLE_VIEW);
        ObservableList<TablePosition> selectedCells = officeHoursTableView.getSelectionModel().getSelectedCells();
        if (selectedCells.size() > 0) {
            TablePosition cell = selectedCells.get(0);
            int cellColumnNumber = cell.getColumn();
            OfficeHoursData data = ((CSGData)app.getDataComponent()).getOHData();
            if (data.isDayOfWeekColumn(cellColumnNumber)) {
                DayOfWeek dow = data.getColumnDayOfWeek(cellColumnNumber);
                TableView<TeachingAssistantPrototype> taTableView = (TableView)gui.getGUINode(OH_TAS_TABLE_VIEW);
                TeachingAssistantPrototype ta = taTableView.getSelectionModel().getSelectedItem();
                if (ta != null) {
                    TimeSlot timeSlot = officeHoursTableView.getSelectionModel().getSelectedItem();
                    ToggleOfficeHours_Transaction transaction = new ToggleOfficeHours_Transaction(data, timeSlot, dow, ta);
                    app.processTransaction(transaction);
                }
                else {
                    Stage window = app.getGUIModule().getWindow();
                    AppDialogsFacade.showMessageDialog(window, OH_NO_TA_SELECTED_TITLE, OH_NO_TA_SELECTED_CONTENT);
                }
            }
            int row = cell.getRow();
            cell.getTableView().refresh();
        }
    }

    public void processTypeTA() {
        app.getFoolproofModule().updateControls(OH_FOOLPROOF_SETTINGS);
    }

    public void processEditTA() {
        OfficeHoursData data = ((CSGData)app.getDataComponent()).getOHData();
        if (data.isTASelected()) {
            TeachingAssistantPrototype taToEdit = data.getSelectedTA();
            TADialog taDialog = (TADialog)app.getGUIModule().getDialog(OH_TA_EDIT_DIALOG);
            taDialog.showEditDialog(taToEdit);
            TeachingAssistantPrototype editTA = taDialog.getEditTA();
            if (editTA != null) {
                EditTA_Transaction transaction = new EditTA_Transaction(taToEdit, editTA.getName(), editTA.getEmail(), editTA.getType());
                app.processTransaction(transaction);
            }
        }
    }

    public void processSelectAllTAs() {
        OfficeHoursData data = ((CSGData)app.getDataComponent()).getOHData();
        data.selectTAs(TAType.All);
    }

    public void processSelectGradTAs() {
        OfficeHoursData data = ((CSGData)app.getDataComponent()).getOHData();
        data.selectTAs(TAType.Graduate);
    }

    public void processSelectUndergradTAs() {
        OfficeHoursData data = ((CSGData)app.getDataComponent()).getOHData();
        data.selectTAs(TAType.Undergraduate);
    }

    public void processSelectTA() {
        AppGUIModule gui = app.getGUIModule();
        TableView<TimeSlot> officeHoursTableView = (TableView) gui.getGUINode(OH_OFFICE_HOURS_TABLE_VIEW);
        officeHoursTableView.refresh();
    }

    public void processSelectTime(String startTime, String endTime) {
        OfficeHoursData data = ((CSGData)app.getDataComponent()).getOHData();
        data.selectOHs(startTime, endTime);
        
        app.getFoolproofModule().updateControls(OH_FOOLPROOF_SETTINGS);
    }
}