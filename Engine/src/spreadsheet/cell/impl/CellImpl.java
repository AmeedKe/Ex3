    package spreadsheet.cell.impl;

    import ExceptionHandler.NullValueException;
    import expression.api.Expression;
    import functions.api.FunctionType;
    import spreadsheet.Validators.SheetValidator;
    import spreadsheet.cell.api.Cell;
    import spreadsheet.cell.api.CellType;
    import spreadsheet.cell.api.EffectiveValue;
    import spreadsheet.impl.SheetImpl;

    import java.util.*;

    public class CellImpl implements Cell {
        private final String cellId;
        private String originalValue;
        private EffectiveValue effectiveValue;
        private int lastUpdatedVersion;
        private final Set<String> dependencies;
        private final Set<String> influences;
        private final SheetImpl sheet;
        private String textColor;
        private String backgroundColor;
        private Double temporaryValue = null;

        // Store the last valid state
        private String lastValidOriginalValue;
        private EffectiveValue lastValidEffectiveValue;
        private Set<String> lastValidDependencies;
        private Set<String> lastValidInfluences;

        // Constructor now accepts the cell ID and the sheet reference
        public CellImpl(String cellId, String originalValue, SheetImpl sheet) {
            this.cellId = cellId;
            this.originalValue = originalValue.trim();
            this.sheet = sheet;
            this.dependencies = new HashSet<>();
            this.influences = new HashSet<>();
            this.effectiveValue = evaluateEffectiveValue(this.originalValue);
            this.lastUpdatedVersion = 1;
            saveLastValidState();  // Save the initial state
        }

        // Save the current valid state of the cell
        private void saveLastValidState() {
            lastValidOriginalValue = originalValue;
            lastValidEffectiveValue = effectiveValue;
            lastValidDependencies = new HashSet<>(dependencies); // Clone to avoid reference issues
            lastValidInfluences = new HashSet<>(influences);
        }

        // Revert to the last valid state in case of an error
        private void revertToLastValidState() {
            this.originalValue = lastValidOriginalValue;
            this.effectiveValue = lastValidEffectiveValue;
            this.dependencies.clear();
            this.dependencies.addAll(lastValidDependencies);
            this.influences.clear();
            this.influences.addAll(lastValidInfluences);
        }


        public String getTextColor() {
            return textColor;
        }

        public void setTextColor(String textColor) {
            this.textColor = textColor;
        }

        public String getBackgroundColor() {
            return backgroundColor;
        }

        public void setBackgroundColor(String backgroundColor) {
            this.backgroundColor = backgroundColor;
        }

        public void decrementVersion() {
            this.lastUpdatedVersion--;
        }

        // Getter for cell ID
        public String getCellId() {
            return cellId;
        }

        // Updated method to evaluate effective value using the sheet reference
        private EffectiveValue evaluateEffectiveValue(String value) {
            return evaluateValue(value);  // Pass the sheet reference to evaluateValue
        }

        @Override
        public EffectiveValue getEffectiveValueObject() {
            return effectiveValue;
        }

        @Override
        public String getEffectiveValue() {
            try {
                Object value = effectiveValue.getValue();
                return value != null ? value.toString() : "!UNDEFINED!";
            } catch (Exception e) {
                System.out.print(e.getMessage());
                return "1";
            }
        }
        public void recalculate() {
            // If there is a temporary value, use it as the effective value
            if (temporaryValue != null) {
                this.effectiveValue = new EffectiveValueImpl(CellType.NUMERIC, temporaryValue);
            } else {
                // Otherwise, re-evaluate the original value (formula or constant)
                this.effectiveValue = evaluateEffectiveValue(this.originalValue);
            }

            // Notify influenced cells (cells that depend on this cell) to recalculate
            for (String influencedCellId : influences) {
                CellImpl influencedCell = sheet.getCell(influencedCellId);
                if (influencedCell != null && influencedCell.temporaryValue == null) {
                    // Trigger recalculation for dependent cells only if they don't have a temporary value
                    influencedCell.recalculate();
                }
            }
        }

        public void setTemporaryValue(double tempValue) {
            this.temporaryValue = tempValue;
        }

        public void clearTemporaryValue() {
            this.temporaryValue = null;
        }

        @Override
        public String getOriginalValue() {
            return originalValue;
        }

    //    @Override
    //    public void updateValue(String newValue, int currentVersion) throws NullValueException {
    //
    //        SheetValidator.validateNotNull(newValue);
    //        this.lastUpdatedVersion = currentVersion;
    //        this.originalValue = newValue.trim();
    //        this.effectiveValue = evaluateEffectiveValue(this.originalValue);  // Re-evaluate with the new value
    //    }
    //
    //    public String getFormula() {
    //        return originalValue.startsWith("{") ? originalValue : null;  // Return the formula if it exists
    //    }

        @Override
        public void updateValue(String newValue, int currentVersion) throws NullValueException {
            // Validate the new value
            SheetValidator.validateNotNull(newValue);

            // Clear existing dependencies and influences
            clearDependenciesAndInfluences();

            // Update the version and the original value
            this.lastUpdatedVersion = currentVersion;
            this.originalValue = newValue.trim();

            // Re-evaluate the effective value based on the new input
            this.effectiveValue = evaluateEffectiveValue(this.originalValue);


            // Recalculate dependencies and influences based on the new value
            recalculate();

            int y;
        }

        // Method to clear existing dependencies and influences
        private void clearDependenciesAndInfluences() {
            // Remove this cell from the influences of all dependent cells
            for (String dependency : dependencies) {
                CellImpl dependentCell = sheet.getCell(dependency);
                if (dependentCell != null) {
                    dependentCell.getInfluences().remove(this.cellId);  // Remove this cell from the influences of the dependent cell
                }
            }

            // Clear this cell's dependencies
            dependencies.clear();
        }

        // Updated evaluateValue method
        public EffectiveValue evaluateValue(String value) {
            if (value == null || value.trim().isEmpty()) {
                return new EffectiveValueImpl(CellType.STRING, "");
            }

            value = value.trim();

            // Try parsing the value as a numeric value
            try {
                double doubleValue = Double.parseDouble(value);
                return new EffectiveValueImpl(CellType.NUMERIC, doubleValue);
            } catch (NumberFormatException e) {
                // Continue to the next checks if it's not numeric
            }

            // Try parsing the value as a boolean value
            if (value.equalsIgnoreCase("TRUE")) {
                return new EffectiveValueImpl(CellType.BOOLEAN, true);
            } else if (value.equalsIgnoreCase("FALSE")) {
                return new EffectiveValueImpl(CellType.BOOLEAN, false);
            } else if (value.startsWith("{") && value.endsWith("}")) {
                // Handle function or expression if it starts and ends with curly braces
                return evaluateFunction(value.substring(1, value.length() - 1));
            }

            // If it doesn't match any known types, return it as a string
            return new EffectiveValueImpl(CellType.STRING, value);
        }


        // Updated evaluateFunction method with upper case cell references
        public EffectiveValue evaluateFunction(String expression) {
            String[] tokens = splitFunctionArguments(expression);
            String functionName = tokens[0].trim().toUpperCase();

            try {
                FunctionType function = FunctionType.valueOf(functionName);
                List<Expression> operands = new ArrayList<>();

                for (int i = 1; i < tokens.length; i++) {
                    String operandStr = tokens[i].trim();
                    String normalizedOperand = operandStr.toUpperCase(); // Ensure operand (cell references) are in uppercase

                    if (sheet.getx() != 0) {
                        // Special handling for SUM and AVERAGE functions with ranges
                        if (functionName.equals("SUM") || functionName.equals("AVERAGE")) {
                            // Assuming the operandStr is the name of the range
                            Set<String> cellIdsInRange = sheet.getRanges().get(normalizedOperand); // Normalize range name too

                            if (cellIdsInRange != null) {
                                // Add all cells in the range to dependencies and influences
                                for (String cellIdInRange : cellIdsInRange) {
                                    this.addDependency(cellIdInRange.toUpperCase()); // Normalize cell IDs to upper case
                                    CellImpl referencedCell = sheet.getCell(cellIdInRange.toUpperCase());
                                    if (referencedCell != null) {
                                        referencedCell.addInfluence(this.cellId.toUpperCase()); // Add this cell to their influences
                                    }
                                }
                            } else {
                                throw new IllegalArgumentException("Invalid range: " + operandStr);
                            }
                        }
                    }

                    // Handle regular REF function for individual cell references
                    if (functionName.equals("REF")) {
                        this.addDependency(normalizedOperand); // Normalize individual cell references
                        CellImpl referencedCell = sheet.getCell(normalizedOperand); // Retrieve cell with upper case ID
                        if (referencedCell != null) {
                            referencedCell.addInfluence(this.cellId.toUpperCase());
                        }
                    }

                    // Add the operand as an expression
                    if (operandStr.startsWith("{") && operandStr.endsWith("}")) {
                        operands.add((sheet) -> evaluateFunction(operandStr.substring(1, operandStr.length() - 1)));
                    } else {
                        operands.add((sheet) -> evaluateValue(operandStr));
                    }
                }

                if (operands.size() != function.getOperandCount()) {
                    throw new IllegalArgumentException("Invalid number of operands for function: " + functionName);
                }

                return function.createExpression(operands, sheet).eval(sheet);

            } catch (IllegalArgumentException e) {
                return new EffectiveValueImpl(CellType.STRING, "!UNDEFINED!");
            }
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

        @Override
        public int getLastUpdatedVersion() {
            return lastUpdatedVersion;
        }

        public Set<String> getDependencies() {
            return dependencies;
        }

        public void clearDependencies() {
            dependencies.clear();
        }

        public Set<String> getInfluences() {
            return influences;
        }
        // Extract and return the row number from the cellId (e.g., A1 -> 1)
        public int getRow() {
            return Integer.parseInt(cellId.replaceAll("[^\\d]", "")) - 1;  // Convert row to 0-based index
        }

        // Extract and return the column number from the cellId (e.g., A1 -> 0 for 'A')
        public int getCol() {
            String columnPart = cellId.replaceAll("\\d", "");
            return convertColumnToIndex(columnPart);
        }

        // Convert column letters (e.g., A, B, AA) to a 0-based column index
        private int convertColumnToIndex(String column) {
            int result = 0;
            for (int i = 0; i < column.length(); i++) {
                result = result * 26 + (column.charAt(i) - 'A' + 1);
            }
            return result - 1;  // Convert to 0-based index
        }
        public void addDependency(String cellId) {
            dependencies.add(cellId.toUpperCase()); // Ensure the cellId is always uppercase
        }

        public void addInfluence(String cellId) {
            influences.add(cellId.toUpperCase()); // Ensure the cellId is always uppercase
        }

        public void setLastUpdatedVersion(int currentVersionNumber) {
            lastUpdatedVersion=currentVersionNumber;
        }
    }
