    package components.sheet.leftsidecontroller;
    
    import components.sheet.appcontroller.AppController;
    import ExceptionHandler.InvalidFunctionInputException;
    import ExceptionHandler.NullValueException;
    import ExceptionHandler.OutOfBoundsException;
    import cammands.impl.UpdateCellCommand;
    import javafx.animation.*;
    import javafx.application.Platform;
    import javafx.fxml.FXML;
    import javafx.geometry.Insets;
    import javafx.geometry.Pos;
    import javafx.scene.Node;
    import javafx.scene.Scene;
    import javafx.scene.chart.*;
    import javafx.scene.control.*;
    import javafx.scene.layout.*;
    import javafx.stage.Stage;
    import javafx.scene.paint.Color;
    import javafx.util.Duration;
    import spreadsheet.cell.impl.CellImpl;
    
    import java.util.*;


    public class LeftSideController extends Node {
    
        private AppController mainController;
    
    
        @FXML
        private Button FilterButton;
    
        @FXML
        private Button RowSortButton;
    
        @FXML
        private MenuButton skinMenuButton1;
    
        @FXML
        private MenuButton changeCellStyleButton;
    
        @FXML
        private MenuItem textColorMenuItem;

        @FXML
        private CheckBox animationCheckbox;

        @FXML
        private MenuItem backgroundColorMenuItem;
    
        @FXML
        private MenuItem resetStyleMenuItem;
    
        @FXML
        private MenuButton columnAlignmentButton;
    
        @FXML
        private MenuItem leftAlignment;
        @FXML
        private MenuItem rightAlignment;
        @FXML
        private MenuItem centerAlignment;
    
        @FXML
        private Button dynamicAnalysisButton; // Declare the dynamic analysis button
    
        @FXML
        private MenuButton rowColDimensionsButton;
    
        @FXML
        private MenuItem changeRowHeightMenuItem;
        @FXML
        private MenuItem changeColumnWidthMenuItem;
    
        @FXML
        private Button addRangeButton;
    
        @FXML
        private Button showRangeButton;
    
        @FXML
        private Button chartDisplayButton;
    
    
        @FXML
        private Button deleteRangeButton;
    
        private String selectedCellId;
        private String selectedRangeId;
        // Add this variable to track the previous slider value
        private double previousSliderValue = 0;
    
        @Override
        public Node getStyleableNode() {
            return super.getStyleableNode();
        }
    
        public void setMainController(AppController mainController) {
            this.mainController = mainController;
        }
    
        @FXML
        public void initialize() {
    
            chartDisplayButton.setOnAction(event -> showChartSelectionDialog());
    
            FilterButton.setOnAction(event->handleFilterAction());
            RowSortButton.setOnAction(event->handleSortAction());
            // Event listeners for range buttons
            addRangeButton.setOnAction(event -> handleAddRange());
            showRangeButton.setOnAction(event -> handleShowRange());
            deleteRangeButton.setOnAction(event -> handleDeleteRange());
    
            // Listener for text and background color change
            textColorMenuItem.setOnAction(event -> openColorPicker("text"));
            backgroundColorMenuItem.setOnAction(event -> openColorPicker("background"));
    
            // Listener for resetting the cell style
            resetStyleMenuItem.setOnAction(event -> resetCellStyle());
    
            // Handle column alignment selection
            leftAlignment.setOnAction(event -> handleColumnAlignmentSelection("LEFT"));
            rightAlignment.setOnAction(event -> handleColumnAlignmentSelection("RIGHT"));
            centerAlignment.setOnAction(event -> handleColumnAlignmentSelection("CENTER"));

            // Handle row and column dimension changes
            changeRowHeightMenuItem.setOnAction(event -> handleDimensionChange("Row"));
            changeColumnWidthMenuItem.setOnAction(event -> handleDimensionChange("Column"));

            // Initialize the dynamic analysis button to open the dynamic analysis window
            dynamicAnalysisButton.setOnAction(event -> handleDynamicAnalysis());

            // Listen for animation checkbox change and apply/remove animations accordingly
            animationCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    applyAnimationsToButtons();
                } else {
                    removeAnimationsFromButtons();
                }
            });
        }
        // Apply animations to buttons in the grid
        private void applyAnimationsToButtons() {
            for (Node node : mainController.getGridController().getGridPane().getChildren()) {
                if (node instanceof Button) {
                    Button button = (Button) node;

                    // Apply translation animation (bouncing effect)
                    TranslateTransition translate = new TranslateTransition();
                    translate.setNode(button);
                    translate.setDuration(Duration.millis(500));
                    translate.setByY(10); // Move down by 10 pixels
                    translate.setCycleCount(TranslateTransition.INDEFINITE); // Loop indefinitely
                    translate.setAutoReverse(true); // Reverse after reaching the limit

                    // Apply fade animation
                    FadeTransition fade = new FadeTransition(Duration.millis(500), button);
                    fade.setFromValue(1.0); // Fully visible
                    fade.setToValue(0.5);   // Half visible
                    fade.setCycleCount(FadeTransition.INDEFINITE); // Loop indefinitely
                    fade.setAutoReverse(true); // Reverse after fading

                    // Apply rotation animation (spin effect)
                    RotateTransition rotate = new RotateTransition(Duration.millis(1000), button);
                    rotate.setByAngle(360); // Rotate full circle
                    rotate.setCycleCount(RotateTransition.INDEFINITE); // Loop indefinitely

                    // Apply scale animation (pulse effect)
                    ScaleTransition scale = new ScaleTransition(Duration.millis(500), button);
                    scale.setToX(1.2); // Enlarge horizontally by 20%
                    scale.setToY(1.2); // Enlarge vertically by 20%
                    scale.setCycleCount(ScaleTransition.INDEFINITE); // Loop indefinitely
                    scale.setAutoReverse(true); // Reverse after reaching the scale

                    // Apply color transition manually using Timeline
                    Timeline colorTransition = new Timeline(
                            new KeyFrame(Duration.ZERO, new KeyValue(button.styleProperty(), "-fx-background-color: #ADD8E6")), // LightBlue
                            new KeyFrame(Duration.seconds(1), new KeyValue(button.styleProperty(), "-fx-background-color: #FFB6C1")) // LightPink
                    );
                    colorTransition.setCycleCount(Timeline.INDEFINITE); // Loop indefinitely
                    colorTransition.setAutoReverse(true); // Reverse after reaching the second color

                    // Apply earthquake (random shake) effect
                    TranslateTransition earthquake = new TranslateTransition(Duration.millis(100), button);
                    earthquake.setByX(5); // Shake by 5 pixels left and right
                    earthquake.setCycleCount(TranslateTransition.INDEFINITE);
                    earthquake.setAutoReverse(true); // Reverse after shaking
                    earthquake.setInterpolator(Interpolator.LINEAR);

                    // Randomize shaking direction (left-right, up-down)
                    earthquake.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                        earthquake.setByX(Math.random() * 10 - 5); // Shake left and right randomly
                        earthquake.setByY(Math.random() * 10 - 5); // Shake up and down randomly
                    });

                    // Start all animations
                    translate.play();
                    fade.play();
                    rotate.play();
                    scale.play();
                    colorTransition.play();
                    earthquake.play();

                    // Store animations in the button's properties for later access (used when stopping animations)
                    button.getProperties().put("translate", translate);
                    button.getProperties().put("fade", fade);
                    button.getProperties().put("rotate", rotate);
                    button.getProperties().put("scale", scale);
                    button.getProperties().put("colorTransition", colorTransition);
                    button.getProperties().put("earthquake", earthquake);
                }
            }
        }

        // Remove animations from buttons
        private void removeAnimationsFromButtons() {
            for (Node node : mainController.getGridController().getGridPane().getChildren()) {
                if (node instanceof Button) {
                    Button button = (Button) node;

                    // Stop all animations safely
                    TranslateTransition translate = (TranslateTransition) button.getProperties().get("translate");
                    FadeTransition fade = (FadeTransition) button.getProperties().get("fade");
                    RotateTransition rotate = (RotateTransition) button.getProperties().get("rotate");
                    ScaleTransition scale = (ScaleTransition) button.getProperties().get("scale");
                    Timeline colorTransition = (Timeline) button.getProperties().get("colorTransition");
                    TranslateTransition earthquake = (TranslateTransition) button.getProperties().get("earthquake");

                    if (translate != null) translate.stop();
                    if (fade != null) fade.stop();
                    if (rotate != null) rotate.stop();
                    if (scale != null) scale.stop();
                    if (colorTransition != null) colorTransition.stop();
                    if (earthquake != null) earthquake.stop();

                    // Clear the animations stored in button properties
                    button.getProperties().remove("translate");
                    button.getProperties().remove("fade");
                    button.getProperties().remove("rotate");
                    button.getProperties().remove("scale");
                    button.getProperties().remove("colorTransition");
                    button.getProperties().remove("earthquake");

                    // Reset the button's transformation and opacity to the default state
                    button.setTranslateY(0); // Reset translation (stop bouncing)
                    button.setRotate(0);     // Reset rotation (stop spinning)
                    button.setScaleX(1);     // Reset scale X to default size
                    button.setScaleY(1);     // Reset scale Y to default size
                    button.setOpacity(1.0);  // Reset opacity to fully visible
                    button.setTranslateX(0); // Reset X translation (stop shaking)

                    // Reset background color to default by clearing the custom style
                    button.setStyle(""); // Clears custom style such as background color or text color

                    // Reapply the black border to the buttons
                    button.setStyle("-fx-border-color: black; -fx-border-width: 1px;"); // Black border

                    // Optional: You can reapply a default background and text color if needed
                    // button.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: black; -fx-border-color: black; -fx-border-width: 1px;");
                }
            }
        }




        private void showChartSelectionDialog() {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Select Chart Settings");
            dialog.setHeaderText("Choose the cell ranges for X and Y axes, and select the chart type.");
    
            // Create a GridPane for input fields
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));
    
            // Inputs for X and Y ranges
            TextField xAxisRange = new TextField();
            TextField yAxisRange = new TextField();
            ComboBox<String> chartTypeComboBox = new ComboBox<>();
            chartTypeComboBox.getItems().addAll("Bar Chart", "Line Chart");
    
            // Add inputs to the grid
            grid.add(new Label("X-Axis Range (e.g., A1..A5):"), 0, 0);
            grid.add(xAxisRange, 1, 0);
            grid.add(new Label("Y-Axis Range (e.g., B1..B5):"), 0, 1);
            grid.add(yAxisRange, 1, 1);
            grid.add(new Label("Chart Type:"), 0, 2);
            grid.add(chartTypeComboBox, 1, 2);
    
            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    
            dialog.showAndWait().ifPresent(result -> {
                if (result == ButtonType.OK) {
                    String xRange = xAxisRange.getText();
                    String yRange = yAxisRange.getText();
                    String chartType = chartTypeComboBox.getValue();
    
                    if (xRange.isEmpty() || yRange.isEmpty() || chartType == null) {
                        showError("Please fill in all fields.");
                        return;
                    }
    
                    // Call method to generate the chart
                    generateChart(xRange, yRange, chartType);
                }
            });
        }
    
        private void generateChart(String xRange, String yRange, String chartType) {
            try {
                // Get the data from the ranges
                List<String> xData = getDataFromRange(xRange);
                List<String> yData = getDataFromRange(yRange);
    
                if (xData.size() != yData.size()) {
                    showError("The X and Y ranges must have the same number of cells.");
                    return;
                }
    
                // Create a new stage for the chart
                Stage chartStage = new Stage();
                chartStage.setTitle("Generated Chart");
    
                if ("Bar Chart".equals(chartType)) {
                    BarChart<String, Number> barChart = createBarChart(xData, yData);
                    Scene scene = new Scene(barChart, 800, 600);
                    chartStage.setScene(scene);
                } else if ("Line Chart".equals(chartType)) {
                    LineChart<String, Number> lineChart = createLineChart(xData, yData);
                    Scene scene = new Scene(lineChart, 800, 600);
                    chartStage.setScene(scene);
                }
    
                chartStage.show();
    
            } catch (Exception e) {
                showError("Error generating chart: " + e.getMessage());
            }
        }
    
        private List<String> getDataFromRange(String range) {
            String[] parts = range.split("\\.\\.");
            String startCell = parts[0];
            String endCell = parts[1];
    
            Set<String> cellIds = mainController.getGridController().getRange(startCell, endCell);
    
            List<String> data = new ArrayList<>();
            for (String cellId : cellIds) {
                Object value = mainController.getGridController().getCellValue(cellId);
                if (value instanceof String) {
                    data.add((String) value);
                }
            }
            return data;
        }
    
        private BarChart<String, Number> createBarChart(List<String> xData, List<String> yData) {
            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel("X-Axis");
    
            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Y-Axis");
    
            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
    
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            for (int i = 0; i < xData.size(); i++) {
                series.getData().add(new XYChart.Data<>(xData.get(i), Double.parseDouble(yData.get(i))));
            }
    
            barChart.getData().add(series);
            return barChart;
        }
    
        private LineChart<String, Number> createLineChart(List<String> xData, List<String> yData) {
            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel("X-Axis");
    
            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Y-Axis");
    
            LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
    
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            for (int i = 0; i < xData.size(); i++) {
                series.getData().add(new XYChart.Data<>(xData.get(i), Double.parseDouble(yData.get(i))));
            }
    
            lineChart.getData().add(series);
            return lineChart;
        }
    
        private void handleDeleteRange() {
            Map<String, Set<String>> rangeMap = mainController.getRangeList(); // Get all the existing ranges
    
            // Debug print to check if method is triggered and rangeMap has data
            System.out.println("Delete Range button pressed. Range Map: " + rangeMap);
    
            if (rangeMap.isEmpty()) {
                showError("No ranges available to delete.");
                return;
            }
    
            // Create a new Stage for range selection
            Stage rangeSelectionStage = new Stage();
            rangeSelectionStage.setTitle("Select Range to Delete");
    
            VBox vbox = new VBox();
            vbox.setSpacing(10);
            vbox.setPadding(new javafx.geometry.Insets(20));
    
            // Add buttons for each range in rangeMap
            for (String rangeName : rangeMap.keySet()) {
                System.out.println("Adding range button for: " + rangeName);  // Debug message
                Button rangeButton = new Button(rangeName);
    
                // Set up event handler for range deletion
                rangeButton.setOnAction(event -> {
                    System.out.println("Attempting to delete range: " + rangeName); // Debug message
                    mainController.deleteRange(rangeName); // Call deleteRange method in AppController
                    rangeSelectionStage.close(); // Close the selection stage after deletion
                });
    
                vbox.getChildren().add(rangeButton); // Add button to VBox
            }
    
            ScrollPane scrollPane = new ScrollPane(vbox);
            scrollPane.setFitToWidth(true);
    
            Scene scene = new Scene(scrollPane, 300, 400);
            rangeSelectionStage.setScene(scene);
            rangeSelectionStage.showAndWait(); // Wait until the user selects a range
        }
    
        private void handleAddRange() {
            // Prompt the user for the range name
            TextInputDialog nameDialog = new TextInputDialog();
            nameDialog.setTitle("Add Range");
            nameDialog.setHeaderText("Enter a name for the range:");
            nameDialog.setContentText("Range Name:");
    
            nameDialog.showAndWait().ifPresent(rangeName -> {
                if (rangeName.trim().isEmpty()) {
                    showError("Invalid range name. Please provide a valid name.");
                    return;
                }
    
                // Prompt the user to input the start and end of the range in the format A1..B5
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Add Range Limits");
                dialog.setHeaderText("Enter the start and end range in the format (e.g., A1..B5)");
                dialog.setContentText("Range:");
    
                dialog.showAndWait().ifPresent(range -> {
                    // Process the entered range
                    String[] rangeParts = range.split("\\.\\.");  // Split based on ".."
                    if (rangeParts.length == 2) {
                        String selectedRangeStart = rangeParts[0].trim();
                        String selectedRangeEnd = rangeParts[1].trim();
    
                        // Call the addRange method in AppController
                        mainController.addRange(rangeName, selectedRangeStart, selectedRangeEnd);
                    } else {
                        showError("Invalid range format. Please enter the range in the format A1..B5.");
                    }
                });
            });
        }
    
        private void handleShowRange() {
            Map<String, Set<String>> rangeMap = mainController.getRangeList(); // Get all the existing ranges
            if (rangeMap.isEmpty()) {
                showError("No ranges available. Please add a range first.");
                return;
            }
    
            // Create a new Stage for range selection
            Stage rangeSelectionStage = new Stage();
            rangeSelectionStage.setTitle("Select Range to Show");
    
            VBox vbox = new VBox();
            vbox.setSpacing(10);
            vbox.setPadding(new javafx.geometry.Insets(20));
    
            for (String rangeName : rangeMap.keySet()) {
                Button rangeButton = new Button(rangeName);
                rangeButton.setOnAction(event -> {
                    Set<String> cellIds = rangeMap.get(rangeName);
                    if (cellIds != null) {
                        // Clear any existing highlights
                        mainController.getGridController().clearHighlights();
    
                        // Highlight the selected range cells
                        mainController.highlightCells(cellIds, "LAVENDER");
                    }
                    rangeSelectionStage.close();
                });
                vbox.getChildren().add(rangeButton);
            }
    
            ScrollPane scrollPane = new ScrollPane(vbox);
            scrollPane.setFitToWidth(true);
    
            Scene scene = new Scene(scrollPane, 300, 400);
            rangeSelectionStage.setScene(scene);
            rangeSelectionStage.showAndWait(); // Wait until the user selects a range
        }
    
        private void handleSortAction() {
            // Step 1: Create a popup window for user input (Range and Columns)
            Stage sortStage = new Stage();
            sortStage.setTitle("Sort Range in Copied Sheet");
    
            VBox vbox = new VBox();
            vbox.setSpacing(10);
            vbox.setPadding(new Insets(20));
    
            // Step 2: Input for the range (e.g., A1..B5)
            Label rangeLabel = new Label("Enter range to sort (e.g., A1..B5):");
            TextField rangeInput = new TextField();
            vbox.getChildren().addAll(rangeLabel, rangeInput);
    
            // Step 3: Placeholder for selected column (store only one column)
            String[] selectedColumn = new String[1];  // Array to store only one selected column
    
            // Step 4: Button to trigger column selection
            Button selectColumnButton = new Button("Select Column for Sorting");
            vbox.getChildren().add(selectColumnButton);
    
            // Step 5: Label to display the selected column
            Label selectedColumnLabel = new Label("Selected column: None");
            vbox.getChildren().add(selectedColumnLabel);
    
            selectColumnButton.setOnAction(event -> {
                String range = rangeInput.getText();
    
                // Validate the range before opening the column selection dialog
                if (range == null || range.isEmpty() || !validateRange(range)) {
                    showError("Invalid range. Please enter a valid range in the format A1..B5.");
                    return;  // Stop if the range is invalid
                }
    
                Stage columnSelectionStage = new Stage();
                columnSelectionStage.setTitle("Select Column");
    
                VBox columnBox = new VBox();
                columnBox.setSpacing(10);
                columnBox.setPadding(new Insets(20));
    
                // Generate column buttons dynamically based on available columns in the selected range
                String[] availableColumns = getAvailableColumnsInRange(range);
    
                // Allow only one column to be selected
                for (int i = 0; i < availableColumns.length; i++) {
                    String column = availableColumns[i];
                    int columnIndex = i;  // Capture the actual index of the column
    
                    Button columnButton = new Button(column);
                    columnButton.setOnAction(colEvent -> {
                        selectedColumn[0] = column + ":" + columnIndex;  // Store the selected column with its index
                        selectedColumnLabel.setText("Selected column: " + column);  // Display the selected column
                        selectColumnButton.setDisable(true);  // Disable further selection after one column is selected
                        columnSelectionStage.close();
                    });
                    columnButton.setPrefWidth(50); // Set a fixed width for buttons
                    columnBox.getChildren().add(columnButton);
                }
    
                ScrollPane scrollPane = new ScrollPane(columnBox);
                scrollPane.setFitToWidth(true);
                scrollPane.setPrefViewportHeight(200);
    
                Scene columnScene = new Scene(scrollPane, 300, 300);
                columnSelectionStage.setScene(columnScene);
                columnSelectionStage.showAndWait();
            });
    
            // Step 6: Button to trigger the sorting
            Button sortButton = new Button("Sort");
            sortButton.setOnAction(event -> {
                String range = rangeInput.getText();
                if (validateRange(range)) {  // Validate the range before proceeding
                    if (selectedColumn[0] != null) {  // Ensure a column has been selected
                        // Step 7: Create a copy of the entire sheet data
                        Map<String, List<CellImpl>> copiedSheetData = copyDataFromSheet();
    
                        // Step 8: Extract and sort the data within the selected range in the copied sheet
                        Map<String, List<CellImpl>> copiedRangeData = copyDataFromRangeInSheetCopy(copiedSheetData, range);
                        List<List<CellImpl>> sortedRows = sortRowsByColumn(copiedRangeData, selectedColumn[0]);  // Sort by the selected column
    
                        // Step 9: Replace the range in the copied sheet with sorted data
                        updateSheetCopyWithSortedRange(copiedSheetData, sortedRows, range);
    
                        // Step 10: Display the entire sorted sheet (copied sheet, with the sorted range)
                        showSortedSheet(copiedSheetData);
    
                        sortStage.close();  // Close the popup after sorting
                    } else {
                        showError("Please select a column for sorting.");
                    }
                } else {
                    showError("Invalid range. Please enter a valid range in the format A1..B5.");
                }
            });
            vbox.getChildren().add(sortButton);
    
            Scene scene = new Scene(vbox, 300, 300);
            sortStage.setScene(scene);
            sortStage.showAndWait();
        }
    
        private List<List<CellImpl>> sortRowsByColumn(Map<String, List<CellImpl>> data, String column) {
            List<Map.Entry<String, List<CellImpl>>> rows = new ArrayList<>(data.entrySet());
    
            // Extract the column index from the selected column
            String[] columnInfo = column.split(":");
            int columnIndex = Integer.parseInt(columnInfo[1]);
    
            // Sort rows based on the selected column
            rows.sort((row1, row2) -> {
                try {
                    double value1 = Double.parseDouble(row1.getValue().get(columnIndex).getEffectiveValue());
                    double value2 = Double.parseDouble(row2.getValue().get(columnIndex).getEffectiveValue());
                    return Double.compare(value1, value2);  // Sort in ascending order
                } catch (NumberFormatException e) {
                    // If data is not numeric, compare as strings
                    String value1 = row1.getValue().get(columnIndex).getEffectiveValue();
                    String value2 = row2.getValue().get(columnIndex).getEffectiveValue();
                    return value1.compareTo(value2);  // Sort in ascending order
                }
            });
    
            // Extract the sorted rows
            List<List<CellImpl>> sortedRows = new ArrayList<>();
            for (Map.Entry<String, List<CellImpl>> entry : rows) {
                sortedRows.add(entry.getValue());
            }
    
            return sortedRows;
        }
    
    
        private String[] getAvailableColumnsInRange(String range) {
            // Split the range into top-left and bottom-right cells
            String[] rangeParts = range.split("\\.\\.");
            if (rangeParts.length != 2) {
                showError("Invalid range format.");
                return new String[0];  // Return an empty array if the range is invalid
            }
    
            // Parse the top-left and bottom-right cell coordinates
            String topLeft = rangeParts[0].trim();
            String bottomRight = rangeParts[1].trim();
            int[] topLeftCoords = parseCellId(topLeft);
            int[] bottomRightCoords = parseCellId(bottomRight);
    
            if (topLeftCoords == null || bottomRightCoords == null) {
                showError("Invalid cell references in the range.");
                return new String[0];  // Return an empty array if the cell coordinates are invalid
            }
    
            // Calculate the number of columns within the range
            int startColumnIndex = topLeftCoords[0];
            int endColumnIndex = bottomRightCoords[0];
    
            // Create a list to store available columns in the range
            List<String> availableColumns = new ArrayList<>();
    
            // Loop through the column indices and convert them to column letters
            for (int col = startColumnIndex; col <= endColumnIndex; col++) {
                availableColumns.add(String.valueOf((char) ('A' + col)));  // Convert column index to letter (A, B, C, ...)
            }
    
            // Convert the list to an array and return it
            return availableColumns.toArray(new String[0]);
        }
    
        private Map<String, List<CellImpl>> copyDataFromSheet() {
            int totalRows = mainController.getGridController().getRowCount();
            int totalColumns = mainController.getGridController().getColumnCount();
    
            Map<String, List<CellImpl>> copiedData = new LinkedHashMap<>();
    
            for (int row = 0; row < totalRows; row++) {
                List<CellImpl> rowData = new ArrayList<>();
                for (int col = 0; col < totalColumns; col++) {
                    String cellId = mainController.getGridController().getCellId(row, col);
                    CellImpl cell = mainController.getGridController().getCell(cellId); // Get the actual CellImpl object
                    rowData.add(cell);
                }
                copiedData.put("Row" + (row + 1), rowData);
            }
    
            return copiedData;  // Return the copied sheet data with CellImpl objects
        }
    
    
        private Map<String, List<CellImpl>> copyDataFromRangeInSheetCopy(Map<String, List<CellImpl>> sheetData, String range) {
            String[] rangeParts = range.split("\\.\\.");
            if (rangeParts.length != 2) {
                showError("Invalid range format.");
                return null;
            }
    
            String topLeft = rangeParts[0].trim();
            String bottomRight = rangeParts[1].trim();
            int[] topLeftCoords = parseCellId(topLeft);
            int[] bottomRightCoords = parseCellId(bottomRight);
    
            if (topLeftCoords == null || bottomRightCoords == null) {
                showError("Invalid cell references in the range.");
                return null;
            }
    
            Map<String, List<CellImpl>> copiedRangeData = new LinkedHashMap<>();
    
            for (int row = topLeftCoords[1]; row <= bottomRightCoords[1]; row++) {
                List<CellImpl> rowData = new ArrayList<>(sheetData.get("Row" + (row + 1)).subList(topLeftCoords[0], bottomRightCoords[0] + 1));
                copiedRangeData.put("Row" + (row + 1), rowData);
            }
    
            return copiedRangeData;
        }
    
        private void updateSheetCopyWithSortedRange(Map<String, List<CellImpl>> sheetData, List<List<CellImpl>> sortedRows, String range) {
            // Split the range into top-left and bottom-right cells
            String[] rangeParts = range.split("\\.\\.");
            int[] topLeftCoords = parseCellId(rangeParts[0]);
    
            // Insert the sorted rows back into the copied sheet data
            for (int row = topLeftCoords[1]; row < topLeftCoords[1] + sortedRows.size(); row++) {
                List<CellImpl> sortedRowData = sortedRows.get(row - topLeftCoords[1]);
                for (int col = 0; col < sortedRowData.size(); col++) {
                    List<CellImpl> rowData = sheetData.get("Row" + (row + 1));
                    rowData.set(topLeftCoords[0] + col, sortedRowData.get(col));  // Update the sorted data in the copied sheet
                }
            }
        }
    
        @FXML
        private void handleFilterAction() {
            Stage filterStage = new Stage();
            filterStage.setTitle("Filter Data");
    
            VBox vbox = new VBox();
            vbox.setSpacing(10);
            vbox.setPadding(new Insets(20));
    
            // Step 1: Input for the range (e.g., A1..B5)
            Label rangeLabel = new Label("Enter range to filter (e.g., A1..B5):");
            TextField rangeInput = new TextField();
            vbox.getChildren().addAll(rangeLabel, rangeInput);
    
            // Step 2: Button to trigger column selection
            Button addColumnButton = new Button("Add Column for Filtering");
            vbox.getChildren().add(addColumnButton);
    
            // Container for displaying columns and selected values
            VBox columnsSelectionBox = new VBox();
            columnsSelectionBox.setSpacing(10); // Space between columns
    
            // Wrap the columnsSelectionBox in a ScrollPane so the user can scroll through many columns
            ScrollPane scrollPane = new ScrollPane(columnsSelectionBox);
            scrollPane.setFitToWidth(true); // Ensure it stretches horizontally
            scrollPane.setPrefViewportHeight(200); // Set the preferred height for the visible area
    
            vbox.getChildren().add(scrollPane);
    
            // Step 3: Button to trigger filtering
            Button filterButton = new Button("Filter");
            vbox.getChildren().add(filterButton);
    
            addColumnButton.setOnAction(event -> {
                // Popup to select a column within the range
                Stage columnSelectionStage = new Stage();
                columnSelectionStage.setTitle("Select Column");
    
                VBox columnBox = new VBox();
                columnBox.setSpacing(10);
                columnBox.setPadding(new Insets(20));
    
                // Get available columns in the range
                String[] availableColumns = getAvailableColumnsInRange(rangeInput.getText());
    
                for (String column : availableColumns) {
                    Button columnButton = new Button(column);
    
                    columnButton.setOnAction(colEvent -> {
                        // Add selected column and its unique values for filtering
                        VBox columnFilterBox = new VBox();
                        Label columnLabel = new Label("Column: " + column);
                        columnFilterBox.getChildren().add(columnLabel);
    
                        // Get unique values for this column
                        List<String> uniqueValues = getUniqueValuesFromColumn(rangeInput.getText(), column);
    
                        // Display the unique values as checkboxes
                        VBox valuesCheckboxes = new VBox();
                        for (String value : uniqueValues) {
                            CheckBox valueCheckbox = new CheckBox(value);
                            valuesCheckboxes.getChildren().add(valueCheckbox);
                        }
    
                        columnFilterBox.getChildren().add(valuesCheckboxes);
                        columnsSelectionBox.getChildren().add(columnFilterBox);
                        columnSelectionStage.close();
                    });
    
                    columnBox.getChildren().add(columnButton);
                }
    
                ScrollPane columnScrollPane = new ScrollPane(columnBox);
                columnScrollPane.setFitToWidth(true);
                columnScrollPane.setPrefViewportHeight(200);
    
                Scene columnScene = new Scene(columnScrollPane, 300, 300);
                columnSelectionStage.setScene(columnScene);
                columnSelectionStage.showAndWait();
            });
    
            filterButton.setOnAction(event -> {
                String range = rangeInput.getText();
    
                if (validateRange(range)) {
                    Map<String, List<String>> selectedColumnFilters = new LinkedHashMap<>();
    
                    // Loop through each column filter box to gather selected values
                    for (Node node : columnsSelectionBox.getChildren()) {
                        if (node instanceof VBox) {
                            VBox columnFilterBox = (VBox) node;
                            Label columnLabel = (Label) columnFilterBox.getChildren().get(0);
                            String column = columnLabel.getText().replace("Column: ", "");
    
                            List<String> selectedValues = new ArrayList<>();
                            VBox valuesCheckboxes = (VBox) columnFilterBox.getChildren().get(1);
                            for (Node valueNode : valuesCheckboxes.getChildren()) {
                                if (valueNode instanceof CheckBox && ((CheckBox) valueNode).isSelected()) {
                                    selectedValues.add(((CheckBox) valueNode).getText());
                                }
                            }
    
                            if (!selectedValues.isEmpty()) {
                                selectedColumnFilters.put(column, selectedValues);
                            }
                        }
                    }
    
                    if (!selectedColumnFilters.isEmpty()) {
                        // Perform filtering
                        Map<String, List<CellImpl>> copiedSheetData = copyDataFromSheet(); // Copy entire sheet
                        Map<String, List<CellImpl>> copiedRangeData = copyDataFromRangeInSheetCopy(copiedSheetData, range); // Copy range data
    
                        // Filter rows based on all selected columns and values
                        List<List<CellImpl>> filteredRows = filterRowsByMultipleColumns(copiedRangeData, selectedColumnFilters, range);
    
                        // Update the sheet with the filtered rows
                        updateSheetCopyWithFilteredRange(copiedSheetData, filteredRows, range);
    
                        // Show the filtered data
                        showFilteredSheet(copiedSheetData);
                        filterStage.close();
                    } else {
                        showError("No column filters selected.");
                    }
                } else {
                    showError("Invalid range.");
                }
            });
    
            Scene scene = new Scene(vbox, 400, 600);
            filterStage.setScene(scene);
            filterStage.showAndWait();
        }
    
        private void updateSheetCopyWithFilteredRange(Map<String, List<CellImpl>> sheetData, List<List<CellImpl>> filteredRows, String range) {
            String[] rangeParts = range.split("\\.\\.");
            int[] topLeftCoords = parseCellId(rangeParts[0]);
    
            for (int row = 0; row < filteredRows.size(); row++) {
                List<CellImpl> filteredRowData = filteredRows.get(row);
                int sheetRow = topLeftCoords[1] + row;
    
                for (int col = 0; col < filteredRowData.size(); col++) {
                    int sheetColumn = topLeftCoords[0] + col;
                    List<CellImpl> rowData = sheetData.get("Row" + (sheetRow + 1));
                    rowData.set(sheetColumn, filteredRowData.get(col));  // Update the cell with the filtered data
                }
            }
        }
    
        private List<List<CellImpl>> filterRowsByMultipleColumns(Map<String, List<CellImpl>> data, Map<String, List<String>> selectedColumnFilters, String range) {
            List<List<CellImpl>> filteredRows = new ArrayList<>();
    
            for (List<CellImpl> rowData : data.values()) {
                boolean matchAllConditions = true;
    
                for (Map.Entry<String, List<String>> columnFilter : selectedColumnFilters.entrySet()) {
                    String column = columnFilter.getKey();
                    List<String> selectedValues = columnFilter.getValue();
    
                    int sheetColumnIndex = getColumnAbsoluteIndex(column);
                    String[] rangeParts = range.split("\\.\\.");
                    String startCell = rangeParts[0].trim();
                    int[] startCellCoords = parseCellId(startCell);
                    int rangeStartColumn = startCellCoords[0];
                    int relativeColumnIndex = sheetColumnIndex - rangeStartColumn;
    
                    if (relativeColumnIndex >= 0 && relativeColumnIndex < rowData.size()) {
                        CellImpl cell = rowData.get(relativeColumnIndex);
                        String cellValue = cell.getEffectiveValue();
                        if (!selectedValues.contains(cellValue)) {
                            matchAllConditions = false;
                            break;
                        }
                    }
                }
    
                if (matchAllConditions) {
                    filteredRows.add(new ArrayList<>(rowData));  // Keep the row if all conditions match
                } else {
                    List<CellImpl> emptyRow = new ArrayList<>(Collections.nCopies(rowData.size(), null));  // Create an empty row with null cells
                    filteredRows.add(emptyRow);
                }
            }
    
            return filteredRows;
        }
    
        private List<String> getUniqueValuesFromColumn(String range, String column) {
            Map<String, List<CellImpl>> copiedSheetData = copyDataFromSheet();
            Map<String, List<CellImpl>> copiedRangeData = copyDataFromRangeInSheetCopy(copiedSheetData, range);
    
            String[] rangeParts = range.split("\\.\\.");
            String startCell = rangeParts[0];
            String startColumn = getColumnPart(startCell);
    
            int columnIndex = getRelativeColumnIndex(startColumn, column);
            Set<String> uniqueValuesSet = new HashSet<>();
    
            for (List<CellImpl> rowData : copiedRangeData.values()) {
                if (columnIndex < rowData.size()) {
                    CellImpl cell = rowData.get(columnIndex);
                    if (cell != null && cell.getEffectiveValue() != null && !cell.getEffectiveValue().trim().isEmpty()) {
                        uniqueValuesSet.add(cell.getEffectiveValue());
                    }
                }
            }
    
            List<String> uniqueValuesList = new ArrayList<>(uniqueValuesSet);
            Collections.sort(uniqueValuesList);
            return uniqueValuesList;
        }
        // Helper method to get the column part from a cell reference, e.g., "B3" -> "B"
        private String getColumnPart(String cellId) {
            return cellId.replaceAll("\\d", "");  // Remove digits to get the column part
        }
    
        // Helper method to calculate the column index relative to the starting column in the range
        private int getRelativeColumnIndex(String startColumn, String targetColumn) {
            int startIndex = getColumnAbsoluteIndex(startColumn);
            int targetIndex = getColumnAbsoluteIndex(targetColumn);
            return targetIndex - startIndex;  // Return the relative index
        }
    
        // Helper method to calculate the absolute column index (e.g., "A" -> 0, "B" -> 1, "AA" -> 26, etc.)
        private int getColumnAbsoluteIndex(String column) {
            int index = 0;
            for (int i = 0; i < column.length(); i++) {
                index = index * 26 + (column.charAt(i) - 'A' + 1);
            }
            return index - 1;  // Adjust for 0-based index
        }
    
        private void showFilteredSheet(Map<String, List<CellImpl>> sheetData) {
            Stage filteredStage = new Stage();
            filteredStage.setTitle("Filtered Data");
    
            GridPane filteredGridPane = new GridPane();
            filteredGridPane.setHgap(1);
            filteredGridPane.setVgap(1);
            filteredGridPane.setPadding(new Insets(10));
    
            int totalRows = sheetData.size();
            int totalColumns = sheetData.values().iterator().next().size();
    
            double columnWidth = 100;
            for (int col = 0; col < totalColumns; col++) {
                Label columnHeader = new Label(String.valueOf((char) ('A' + col)));
                columnHeader.setPrefHeight(30);
                columnHeader.setPrefWidth(columnWidth);
                columnHeader.setAlignment(Pos.CENTER);
                columnHeader.setStyle("-fx-background-color: lightgray; -fx-font-weight: bold; -fx-border-color: black;");
                filteredGridPane.add(columnHeader, col + 1, 0);
            }
    
            for (int row = 0; row < totalRows; row++) {
                Label rowHeader = new Label("Row " + (row + 1));
                rowHeader.setPrefHeight(30);
                rowHeader.setPrefWidth(50);
                rowHeader.setAlignment(Pos.CENTER);
                rowHeader.setStyle("-fx-background-color: lightgray; -fx-font-weight: bold; -fx-border-color: black;");
                filteredGridPane.add(rowHeader, 0, row + 1);
            }
    
            for (int row = 0; row < totalRows; row++) {
                List<CellImpl> rowData = sheetData.get("Row" + (row + 1));
                for (int col = 0; col < totalColumns; col++) {
                    CellImpl cell = rowData.get(col);
                    String cellValue = (cell != null) ? cell.getEffectiveValue() : "";
    
                    Label cellLabel = new Label(cellValue);
                    cellLabel.setPrefHeight(30);
                    cellLabel.setPrefWidth(columnWidth);
                    cellLabel.setAlignment(Pos.CENTER);
                    cellLabel.setStyle("-fx-border-color: black;");
    
                    // Apply background color
                    String backgroundColor = (cell != null && cell.getBackgroundColor() != null && !cell.getBackgroundColor().isEmpty())
                            ? "#" + cell.getBackgroundColor() : "#FFFFFF";
                    cellLabel.setStyle(cellLabel.getStyle() + "-fx-background-color: " + backgroundColor + ";");
    
                    // Apply text color
                    String textColor = (cell != null && cell.getTextColor() != null && !cell.getTextColor().isEmpty())
                            ? "#" + cell.getTextColor() : "#000000";
                    cellLabel.setTextFill(Color.web(textColor));
    
                    filteredGridPane.add(cellLabel, col + 1, row + 1);
                }
            }
    
            // Wrap the grid in a ScrollPane without shrinking content
            ScrollPane scrollPane = new ScrollPane(filteredGridPane);
            scrollPane.setPrefSize(1000, 400); // Set preferred size
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Enable horizontal scroll as needed
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Enable vertical scroll as needed
    
            Scene filteredScene = new Scene(scrollPane);
            filteredStage.setScene(filteredScene);
            filteredStage.showAndWait();
        }

        private void showSortedSheet(Map<String, List<CellImpl>> sheetData) {
            Stage sortedStage = new Stage();
            sortedStage.setTitle("Sorted Data");
    
            GridPane sortedGridPane = new GridPane();
            sortedGridPane.setHgap(1);
            sortedGridPane.setVgap(1);
            sortedGridPane.setPadding(new Insets(10));
    
            int totalRows = sheetData.size();
            int totalColumns = sheetData.values().iterator().next().size();
    
            double columnWidth = 100;
            for (int col = 0; col < totalColumns; col++) {
                Label columnHeader = new Label(String.valueOf((char) ('A' + col)));
                columnHeader.setPrefHeight(30);
                columnHeader.setPrefWidth(columnWidth);
                columnHeader.setAlignment(Pos.CENTER);
                columnHeader.setStyle("-fx-background-color: lightgray; -fx-font-weight: bold; -fx-border-color: black;");
                sortedGridPane.add(columnHeader, col + 1, 0);
            }
    
            for (int row = 0; row < totalRows; row++) {
                Label rowHeader = new Label("Row " + (row + 1));
                rowHeader.setPrefHeight(30);
                rowHeader.setPrefWidth(50);
                rowHeader.setAlignment(Pos.CENTER);
                rowHeader.setStyle("-fx-background-color: lightgray; -fx-font-weight: bold; -fx-border-color: black;");
                sortedGridPane.add(rowHeader, 0, row + 1);
            }
    
            for (int row = 0; row < totalRows; row++) {
                List<CellImpl> rowData = sheetData.get("Row" + (row + 1));
                for (int col = 0; col < totalColumns; col++) {
                    CellImpl cell = rowData.get(col);
                    String cellValue = (cell != null) ? cell.getEffectiveValue() : "";
    
                    Label cellLabel = new Label(cellValue);
                    cellLabel.setPrefHeight(30);
                    cellLabel.setPrefWidth(columnWidth);
                    cellLabel.setAlignment(Pos.CENTER);
                    cellLabel.setStyle("-fx-border-color: black;");
    
                    // Apply background color
                    String backgroundColor = (cell != null && cell.getBackgroundColor() != null && !cell.getBackgroundColor().isEmpty())
                            ? "#" + cell.getBackgroundColor() : "#FFFFFF";
                    cellLabel.setStyle(cellLabel.getStyle() + "-fx-background-color: " + backgroundColor + ";");
    
                    // Apply text color
                    String textColor = (cell != null && cell.getTextColor() != null && !cell.getTextColor().isEmpty())
                            ? "#" + cell.getTextColor() : "#000000";
                    cellLabel.setTextFill(Color.web(textColor));
    
                    sortedGridPane.add(cellLabel, col + 1, row + 1);
                }
            }
    
            // Wrap the grid in a ScrollPane without shrinking content
            ScrollPane scrollPane = new ScrollPane(sortedGridPane);
            scrollPane.setPrefSize(1000, 400); // Set preferred size
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Enable horizontal scroll as needed
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Enable vertical scroll as needed
    
            Scene sortedScene = new Scene(scrollPane);
            sortedStage.setScene(sortedScene);
            sortedStage.showAndWait();
        }
    
    
        private boolean validateRange(String range) {
            // Example range validation: check if it's in the correct format (e.g., A1..B5)
            if (range == null || !range.matches("[A-Z]+[0-9]+\\.\\.[A-Z]+[0-9]+")) {
                return false;
            }
    
            // Further validation can be done to ensure the range is within the table's bounds
            String[] rangeParts = range.split("\\.\\.");
            String topLeft = rangeParts[0];
            String bottomRight = rangeParts[1];
    
            // Parse row/col indices from the range
            int[] topLeftCoords = parseCellId(topLeft);
            int[] bottomRightCoords = parseCellId(bottomRight);
    
            if (topLeftCoords == null || bottomRightCoords == null) {
                return false;
            }
    
            // Check if top-left is indeed "above" and "to the left" of bottom-right
            return topLeftCoords[0] <= bottomRightCoords[0] && topLeftCoords[1] <= bottomRightCoords[1];
        }
    
        private int[] parseCellId(String cellId) {
            if (!cellId.matches("[A-Z]+[0-9]+")) {
                return null;
            }
    
            String columnPart = cellId.replaceAll("\\d", "");
            String rowPart = cellId.replaceAll("\\D", "");
    
            int column = columnPart.charAt(0) - 'A';  // Convert the letter to a column index
            int row = Integer.parseInt(rowPart) - 1;  // Convert the number to a row index
    
            return new int[]{column, row};
        }
    
        @FXML
        public void applyDefaultSkin() {
            clearStyles();
        }
    
        private void clearStyles() {
            Stage primaryStage = mainController.getPrimaryStage();
            primaryStage.getScene().getStylesheets().clear();
        }
    
        @FXML
        public void applySkin1() {
            applySkin("skin1.css");
        }
    
        @FXML
        public void applySkin2() {
            applySkin("skin2.css");
        }
    
        private void applySkin(String skinFile) {
            Stage primaryStage = mainController.getPrimaryStage();
            primaryStage.getScene().getStylesheets().clear();
            primaryStage.getScene().getStylesheets().add(getClass().getResource("/Controllers/Styles/" + skinFile).toExternalForm());
        }
    
        public void enableChangeCellStyleButton(String cellId) {
            this.selectedCellId = cellId;
            changeCellStyleButton.setDisable(false);
            textColorMenuItem.setDisable(false);
            backgroundColorMenuItem.setDisable(false);
            resetStyleMenuItem.setDisable(false);
        }
    
        private void openColorPicker(String type) {
            Stage colorPickerStage = new Stage();
            colorPickerStage.setTitle("Choose Color");
    
            ColorPicker colorPicker = new ColorPicker();
            colorPicker.setOnAction(event -> {
                Color selectedColor = colorPicker.getValue();
                String colorHex = selectedColor.toString().substring(2, 8);
    
                // Debug print to verify selectedCellId
                System.out.println("Applying " + type + " color to cell ID: " + selectedCellId);
    
                // Apply the color to the currently selected cell
                if (selectedCellId != null && !selectedCellId.isEmpty()) {
                    mainController.updateCellStyle(selectedCellId, type, colorHex);
                } else {
                    System.out.println("Error: No cell selected or invalid cell ID.");
                }
    
                colorPickerStage.close();
            });
    
            VBox vbox = new VBox(colorPicker);
            Scene scene = new Scene(vbox, 200, 100);
            colorPickerStage.setScene(scene);
            colorPickerStage.showAndWait();
        }
        private String formatCellValue(String cellValue) {
            // Check if the value is boolean and return in uppercase
            if (cellValue.equalsIgnoreCase("true") || cellValue.equalsIgnoreCase("false")) {
                return cellValue.toUpperCase(); // Return TRUE or FALSE
            }
    
            try {
                // Attempt to parse the value as a double
                double numericValue = Double.parseDouble(cellValue);
    
                // If the value is an integer (e.g., 2.0), return it without decimal points
                if (numericValue == Math.floor(numericValue)) {
                    return String.format("%.0f", numericValue); // Return as integer
                } else {
                    return String.format("%.2f", numericValue); // Return with 2 decimal places
                }
            } catch (NumberFormatException e) {
                // If the value is not numeric or boolean, return it as-is
                return cellValue;
            }
        }
    
    
        private void resetCellStyle() {
            if (selectedCellId != null) {
                // Get the current value of the cell
                String cellValue = mainController.getGridController().getCellValue(selectedCellId).toString();
    
                // Format the cell value based on whether it's an integer or a double
                String formattedValue = formatCellValue(cellValue);
    
                // Apply the reset style
                mainController.updateCellStyle(selectedCellId, "reset", "");
    
                // Update the cell value with the formatted result (if necessary)
                mainController.updateCellValue(selectedCellId, formattedValue);
            }
        }
    
    
        private void handleColumnAlignmentSelection(String alignment) {
            Stage columnSelectionStage = new Stage();
            columnSelectionStage.setTitle("Select Column for Alignment");
    
            columnSelectionStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
    
            VBox vbox = new VBox();
            vbox.setSpacing(10);
            vbox.setPadding(new javafx.geometry.Insets(20));
    
            for (int col = 1; col <= mainController.getGridController().getColumnCount(); col++) {
                Button columnButton = new Button("Column " + (char) ('A' + col - 1));
                int finalCol = col;
                columnButton.setOnAction(event -> {
                    mainController.applyColumnAlignment(finalCol, alignment);
                    columnSelectionStage.close();
                });
                vbox.getChildren().add(columnButton);
            }
    
            ScrollPane scrollPane = new ScrollPane(vbox);
            scrollPane.setFitToWidth(true);
    
            Scene scene = new Scene(scrollPane, 130, 400);
            columnSelectionStage.setScene(scene);
            columnSelectionStage.showAndWait();
        }

        private void handleDimensionChange(String type) {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Adjust " + (type.equals("Row") ? "Height" : "Width"));
            dialog.setHeaderText("Select and adjust " + (type.equals("Row") ? "Row Height" : "Column Width"));

            GridPane gridPane = new GridPane();
            gridPane.setHgap(10);
            gridPane.setVgap(10);
            gridPane.setPadding(new Insets(20));

            Label selectionLabel = new Label("Select " + type + ":");
            gridPane.add(selectionLabel, 0, 0);

            ComboBox<String> selectionComboBox = new ComboBox<>();
            int total = type.equals("Row") ? mainController.getGridController().getRowCount() : mainController.getGridController().getColumnCount();
            for (int i = 1; i <= total; i++) {
                String labelText = type.equals("Row") ? "Row " + i : "Column " + getColumnLabel(i);
                selectionComboBox.getItems().add(labelText);
            }
            gridPane.add(selectionComboBox, 1, 0);

            Label adjustmentLabel = new Label("Adjust " + (type.equals("Row") ? "Height (px)" : "Width (px)") + ":");
            gridPane.add(adjustmentLabel, 0, 1);

            Slider dimensionSlider = new Slider();
            dimensionSlider.setMin(10); // Minimum value for height or width
            dimensionSlider.setMax(500); // Maximum value for height or width
            dimensionSlider.setValue(100); // Default value
            dimensionSlider.setShowTickLabels(true);
            dimensionSlider.setShowTickMarks(true);
            dimensionSlider.setMajorTickUnit(50);
            dimensionSlider.setMinorTickCount(5);
            dimensionSlider.setBlockIncrement(10);

            Label currentValueLabel = new Label(String.format("%.0f px", dimensionSlider.getValue()));
            dimensionSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                currentValueLabel.setText(String.format("%.0f px", newVal));
            });

            gridPane.add(dimensionSlider, 1, 1);
            gridPane.add(currentValueLabel, 2, 1);

            ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

            Node okButton = dialog.getDialogPane().lookupButton(okButtonType);
            okButton.setDisable(true);

            selectionComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    okButton.setDisable(false);
                }
            });

            dialog.getDialogPane().setContent(gridPane);

            dialog.showAndWait().ifPresent(result -> {
                if (result == okButtonType) {
                    int selectedIndex = selectionComboBox.getSelectionModel().getSelectedIndex() + 1;
                    double newDimension = dimensionSlider.getValue();

                    if (type.equals("Row")) {
                        mainController.getGridController().setRowHeight(selectedIndex, newDimension);
                    } else {
                        mainController.getGridController().setColumnWidth(selectedIndex, newDimension);
                    }
                }
            });
        }

        private void handleDynamicAnalysis() {
            // Step 1: Create a deep copy of the sheet data
            Map<String, List<CellImpl>> copiedSheetData = copyDataFromSheet();

            // Step 2: Set up the pop-up for the copied sheet
            Stage dynamicAnalysisStage = new Stage();
            dynamicAnalysisStage.setTitle("Dynamic Analysis on Copied Sheet");

            VBox vbox = new VBox();
            vbox.setSpacing(10);
            vbox.setPadding(new Insets(20));

            // Label and input fields for range and step size
            Label instructionLabel = new Label("Select a number range for dynamic analysis:");
            TextField rangeInput = new TextField();
            rangeInput.setPromptText("Enter range (e.g., 0..500)");

            Label jumpLabel = new Label("Select jump size:");
            TextField stepInput = new TextField();
            stepInput.setPromptText("Enter step size (e.g., 5)");

            // Slider and its label (initially disabled)
            Label sliderLabel = new Label("Slider:");
            Slider dynamicSlider = new Slider();
            dynamicSlider.setDisable(true); // Initially disabled
            Label sliderValueLabel = new Label("Slider value: 0");

            // Button to apply range and step size
            Button applyRangeButton = new Button("Apply Range and Step");

            // Add all elements to the VBox
            vbox.getChildren().addAll(instructionLabel, rangeInput, jumpLabel, stepInput, applyRangeButton, sliderLabel, dynamicSlider, sliderValueLabel);

            // Store a reference to the selected cell
            final CellImpl[] selectedCell = {null};

            // Step 3: Set up the event for range and step size input
            applyRangeButton.setOnAction(event -> {
                String rangeText = rangeInput.getText();
                String stepText = stepInput.getText();

                // Validate range and step inputs
                if (validateRangeAndStep(rangeText, stepText)) {
                    String[] rangeParts = rangeText.split("\\.\\.");
                    int startRange = Integer.parseInt(rangeParts[0]);
                    int endRange = Integer.parseInt(rangeParts[1]);
                    int stepSize = Integer.parseInt(stepText);

                    // Configure the slider based on user input, but keep it disabled for now
                    dynamicSlider.setMin(startRange);
                    dynamicSlider.setMax(endRange);
                    dynamicSlider.setMajorTickUnit(stepSize);
                    dynamicSlider.setBlockIncrement(stepSize);
                    dynamicSlider.setShowTickLabels(true);
                    dynamicSlider.setShowTickMarks(true);

                    dynamicSlider.setDisable(true);  // Slider remains disabled until a numeric cell is clicked
                } else {
                    showError("Invalid range or step size. Please enter a valid format.");
                }
            });

            // Step 4: Display the copied sheet in the pop-up window and allow cell selection
            GridPane copiedGridPane = createGridPaneForCopiedSheet(copiedSheetData, dynamicSlider, sliderValueLabel, selectedCell, rangeInput, stepInput);
            ScrollPane copiedScrollPane = new ScrollPane(copiedGridPane);
            copiedScrollPane.setPrefSize(800, 400); // Set the preferred size of the copied sheet

            vbox.getChildren().add(copiedScrollPane);  // Add the copied sheet below the slider section


            // Step 5: Add slider functionality to update the selected cell dynamically using UpdateCellCommand
            // In the dynamic slider event listener:
            dynamicSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (selectedCell[0] != null) {
                    double sliderValue = newVal.doubleValue();
                    double jumpSize = Double.parseDouble(stepInput.getText()); // Get the jump size from the input field

                    // Calculate the change based on the direction of slider movement
                    double change;
                    if (sliderValue > previousSliderValue) {
                        // Slider moved to the right, increase by jump size
                        change = jumpSize;
                    } else if (sliderValue < previousSliderValue) {
                        // Slider moved to the left, decrease by jump size
                        change = -jumpSize;
                    } else {
                        // No change in slider value
                        return;
                    }

                    // Update the previous slider value for the next comparison
                    previousSliderValue = sliderValue;

                    // Apply the change to the selected cell's value, making sure it doesn't exceed the bounds
                    double currentCellValue = Double.parseDouble(selectedCell[0].getEffectiveValue());
                    double newCellValue = currentCellValue + change;

                    // Enforce range limits
                    double minRange = dynamicSlider.getMin();
                    double maxRange = dynamicSlider.getMax();
                    if (newCellValue < minRange) {
                        newCellValue = minRange; // Cap at minimum
                    } else if (newCellValue > maxRange) {
                        newCellValue = maxRange; // Cap at maximum
                    }

                    // Update only the selected numeric cell with the new value
                    updateSelectedCellWithSliderValue(copiedSheetData, selectedCell[0], newCellValue, copiedGridPane);

                    // Update the slider label to reflect the new value
                    sliderValueLabel.setText("Slider value: " + sliderValue);
                } else {
                    showError("Please select a numeric cell first.");
                }
            });


            // Display the dynamic analysis stage
            Scene scene = new Scene(vbox, 1000, 600);
            dynamicAnalysisStage.setScene(scene);
            dynamicAnalysisStage.show();
        }

        // Helper function to validate the range and step size input
        private boolean validateRangeAndStep(String rangeText, String stepText) {
            if (rangeText == null || !rangeText.matches("\\d+\\.\\.\\d+")) return false;
            if (stepText == null || !stepText.matches("\\d+")) return false;

            int startRange = Integer.parseInt(rangeText.split("\\.\\.")[0]);
            int endRange = Integer.parseInt(rangeText.split("\\.\\.")[1]);
            int stepSize = Integer.parseInt(stepText);

            return startRange < endRange && stepSize > 0;
        }

        // Helper function to create a grid for displaying the copied sheet with cell click functionality
        private GridPane createGridPaneForCopiedSheet(Map<String, List<CellImpl>> copiedSheetData, Slider dynamicSlider, Label sliderValueLabel, CellImpl[] selectedCell, TextField rangeInput, TextField stepInput) {
            GridPane gridPane = new GridPane();
            gridPane.setHgap(1);
            gridPane.setVgap(1);
            gridPane.setPadding(new Insets(10));

            int totalRows = copiedSheetData.size();
            int totalColumns = copiedSheetData.values().iterator().next().size(); // Ensure totalColumns is calculated here

            // Create headers
            for (int col = 0; col < totalColumns; col++) {
                Label columnHeader = new Label(String.valueOf((char) ('A' + col)));
                columnHeader.setPrefHeight(30);
                columnHeader.setPrefWidth(100);
                columnHeader.setAlignment(Pos.CENTER);
                columnHeader.setStyle("-fx-background-color: lightgray; -fx-font-weight: bold; -fx-border-color: black;");
                gridPane.add(columnHeader, col + 1, 0);
            }

            for (int row = 0; row < totalRows; row++) {
                Label rowHeader = new Label("Row " + (row + 1));
                rowHeader.setPrefHeight(30);
                rowHeader.setPrefWidth(50);
                rowHeader.setAlignment(Pos.CENTER);
                rowHeader.setStyle("-fx-background-color: lightgray; -fx-font-weight: bold; -fx-border-color: black;");
                gridPane.add(rowHeader, 0, row + 1);
            }

            // Populate the grid with copied cell data and allow cell selection
            for (int row = 0; row < totalRows; row++) {
                List<CellImpl> rowData = copiedSheetData.get("Row" + (row + 1));
                for (int col = 0; col < totalColumns; col++) {
                    CellImpl cell = rowData.get(col);
                    String cellValue = (cell != null) ? cell.getEffectiveValue() : "";

                    Label cellLabel = new Label(cellValue);
                    cellLabel.setPrefHeight(30);
                    cellLabel.setPrefWidth(100);
                    cellLabel.setAlignment(Pos.CENTER);
                    cellLabel.setStyle("-fx-border-color: black;");

                    // Only allow enabling the slider if the cell is numeric and if the range and jumps are set
                    final int selectedRow = row;
                    final int selectedCol = col; // Track the row and column
                    cellLabel.setOnMouseClicked(event -> {
                        if (validateRangeAndStep(rangeInput.getText(), stepInput.getText())) {
                            if (cell != null && isNumeric(cell.getEffectiveValue())) {
                                selectedCell[0] = cell;  // Store the selected cell reference
                                double currentCellValue = Double.parseDouble(cell.getEffectiveValue());

                                // Get the range from the rangeInput field
                                String[] rangeParts = rangeInput.getText().split("\\.\\.");
                                int minRange = Integer.parseInt(rangeParts[0]);
                                int maxRange = Integer.parseInt(rangeParts[1]);

                                // Check if the cell value is within the specified range
                                if (currentCellValue < minRange || currentCellValue > maxRange) {
                                    showError("Error: The cell value is outside the valid range of " + minRange + " to " + maxRange + ".");
                                    return;
                                }

                                // Set the slider to the current value of the selected cell
                                dynamicSlider.setValue(currentCellValue);  // Set the slider's value to match the cell value
                                dynamicSlider.setDisable(false);  // Enable the slider after valid cell selection
                                sliderValueLabel.setText("Slider enabled. Adjust the slider to update values dynamically.");
                            } else {
                                showError("Please select a numeric cell.");
                            }
                        } else {
                            showError("Please choose a valid range and jump size first.");
                        }
                    });

                    gridPane.add(cellLabel, col + 1, row + 1);
                }
            }

            return gridPane;
        }


        // Helper function to check if a string is numeric
        private boolean isNumeric(String str) {
            if (str == null || str.isEmpty()) {
                return false;
            }
            try {
                Double.parseDouble(str);  // Attempt to parse the string as a double
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        private void updateSelectedCellWithSliderValue(Map<String, List<CellImpl>> copiedSheetData, CellImpl selectedCell, double newValue, GridPane copiedGridPane) {
            try {
                // Create a Set to track all affected cells
                Set<String> affectedCells = new HashSet<>();

                // Set the temporary value to the selected cell and recalculate it
                selectedCell.setTemporaryValue(newValue);
                recalculateCellAndInfluences(selectedCell, affectedCells);

                // Update the UI with the new values
                updateUIWithChanges(copiedSheetData, copiedGridPane, affectedCells);

            } catch (Exception e) {
                showError("Error updating cell: " + e.getMessage());
            }
        }
        private void recalculateCellAndInfluences(CellImpl cell, Set<String> affectedCells) {
            // Recalculate the current cell
            cell.recalculate();

            // Add the current cell to the affected cells set
            affectedCells.add(cell.getCellId());

            // Recursively recalculate all influenced cells
            for (String influencedCellId : cell.getInfluences()) {
                CellImpl influencedCell = mainController.getsheet().getCell(influencedCellId);
                if (influencedCell != null) {
                    recalculateCellAndInfluences(influencedCell, affectedCells);
                }
            }
        }

        private void updateUIWithChanges(Map<String, List<CellImpl>> copiedSheetData, GridPane copiedGridPane, Set<String> affectedCells) {
            Platform.runLater(() -> {
                for (String cellId : affectedCells) {
                    // Find the affected cell in the copiedSheetData
                    CellImpl affectedCell = copiedSheetData.values().stream()
                            .flatMap(List::stream)
                            .filter(cell -> cell.getCellId().equals(cellId))
                            .findFirst()
                            .orElse(null);

                    if (affectedCell != null) {
                        // Find the corresponding label in the grid pane and update its value
                        Label cellLabel = findLabelByCellId(copiedGridPane, cellId);
                        if (cellLabel != null) {
                            cellLabel.setText(affectedCell.getEffectiveValue());
                        }
                    }
                }
            });
        }


        // Helper method to find the Label in the GridPane by cellId (e.g., B3)
        private Label findLabelByCellId(GridPane gridPane, String cellId) {
            for (Node node : gridPane.getChildren()) {
                if (node instanceof Label) {
                    int rowIndex = GridPane.getRowIndex(node) - 1; // Adjust for 1-based index
                    int colIndex = GridPane.getColumnIndex(node) - 1;
                    String currentCellId = getCellId(rowIndex, colIndex);

                    if (cellId.equals(currentCellId)) {
                        return (Label) node;  // Return the matching label
                    }
                }
            }
            return null; // Return null if no matching label is found
        }


        // Helper method to find the Button in the GridPane by cellId (e.g., B3)
        private Button findButtonByCellId(GridPane gridPane, String cellId) {
            for (Node node : gridPane.getChildren()) {
                if (node instanceof Button) {
                    int rowIndex = GridPane.getRowIndex(node) - 1; // Adjusting for 1-based index
                    int colIndex = GridPane.getColumnIndex(node) - 1;
                    String currentCellId = getCellId(rowIndex, colIndex);

                    if (cellId.equals(currentCellId)) {
                        return (Button) node;  // Return the matching button
                    }
                }
            }
            return null; // Return null if no matching button is found
        }

        public String getCellId(int row, int col) {
            return String.valueOf((char) ('A' + col)) + (row + 1);
        }




        private void showInputDialog(int index, String type) {
            // Create a new dialog
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Change " + (type.equals("Row") ? "Height" : "Width"));
            dialog.setHeaderText("Adjust the " + (type.equals("Row") ? "Row Height" : "Column Width") + " for " + (type.equals("Row") ? "Row " + index : "Column " + getColumnLabel(index)));
    
            // Create a GridPane to hold the label and slider
            GridPane gridPane = new GridPane();
            gridPane.setHgap(10);
            gridPane.setVgap(10);
            gridPane.setPadding(new javafx.geometry.Insets(20));
    
            // Label for the slider
            Label label = new Label("Adjust " + (type.equals("Row") ? "Height (px)" : "Width (px)") + ": ");
    
            // Create a slider for adjusting the dimension
            Slider dimensionSlider = new Slider();
            dimensionSlider.setMin(10); // Minimum value for height or width
            dimensionSlider.setMax(500); // Maximum value for height or width
            dimensionSlider.setValue(100); // Default value
            dimensionSlider.setShowTickLabels(true);
            dimensionSlider.setShowTickMarks(true);
            dimensionSlider.setMajorTickUnit(50);
            dimensionSlider.setMinorTickCount(5);
            dimensionSlider.setBlockIncrement(10);
    
            // Display the current value of the slider
            Label currentValueLabel = new Label(String.format("%.0f px", dimensionSlider.getValue()));
            dimensionSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                currentValueLabel.setText(String.format("%.0f px", newVal));
            });
    
            // Add label, slider, and value display to the grid
            gridPane.add(label, 0, 0);
            gridPane.add(dimensionSlider, 1, 0);
            gridPane.add(currentValueLabel, 2, 0);
    
            // Set the content of the dialog
            dialog.getDialogPane().setContent(gridPane);
    
            // Add OK and Cancel buttons
            ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);
    
            // Show the dialog and handle the result
            dialog.showAndWait().ifPresent(result -> {
                if (result == okButtonType) {
                    int newDimension = (int) dimensionSlider.getValue(); // Get the selected value from the slider
                    if (type.equals("Row")) {
                        mainController.getGridController().setRowHeight(index, newDimension); // Apply the new row height
                    } else {
                        mainController.getGridController().setColumnWidth(index, newDimension); // Apply the new column width
                    }
                }
            });
        }
    
    
        private String getColumnLabel(int col) {
            StringBuilder columnLabel = new StringBuilder();
            while (col > 0) {
                col--;
                columnLabel.insert(0, (char) ('A' + (col % 26)));
                col = col / 26;
            }
            return columnLabel.toString();
        }
    
    
        private void showError(String message) {
            Stage errorStage = new Stage();
            errorStage.setTitle("Error");
    
            VBox vbox = new VBox();
            vbox.setSpacing(10);
            vbox.setPadding(new javafx.geometry.Insets(20));
    
            Label errorMessage = new Label(message);
            Button closeButton = new Button("Close");
            closeButton.setOnAction(event -> errorStage.close());
    
            vbox.getChildren().addAll(errorMessage, closeButton);
    
            Scene scene = new Scene(vbox, 300, 100);
            errorStage.setScene(scene);
            errorStage.showAndWait();
        }
    
        public void enableButtons() {
            skinMenuButton1.setDisable(false);
            columnAlignmentButton.setDisable(false);
            rowColDimensionsButton.setDisable(false);
            addRangeButton.setDisable(false);
            deleteRangeButton.setDisable(false);
            showRangeButton.setDisable(false);
            RowSortButton.setDisable(false);
            FilterButton.setDisable(false);
            chartDisplayButton.setDisable(false); // Disable the chart display button
            dynamicAnalysisButton.setDisable(false);
            animationCheckbox.setDisable(false);
        }
    
        public void disableButtons() {
            skinMenuButton1.setDisable(true);
            changeCellStyleButton.setDisable(true);
            textColorMenuItem.setDisable(true);
            backgroundColorMenuItem.setDisable(true);
            resetStyleMenuItem.setDisable(true);
            columnAlignmentButton.setDisable(true);
            rowColDimensionsButton.setDisable(true);
            addRangeButton.setDisable(true);
            deleteRangeButton.setDisable(true);
            showRangeButton.setDisable(true);
            chartDisplayButton.setDisable(true); // Disable the chart display button
            animationCheckbox.setDisable(true);
        }
    }