    package spreadsheet.impl;

    import DTO.CellDTO;
    import DTO.SheetDTO;
    import ExceptionHandler.*;
    import functions.api.FunctionType;
    import spreadsheet.api.Sheet;
    import spreadsheet.cell.api.EffectiveValue;
    import spreadsheet.cell.impl.CellImpl;
    import spreadsheet.Validators.SheetValidator;
    import java.util.regex.Matcher;
    import java.util.regex.Pattern;

    import java.util.*;

    /**
     * Implementation of the Sheet interface, representing a spreadsheet with cells, version history, and other properties.
     */
    public class SheetImpl implements Sheet {

        // Fields representing the state of the sheet
        private String name;
        private final Map<String, CellImpl> cells; // A map of cell identifiers to CellImpl objects
        private final Map<Integer, VersionImpl> versionHistory; // A map of version numbers to VersionImpl objects (history of the sheet)
        private int currentVersionNumber; // Tracks the current version of the sheet
        private int rows;
        private int columns;
        private int columnWidth;
        int changedCellsCount = 1;
        private final String versionPrefix = "Version"; // Prefix used to name versions
        private Map<String, Set<String>> ranges;  // A map of range names to sets of cell IDs

        private int x=0;



        /**
         * Default constructor for initializing a new, empty sheet.
         */
        public SheetImpl() {
            this.versionHistory = new HashMap<>();
            this.cells = new HashMap<>();
            this.ranges = new HashMap<>();  // Initialize the map of ranges


        }

        /**
         * Constructor for initializing a new sheet with the given name, rows, columns, and column width.
         * This constructor also validates the sheet size and initializes the version history.
         *
         * @param name The name of the sheet.
         * @param rows The number of rows in the sheet.
         * @param columns The number of columns in the sheet.
         * @param columnWidth The width of each column in the sheet.
         * @throws Exception If the sheet size is invalid.
         */
        public SheetImpl(String name, int rows, int columns, int columnWidth) throws Exception {
            // Validate the sheet size to ensure it's within allowable limits
            SheetValidator.validateSheetSize(rows, columns);

            // Initialize fields
            this.name = name;
            this.rows = rows;
            this.columns = columns;
            this.columnWidth = columnWidth;
            this.versionHistory = new HashMap<>();
            this.cells = new HashMap<>();
            this.currentVersionNumber = 0;
            // Store the initial state as version 1 (empty sheet)
            storeVersionState();
        }

        public void setRows(int rows){
            this.rows = rows;
        }
        public void setColumns(int columns){
            this.columns = columns;
        }

        public Map<String, Set<String>> getRanges(){
            return ranges;
        }
        public int getx(){
            return x;
        }
        public void setx(int x){
            this.x = x;
        }

        /**
         * Initializes the cells map with empty cells based on the sheet size (rows and columns).
         */
        /**
         * Initializes the cells map with empty cells based on the sheet size (rows and columns),
         * but skips cells that already exist in the map.
         */
        public void initializeCells() {
            for (int row = 1; row <= rows; row++) {
                for (int col = 1; col <= columns; col++) {
                    String cellId = getCellId(row - 1, col - 1);
                    // Only initialize the cell if it's not already in the map
                    if (!cells.containsKey(cellId)) {
                        cells.put(cellId, new CellImpl(cellId, "", this)); // Create empty cell with default values
                    }
                }
            }
        }



        /**
         * Adds a new range to the system.
         *
         * @param rangeName  The unique name of the range.
         * @param rangeInput The input representing the range (e.g., A1..A4).
         * @throws InvalidRangeException If the range is invalid.
         * @throws RangeExistsException  If a range with the same name already exists.
         */
        public void addRange(String rangeName, String rangeInput) throws InvalidRangeException, RangeExistsException {
            if (ranges.containsKey(rangeName)) {
                throw new RangeExistsException("Range with the name '" + rangeName + "' already exists.");
            }

            // Parse the range input (e.g., A1..A4)
            String[] rangeParts = rangeInput.split("\\.\\.");
            if (rangeParts.length != 2) {
                throw new InvalidRangeException("Invalid range format. Use <top-left cell>..<bottom-right cell>.");
            }

            String topLeftCellId = rangeParts[0].trim();
            String bottomRightCellId = rangeParts[1].trim();

            // Validate the range and add the range cells
            Set<String> rangeCells = validateAndCollectRange(topLeftCellId, bottomRightCellId);
            ranges.put(rangeName, rangeCells);  // Store the set of cell IDs in the ranges map
        }

        /**
         * Validates the range and collects the cell IDs in the range.
         *
         * @param topLeftCellId     The top-left cell of the range.
         * @param bottomRightCellId The bottom-right cell of the range.
         * @return A set of cell IDs in the valid range.
         * @throws InvalidRangeException If the range is invalid.
         */
        public Set<String> validateAndCollectRange(String topLeftCellId, String bottomRightCellId) throws InvalidRangeException {
            // Parse the top-left and bottom-right cells (e.g., A1 -> col=A, row=1)
            int[] topLeft = parseCellId(topLeftCellId);
            int[] bottomRight = parseCellId(bottomRightCellId);

            if (topLeft == null || bottomRight == null) {
                throw new InvalidRangeException("Invalid cell IDs.");
            }

            int topRow = topLeft[1];
            int topCol = topLeft[0];
            int bottomRow = bottomRight[1];
            int bottomCol = bottomRight[0];

            // Ensure the range is valid: same row, same column, or rectangular area
            if (topRow > bottomRow || topCol > bottomCol) {
                throw new InvalidRangeException("Invalid range: Top-left cell must be above and to the left of the bottom-right cell.");
            }

            Set<String> rangeCells = new HashSet<>();
            for (int row = topRow; row <= bottomRow; row++) {
                for (int col = topCol; col <= bottomCol; col++) {
                    rangeCells.add(getCellId(row, col));  // Add each cell ID to the set
                }
            }

            return rangeCells;
        }

        /**
         * Deletes a range by its name if it is not in use.
         *
         * @param rangeName The name of the range to delete.
         * @throws RangeNotFoundException If the range does not exist.
         * @throws RangeInUseException If the range is currently being used in a formula or function.
         */
        public void deleteRange(String rangeName) throws RangeNotFoundException, RangeInUseException {
            if (!ranges.containsKey(rangeName)) {
                throw new RangeNotFoundException("Range with the name '" + rangeName + "' not found.");
            }

            // Check if the range is being used in any cell or function
            if (isRangeInUse(rangeName)) {
                throw new RangeInUseException("Range '" + rangeName + "' is in use and cannot be deleted.");
            }

            ranges.remove(rangeName);  // Remove the range from the map
        }

        /**
         * Checks if a range is currently being used in any formula or function.
         *
         * @param rangeName The name of the range to check.
         * @return True if the range is in use, otherwise false.
         */
        private boolean isRangeInUse(String rangeName) {
            // Iterate over all the cells in the sheet
            for (CellImpl cell : cells.values()) {
                String originalValue = cell.getOriginalValue();

                if (originalValue.startsWith("{") && originalValue.endsWith("}")) {
                    // Parse and check the formula recursively
                    if (isRangeInFormula(originalValue.substring(1, originalValue.length() - 1), rangeName)) {
                        return true;  // Range is in use in a formula
                    }
                }
            }
            return false;  // The range is not in use
        }

        /**
         * Recursively checks if a range is used in a given formula.
         *
         * @param expression The expression to check (e.g., "SUM,range").
         * @param rangeName  The name of the range to look for.
         * @return True if the range is used in the formula, false otherwise.
         */
        private boolean isRangeInFormula(String expression, String rangeName) {
            // Split the expression into function and operands
            String[] tokens = splitFunctionArguments(expression);
            String functionName = tokens[0].trim().toUpperCase();

            // Check if the function is SUM or AVERAGE
            if (functionName.equals("SUM") || functionName.equals("AVERAGE")) {
                // The second token should be the range name
                if (tokens.length > 1 && tokens[1].trim().equalsIgnoreCase(rangeName)) {
                    return true;  // The range is directly referenced in SUM or AVERAGE
                }
            }

            // Recursively check if the function contains nested expressions
            for (int i = 1; i < tokens.length; i++) {
                String operandStr = tokens[i].trim();
                if (operandStr.startsWith("{") && operandStr.endsWith("}")) {
                    // Recursively check nested function
                    if (isRangeInFormula(operandStr.substring(1, operandStr.length() - 1), rangeName)) {
                        return true;
                    }
                }
            }

            return false;  // The range is not found in the formula
        }

        public String[] splitFunctionArguments(String expression) {
            List<String> args = new ArrayList<>();
            StringBuilder currentArg = new StringBuilder();
            int nestedLevel = 0;

            for (char ch : expression.toCharArray()) {
                if (ch == '{') {
                    nestedLevel++;
                } else if (ch == '}') {
                    nestedLevel--;
                }

                if (ch == ',' && nestedLevel == 0) {
                    args.add(currentArg.toString().trim());
                    currentArg.setLength(0);
                } else {
                    currentArg.append(ch);
                }
            }

            if (!currentArg.isEmpty()) {
                args.add(currentArg.toString().trim());
            }

            return args.toArray(new String[0]);
        }


        /**
         * Displays the cells in a given range by its name.
         *
         * @param rangeName The name of the range to view.
         * @return The set of cell IDs in the range.
         * @throws RangeNotFoundException If the range does not exist.
         */
        public Set<String> viewRange(String rangeName) throws RangeNotFoundException {
            if (!ranges.containsKey(rangeName)) {
                throw new RangeNotFoundException("Range with the name '" + rangeName + "' not found.");
            }

            return ranges.get(rangeName);  // Return the set of cell IDs
        }

        /**
         * Parses a cell ID (e.g., A1) into column and row indices.
         *
         * @param cellId The cell ID to parse.
         * @return An array with [columnIndex, rowIndex] or null if invalid.
         */
        private int[] parseCellId(String cellId) {
            if (!cellId.matches("[A-Z]+\\d+")) {
                return null;
            }

            String columnPart = cellId.replaceAll("\\d", "");
            String rowPart = cellId.replaceAll("\\D", "");

            int column = columnPart.charAt(0) - 'A';
            int row = Integer.parseInt(rowPart) - 1;

            return new int[]{column, row};
        }
        /**
         * Generates a cell ID based on row and column indexes.
         *
         * @param row The row index (starting from 0).
         * @param col The column index (starting from 0).
         * @return The generated cell ID (e.g., A1, B2).
         */
        public String getCellId(int row, int col) {
            return String.valueOf((char) ('A' + col)).toUpperCase() + (row + 1);
        }



        /**
         * Stores the current state of the sheet as a version in the version history.
         *
         * @throws NullValueException If the current state is invalid.
         */
        private void storeVersionState() throws NullValueException {
            versionHistory.put(currentVersionNumber, new VersionImpl(deepCopyCells(), 0, versionPrefix + currentVersionNumber));
        }

        /**
         * Sets new sheet data, including name, size, and cells.
         * This method also resets dependencies and influences and starts a new version history.
         *
         * @param name The name of the sheet.
         * @param rows The number of rows in the sheet.
         * @param columns The number of columns in the sheet.
         * @param columnWidth The width of each column in the sheet.
         * @param newCells A map of the new cells to populate the sheet.
         * @throws Exception If the sheet size is invalid.
         */
        @Override
        public void setSheetData(String name, int rows, int columns, int columnWidth, Map<String, CellImpl> newCells) throws Exception {
            // Validate the new sheet size
            SheetValidator.validateSheetSize(rows, columns);

            // Update the sheet's attributes
            this.name = name;
            this.rows = rows;
            this.columns = columns;
            this.columnWidth = columnWidth;
            this.cells.clear(); // Clear the current cells map
            this.cells.putAll(newCells); // Populate with new cells

            // Reset dependencies and influences among the cells
            resetDependenciesAndInfluences();

            // Reset the version history and store the initial state
            this.currentVersionNumber = 1;
            storeVersionState();
        }

        /**
         * Updates a specific cell's value and handles any resulting dependencies or version updates.
         * This method also updates the version history and recalculates influenced cells.
         *
         * @param cellId The ID of the cell to update.
         * @param newValue The new value to set in the cell.
         * @throws NullValueException If the new value is null or invalid.
         */
        @Override
        public void updateCell(String cellId, String newValue) throws NullValueException, InvalidFunctionInputException, OutOfBoundsException {
            // Reset the count for changed cells in this update
            changedCellsCount = 0;

            // Deep copy the current state of cells before the update
            Map<String, CellImpl> oldVersion = deepCopyCells();

            // Validate the new value (e.g., check for functions and references)
            newValue = validateFunction(newValue);  // Throws exception if the function is invalid

            // Update the cell's value, creating the cell if it doesn't exist
            CellImpl cell = updateCellValue(cellId, newValue);

            // Update dependencies and recalculate influenced cells in topological order
            updateDependencies(cellId, newValue);
            recalculateInfluencedCells(cellId);

            // After recalculation, compare the old version with the new one to count changed cells
            Map<String, CellImpl> newVersion = deepCopyCells();
            int changedCells = compareVersions(oldVersion, newVersion);  // Get the number of changed cells

            // Check if any cells have changed
            if (changedCells > 0) {
                // Increment version number for the new version
                currentVersionNumber++;

                // Create and store the new version with the changed cells count
                VersionImpl newVersionData = new VersionImpl(newVersion, changedCells, versionPrefix + currentVersionNumber);
                versionHistory.put(currentVersionNumber, newVersionData);

                // Adjust the lastUpdatedVersion of the cells in the sheet
                adjustLastUpdatedVersionsInSheet();
            }
        }



        private void adjustLastUpdatedVersionsInSheet() {
            // Iterate over all the current cells in the sheet
            for (String cellId : cells.keySet()) {
                CellImpl currentCell = cells.get(cellId);

                // Get the version based on the cell's lastUpdatedVersion
                VersionImpl previousVersion = versionHistory.get(currentCell.getLastUpdatedVersion());

                if (previousVersion != null) {
                    // Get the corresponding cell from the extracted version
                    CellImpl previousCell = previousVersion.getData().get(cellId);

                    // Compare the current cell's value with the value from the extracted version
                    if (previousCell != null && currentCell.getEffectiveValue().equals(previousCell.getEffectiveValue())) {
                        // If the values are the same, set the lastUpdatedVersion to 1
                        currentCell.setLastUpdatedVersion(1);
                    } else {
                        // If the values are different, set the lastUpdatedVersion to the current version number
                        currentCell.setLastUpdatedVersion(currentVersionNumber);
                    }
                } else {
                    // If no previous version exists, just set the lastUpdatedVersion to the current version number
                    currentCell.setLastUpdatedVersion(currentVersionNumber);
                }
            }
        }







        public int compareVersions(Map<String, CellImpl> oldVersion, Map<String, CellImpl> newVersion) {
            int changedCellCount = 0;

            // Loop through the old version's cells
            for (String cellId : oldVersion.keySet()) {
                CellImpl oldCell = oldVersion.get(cellId);
                CellImpl newCell = newVersion.get(cellId);

                // If the cell exists in both versions, compare their effective values
                if (newCell != null) {
                    EffectiveValue oldValue = oldCell.getEffectiveValueObject();
                    EffectiveValue newValue = newCell.getEffectiveValueObject();

                    // Check if the effective values are different
                    if (!(oldValue.getValue().equals(newValue.getValue()))) {
                        changedCellCount++;
                    }
                }
            }

            return changedCellCount;
        }


        // Helper function to convert cell references (e.g., a1, g2) to uppercase
        private String convertCellReferencesToUpperCase(String input) {
            // Regular expression to match cell references in function arguments (e.g., a1, b2, etc.)
            Pattern pattern = Pattern.compile("([a-zA-Z]+)([0-9]+)");
            Matcher matcher = pattern.matcher(input);
            StringBuffer result = new StringBuffer();

            // Iterate through all matches in the input string
            while (matcher.find()) {
                // Convert the letter part to uppercase while keeping the number part unchanged
                matcher.appendReplacement(result, matcher.group(1).toUpperCase() + matcher.group(2));
            }

            // Append the remaining part of the input string
            matcher.appendTail(result);

            return result.toString();
        }

        public String validateFunction(String newValue) throws InvalidFunctionInputException, InvalidCellReferenceException, OutOfBoundsException {
            // Convert cell references to uppercase first
            newValue = convertCellReferencesToUpperCase(newValue);

            // Now perform the rest of the validation logic
            if (newValue.startsWith("{") && newValue.endsWith("}")) {
                // Remove the curly braces and split the function name and arguments
                String expression = newValue.substring(1, newValue.length() - 1).trim();
                String[] tokens = splitFunctionArguments(expression);

                if (tokens.length < 1) {
                    throw new InvalidFunctionInputException("Invalid function format: " + newValue);
                }

                // Extract function name and normalize to uppercase
                String functionName = tokens[0].trim().toUpperCase();

                // Check if the function exists in the FunctionType enum
                try {
                    FunctionType function = FunctionType.valueOf(functionName);

                    // For REF function, validate cell reference
                    if (function == FunctionType.REF) {
                        // Ensure there is one argument for REF
                        if (tokens.length != 2) {
                            throw new InvalidFunctionInputException("REF function must have exactly one argument.");
                        }

                        String cellReference = tokens[1].trim().toUpperCase(); // The reference is already in uppercase by now

                        // Validate if the cell reference is valid (like A1, B2, etc.)
                        if (!isValidCellReference(cellReference)) {
                            throw new InvalidCellReferenceException("Invalid cell reference: " + cellReference);
                        }

                        // Check if the cell is within bounds
                        if (isOutOfBounds(cellReference)) {
                            throw new OutOfBoundsException("Cell reference " + cellReference + " is out of bounds.");
                        }

                        // Return the updated function string with REF validation complete
                        return "{" + functionName + "," + cellReference + "}";
                    }

                    // Validate arguments for other functions (including nested functions)
                    StringBuilder updatedValue = new StringBuilder("{").append(functionName);
                    for (int i = 1; i < tokens.length; i++) {
                        String argument = tokens[i].trim();

                        // Check if the argument is a nested function
                        if (argument.startsWith("{") && argument.endsWith("}")) {
                            // Recursively validate the nested function and append it
                            updatedValue.append(",").append(validateFunction(argument));
                        } else {
                            // Append the argument directly (it's already uppercase for cell references)
                            updatedValue.append(",").append(argument);
                        }
                    }

                    // Check if the number of arguments matches the function's expected operand count
                    if (tokens.length - 1 != function.getOperandCount()) {
                        throw new InvalidFunctionInputException("Invalid number of arguments for function " + functionName +
                                ". Expected " + function.getOperandCount() + ", but got " + (tokens.length - 1));
                    }

                    updatedValue.append("}");
                    return updatedValue.toString(); // Return the updated newValue with validated arguments

                } catch (IllegalArgumentException e) {
                    throw new InvalidFunctionInputException("Unknown function name: " + functionName);
                }
            }

            return newValue; // Return the unchanged newValue if it's not a function
        }




        // Function to update range dependencies and influences for all cells in the sheet
        private void updateRangeDependenciesAndInfluences() {
            // Iterate through all cells in the sheet
            for (Map.Entry<String, CellImpl> cellEntry : cells.entrySet()) {
                String currentCellId = cellEntry.getKey();
                CellImpl currentCell = cellEntry.getValue();

                // Check if the cell's formula references a range
                String originalValue = currentCell.getOriginalValue();
                if (originalValue.startsWith("{") && originalValue.endsWith("}")) {
                    // Extract the expression inside the curly braces
                    String expression = originalValue.substring(1, originalValue.length() - 1).trim();
                    String[] tokens = splitFunctionArguments(expression);
                    String functionName = tokens[0].trim().toUpperCase();

                    // Handle functions like SUM or AVERAGE that deal with ranges
                    if (functionName.equals("SUM") || functionName.equals("AVERAGE")) {
                        for (int i = 1; i < tokens.length; i++) {
                            String operandStr = tokens[i].trim();

                            // Check if the operand is a range
                            Set<String> rangeCells = ranges.get(operandStr);

                            if (rangeCells != null) {
                                // Add each cell in the range as a dependency for the current cell
                                for (String rangeCellId : rangeCells) {
                                    currentCell.addDependency(rangeCellId); // Add range cell as a dependency
                                    CellImpl rangeCell = getCell(rangeCellId);
                                    if (rangeCell != null) {
                                        rangeCell.addInfluence(currentCellId); // Add the current cell as an influence
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }



        private void setInfluencedCellsToUndefined(CellImpl cell) throws NullValueException {
            Set<String> visited = new HashSet<>();
            Queue<String> queue = new LinkedList<>();
            queue.add(cell.getCellId());

            while (!queue.isEmpty()) {
                String currentCellId = queue.poll();
                visited.add(currentCellId);

                CellImpl currentCell = cells.get(currentCellId);
                if (currentCell != null) {
                    for (String influencedCellId : currentCell.getInfluences()) {
                        if (!visited.contains(influencedCellId)) {
                            CellImpl influencedCell = cells.get(influencedCellId);
                            if (influencedCell != null) {
                                // Set the influenced cell's value to "undefined"
                                updateCellValue(influencedCellId, "undefined");
                                queue.add(influencedCellId);
                            }
                        }
                    }
                }
            }
        }

        /**
         * Retrieves the effective value of a specific cell.
         * If the cell does not exist, a new CellImpl object is created with default values.
         *
         * @param cellId The ID of the cell to retrieve the value from.
         * @return The effective value of the cell.
         */
        @Override
        public Object getCellValue(String cellId) {
            try {
                // Validate the cell ID before retrieving its value
                validateCellId(cellId);

                // Retrieve the cell using the cellId, or create a new one if it doesn't exist
                CellImpl cell = cells.getOrDefault(cellId, new CellImpl(cellId, "", this));
                return cell.getEffectiveValue();
            } catch (Exception e) {
                throw new RuntimeException("Error retrieving value for cell " + cellId + ": " + e.getMessage(), e);
            }
        }

        /**
         * Updates the value of a specific cell or creates the cell if it does not already exist in the map.
         *
         * @param cellId The ID of the cell to update.
         * @param newValue The new value to set in the cell.
         * @return The updated or newly created CellImpl object.
         * @throws NullValueException If the cell ID is invalid.
         */
        private CellImpl updateCellValue(String cellId, String newValue) throws NullValueException {
            // If the cell does not exist in the map, create it and add it to the map
            CellImpl cell = cells.computeIfAbsent(cellId, id -> new CellImpl(id, "", this));
            // Update the cell's value
            cell.updateValue(newValue, currentVersionNumber);
            return cell;
        }

        /**
         * Creates a deep copy of the current cells in the sheet, including their values and dependencies.
         *
         * @return A map containing deep copies of the current cells.
         * @throws NullValueException If a cell value is null.
         */
        public Map<String, CellImpl> deepCopyCells() throws NullValueException {

            Map<String, CellImpl> copy = new HashMap<>();
            for (Map.Entry<String, CellImpl> entry : cells.entrySet()) {
                CellImpl originalCell = entry.getValue();
                CellImpl copiedCell = new CellImpl(originalCell.getCellId(), originalCell.getOriginalValue(), this);
                copiedCell.updateValue(originalCell.getOriginalValue(), originalCell.getLastUpdatedVersion());
                copiedCell.setBackgroundColor(originalCell.getBackgroundColor());
                copiedCell.setTextColor(originalCell.getTextColor());
                copy.put(entry.getKey(), copiedCell);
            }
            return copy;
        }

        /**
         * Updates the dependencies of a cell and handles changes in dependent cells,
         * including handling ranges without clearing other dependencies.
         *
         * @param cellId The ID of the cell to update dependencies for.
         * @param newValue The new value of the cell.
         * @throws NullValueException If the cell ID or new value is invalid.
         */
        public void updateDependencies(String cellId, String newValue) throws NullValueException {
            CellImpl cell = cells.get(cellId);

            if (cell != null) {
                // Clear only the dependencies of the current cell, not the influences
                Set<String> previousDependencies = new HashSet<>(cell.getDependencies());
                cell.clearDependencies();

                // Handle complex expressions, such as those using ranges or references
                if (isComplexExpression(newValue)) {
                    String[] parts = newValue.split(",");
                    for (int i = 1; i < parts.length; i++) {
                        String arg = parts[i].trim();
                        arg = arg.replaceAll("[{}]", "");

                        // Check if the argument is a range reference
                        if (ranges.containsKey(arg)) {
                            // Add all cells in the range as dependencies
                            Set<String> rangeCells = ranges.get(arg);
                            for (String rangeCellId : rangeCells) {
                                if (!detectCircularDependency(cellId, rangeCellId)) {
                                    cell.addDependency(rangeCellId);
                                    CellImpl dependentCell = cells.computeIfAbsent(rangeCellId, id -> new CellImpl(id, "", this));
                                    dependentCell.addInfluence(cellId);
                                }
                            }
                        }
                        // Handle regular cell references
                        else if (isValidCellReference(arg)) {
                            if (!detectCircularDependency(cellId, arg)) {
                                cell.addDependency(arg);
                                CellImpl dependentCell = cells.computeIfAbsent(arg, id -> new CellImpl(id, "", this));
                                dependentCell.addInfluence(cellId);
                            }
                        }
                    }
                }

                // Remove this cell from the influence list of any previous dependencies no longer needed
                for (String oldDependency : previousDependencies) {
                    if (!cell.getDependencies().contains(oldDependency)) {
                        CellImpl oldDependentCell = cells.get(oldDependency);
                        if (oldDependentCell != null) {
                            oldDependentCell.getInfluences().remove(cellId);
                        }
                    }
                }
            }
        }



        /**
         * Determines if a given expression is complex, meaning it involves multiple cell references.
         *
         * @param expression The expression to check.
         * @return True if the expression is complex, false otherwise.
         */
        private boolean isComplexExpression(String expression) {
            return expression.startsWith("{") && expression.contains(",");
        }

        /**
         * Determines if a string is a valid cell reference (e.g., "A1").
         *
         * @param arg The string to validate as a cell reference.
         * @return True if the string is a valid cell reference, false otherwise.
         */
        private boolean isValidCellReference(String arg) {
            return arg.matches("[A-Z]+\\d+");
        }

        /**
         * Detects if adding a dependency between two cells would cause a circular dependency.
         *
         * @param startCellId The starting cell ID.
         * @param targetCellId The target cell ID.
         * @return True if a circular dependency would be created, false otherwise.
         */
        public boolean detectCircularDependency(String startCellId, String targetCellId) {
            Set<String> visited = new HashSet<>();
            Queue<String> queue = new LinkedList<>();
            queue.add(targetCellId);

            while (!queue.isEmpty()) {
                String currentCellId = queue.poll();
                if (currentCellId.equals(startCellId)) {
                    return true;
                }
                visited.add(currentCellId);
                CellImpl currentCell = cells.get(currentCellId);
                if (currentCell != null) {
                    for (String dep : currentCell.getDependencies()) {
                        if (!visited.contains(dep)) {
                            queue.add(dep);
                        }
                    }
                }
            }
            return false;
        }

        /**
         * Recalculates the values of cells that are influenced by the specified starting cell.
         *
         * @param startingCellId The ID of the starting cell.
         */
        public void recalculateInfluencedCells(String startingCellId) throws NullValueException {
            // Step 1: Build the graph of cell dependencies
            Map<String, List<String>> graph = buildDependencyGraph();

            // Step 2: Perform topological sorting on the graph
            List<String> sortedCells = topologicalSort(graph);

            // Step 3: Recalculate the cells in topological order
            for (String cellId : sortedCells) {
                CellImpl cell = cells.get(cellId);
                if (cell != null) {
                    if (dependsOnUndefinedCell(cell)) {
                        updateCellValue(cellId, "undefined");
                    } else {
                        updateCellValue(cellId, cell.getOriginalValue());
                    }

                }
            }
            // Update the changedCellsCount based on the number of dependencies
//            changedCellsCount += calculateInfluences(startingCellId);
            calculateInfluences(startingCellId);

        }

        private boolean dependsOnUndefinedCell(CellImpl cell) {
            for (String dependency : cell.getDependencies()) {
                CellImpl dependentCell = cells.get(dependency);
                if (dependentCell != null && "undefined".equals(dependentCell.getEffectiveValue())) {
                    return true;
                }
            }
            return false;
        }



        private void calculateInfluences(String cellId) {
            Set<String> visited = new HashSet<>();
            Queue<String> queue = new LinkedList<>();
            int influenceCount = 0;

            queue.add(cellId);

            while (!queue.isEmpty()) {
                String currentCellId = queue.poll();
                visited.add(currentCellId);

                CellImpl currentCell = cells.get(currentCellId);
                if (currentCell != null) {
                    for (String influence : currentCell.getInfluences()) {
                        if (!visited.contains(influence)) {
                            queue.add(influence);
                            visited.add(influence);
                            influenceCount++;
                        }
                    }
                }
            }

        }



        private Map<String, List<String>> buildDependencyGraph() {
            Map<String, List<String>> graph = new HashMap<>();
            for (CellImpl cell : cells.values()) {
                graph.putIfAbsent(cell.getCellId(), new ArrayList<>());
                for (String dep : cell.getDependencies()) {
                    graph.putIfAbsent(dep, new ArrayList<>());
                    graph.get(dep).add(cell.getCellId());
                }
            }
            return graph;
        }

        private List<String> topologicalSort(Map<String, List<String>> graph) {
            List<String> sortedList = new ArrayList<>();
            Set<String> visited = new HashSet<>();
            Set<String> inStack = new HashSet<>();

            for (String node : graph.keySet()) {
                if (!visited.contains(node)) {
                    topologicalSortHelper(node, graph, visited, inStack, sortedList);
                }
            }

            Collections.reverse(sortedList); // Reverse to get correct topological order
            return sortedList;
        }

        private void topologicalSortHelper(String node, Map<String, List<String>> graph, Set<String> visited, Set<String> inStack, List<String> sortedList) {
            visited.add(node);
            inStack.add(node);

            for (String neighbor : graph.get(node)) {
                if (!visited.contains(neighbor)) {
                    topologicalSortHelper(neighbor, graph, visited, inStack, sortedList);
                } else if (inStack.contains(neighbor)) {
                    // Skip circular dependencies
                }
            }

            inStack.remove(node);
            sortedList.add(node);
        }
    //
    //    /**
    //     * Updates the influenced cells based on the current cell's influences.
    //     *
    //     * @param currentCellId The ID of the current cell.
    //     * @param visited A set of visited cell IDs to prevent redundant calculations.
    //     * @param queue A queue of cells to process for influence recalculation.
    //     */
    //    private void updateInfluencedCells(String currentCellId, Set<String> visited, Queue<String> queue) throws NullValueException {
    //        // Retrieve the current cell object based on the current cell ID
    //        CellImpl currentCell = cells.get(currentCellId);
    //
    //        // Proceed only if the current cell exists
    //        if (currentCell != null) {
    //            // Iterate over each cell that the current cell influences
    //            for (String influencedCellId : currentCell.getInfluences()) {
    //                // Check if the influenced cell has already been visited
    //                if (!visited.contains(influencedCellId)) {
    //                    // Retrieve the influenced cell object
    //                    CellImpl influencedCell = cells.get(influencedCellId);
    //
    //                    // Proceed only if the influenced cell exists
    //                    if (influencedCell != null) {
    //                        // Flag to check if all dependencies of the influenced cell have been visited
    //                        boolean allDependenciesVisited = true;
    //
    //                        // Iterate over each dependency of the influenced cell
    //                        for (String dependency : influencedCell.getDependencies()) {
    //                            // If any dependency hasn't been visited, mark the flag as false and break the loop
    //                            if (!visited.contains(dependency)) {
    //                                allDependenciesVisited = false;
    //                                break;
    //                            }
    //                        }
    //
    //                        // If all dependencies have been visited, update the influenced cell and add it to the queue
    //                        if (allDependenciesVisited) {
    //                            // Update the influenced cell with its recalculated value based on its original value
    //                            updateCellValue(influencedCellId, influencedCell.getOriginalValue());
    //
    //                            // Add the influenced cell ID to the queue for further processing
    //                            queue.add(influencedCellId);
    //                        }
    //                    }
    //                }
    //            }
    //        }
    //    }


        /**
         * Resets the dependencies and influences of all cells in the sheet.
         */
        public void resetDependenciesAndInfluences() {
            for (CellImpl cell : cells.values()) {
                cell.clearDependencies();
                cell.getInfluences().clear();
            }
        }

        /**
         * Validates a cell ID to ensure it is not null, empty, or out of bounds.
         *
         * @param cellId The cell ID to validate.
         * @throws InvalidCellIdException If the cell ID is invalid.
         */
        private void validateCellId(String cellId) {
            if (cellId == null || cellId.trim().isEmpty()) {
                throw new InvalidCellIdException("Cell ID cannot be null or empty.");
            }

            // Convert cellId to uppercase before validation
            cellId = cellId.toUpperCase();

            if (!isValidCellId(cellId)) {
                throw new InvalidCellIdException("Invalid Cell ID format: " + cellId);
            }

            if (isOutOfBounds(cellId)) {
                throw new InvalidCellIdException("Cell ID out of bounds: " + cellId);
            }

            if (isEmptyCell(cellId)) {
                throw new CellNotFoundException("Cell with ID " + cellId + " is empty.");
            }
        }


        /**
         * Checks if the cell ID format is valid.
         *
         * @param cellId The cell ID to check.
         * @return True if the cell ID is valid, false otherwise.
         */
        private boolean isValidCellId(String cellId) {
            // Convert cellId to uppercase before checking its format
            return cellId.toUpperCase().matches("^[A-Z]+[0-9]+$");
        }


        /**
         * Checks if a cell ID is out of the bounds of the sheet.
         *
         * @param cellId The cell ID to check.
         * @return True if the cell ID is out of bounds, false otherwise.
         */
        private boolean isOutOfBounds(String cellId) {
            String columnPart = cellId.replaceAll("[0-9]", ""); // Extract letters
            String rowPart = cellId.replaceAll("[A-Z]", "");    // Extract numbers

            int column = columnPart.charAt(0) - 'A' + 1; // Convert column letter to a number (e.g., A -> 1)
            int row = Integer.parseInt(rowPart);

            return column < 1 || column > columns || row < 1 || row > rows;
        }

        /**
         * Checks if a cell is empty (i.e., does not exist in the map).
         *
         * @param cellId The cell ID to check.
         * @return True if the cell is empty, false otherwise.
         */
        private boolean isEmptyCell(String cellId) {
            return !cells.containsKey(cellId);
        }

        /**
         * Retrieves a specific version of the sheet as a DTO.
         * This method looks up the version in the version history.
         *
         * @param versionNumber The version number to retrieve.
         * @return The SheetDTO representing the specified version.
         */
        @Override
        public SheetDTO getVersionDTO(int versionNumber) {
            VersionImpl version = versionHistory.get(versionNumber);

            if (version == null) {
                throw new IllegalArgumentException("Version number does not exist.");
            }

            return createSheetDTO(version, versionNumber);
        }

        /**
         * Creates a SheetDTO from a VersionImpl object.
         *
         * @param version The version to convert to a DTO.
         * @param versionNumber The version number.
         * @return The SheetDTO representing the specified version.
         */
        private SheetDTO createSheetDTO(VersionImpl version, int versionNumber) {
            SheetDTO sheetDTO = new SheetDTO(this.name, this.rows, this.columns, this.columnWidth, versionNumber);

            for (Map.Entry<String, CellImpl> entry : version.getData().entrySet()) {
                String cellId = entry.getKey();
                CellImpl cellImpl = entry.getValue();
                CellDTO cellDTO = new CellDTO(cellImpl);
                sheetDTO.addCell(cellId, cellDTO);
            }

            return sheetDTO;
        }

        public void setChangedCellsCount(int changedCellsCount) {
            this.changedCellsCount = changedCellsCount;
        }

        /**
         * Converts the current state of the sheet into a DTO.
         * This method includes the current version number in the DTO.
         *
         * @return The SheetDTO representing the current state of the sheet.
         */
        @Override
        public SheetDTO toSheetDTO() {
            Map<String, CellDTO> cellDTOs = convertCellsToDTOs();

            SheetDTO sheetDTO = new SheetDTO(
                    this.name,
                    this.rows,
                    this.columns,
                    this.columnWidth,
                    currentVersionNumber
            );

            for (Map.Entry<String, CellDTO> entry : cellDTOs.entrySet()) {
                sheetDTO.addCell(entry.getKey(), entry.getValue());
            }

            return sheetDTO;
        }

        /**
         * Converts the current cells into a map of CellDTOs.
         *
         * @return A map of CellDTOs representing the current cells in the sheet.
         */
        private Map<String, CellDTO> convertCellsToDTOs() {
            Map<String, CellDTO> cellDTOs = new HashMap<>();
            for (Map.Entry<String, CellImpl> entry : cells.entrySet()) {
                String cellId = entry.getKey();
                CellImpl cellImpl = entry.getValue();

                CellDTO cellDTO = new CellDTO(
                        cellImpl.getOriginalValue(),
                        cellImpl.getEffectiveValueObject(),
                        cellImpl.getLastUpdatedVersion(),
                        new HashSet<>(cellImpl.getDependencies()),
                        new HashSet<>(cellImpl.getInfluences()),
                        cellImpl.getCellId()
                );

                cellDTOs.put(cellId, cellDTO);
            }
            return cellDTOs;
        }

        /**
         * Retrieves the CellDTO for a specific cell ID.
         *
         * @param cellId The ID of the cell to retrieve.
         * @return The CellDTO representing the specified cell.
         * @throws CellNotFoundException If the cell does not exist or is invalid.
         */
        @Override
        public CellDTO getCellDTO(String cellId) {
            validateCellId(cellId);

            CellImpl cellImpl = cells.get(cellId);
    //        if (cellImpl == null) {
    //            throw new CellNotFoundException("Cell with ID " + cellId + " is empty.");
    //        }

            return new CellDTO(cellImpl);
        }

        /**
         * Returns the name of the sheet.
         *
         * @return The sheet's name.
         */
        @Override
        public String getName() {
            return name;
        }

        /**
         * Returns the number of rows in the sheet.
         *
         * @return The number of rows in the sheet.
         */
        @Override
        public int getRows() {
            return rows;
        }

        /**
         * Returns the number of columns in the sheet.
         *
         * @return The number of columns in the sheet.
         */
        @Override
        public int getColumns() {
            return columns;
        }

        /**
         * Returns the width of each column in the sheet.
         *
         * @return The width of the columns.
         */
        @Override
        public int getColumnWidth() {
            return columnWidth;
        }

        /**
         * Returns the current version number of the sheet.
         *
         * @return The current version number.
         */
        @Override
        public int getCurrentVersionNumber() {
            return currentVersionNumber;
        }

        /**
         * Retrieves the version history of the sheet.
         *
         * @return A map representing the version history, keyed by version number.
         */
        @Override
        public Map<Integer, VersionImpl> getVersionHistory() {
            return versionHistory;
        }

        /**
         * Retrieves a specific cell by its ID.
         *
         * @param cellId The ID of the cell to retrieve.
         * @return The CellImpl representing the specified cell.
         */
        @Override
        public CellImpl getCell(String cellId) {
            return cells.get(cellId);
        }

        /**
         * Returns the cells in the sheet.
         *
         * @return A map of cells in the sheet, keyed by cell ID.
         */
        @Override
        public Map<String, CellImpl> getCells() {
            return cells;
        }
    }
