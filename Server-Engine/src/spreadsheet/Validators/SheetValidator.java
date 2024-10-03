package spreadsheet.Validators;

import ExceptionHandler.NullValueException;
import ExceptionHandler.SheetSizeException;

public class SheetValidator {

    public static void validateSheetSize(int rows, int columns) throws SheetSizeException {
        if (!isValidSheetSize(rows, columns)) {
            throw new SheetSizeException("Invalid sheet size: " + rows + "x" + columns);
        }
    }

    private static boolean isValidSheetSize(int rows, int columns) {
        return rows >= 1 && rows <= 50 && columns >= 1 && columns <= 20;
    }

    public static void validateNotNull(Object value) throws NullValueException {
        if (value == null) {
            throw new NullValueException("Null value not allowed");
        }
    }
}
