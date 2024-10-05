package components.sheet.appcontroller;

import components.dashboard.DashboardController;
import components.login.LoginController;
import components.sheet.gridcontroller.GridController;
import components.sheet.headerconroller.HeaderController;
import components.sheet.leftsidecontroller.LeftSideController;
import ExceptionHandler.*;
import cammands.impl.UpdateCellCommand;
import engineimpl.EngineImpl;
import javafx.animation.*;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.effect.Glow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import spreadsheet.cell.impl.CellImpl;
import spreadsheet.impl.SheetImpl;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class AppController {
    @FXML private GridController gridController;
    @FXML private HeaderController headerController;
    @FXML private LeftSideController leftSideController;
    private EngineImpl engine = null;
    private Label initialLabel;  // Add the initial label as a member variable
    private Stage primaryStage;  // Add reference to the primary stage
    private boolean animationsEnabled = true;  // Default: animations are enabled
    @FXML
    private Slider dynamicSlider;
    @FXML
    private ProgressIndicator progressIndicator;
    private String selectedCellId;
    private DashboardController dashboardController;
    private LoginController loginController;



    @FXML
    public void initialize() {
        if (headerController != null && leftSideController != null && gridController != null) {
            dashboardController.setMainController(this);
                    loginController.setAppMainController(this);
            leftSideController.setMainController(this);
            headerController.setMainController(this);
            gridController.setMainController(this);
            // Trigger animations right after the application window pops up
            runStartupAnimations();
        }

    }


    public Map<String, Set<String>> getRangeList(){
        return engine.getSheet().getRanges();
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public SheetImpl getsheet()
    {
        return engine.getSheet();
    }

    public EngineImpl getEngine()
    {
        return engine;
    }

    private void runStartupAnimations() {
        if (!animationsEnabled || initialLabel == null) {
            System.out.println("Animations disabled or label is null."); // Debug logging
            return;  // Skip animations if disabled
        }

        System.out.println("Running animations..."); // Debug logging

        // Fade Transition
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1.5), initialLabel);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);

        // Translate Transition (move label slightly to the right)
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.6), initialLabel);
        translateTransition.setByX(50);

        // Rotate Transition (rotate the label)
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(1.5), initialLabel);
        rotateTransition.setByAngle(360);

        // Play all animations in sequence
        SequentialTransition sequentialTransition = new SequentialTransition(fadeTransition, translateTransition, rotateTransition);
        sequentialTransition.play();

        System.out.println("Animations completed."); // Debug logging
    }

    public void setGridController(GridController gridController) {
        this.gridController = gridController;
        this.gridController.setMainController(this);
    }

    public void setInitialLabel(Label initialLabel) {
        this.initialLabel = initialLabel;
    }

    public void updateCellStyle(String cellId, String styleType, String color) {
        gridController.updateCellStyle(cellId, styleType, color);
    }

    public void setHeaderController(HeaderController headerController) {
        this.headerController = headerController;
        this.headerController.setMainController(this);
    }

    public void setLeftSideController(LeftSideController leftSideController) {
        this.leftSideController = leftSideController;
        this.leftSideController.setMainController(this);
    }

    public void loadFile(File file) {
        // Create a Task for loading the file in the background
        Task<Void> loadFileTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                updateProgress(0, 100); // Initial progress

                // Simulate loading progress faster (replace this with actual file loading logic)
                for (int i = 1; i <= 10; i++) {
                    Thread.sleep(50); // Reduced sleep time to 50 milliseconds (faster progress)
                    updateProgress(i * 10, 100); // Update progress
                }

                // Load the XML into the engine (replace this with actual loading logic)
                engine = new EngineImpl(file.getAbsolutePath());
                SheetImpl sheet = engine.getSheet();

                return null;
            }
        };

        // Show the progress indicator when the task starts
        progressIndicator.setVisible(true);

        // Bind the progress of the progress indicator to the task's progress
        progressIndicator.progressProperty().bind(loadFileTask.progressProperty());

        // Handle task success (hide the progress indicator and update the UI)
        loadFileTask.setOnSucceeded(event -> {
            progressIndicator.setVisible(false); // Hide progress indicator

            // Hide the welcome label (initialLabel) once the XML file is loaded
            if (initialLabel != null) {
                initialLabel.setVisible(false);  // Hide the initial welcome label
            }

            // Load the sheet into the grid controller
            if (gridController != null) {
                gridController.loadSheet(engine.getSheet());
            }

            // Enable buttons in controllers ONLY after a successful load
            headerController.enableVersionSelectorButton();
            leftSideController.enableButtons();

            // Optionally, show a success message or animation
            showSuccessAnimation();
        });

        // Handle task failure (display an error message)
        loadFileTask.setOnFailed(event -> {
            progressIndicator.setVisible(false); // Hide progress indicator
            displayError("Failed to load the XML file: " + loadFileTask.getException().getMessage());
            // Keep buttons disabled since the task failed
            headerController.disableButtons();
            leftSideController.disableButtons();
        });

        // Run the task in a background thread
        Thread loadThread = new Thread(loadFileTask);
        loadThread.setDaemon(true); // Ensure the thread will not block JVM shutdown
        loadThread.start();
    }

    private void showSuccessAnimation() {
        // Create the success message label
        Label successLabel = new Label("Your XML file was loaded successfully!");
        successLabel.setStyle("-fx-font-size: 36px; -fx-text-fill: black; -fx-font-weight: bold;");
        successLabel.setOpacity(0); // Initially invisible

        // Create a larger circle background for the label
        Circle circle = new Circle(200);  // Larger circle with radius 200
        circle.setFill(Color.TRANSPARENT);  // Transparent fill
        circle.setStroke(Color.BLUE);  // Stroke with blue color
        circle.setStrokeWidth(10);  // Thicker stroke
        circle.setOpacity(0);  // Initially invisible

        // Add the label and the circle to the scene
        StackPane root = (StackPane) primaryStage.getScene().getRoot();
        root.getChildren().addAll(circle, successLabel);

        // 1. Fade In the circle and label
        FadeTransition fadeInCircle = new FadeTransition(Duration.seconds(0.3), circle);
        fadeInCircle.setFromValue(0.0);
        fadeInCircle.setToValue(1.0);

        FadeTransition fadeInLabel = new FadeTransition(Duration.seconds(0.3), successLabel);
        fadeInLabel.setFromValue(0.0);
        fadeInLabel.setToValue(1.0);

        // 2. Scale Up both the circle and the label
        ScaleTransition scaleUpCircle = new ScaleTransition(Duration.seconds(0.3), circle);
        scaleUpCircle.setFromX(0.8);
        scaleUpCircle.setFromY(0.8);
        scaleUpCircle.setToX(1.2);  // Slightly larger scale
        scaleUpCircle.setToY(1.2);

        ScaleTransition scaleUpLabel = new ScaleTransition(Duration.seconds(0.3), successLabel);
        scaleUpLabel.setFromX(0.8);
        scaleUpLabel.setFromY(0.8);
        scaleUpLabel.setToX(1.2);
        scaleUpLabel.setToY(1.2);

        // 3. Rotate the circle
        RotateTransition rotateCircle = new RotateTransition(Duration.seconds(0.5), circle);
        rotateCircle.setByAngle(360);

        // 4. Glow effect for the circle
        Glow glow = new Glow(0.0);
        circle.setEffect(glow);

        Timeline glowTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(glow.levelProperty(), 0.0)),
                new KeyFrame(Duration.seconds(0.7), new KeyValue(glow.levelProperty(), 1.0)),
                new KeyFrame(Duration.seconds(1.0), new KeyValue(glow.levelProperty(), 0.0))
        );

        // 5. Text Color change effect for the label
        Timeline colorChangeLabel = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(successLabel.textFillProperty(), Color.BLACK)),
                new KeyFrame(Duration.seconds(0.7), new KeyValue(successLabel.textFillProperty(), Color.BLUE)),
                new KeyFrame(Duration.seconds(1.0), new KeyValue(successLabel.textFillProperty(), Color.BLACK))
        );

        // 6. Fade Out both the circle and label
        FadeTransition fadeOutCircle = new FadeTransition(Duration.seconds(0.3), circle);
        fadeOutCircle.setFromValue(1.0);
        fadeOutCircle.setToValue(0.0);
        fadeOutCircle.setDelay(Duration.seconds(0.8));  // Delay fade-out by 0.8 seconds

        FadeTransition fadeOutLabel = new FadeTransition(Duration.seconds(0.3), successLabel);
        fadeOutLabel.setFromValue(1.0);
        fadeOutLabel.setToValue(0.0);
        fadeOutLabel.setDelay(Duration.seconds(0.8));  // Delay fade-out by 0.8 seconds

        // Combine all animations in sequence
        SequentialTransition sequentialTransition = new SequentialTransition(
                fadeInCircle, scaleUpCircle, rotateCircle, fadeInLabel, scaleUpLabel, glowTimeline, colorChangeLabel, fadeOutCircle, fadeOutLabel
        );

        // Set up a click handler to stop the animation and hide everything when the user clicks
        root.setOnMouseClicked(event -> {
            sequentialTransition.stop();  // Stop the animation
            root.getChildren().removeAll(successLabel, circle);  // Remove label and circle
        });

        sequentialTransition.setOnFinished(event -> {
            root.getChildren().removeAll(successLabel, circle);  // Remove label and circle after animation
            root.setOnMouseClicked(null);  // Remove the click handler after the animation completes
        });

        // Start the animation
        sequentialTransition.play();
    }

    public void addRange(String rangeName, String startCell, String endCell) {
        try {
            // Add the range to the SheetImpl using the range name and cell bounds
            engine.getSheet().addRange(rangeName, startCell + ".." + endCell);
        } catch (InvalidRangeException | RangeExistsException e) {
            displayError(e.getMessage());
        }
    }

    public void highlightCells(Set<String> cellIds, String color) {
        gridController.highlightCells(cellIds, color);
    }

    public void deleteRange(String rangeName) {
        try {
            // Step 1: Get the set of cells that belong to the range to be deleted
            Set<String> cellIds = engine.getSheet().getRanges().get(rangeName);

            // Step 2: Delete the range in SheetImpl by range name
            engine.getSheet().deleteRange(rangeName);

            // Step 3: Clear highlights for the specific cells in the deleted range
            gridController.clearHighlights(cellIds);

            // Step 4: Show success message
            showSuccessMessage("Range '" + rangeName + "' deleted successfully.");

        } catch (RangeInUseException | RangeNotFoundException e) {
            // Step 5: Handle any errors by displaying an error message
            displayError(e.getMessage());
        }
    }

    private void showSuccessMessage(String message) {
        Stage successStage = new Stage();
        successStage.setTitle("Success");

        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new javafx.geometry.Insets(20));

        Label successMessage = new Label(message);
        Button closeButton = new Button("Close");
        closeButton.setOnAction(event -> successStage.close());

        vbox.getChildren().addAll(successMessage, closeButton);

        Scene scene = new Scene(vbox, 300, 100);
        successStage.setScene(scene);
        successStage.showAndWait();
    }

    public void updateCellValue(String cellId, String newValue) {
        if (cellId.isEmpty()) {
            displayError("You need to press on a Cell");
            return;
        }

        try {
            if (engine != null) {
                // Retrieve the cell's current original and effective values
                CellImpl cell = engine.getSheet().getCell(cellId);

                // Check if the new value is different from the current effective value
                if (cell != null && cell.getEffectiveValue().equals(newValue)) {
                    // If the values are the same, no update is needed
                    displayError("The original and effective values are the same. No update performed.");
                    return;
                }

                // If values are different, proceed with the update
                UpdateCellCommand updateCellCommand = new UpdateCellCommand(engine.getSheet(), cellId, newValue);
                updateCellCommand.execute();
                gridController.loadSheet(engine.getSheet());

            }
        } catch (Exception e) {
            displayError(e.getMessage());
        }
    }

    public void handleCellClick(String cellId, String originalValue) {
        // Update selectedCellId when a cell is clicked
        selectedCellId = cellId;
        System.out.println("Selected Cell ID: " + selectedCellId);  // Debug print

        // Existing functionality
        headerController.enableButtons();
        CellImpl cell = engine.getSheet().getCell(cellId);

        if (cell != null) {
            String cellLastUpdatedVersion = String.valueOf(cell.getLastUpdatedVersion());
            headerController.updateCellInfo(cellId, originalValue, cellLastUpdatedVersion);
        } else {
            headerController.updateCellInfo(cellId, originalValue, "N/A");
        }

        leftSideController.enableChangeCellStyleButton(cellId);

        // Highlight dependencies and influences for the selected cell
        highlightDependenciesAndInfluences(cellId);

    }

    private void highlightDependenciesAndInfluences(String cellId) {
        // Clear previous highlights
        gridController.clearHighlights();

        // Get dependencies and influences from the engine or sheet
        Set<String> dependencies = engine.getSheet().getCell(cellId).getDependencies();
        Set<String> influences = engine.getSheet().getCell(cellId).getInfluences();

        // Highlight the cells in the grid
        gridController.highlightCells(dependencies, "lightblue");
        gridController.highlightCells(influences, "lightgreen");
    }

    public void applyColumnAlignment(int column, String alignment) {
        // Get the number of rows in the sheet
        int rows = gridController.getRowCount();

        // Apply alignment to all cells in the specified column
        for (int row = 1; row <= rows; row++) {
            String cellId = gridController.getCellId(row - 1, column - 1); // get cell ID based on row and column
            gridController.applyAlignmentToCell(cellId, alignment); // Apply alignment to each cell
        }
    }

    public GridController getGridController() {
        return gridController;
    }

    private void displayError(String message) {
        // Create a new stage for displaying the error message
        Stage errorStage = new Stage();
        errorStage.setTitle("Error");

        // Create a BorderPane for better layout control
        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new javafx.geometry.Insets(20, 20, 20, 20));

        // Create a Label for the error message
        Label errorMessage = new Label(message);
        errorMessage.setTextFill(Color.RED);
        errorMessage.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        errorMessage.setWrapText(true);  // Allow the text to wrap if it's too long

        // Center the text horizontally and vertically
        BorderPane.setAlignment(errorMessage, Pos.CENTER);

        // Add a button to close the error window
        Button closeButton = new Button("Close");
        closeButton.setStyle("-fx-background-color: #FF4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px;");
        closeButton.setOnAction(e -> errorStage.close());

        // Center the button at the bottom
        BorderPane.setAlignment(closeButton, Pos.CENTER);
        borderPane.setCenter(errorMessage);
        borderPane.setBottom(closeButton);

        // Create a Scene with the BorderPane as the root node
        Scene scene = new Scene(borderPane, 500, 200);

        // Add some additional styling to make it look nicer
        scene.getStylesheets().add("https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap");  // Optional, add Google Fonts if needed
        errorStage.setScene(scene);

        // Show the error stage
        errorStage.showAndWait();
    }
}