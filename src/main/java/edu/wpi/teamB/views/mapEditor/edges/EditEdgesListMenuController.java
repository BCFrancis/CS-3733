package edu.wpi.teamB.views.mapEditor.edges;

import com.jfoenix.controls.JFXButton;
import edu.wpi.teamB.App;
import edu.wpi.teamB.database.DatabaseHandler;
import edu.wpi.teamB.entities.Edge;
import edu.wpi.teamB.pathfinding.Graph;
import edu.wpi.teamB.util.CSVHandler;
import edu.wpi.teamB.util.EdgeWrapper;
import edu.wpi.teamB.util.SceneSwitcher;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@SuppressWarnings("unchecked") // Added so Java doesn't get mad at the raw use of TableView that is necessary
public class EditEdgesListMenuController implements Initializable {

    @FXML
    private JFXButton btnEmergency;

    @FXML
    private JFXButton btnLoad;

    @FXML
    private JFXButton btnSave;

    @FXML
    private JFXButton btnBack;

    @FXML
    private JFXButton btnAddEdge;

    @FXML
    private TableView tblEdges;

    @FXML
    private TableColumn<String, JFXButton> editBtnCol;

    @FXML
    private TableColumn<String, Label> idCol;

    @FXML
    private TableColumn<String, Label> startNodeCol;

    @FXML
    private TableColumn<String, Label> endNodeCol;

    @FXML
    private TableColumn<String, JFXButton> delCol;

    final FileChooser fileChooser = new FileChooser();
    final DirectoryChooser directoryChooser = new DirectoryChooser();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Map<String, Edge> edges = DatabaseHandler.getDatabaseHandler("main.db").getEdges();
        ObservableList<TableColumn<String, Label>> cols = tblEdges.getColumns();
        for (TableColumn<String, Label> c : cols) {
            c.getId();
            switch (c.getId()) {
                case "editBtnCol":
                    c.setCellValueFactory(new PropertyValueFactory<>("btnEdit"));
                    break;
                case "idCol":
                    c.setCellValueFactory(new PropertyValueFactory<>("id"));
                    break;
                case "startNodeCol":
                    c.setCellValueFactory(new PropertyValueFactory<>("startNode"));
                    break;
                case "endNodeCol":
                    c.setCellValueFactory(new PropertyValueFactory<>("endNode"));
                    break;
                case "delCol":
                    c.setCellValueFactory(new PropertyValueFactory<>("btnDel"));
                    break;
            }
        }

        assert edges != null;
        for (Edge e : edges.values()) {
            try {
                tblEdges.getItems().add(new EdgeWrapper(e, tblEdges));
            } catch (IOException err) {
                err.printStackTrace();
            }
        }


        // Set up Load and Save buttons
        btnLoad.setOnAction(
                event -> {
                    // Get the CSV file and load it
                    Stage stage = App.getPrimaryStage();
                    fileChooser.setTitle("Select Edges CSV file");
                    fileChooser.setInitialDirectory(new File(new File("").getAbsolutePath()));
                    File file = fileChooser.showOpenDialog(stage);
                    if (file == null) return;

                    List<Edge> newEdges = CSVHandler.loadCSVEdgesFromExternalPath(file.toPath());
                    DatabaseHandler.getDatabaseHandler("main.db").loadDatabaseEdges(newEdges);
                    Graph.getGraph().updateGraph();

                    // Add them to the refreshed table
                    tblEdges.getItems().clear();
                    for (Edge e : newEdges) {
                        try {
                            tblEdges.getItems().add(new EdgeWrapper(e, tblEdges));
                        } catch (IOException err) {
                            err.printStackTrace();
                        }
                    }
                }
        );

        btnSave.setOnAction(
                event -> {
                    // Get the CSV directory location
                    Stage stage = App.getPrimaryStage();
                    directoryChooser.setTitle("Select directory to save Edges CSV file to");
                    directoryChooser.setInitialDirectory(new File(new File("").getAbsolutePath()));
                    File file = directoryChooser.showDialog(stage);
                    if (file == null) return;

                    // Save the current database into that csv folder
                    CSVHandler.saveCSVEdges(file.toPath(), false);
                    Graph.getGraph().updateGraph();
                }
        );
    }

    @FXML
    private void handleButtonAction(ActionEvent e) throws IOException {
        JFXButton btn = (JFXButton) e.getSource();

        switch (btn.getId()) {
            case "btnBack":
                SceneSwitcher.switchScene(getClass(), "/edu/wpi/teamB/views/mapEditor/editorIntermediateMenu.fxml");
                break;
            case "btnAddEdge":
                SceneSwitcher.switchScene(getClass(), "/edu/wpi/teamB/views/mapEditor/edges/addEdgeMenu.fxml");
                break;
            case "btnEmergency":
                SceneSwitcher.switchScene(getClass(), "/edu/wpi/teamB/views/requestForms/emergencyForm.fxml");
                break;
        }
    }
}
