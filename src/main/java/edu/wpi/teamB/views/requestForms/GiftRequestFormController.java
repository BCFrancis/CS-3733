package edu.wpi.teamB.views.requestForms;

import com.jfoenix.controls.*;
import com.jfoenix.validation.RequiredFieldValidator;
import edu.wpi.teamB.App;
import edu.wpi.teamB.database.DatabaseHandler;
import edu.wpi.teamB.entities.requests.GiftRequest;
import edu.wpi.teamB.entities.requests.Request;
import edu.wpi.teamB.util.SceneSwitcher;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;

import java.math.RoundingMode;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.UUID;

public class GiftRequestFormController extends DefaultServiceRequestFormController implements Initializable {

    @FXML
    private JFXTextField patientName; //checked

    @FXML
    private JFXDatePicker deliveryDate; //checked

    @FXML
    private JFXTimePicker startTime; //checked

    @FXML
    private JFXTimePicker endTime; //checked

    @FXML
    private JFXTextArea message; //checked

    @FXML
    private Label totalPrice;

    @FXML
    private JFXCheckBox balloons;

    @FXML
    private JFXCheckBox teddyBear;

    @FXML
    private JFXCheckBox chocolate;

    private String id;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        if (SceneSwitcher.peekLastScene().equals("/edu/wpi/teamB/views/menus/serviceRequestDatabase.fxml")) {
            this.id = (String) App.getPrimaryStage().getUserData();
            GiftRequest giftRequest;
            try {
                giftRequest = (GiftRequest) DatabaseHandler.getDatabaseHandler("main.db").getSpecificRequestById(id, Request.RequestType.GIFT);
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }
            patientName.setText(giftRequest.getPatientName());
            getLocationIndex(giftRequest.getLocation());
            String date = giftRequest.getDeliveryDate();
            LocalDate ld = LocalDate.of(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(5, 7)), Integer.parseInt(date.substring(8, 10)));
            deliveryDate.setValue(ld);
            String sTime = giftRequest.getStartTime();
            LocalTime slt = LocalTime.of(Integer.parseInt(sTime.substring(0, 2)), Integer.parseInt(sTime.substring(3, 5)));
            startTime.setValue(slt);
            String eTime = giftRequest.getEndTime();
            LocalTime elt = LocalTime.of(Integer.parseInt(eTime.substring(0, 2)), Integer.parseInt(eTime.substring(3, 5)));
            endTime.setValue(elt);
            message.setText(giftRequest.getDescription());
            balloons.setSelected(giftRequest.getWantsBalloons().equals("T"));
            teddyBear.setSelected(giftRequest.getWantsTeddyBear().equals("T"));
            chocolate.setSelected(giftRequest.getWantsChocolate().equals("T"));


            ArrayList<JFXCheckBox> checkBoxes = new ArrayList<>();
            checkBoxes.add(balloons);
            checkBoxes.add(teddyBear);
            checkBoxes.add(chocolate);


            double price = 0;
            for (JFXCheckBox checkBox : checkBoxes) {
                if (checkBox.isSelected()) {
                    price += 2.99;
                }
            }
            totalPrice.setText("Total Price: $" + price);
        }
        validateButton();


        RequiredFieldValidator validatorBalloons = new RequiredFieldValidator();
        patientName.getValidators().add(validatorBalloons);
        validatorBalloons.setMessage("Please input the patient's name!");
        patientName.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue){
                patientName.validate();
            }
        });

        RequiredFieldValidator validatorDeliveryDate = new RequiredFieldValidator();
        deliveryDate.getValidators().add(validatorDeliveryDate);
        validatorDeliveryDate.setMessage("Please input the patient's name!");
        deliveryDate.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue){
                deliveryDate.validate();
            }
        });

        //start time
        RequiredFieldValidator validatorStartTime = new RequiredFieldValidator();
        startTime.getValidators().add(validatorStartTime);
        validatorStartTime.setMessage("Please input the patient's name!");
        startTime.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue){
                startTime.validate();
            }
        });

        RequiredFieldValidator validatorEndTime = new RequiredFieldValidator();
        endTime.getValidators().add(validatorEndTime);
        validatorEndTime.setMessage("Please input the patient's name!");
        endTime.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue){
                endTime.validate();
            }
        });

        RequiredFieldValidator validatorMessage = new RequiredFieldValidator();
        message.getValidators().add(validatorMessage);
        validatorMessage.setMessage("Please input the patient's name!");
        message.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue){
                message.validate();
            }
        });
    }

    @FXML
    private void updatePrice() {
        validateButton();
        double currentPrice = 0;
        if (balloons.isSelected()) currentPrice += 0.5;
        if (teddyBear.isSelected()) currentPrice += 2;
        if (chocolate.isSelected()) currentPrice += 1;


        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        nf.setRoundingMode(RoundingMode.HALF_UP);
        totalPrice.setText("Total Price: $" + nf.format(currentPrice));
    }

    public void handleButtonAction(ActionEvent e) {
        super.handleButtonAction(e);

        JFXButton btn = (JFXButton) e.getSource();
        if (btn.getId().equals("btnSubmit")) {

            String givenPatientName = patientName.getText();
            String givenDeliveryDate = deliveryDate.getValue().toString();
            String givenStartTime = startTime.getValue().toString();
            String givenEndTime = endTime.getValue().toString();
            String wantsBalloons = balloons.isSelected() ? "T" : "F";
            String wantsTeddyBear = teddyBear.isSelected() ? "T" : "F";
            String wantsChocolate = chocolate.isSelected() ? "T" : "F";

            DateFormat timeFormat = new SimpleDateFormat("HH:mm");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dateInfo = new Date();

            String requestID;
            if (SceneSwitcher.peekLastScene().equals("/edu/wpi/teamB/views/menus/serviceRequestDatabase.fxml"))
                requestID = this.id;
            else requestID = UUID.randomUUID().toString();

            String time = timeFormat.format(dateInfo); // Stored as HH:MM (24 hour time)
            String date = dateFormat.format(dateInfo); // Stored as YYYY-MM-DD
            String complete = "F";
            String givenDescription = message.getText();

            String employeeName;
            if (SceneSwitcher.peekLastScene().equals("/edu/wpi/teamB/views/menus/serviceRequestDatabase.fxml")) {
                try {
                    employeeName = DatabaseHandler.getDatabaseHandler("main.db").getSpecificRequestById(this.id, Request.RequestType.GIFT).getEmployeeName();
                } catch (SQLException err) {
                    err.printStackTrace();
                    return;
                }
            } else {
                employeeName = null;
            }

            GiftRequest request = new GiftRequest(givenPatientName, givenDeliveryDate, givenStartTime, givenEndTime,
                    wantsBalloons, wantsTeddyBear, wantsChocolate,
                    requestID, time, date, complete, employeeName, getLocation(), givenDescription);

            try {
                if (SceneSwitcher.peekLastScene().equals("/edu/wpi/teamB/views/menus/serviceRequestDatabase.fxml"))
                    DatabaseHandler.getDatabaseHandler("main.db").updateRequest(request);
                else DatabaseHandler.getDatabaseHandler("main.db").addRequest(request);
            } catch (SQLException err) {
                err.printStackTrace();
            }
        }
    }

    @FXML
    private void validateButton() {
        btnSubmit.setDisable(
                patientName.getText().isEmpty() || loc.getValue() == null ||
                        deliveryDate.getValue() == null || startTime.getValue() == null ||
                        endTime.getValue() == null || message.getText().isEmpty() ||
                        !(balloons.isSelected() || teddyBear.isSelected() || chocolate.isSelected())
        );
    }

}
