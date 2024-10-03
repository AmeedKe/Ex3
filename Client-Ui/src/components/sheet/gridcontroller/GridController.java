        package components.sheet.gridcontroller;

        import components.sheet.appcontroller.AppController;
        import javafx.fxml.FXML;
        import javafx.scene.Node;
        import javafx.scene.control.Button;
        import javafx.scene.layout.ColumnConstraints;
        import javafx.scene.layout.GridPane;
        import javafx.scene.layout.Priority;
        import javafx.scene.layout.RowConstraints;
        import javafx.geometry.Pos;
        import spreadsheet.cell.impl.CellImpl;
        import spreadsheet.impl.SheetImpl;

        import java.util.HashSet;
        import java.util.Map;
        import java.util.Set;

        public class GridController extends Node {

            @FXML
            private GridPane gridPane;
            private AppController mainController;
            private SheetImpl sheet;

            @FXML
            private void initialize() {
                if (sheet != null) {
                    int rows = sheet.getRows();
                    int columns = sheet.getColumns();
                    double columnWidth = sheet.getColumnWidth();

                    gridPane.getChildren().clear();
                    gridPane.getColumnConstraints().clear();
                    gridPane.getRowConstraints().clear();

                    // Set up column constraints
                    for (int col = 0; col <= columns; col++) {
                        ColumnConstraints colConst = new ColumnConstraints();
                        colConst.setPrefWidth(columnWidth);
                        colConst.setHgrow(Priority.ALWAYS);
                        gridPane.getColumnConstraints().add(colConst);
                    }

                    // Set up row constraints
                    for (int row = 0; row <= rows; row++) {
                        RowConstraints rowConst = new RowConstraints();
                        rowConst.setPrefHeight(30);
                        rowConst.setVgrow(Priority.ALWAYS);
                        gridPane.getRowConstraints().add(rowConst);
                    }

                    // Add column headers
                    for (int col = 1; col <= columns; col++) {
                        Button columnHeader = new Button(String.valueOf((char) ('A' + (col - 1))));
                        columnHeader.setPrefHeight(30);
                        columnHeader.setPrefWidth(columnWidth);
                        columnHeader.setMaxWidth(Double.MAX_VALUE);
                        columnHeader.setAlignment(Pos.CENTER);
                        columnHeader.setStyle("-fx-background-color: lightgray; -fx-font-weight: bold; -fx-border-color: black;");
                        gridPane.add(columnHeader, col, 0);
                    }

                    // Add row headers
                    for (int row = 1; row <= rows; row++) {
                        Button rowHeader = new Button(String.format("%02d", row));
                        rowHeader.setPrefHeight(30);
                        rowHeader.setPrefWidth(50);
                        rowHeader.setMaxWidth(Double.MAX_VALUE);
                        rowHeader.setAlignment(Pos.CENTER);
                        rowHeader.setStyle("-fx-background-color: lightgray; -fx-font-weight: bold; -fx-border-color: black;");
                        gridPane.add(rowHeader, 0, row);
                    }

                    // Set up column and row constraints (existing code)
                    Map<String, CellImpl> cells = sheet.getCells();
                    for (int row = 1; row <= rows; row++) {
                        for (int col = 1; col <= columns; col++) {
                            String cellId = getCellId(row - 1, col - 1);
                            Button button = new Button();
                            button.setPrefHeight(30);
                            button.setPrefWidth(columnWidth);
                            button.setAlignment(Pos.CENTER);
                            button.setMaxWidth(Double.MAX_VALUE);
                            button.setMaxHeight(Double.MAX_VALUE);
                            button.setStyle("-fx-border-color: black;"); // Start with default border style

                            if (cells.containsKey(cellId)) {
                                CellImpl cell = cells.get(cellId);
                                String cellValue = cell.getEffectiveValue();
                                button.setText(formatCellValue(cellValue)); // Format the cell value before displaying

                                // Apply stored text color and background color if they exist
                                StringBuilder style = new StringBuilder(button.getStyle());

                                if (cell.getTextColor() != null) {
                                    style.append("-fx-text-fill: #").append(cell.getTextColor()).append(";");
                                }
                                if (cell.getBackgroundColor() != null) {
                                    style.append("-fx-background-color: #").append(cell.getBackgroundColor()).append(";");
                                }

                                button.setStyle(style.toString());

                                // Handle cell click
                                button.setOnAction(event -> {
                                    mainController.handleCellClick(cellId, cell.getOriginalValue());
                                });
                            }

                            GridPane.setHgrow(button, Priority.ALWAYS);
                            GridPane.setVgrow(button, Priority.ALWAYS);
                            gridPane.add(button, col, row);
                        }
                    }
                }
            }

            public GridPane getGridPane(){
                return this.gridPane;
            }
            private String formatCellValue(String cellValue) {
                // Check if the cellValue is "true" or "false" first (case-insensitive)
                if (cellValue.equalsIgnoreCase("true") || cellValue.equalsIgnoreCase("false")) {
                    return cellValue.toUpperCase();  // Return TRUE or FALSE in uppercase
                }

                try {
                    // Check if the value is numeric
                    double numericValue = Double.parseDouble(cellValue);

                    // If the value is an integer, display it without decimals
                    if (numericValue == (int) numericValue) {
                        return String.format("%d", (int) numericValue);
                    } else {
                        // If it's a double, display two digits after the decimal point
                        return String.format("%.2f", numericValue);
                    }
                } catch (NumberFormatException e) {
                    // If it's not a number, return the original value
                    return cellValue;
                }
            }

            public Set<String> getRange(String startCell, String endCell) {
                Set<String> range = new HashSet<>();

                // Parse start and end cell IDs, e.g., "A1" -> row: 1, col: A
                int startRow = getRowIndex(startCell);
                int startCol = getColIndex(startCell);
                int endRow = getRowIndex(endCell);
                int endCol = getColIndex(endCell);

                // Iterate over the range and collect cell IDs
                for (int row = startRow; row <= endRow; row++) {
                    for (int col = startCol; col <= endCol; col++) {
                        String cellId = getCellId(row, col);  // Assuming you already have a method to get cell ID
                        range.add(cellId);
                    }
                }
                return range;
            }

            private int getRowIndex(String cellId) {
                // Extract the numeric part of the cell ID (the row number)
                String rowPart = cellId.replaceAll("[^0-9]", "");  // Remove all non-numeric characters
                return Integer.parseInt(rowPart) - 1;  // Convert to int and subtract 1 for 0-based index
            }

            private int getColIndex(String cellId) {
                // Extract the letter part of the cell ID (the column label)
                String colPart = cellId.replaceAll("[^A-Za-z]", "").toUpperCase();  // Remove all non-letter characters and convert to uppercase

                int colIndex = 0;
                for (int i = 0; i < colPart.length(); i++) {
                    colIndex *= 26;
                    colIndex += (colPart.charAt(i) - 'A' + 1);
                }

                return colIndex - 1;  // Convert to 0-based index
            }

            public void updateCellStyle(String cellId, String styleType, String color) {
                Button cellButton = findButtonByCellId(cellId);
                if (cellButton != null) {
                    CellImpl cell = sheet.getCell(cellId); // Retrieve the cell model

                    // If the styleType is "reset", reset both text and background color
                    if (styleType.equals("reset")) {
                        // Clear stored colors in the cell model
                        cell.setTextColor(null);
                        cell.setBackgroundColor(null);

                        // Reset the button's style to default
                        cellButton.setStyle("-fx-border-color: black;");
                        cellButton.setText(cell.getEffectiveValue());  // Ensure the text remains unchanged

                    } else {
                        // Initialize the style with the current button's style
                        StringBuilder style = new StringBuilder(cellButton.getStyle());

                        if (styleType.equals("text")) {
                            // Update the text color and store it in the cell model
                            cell.setTextColor(color);
                            // Remove any existing text color from the style
                            style = new StringBuilder(style.toString().replaceAll("-fx-text-fill: #[0-9a-fA-F]{6};", ""));
                            style.append("-fx-text-fill: #").append(color).append(";");
                        } else if (styleType.equals("background")) {
                            // Update the background color and store it in the cell model
                            cell.setBackgroundColor(color);
                            // Remove any existing background color from the style
                            style = new StringBuilder(style.toString().replaceAll("-fx-background-color: #[0-9a-fA-F]{6};", ""));
                            style.append("-fx-background-color: #").append(color).append(";");
                        }

                        // Apply the updated style to the button
                        cellButton.setStyle(style.toString());
                    }
                }
            }

            public void clearHighlights(Set<String> cellIds) {
                for (String cellId : cellIds) {
                    Button cellButton = findButtonByCellId(cellId);
                    if (cellButton != null) {
                        // Reset the button's style to its default
                        cellButton.setStyle("-fx-border-color: black;");
                    }
                }
            }

            public void clearHighlights() {
                for (Node node : gridPane.getChildren()) {
                    if (node instanceof Button) {
                        Button button = (Button) node;

                        // Retrieve cell ID based on the gridPane position of the button
                        Integer rowIndex = GridPane.getRowIndex(button)-1;
                        Integer colIndex = GridPane.getColumnIndex(button)-1;

                        String cellId = getCellId(rowIndex, colIndex);
                        System.out.println("Clearing highlights for cell: " + cellId);  // Debug print

                        CellImpl cell = sheet.getCell(cellId);

                        if (cell != null) {
                            // Apply stored background and text colors
                            String currentStyle = button.getStyle();
                            currentStyle = currentStyle.replaceAll("-fx-background-color: lightblue;", "");
                            currentStyle = currentStyle.replaceAll("-fx-background-color: lightgreen;", "");
                            currentStyle = currentStyle.replaceAll("-fx-background-color: LAVENDER;", "");

                            if (cell.getBackgroundColor() != null && !currentStyle.contains("-fx-background-color:")) {
                                currentStyle += "-fx-background-color: #" + cell.getBackgroundColor() + ";";
                            }

                            if (cell.getTextColor() != null && !currentStyle.contains("-fx-text-fill:")) {
                                currentStyle += "-fx-text-fill: #" + cell.getTextColor() + ";";
                            }

                            button.setStyle(currentStyle);  // Apply updated style
                        }
                    }
                }
            }

            public void highlightCells(Set<String> cellIds, String color) {
                for (String cellId : cellIds) {
                    Button button = findButtonByCellId(cellId);
                    if (button != null) {
                        button.setStyle("-fx-background-color: " + color + "; -fx-border-color: black;");
                    }
                }
            }

            public void applyAlignmentToCell(String cellId, String alignment) {
                Button cellButton = findButtonByCellId(cellId);
                if (cellButton != null) {
                    switch (alignment) {
                        case "LEFT":
                            cellButton.setAlignment(Pos.CENTER_LEFT);
                            break;
                        case "RIGHT":
                            cellButton.setAlignment(Pos.CENTER_RIGHT);
                            break;
                        case "CENTER":
                            cellButton.setAlignment(Pos.CENTER);
                            break;
                    }
                }
            }

            public int getColumnCount() {
                return gridPane.getColumnConstraints().size() - 1; // -1 for the header row
            }

            public int getRowCount() {
                return gridPane.getRowConstraints().size() - 1; // -1 for the header column
            }

            public CellImpl getCell(String cellid){
                return sheet.getCell(cellid);
            }

            public Object getCellValue(String id)
{
    return sheet.getCellValue(id);
}

            private Button findButtonByCellId(String cellId) {
                for (Node node : gridPane.getChildren()) {
                    if (node instanceof Button) {
                        String buttonId = getCellId(GridPane.getRowIndex(node) - 1, GridPane.getColumnIndex(node) - 1);
                        if (buttonId.equals(cellId)) {
                            return (Button) node;
                        }
                    }
                }
                return null;
            }

            public void setMainController(AppController mainController) {
                this.mainController = mainController;
            }

            public void loadSheet(SheetImpl sheet) {
                this.sheet = sheet;
                initialize(); // Re-initialize the grid with the new sheet data
            }

            public void setRowHeight(int rowIndex, double height) {
                if (rowIndex >= 1 && rowIndex <= gridPane.getRowConstraints().size()) {
                    RowConstraints rowConstraints = gridPane.getRowConstraints().get(rowIndex - 1); // Adjust for 1-based index
                    rowConstraints.setPrefHeight(height); // Set the new height
                }

                // Increase the preferred height of the grid to accommodate the new row height
                double totalHeight = gridPane.getRowConstraints().stream()
                        .mapToDouble(RowConstraints::getPrefHeight)
                        .sum();
                gridPane.setPrefHeight(totalHeight);
            }

            public void setColumnWidth(int columnIndex, double width) {
                if (columnIndex >= 1 && columnIndex <= gridPane.getColumnConstraints().size()) {
                    ColumnConstraints columnConstraints = gridPane.getColumnConstraints().get(columnIndex - 1); // Adjust for 1-based index
                    columnConstraints.setPrefWidth(width); // Set the new width
                }

                // Increase the preferred width of the grid to accommodate the new column width
                double totalWidth = gridPane.getColumnConstraints().stream()
                        .mapToDouble(ColumnConstraints::getPrefWidth)
                        .sum();
                gridPane.setPrefWidth(totalWidth);
            }




            public String getCellId(int row, int col) {
                return String.valueOf((char) ('A' + col)) + (row + 1);
            }

            @Override
            public Node getStyleableNode() {
                return super.getStyleableNode();
            }
        }
