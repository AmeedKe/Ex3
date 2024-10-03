package engineapi;

import ExceptionHandler.InvalidFunctionInputException;
import ExceptionHandler.NullValueException;
import ExceptionHandler.OutOfBoundsException;
import spreadsheet.cell.impl.CellImpl;
import spreadsheet.impl.SheetImpl;

import java.util.Map;

public interface Engine {

    SheetImpl getSheet();

    void setSheet(SheetImpl sheet);

    void updateCell(String cellId, String newValue) throws NullValueException, InvalidFunctionInputException, OutOfBoundsException;

    void setSheetData(String name, int rows, int columns, int columnWidth, Map<String, CellImpl> newCells) throws Exception;

    void evaluateAllCells();
}
