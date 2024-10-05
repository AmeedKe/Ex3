package components.dashboard;

import components.sheet.appcontroller.AppController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class DashboardController {

    @FXML
    private Button loadFileButton; // Button to load a file

    @FXML
    private Button viewSheetButton; // Button to view a sheet

    @FXML
    private Button requestPermissionButton; // Button to request permission

    @FXML
    private Button acceptPermissionButton; // Button to accept permission

    @FXML
    private Button denyPermissionButton; // Button to deny permission

    @FXML
    private Label userNameLabel; // Label to display the user name

    private Stage primaryStage;  // Reference to the primary stage

    private AppController mainController;  // Reference to AppController

    @FXML
    public void initialize() {
        // Lambda expressions for button actions
        loadFileButton.setOnAction(event -> handleLoadFileAction());
        viewSheetButton.setOnAction(event -> handleViewSheetAction());
        requestPermissionButton.setOnAction(event -> handleRequestPermissionAction());
        acceptPermissionButton.setOnAction(event -> handleAcceptPermissionAction());
        denyPermissionButton.setOnAction(event -> handleDenyPermissionAction());
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    // Set the username on the dashboard's user label
    public void setUserName(String username) {
        userNameLabel.setText(username);
    }

    @FXML
    private void handleLoadFileAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open XML File");

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        Stage stage = (Stage) loadFileButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
//            mainController.loadFile(selectedFile);
//            enableButtons();
        }
    }


    // Handler for Request Permission button
    private void handleRequestPermissionAction() {
        // Handle request permission logic
        System.out.println("Request Permission action triggered");
    }

    // Handler for Accept Permission button
    private void handleAcceptPermissionAction() {
        // Handle accept permission logic
        System.out.println("Accept Permission action triggered");
    }

    // Handler for Deny Permission button
    private void handleDenyPermissionAction() {
        // Handle deny permission logic
        System.out.println("Deny Permission action triggered");
    }

    // Switch back to the App view (AppController)
    private void handleViewSheetAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/sheet/appcontroller/app.fxml"));
            Parent root = loader.load();

            // Get the controller for App
            mainController = loader.getController();
            mainController.setPrimaryStage(primaryStage);  // Pass the stage to AppController

            // Create and set the scene for the App
            Scene appScene = new Scene(root);
            primaryStage.setScene(appScene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
