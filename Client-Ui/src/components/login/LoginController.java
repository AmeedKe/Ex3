package components.login;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import components.dashboard.DashboardController;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField userNameTextField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorMessageLabel;

    private Stage primaryStage;

    public void initialize() {
        loginButton.setDisable(true); // Disable login button initially

        // Enable login button when text is entered
        userNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        loginButton.setOnAction(event -> handleLogin());
    }

    private void handleLogin() {
        String username = userNameTextField.getText();
        if (username != null && !username.isEmpty()) {
            // Proceed to dashboard after successful login
            switchToDashboard(username);
        } else {
            errorMessageLabel.setText("Username cannot be empty!");
        }
    }

    private void switchToDashboard(String username) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/dashboard/dashboard.fxml"));
            Parent dashboardRoot = loader.load();

            // Get the DashboardController instance and pass the username
            DashboardController dashboardController = loader.getController();
            dashboardController.setUserName(username);  // Pass username to dashboard

            // Switch to the dashboard scene
            Scene dashboardScene = new Scene(dashboardRoot);
            primaryStage.setScene(dashboardScene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            errorMessageLabel.setText("Failed to load the dashboard.");
        }
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
