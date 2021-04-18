package edu.wpi.teamB.views.requestForms;

import com.jfoenix.controls.*;
import edu.wpi.teamB.database.DatabaseHandler;
import edu.wpi.teamB.entities.requests.SanitationRequest;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.UUID;

public class SanitationRequestFormController extends DefaultServiceRequestFormController implements Initializable {

    @FXML
    private JFXTextField loc;

    @FXML
    private JFXComboBox<Label> comboTypeService;

    @FXML
    private JFXComboBox<Label> comboSizeService;

    @FXML
    private JFXTextArea description;

    @FXML
    private JFXCheckBox safetyHazard;

    @FXML
    private JFXCheckBox biologicalSubstance;

    @FXML
    private JFXCheckBox roomOccupied;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comboTypeService.getItems().add(new Label("Wet"));
        comboTypeService.getItems().add(new Label("Dry"));
        comboTypeService.getItems().add(new Label("Glass"));

        comboSizeService.getItems().add(new Label("Small"));
        comboSizeService.getItems().add(new Label("Medium"));
        comboSizeService.getItems().add(new Label("Large"));
    }

    public void handleButtonAction(ActionEvent actionEvent) throws IOException {
        String givenSanitationType = comboTypeService.getValue().toString();
        String givenSanitationSize = comboSizeService.getValue().toString();
        String givenHazardous = safetyHazard.isSelected() ? "T" : "F";
        String givenBiologicalSubstance = biologicalSubstance.isSelected() ? "T" : "F";
        String givenOccupied = roomOccupied.isSelected() ? "T" : "F";

        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");
        Date dateInfo = new Date();

        String requestID = UUID.randomUUID().toString();
        String time = timeFormat.format(dateInfo); // Stored as HH:MM (24 hour time)
        String date = dateFormat.format(dateInfo); // Stored as YYYY-MM-DD
        String complete = "F";
        String employeeName = null; // fix
        String location = loc.getText();
        String givenDescription = description.getText();

        SanitationRequest request = new SanitationRequest(givenSanitationType, givenSanitationSize, givenHazardous, givenBiologicalSubstance, givenOccupied,
                requestID, time, date, complete, employeeName, location, givenDescription);

        JFXButton btn = (JFXButton) actionEvent.getSource();
        if (btn.getId().equals("btnSubmit")) {
            DatabaseHandler.getDatabaseHandler("main.db").addRequest(request);
        }
        super.handleButtonAction(actionEvent);
    }
}
