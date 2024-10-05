package components.login;

import com.sun.istack.NotNull;
import components.sheet.appcontroller.AppController;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import okhttp3.*;

import java.io.IOException;

import static httputils.Constants.LOGIN_PAGE;

public class LoginController {

    @FXML
    private TextField userNameTextField;

    @FXML
    private Label errorMessageLabel;

    @FXML
    private Button quitButton;

    @FXML
    private Button loginButton;



    private AppController mainController;  // Reference to the main controller
    private final StringProperty errorMessageProperty = new SimpleStringProperty();  // Property for error messages
    private Stage primaryStage;  // To handle stage switching

    @FXML
    public void initialize() {
        // Bind error messages to the label in the UI
        errorMessageLabel.textProperty().bind(errorMessageProperty);

        // Disable login button initially
        loginButton.setDisable(true);

        // Handle the quit button action
        quitButton.setOnAction(event -> quitButtonClicked(event));

        // Add a listener to the TextField to enable the button when input is detected
        userNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Enable the button if the new value is not empty, disable it otherwise
            loginButton.setDisable(newValue.trim().isEmpty());
        });
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    // Triggered when the login button is clicked
    @FXML
    private void loginButtonClicked(ActionEvent event) {
        String userName = userNameTextField.getText();

        if (userName.isEmpty()) {
            errorMessageProperty.set("Username cannot be empty.");
            return;
        }

        // Build the login URL with the username as a query parameter
        String finalUrl = HttpUrl.parse(LOGIN_PAGE)
                .newBuilder()
                .addQueryParameter("username", userName)
                .build()
                .toString();

        // Send asynchronous login request to the server
        chat.client.util.http.HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> errorMessageProperty.set("Connection failed: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    // Handle error response
                    String responseBody = response.body().string();
                    Platform.runLater(() -> errorMessageProperty.set("Login failed: " + responseBody));
                } else {
                    // Handle successful login
                    Platform.runLater(() -> switchToDashboard(userName));
                }
            }
        });
    }

    // Switch to the dashboard after successful login
    private void switchToDashboard(String username) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/dashboard/dashboard.fxml"));
            Parent dashboardRoot = loader.load();

            // Pass username to the dashboard controller
            components.dashboard.DashboardController dashboardController = loader.getController();
            dashboardController.setUserName(username);

            // Load new stage for dashboard
            Stage dashboardStage = new Stage();
            dashboardStage.setTitle("Dashboard - " + username);
            Scene dashboardScene = new Scene(dashboardRoot);

            dashboardStage.setScene(dashboardScene);
            dashboardStage.show();

            // Close the login window
            primaryStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            errorMessageProperty.set("Failed to load the dashboard.");
        }
    }

    // Clears the error message when the user types in the username field
    @FXML
    private void userNameKeyTyped(KeyEvent event) {
        errorMessageProperty.set("");
    }

    // Exits the application when the quit button is clicked
    @FXML
    private void quitButtonClicked(ActionEvent event) {
        Platform.exit();
    }

    // Sets the main controller to allow communication between the login and main controllers
    public void setAppMainController(AppController appMainController) {
        this.mainController = appMainController;
    }

    // Set primary stage to handle switching between windows
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
