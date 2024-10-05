package components.sheet.headerconroller;

import components.dashboard.DashboardController;
import components.sheet.appcontroller.AppController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import spreadsheet.cell.impl.CellImpl;
import spreadsheet.impl.SheetImpl;
import spreadsheet.impl.VersionImpl;

import java.io.File;
import java.io.IOException;
import java.util.Map;



public class HeaderController {

    @FXML
    private Label titleLabel;
    @FXML
    private Label cellIdValueLabel;
    @FXML
    private Button updateValueButton;
    @FXML
    private TextField originalValueTextField;
    @FXML
    private Label lastUpdatedVersionValueLabel;
    @FXML
    private Button versionSelectorButton;
    @FXML
    private Button returnToDashboardButton;  // Add the button for "Return to Dashboard"

    private AppController mainController;
    private Stage primaryStage;

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {
        // Initialize button actions here

        // Initialize the "Return to Dashboard" button action
        returnToDashboardButton.setOnAction(event -> switchToDashboard());



        // Initialize the "Update Value" button action
        updateValueButton.setOnAction(event -> handleUpdateValueAction());

        // Initialize the "Version Selector" button action
        versionSelectorButton.setOnAction(event -> handleVersionSelectorAction());
    }

    // Switch back to the Dashboard view
    private void switchToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/dashboard/dashboard.fxml"));
            Parent root = loader.load();

            // Get the DashboardController and set necessary info
            DashboardController dashboardController = loader.getController();
            dashboardController.setPrimaryStage(primaryStage);  // Pass the stage to DashboardController

            // Set the scene to Dashboard
            Scene dashboardScene = new Scene(root);
            primaryStage.setScene(dashboardScene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleVersionSelectorAction() {
        // Retrieve version history from the engine's SheetImpl
        Map<Integer, VersionImpl> versionHistory = mainController.getEngine().getSheet().getVersionHistory();

        // Create a new Stage for displaying the versions
        Stage versionStage = new Stage();
        versionStage.setTitle("Version History");

        // Set modality to block interaction with other windows
        versionStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        versionStage.initOwner(mainController.getPrimaryStage());

        GridPane versionGrid = new GridPane();
        versionGrid.setHgap(10);
        versionGrid.setVgap(10);
        versionGrid.setPadding(new javafx.geometry.Insets(10));

        // Add headers for the version table
        Label versionLabelHeader = new Label("Version");
        versionLabelHeader.setStyle("-fx-font-weight: bold;");
        versionGrid.add(versionLabelHeader, 0, 0);

        Label changedCellsLabelHeader = new Label("Changed Cells");
        changedCellsLabelHeader.setStyle("-fx-font-weight: bold;");
        versionGrid.add(changedCellsLabelHeader, 1, 0);

        Label pickLabelHeader = new Label("Pick");
        pickLabelHeader.setStyle("-fx-font-weight: bold;");
        versionGrid.add(pickLabelHeader, 2, 0);

        // Populate the grid with version data
        int rowIndex = 1;
        for (Map.Entry<Integer, VersionImpl> entry : versionHistory.entrySet()) {
            int versionNumber = entry.getKey();
            VersionImpl version = entry.getValue();

            // Display version number
            Label versionLabel = new Label(version.getName());
            versionGrid.add(versionLabel, 0, rowIndex);

            // Display number of changed cells, centered
            Label changedCellsLabel = new Label(String.valueOf(version.getChangedCellsCount()));
            changedCellsLabel.setStyle("-fx-alignment: center;");
            GridPane.setHalignment(changedCellsLabel, javafx.geometry.HPos.CENTER);  // Center horizontally
            versionGrid.add(changedCellsLabel, 1, rowIndex);

            // Add a "Pick" button for each version
            Button pickButton = new Button("Pick");
            pickButton.setOnAction(event -> {
                // Show the version for the specific cell
                showVersion(version.getName());
            });
            versionGrid.add(pickButton, 2, rowIndex);

            rowIndex++;
        }

        // Wrap the version grid in a ScrollPane
        ScrollPane scrollPane = new ScrollPane(versionGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefViewportHeight(400);  // Set a reasonable height for scrolling

        // Create the scene with scroll pane
        Scene versionScene = new Scene(scrollPane, 500, 400);
        versionStage.setScene(versionScene);
        versionStage.showAndWait();
    }

    private void showVersion(String versionName) {
        // Retrieve the version data from the engine
        VersionImpl selectedVersion = mainController.getEngine().getSheet().getVersionHistory()
                .values().stream()
                .filter(version -> version.getName().equals(versionName))
                .findFirst()
                .orElse(null);

        if (selectedVersion != null) {
            // Create a new stage to display the version's table
            Stage versionStage = new Stage();
            versionStage.setTitle("Version: " + versionName);

            // Create a GridPane for the version's table
            GridPane versionGridPane = new GridPane();
            versionGridPane.setHgap(1); // Reduce gaps to make it look like a clean table
            versionGridPane.setVgap(1);
            versionGridPane.setPadding(new javafx.geometry.Insets(10));

            // Fetch sheet properties
            SheetImpl sheet = mainController.getEngine().getSheet();
            int rows = sheet.getRows();
            int columns = sheet.getColumns();
            double columnWidth = 100; // Set a fixed column width for all columns

            // Set up column headers at the top (row 0)
            for (int col = 0; col < columns; col++) {
                Label columnHeader = new Label(String.valueOf((char) ('A' + col)));  // A, B, C...
                columnHeader.setPrefHeight(30);
                columnHeader.setPrefWidth(columnWidth);
                columnHeader.setMaxWidth(Double.MAX_VALUE);
                columnHeader.setAlignment(Pos.CENTER);
                columnHeader.setStyle("-fx-background-color: lightgray; -fx-font-weight: bold; -fx-border-color: black;");
                versionGridPane.add(columnHeader, col + 1, 0);  // Add headers in row 0
            }

            // Set up row headers at the left (column 0)
            for (int row = 0; row < rows; row++) {
                Label rowHeader = new Label(String.format("%02d", row + 1));  // 01, 02, 03...
                rowHeader.setPrefHeight(30);
                rowHeader.setPrefWidth(50);
                rowHeader.setMaxWidth(Double.MAX_VALUE);
                rowHeader.setAlignment(Pos.CENTER);
                rowHeader.setStyle("-fx-background-color: lightgray; -fx-font-weight: bold; -fx-border-color: black;");
                versionGridPane.add(rowHeader, 0, row + 1);  // Add headers in column 0
            }

            // Populate the grid with cell data from the selected version
            Map<String, CellImpl> versionCells = selectedVersion.getData();
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < columns; col++) {
                    String cellId = getRowColAsCellId(row, col);  // Get cell ID based on row and column
                    Label cellLabel = new Label();
                    cellLabel.setPrefHeight(30);
                    cellLabel.setPrefWidth(columnWidth);  // Set all cells to the same width
                    cellLabel.setAlignment(Pos.CENTER);
                    cellLabel.setMaxWidth(Double.MAX_VALUE);
                    cellLabel.setMaxHeight(Double.MAX_VALUE);
                    cellLabel.setStyle("-fx-border-color: black; -fx-background-color: white;"); // Default border and background

                    // Check if the cell exists in this version
                    if (versionCells.containsKey(cellId)) {
                        CellImpl cell = versionCells.get(cellId);
                        String cellValue = cell.getEffectiveValue();
                        String formattedValue = cellValue;

                        // Format based on the data type
                        try {
                            // Try to format as a double and check if it's an integer
                            double valueAsDouble = Double.parseDouble(cellValue);
                            if (valueAsDouble % 1 == 0) {
                                // If it's an integer (e.g., 2.0), display it without decimals
                                formattedValue = formatInteger((int) valueAsDouble);
                            } else {
                                // Otherwise, format as a double with two decimal points
                                formattedValue = formatDouble(valueAsDouble);
                            }
                        } catch (NumberFormatException ex) {
                            // If it's not a number, leave it as a string
                        }

                        cellLabel.setText(formattedValue);

                        // Apply stored text color and background color if they exist
                        StringBuilder style = new StringBuilder(cellLabel.getStyle());
                        if (cell.getTextColor() != null && !cell.getTextColor().isEmpty()) {
                            style.append("-fx-text-fill: #").append(cell.getTextColor()).append(";"); // Set text color
                        }
                        if (cell.getBackgroundColor() != null && !cell.getBackgroundColor().isEmpty()) {
                            style.append("-fx-background-color: #").append(cell.getBackgroundColor()).append(";"); // Set background color
                        }
                        cellLabel.setStyle(style.toString());
                    }

                    GridPane.setHgrow(cellLabel, Priority.ALWAYS);
                    GridPane.setVgrow(cellLabel, Priority.ALWAYS);
                    versionGridPane.add(cellLabel, col + 1, row + 1);  // Place data in the grid
                }
            }

            // Wrap the grid in a ScrollPane to allow more visibility without shrinking cells
            ScrollPane scrollPane = new ScrollPane(versionGridPane);
            scrollPane.setPrefSize(1000, 400);  // Increased size for better visibility
            scrollPane.setFitToWidth(false);
            scrollPane.setFitToHeight(false);

            // Create a scene for the version's table and display it
            Scene versionScene = new Scene(scrollPane);
            versionStage.setScene(versionScene);
            versionStage.showAndWait();
        }
    }

    // Helper method to format integers without decimal points
    public String formatInteger(int value) {
        return String.valueOf(value);
    }

    // Helper method to format doubles with two decimal points
    public String formatDouble(double value) {
        return String.format("%.2f", value);
    }





    // Helper method to convert row and column into a cell ID (like A1, B2, etc.)
    private String getRowColAsCellId(int row, int col) {
        char columnLetter = (char) ('A' + col);  // Convert column index to letter (A, B, C...)
        return columnLetter + String.valueOf(row + 1);  // Return cell ID (e.g., A1, B2, etc.)
    }


    // Format booleans as uppercase strings
    public String formatBoolean(boolean value) {
        return value ? "TRUE" : "FALSE";
    }


    @FXML
    private void handleUpdateValueAction() {
        String cellId = cellIdValueLabel.getText();
        String newValue = originalValueTextField.getText();

        if (mainController != null) {
            mainController.updateCellValue(cellId, newValue);
        }
    }

    public void updateCellInfo(String cellId, String originalValue,String LastUpdatedversion) {
        cellIdValueLabel.setText(cellId);  // Update cell ID label
        originalValueTextField.setText(originalValue);  // Display the original value of the cell
        lastUpdatedVersionValueLabel.setText(LastUpdatedversion);
    }

    public void disableButtons() {
        updateValueButton.setDisable(true);
        versionSelectorButton.setDisable(true);
        originalValueTextField.setDisable(true);
    }



    public void enableButtons() {
        updateValueButton.setDisable(false);
        versionSelectorButton.setDisable(false);
        originalValueTextField.setDisable(false);
    }

    public void enableVersionSelectorButton() {
        versionSelectorButton.setDisable(false);
    }
}
