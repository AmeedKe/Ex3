package engineimpl;

import ExceptionHandler.*;
import cammands.api.Command;
import cammands.api.CommandsNames;
import functions.api.FunctionType;
import spreadsheet.cell.impl.CellImpl;
import org.w3c.dom.*;
import spreadsheet.impl.SheetImpl;
import STL.STLBoundaries;
import STL.STLRange;
import STL.STLRanges;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Command to read spreadsheet data from an XML file and load it into a Sheet object.
 */
public class XMLRead implements Command {

    private final String filePath;
    private final SheetImpl sheet;
    private final CommandsNames name = CommandsNames.READ_XML;  // Command name member

    /**
     * Constructor to initialize the XMLRead command with the file path and SheetImpl instance.
     *
     * @param filePath The path to the XML file.
     * @param sheet    The SheetImpl object where the data will be loaded.
     */
    public XMLRead(String filePath, SheetImpl sheet) {
        this.filePath = filePath;
        this.sheet = sheet;
    }

    /**
     * Executes the XML reading and sheet loading process.
     */
    @Override
    public void execute() {
        if (!isFilePathValid(filePath)) {
            throw new UnvalidPathException("Invalid file path or file type. Please provide a valid XML file.");
        }

        try {
            Document doc = parseXMLFile(filePath);
            if (doc == null) {
                throw new InvalidXmlContentException("Invalid XML content. Unable to load spreadsheet.");
            }

            // 1. Load the ranges from the XML before calculating functions
            loadRanges(doc);

            // 2. Load the cells into the sheet (this might contain functions using the ranges)
            if (!validateAndLoadSheet(doc)) {
                throw new InvalidXmlContentException("Invalid XML content. Unable to load spreadsheet.");
            }

            // 3. After loading all cells, explicitly update dependencies for each cell
            updateAllCellDependencies();

        } catch (Exception e) {
            throw new RuntimeException("Error reading XML file: " + e.getMessage(), e);
        }
    }

    /**
     * Updates dependencies and influences for all cells in the sheet.
     */
    private void updateAllCellDependencies() throws NullValueException {
        for (String cellId : sheet.getCells().keySet()) {
            CellImpl cell = sheet.getCell(cellId);
            if (cell != null) {
                sheet.updateDependencies(cellId, cell.getOriginalValue());
            }
        }
    }

    /**
     * Returns the command name.
     *
     * @return The CommandsNames enum value representing the command.
     */
    @Override
    public CommandsNames getName() {
        return name;
    }

    /**
     * Validates the file path to ensure it ends with ".xml".
     *
     * @param filePath The file path to validate.
     * @return True if the file path is valid, otherwise false.
     */
    private boolean isFilePathValid(String filePath) {
        return filePath != null && filePath.toLowerCase().endsWith(".xml");
    }

    /**
     * Parses the XML file into a Document object.
     *
     * @param filePath The path to the XML file.
     * @return The parsed Document object, or null if parsing fails.
     * @throws Exception If an error occurs during parsing.
     */
    private Document parseXMLFile(String filePath) throws Exception {
        File file = new File(filePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        return dBuilder.parse(file);
    }

    /**
     * Validates the XML content and loads it into the sheet.
     *
     * @param doc The Document object representing the parsed XML.
     * @return True if the XML content is valid and loaded successfully, otherwise false.
     * @throws Exception If an error occurs during validation or loading.
     */
    private boolean validateAndLoadSheet(Document doc) throws Exception {
        Element root = doc.getDocumentElement();
        if (!root.getNodeName().equalsIgnoreCase("STL-Sheet")) {
            throw new InvalidXmlContentException("Root element must be 'STL-Sheet'.");
        }

        String sheetName = root.getAttribute("name").trim();
        if (sheetName.isEmpty()) {
            throw new InvalidXmlContentException("Sheet name is missing.");
        }

        Element layout = getSingleElement(root, "STL-Layout");
        if (layout == null) return false;

        int rows = parseAttributeAsInt(layout, "rows", 1, 50, "Invalid sheet size. Rows must be 1-50.");
        int columns = parseAttributeAsInt(layout, "columns", 1, 20, "Invalid sheet size. Columns must be 1-20.");
        if (rows < 0 || columns < 0) return false;

        Element size = getSingleElement(layout, "STL-Size");
        if (size == null) return false;

        int columnWidth = parseAttributeAsInt(size, "column-width-units", 1, Integer.MAX_VALUE, "Column width must be greater than 0.");
        int rowHeight = parseAttributeAsInt(size, "rows-height-units", 1, Integer.MAX_VALUE, "Row height must be greater than 0.");
        if (columnWidth < 0 || rowHeight < 0) return false;

        Map<String, CellImpl> cells = loadCells(root, rows, columns);
        if (cells == null) return false;

        sheet.setRows(rows);
        sheet.setColumns(columns);

        // Validate all functions in the cells (including nested functions)
        validateFunctionsInCells(cells);

        // If validation is successful, set the sheet data
        sheet.setSheetData(sheetName, rows, columns, columnWidth, cells);
        return true;
    }


    /**
     * Gets a single child element by tag name.
     *
     * @param parent The parent element.
     * @param tagName The tag name of the child element.
     * @return The child element, or null if not found or if there are multiple elements with the same tag name.
     */
    private Element getSingleElement(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() != 1) {
            throw new InvalidXmlContentException("Invalid number of " + tagName + " elements.");
        }
        return (Element) nodes.item(0);
    }

    /**
     * Parses an integer attribute from an element with validation.
     *
     * @param element The element containing the attribute.
     * @param attributeName The name of the attribute to parse.
     * @param minValue The minimum allowed value.
     * @param maxValue The maximum allowed value.
     * @param errorMessage The error message to display if validation fails.
     * @return The parsed integer value, or -1 if validation fails.
     */
    private int parseAttributeAsInt(Element element, String attributeName, int minValue, int maxValue, String errorMessage) {
        int value;
        try {
            value = Integer.parseInt(element.getAttribute(attributeName));
            if (value < minValue || value > maxValue) {
                throw new InvalidXmlContentException(errorMessage);
            }
        } catch (NumberFormatException e) {
            throw new InvalidXmlContentException("Invalid value for " + attributeName + ": " + element.getAttribute(attributeName));
        }
        return value;
    }

    /**
     * Parses the column attribute as a letter and converts it to a column index.
     *
     * @param column The column letter to parse.
     * @return The corresponding column index, or -1 if invalid.
     */
    private int parseColumn(String column) {
        // Ensure the column is a valid single character from 'A' to 'Z'
        if (column.length() == 1 && column.matches("[A-Z]")) {
            return column.charAt(0) - 'A' + 1;
        } else {
            throw new InvalidXmlContentException("Invalid value for column: " + column);
        }
    }

    /**
     * Loads the cells from the XML document into a map.
     *
     * @param root The root element of the XML document.
     * @param rows The number of rows in the sheet.
     * @param columns The number of columns in the sheet.
     * @return A map of cell identifiers to CellImpl objects, or null if an error occurs.
     */
    private Map<String, CellImpl> loadCells(Element root, int rows, int columns) throws NullValueException, InvalidFunctionInputException {

        NodeList cellsNodes = root.getElementsByTagName("STL-Cells");
        if (cellsNodes.getLength() != 1) {
            throw new InvalidXmlContentException("Invalid number of STL-Cells elements.");
        }

        Element cellsElement = (Element) cellsNodes.item(0);
        NodeList cellList = cellsElement.getElementsByTagName("STL-Cell");
        Map<String, CellImpl> cells = new HashMap<>();

        for (int i = 0; i < cellList.getLength(); i++) {
            Element cellElement = (Element) cellList.item(i);
            int row = parseAttributeAsInt(cellElement, "row", 1, rows, "Invalid row index.");
            String column = cellElement.getAttribute("column").toUpperCase();  // Convert the column to uppercase

            int columnIndex = parseColumn(column);
            if (row < 0 || columnIndex < 0) return null;

            // Get the original value from the XML
            String originalValue = cellElement.getElementsByTagName("STL-Original-Value").item(0).getTextContent().trim();

            // Handle case-insensitive boolean values ("True", "False", etc.)
            if (originalValue.equalsIgnoreCase("true")) {
                originalValue = "TRUE"; // Normalize to uppercase
            } else if (originalValue.equalsIgnoreCase("false")) {
                originalValue = "FALSE"; // Normalize to uppercase
            }

            // Ensure any cell references inside the original value are uppercase
            originalValue = convertCellReferencesToUpperCase(originalValue);

            // Generate the cell ID (with row and column now normalized to uppercase)
            String cellId = getCellId(row - 1, columnIndex - 1);

            // Create and add the cell to the sheet
            sheet.getCells().put(cellId, new CellImpl(cellId, originalValue, sheet));
        }

        // Deep copy the cells after loading
        cells = sheet.deepCopyCells();

        return cells;
    }

    /**
     * Helper function to convert cell references (e.g., a1, g2) to uppercase
     */
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




    /**
     * Loads the ranges from the XML document into the sheet.
     *
     * @param doc The parsed XML document.
     */
    private void loadRanges(Document doc) throws Exception {
        NodeList rangesNodes = doc.getElementsByTagName("STL-Ranges");
        if (rangesNodes.getLength() != 1) {
            throw new InvalidXmlContentException("Invalid number of STL-Ranges elements.");
        }

        Element rangesElement = (Element) rangesNodes.item(0);
        NodeList rangeList = rangesElement.getElementsByTagName("STL-Range");

        for (int i = 0; i < rangeList.getLength(); i++) {
            Element rangeElement = (Element) rangeList.item(i);

            // Get the name of the range
            String rangeName = rangeElement.getAttribute("name").trim();
            if (rangeName.isEmpty()) {
                throw new InvalidXmlContentException("Range name is missing.");
            }

            // Get the boundaries
            Node boundariesNode = rangeElement.getElementsByTagName("STL-Boundaries").item(0);
            if (boundariesNode == null) {
                throw new InvalidXmlContentException("Range boundaries missing.");
            }
            Element boundariesElement = (Element) boundariesNode;

            // Use the STLBoundaries class to parse the "from" and "to" attributes
            STLBoundaries boundaries = new STLBoundaries();
            boundaries.setFrom(boundariesElement.getAttribute("from").trim());
            boundaries.setTo(boundariesElement.getAttribute("to").trim());

            // Validate and add the range to the sheet
            sheet.addRange(rangeName, boundaries.getFrom() + ".." + boundaries.getTo());
        }
    }

    private void validateFunctionsInCells(Map<String, CellImpl> cells) throws InvalidFunctionInputException, InvalidCellReferenceException, OutOfBoundsException {
        for (Map.Entry<String, CellImpl> entry : cells.entrySet()) {
            String cellValue = entry.getValue().getOriginalValue();
            // Check if the value is in function format {FunctionName,arg1,arg2,...}
            if (cellValue.startsWith("{") && cellValue.endsWith("}")) {
                // Call the validateFunction method to check if the function is valid
                validateFunction(cellValue);
            }
        }
    }

//    // Helper function to convert cell references (e.g., a1, g2) to uppercase
//    private String convertCellReferencesToUpperCase(String input) {
//        // Regular expression to match cell references in function arguments (e.g., a1, b2, etc.)
//        Pattern pattern = Pattern.compile("([a-zA-Z]+)([0-9]+)");
//        Matcher matcher = pattern.matcher(input);
//        StringBuffer result = new StringBuffer();
//
//        // Iterate through all matches in the input string
//        while (matcher.find()) {
//            // Convert the letter part to uppercase while keeping the number part unchanged
//            matcher.appendReplacement(result, matcher.group(1).toUpperCase() + matcher.group(2));
//        }
//
//        // Append the remaining part of the input string
//        matcher.appendTail(result);
//
//        return result.toString();
//    }

    private void validateFunction(String newValue) throws InvalidFunctionInputException, InvalidCellReferenceException, OutOfBoundsException {
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

            String functionName = tokens[0].trim().toUpperCase();  // Extract function name and normalize to uppercase

            // Check if the function exists in the FunctionType enum
            try {
                FunctionType function = FunctionType.valueOf(functionName);

                // For REF function, validate cell reference
                if (function == FunctionType.REF) {
                    // Ensure there is one argument for REF
                    if (tokens.length != 2) {
                        throw new InvalidFunctionInputException("REF function must have exactly one argument.");
                    }

                    String cellReference = tokens[1].trim(); // Already in uppercase due to earlier conversion

                    // Validate if the cell reference is valid (like A1, B2, etc.)
                    if (!isValidCellReference(cellReference)) {
                        throw new InvalidCellReferenceException("Invalid cell reference: " + cellReference);
                    }

                    // Check if the cell is within bounds
                    if (isOutOfBounds(cellReference)) {
                        throw new OutOfBoundsException("Cell reference " + cellReference + " is out of bounds.");
                    }
                }

                // Validate arguments for other functions (including nested functions)
                for (int i = 1; i < tokens.length; i++) {
                    String argument = tokens[i].trim();

                    // Check if the argument is a nested function (starts and ends with curly braces)
                    if (argument.startsWith("{") && argument.endsWith("}")) {
                        // Recursively validate the nested function
                        validateFunction(argument);
                    } else {
                        // Validate cell reference or value if it's not a nested function
                        if (function == FunctionType.REF && !isValidCellReference(argument)) {
                            throw new InvalidCellReferenceException("Invalid cell reference in REF function: " + argument);
                        }
                    }
                }

                // Check if the number of arguments matches the function's expected operand count
                if (tokens.length - 1 != function.getOperandCount()) {
                    throw new InvalidFunctionInputException("Invalid number of arguments for function " + functionName +
                            ". Expected " + function.getOperandCount() + ", but got " + (tokens.length - 1));
                }

            } catch (IllegalArgumentException e) {
                throw new InvalidFunctionInputException("Unknown function name: " + functionName);
            }
        }
    }


    // Helper method to check if the cell reference is valid
    private boolean isValidCellReference(String reference) {
        return reference.matches("^[A-Z]+[0-9]+$");
    }

    private boolean isOutOfBounds(String cellReference) {
        String columnPart = cellReference.replaceAll("\\d", "");
        String rowPart = cellReference.replaceAll("\\D", "");

        int column = columnPart.charAt(0) - 'A' + 1;
        int row = Integer.parseInt(rowPart);

        return column < 1 || column > sheet.getColumns() || row < 1 || row > sheet.getRows();
    }

    private String[] splitFunctionArguments(String expression) {
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
             * Converts row and column indices to a cell identifier (e.g., A1).
             *
             * @param row The row index.
             * @param col The column index.
             * @return The cell identifier as a string.
             */
    private String getCellId(int row, int col) {
        return (char) ('A' + col) + String.valueOf(row + 1);
    }
}


