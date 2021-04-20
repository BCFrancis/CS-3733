package edu.wpi.teamB.views.requestForms;

import com.jfoenix.controls.*;
import edu.wpi.teamB.App;
import edu.wpi.teamB.database.DatabaseHandler;
import edu.wpi.teamB.entities.requests.Request;
import edu.wpi.teamB.entities.requests.SanitationRequest;
import edu.wpi.teamB.util.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.UUID;

public class SanitationRequestFormController extends DefaultServiceRequestFormController implements Initializable {


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

    private String id;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location,resources);

        comboTypeService.getItems().add(new Label("Wet"));
        comboTypeService.getItems().add(new Label("Dry"));
        comboTypeService.getItems().add(new Label("Glass"));

        comboSizeService.getItems().add(new Label("Small"));
        comboSizeService.getItems().add(new Label("Medium"));
        comboSizeService.getItems().add(new Label("Large"));

        if (SceneSwitcher.peekLastScene().equals("/edu/wpi/teamB/views/menus/serviceRequestDatabase.fxml")) {
            this.id = (String) App.getPrimaryStage().getUserData();
            SanitationRequest sanitationRequest = (SanitationRequest) DatabaseHandler.getDatabaseHandler("main.db").getSpecificRequestById(id, Request.RequestType.SANITATION);
            getLocationIndex(sanitationRequest.getLocation());
            int indexType = -1;
            if (sanitationRequest.getSanitationType().equals("Wet")) {
                indexType = 0;
            } else if (sanitationRequest.getSanitationType().equals("Dry")) {
                indexType = 1;
            } else if (sanitationRequest.getSanitationType().equals("Glass")) {
                indexType = 2;
            }
            comboTypeService.getSelectionModel().select(indexType);
            int indexSize = -1;
            if (sanitationRequest.getSanitationSize().equals("Small")) {
                indexSize = 0;
            } else if (sanitationRequest.getSanitationSize().equals("Medium")) {
                indexSize = 1;
            } else if (sanitationRequest.getSanitationSize().equals("Large")) {
                indexSize = 2;
            }
            comboSizeService.getSelectionModel().select(indexSize);
            description.setText(sanitationRequest.getDescription());
            safetyHazard.setSelected(sanitationRequest.getHazardous().equals("T"));
            biologicalSubstance.setSelected(sanitationRequest.getBiologicalSubstance().equals("T"));
            roomOccupied.setSelected(sanitationRequest.getOccupied().equals("T"));
        }
        validateButton();
    }

    @FXML
    private void validateButton(){
        btnSubmit.setDisable(
                loc.getValue() == null || comboTypeService.getValue() == null || comboSizeService.getValue() == null ||
            description.getText().isEmpty()
        );
    }

    public void handleButtonAction(ActionEvent actionEvent) {
        super.handleButtonAction(actionEvent);

        JFXButton btn = (JFXButton) actionEvent.getSource();
        if (btn.getId().equals("btnSubmit")) {
            String givenSanitationType = comboTypeService.getValue().getText();
            String givenSanitationSize = comboSizeService.getValue().getText();
            String givenHazardous = safetyHazard.isSelected() ? "T" : "F";
            String givenBiologicalSubstance = biologicalSubstance.isSelected() ? "T" : "F";
            String givenOccupied = roomOccupied.isSelected() ? "T" : "F";

            DateFormat timeFormat = new SimpleDateFormat("HH:mm");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dateInfo = new Date();

            String requestID;
            if (SceneSwitcher.peekLastScene().equals("/edu/wpi/teamB/views/menus/serviceRequestDatabase.fxml")) {
                requestID = this.id;
            } else {
                requestID = UUID.randomUUID().toString();
            }

            String time = timeFormat.format(dateInfo); // Stored as HH:MM (24 hour time)
            String date = dateFormat.format(dateInfo); // Stored as YYYY-MM-DD
            String complete = "F";
            String employeeName = null; // fix
            String givenDescription = description.getText();

            SanitationRequest request = new SanitationRequest(givenSanitationType, givenSanitationSize, givenHazardous, givenBiologicalSubstance, givenOccupied,
                    requestID, time, date, complete, employeeName, getLocation(), givenDescription);

            if (SceneSwitcher.peekLastScene().equals("/edu/wpi/teamB/views/menus/serviceRequestDatabase.fxml")) {
                DatabaseHandler.getDatabaseHandler("main.db").updateRequest(request);
            } else {
                DatabaseHandler.getDatabaseHandler("main.db").addRequest(request);
            }
        }
    }
}
