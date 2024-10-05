package spreadsheet.api;

import DTO.CellDTO;
import DTO.SheetDTO;
import ExceptionHandler.InvalidFunctionInputException;
import ExceptionHandler.NullValueException;
import ExceptionHandler.OutOfBoundsException;
import spreadsheet.cell.impl.CellImpl;
import spreadsheet.impl.VersionImpl;

import java.util.Map;

/**
 * Interface representing a sheet in a spreadsheet, with methods to manipulate and retrieve cell data,
 * handle versioning, and manage the overall structure of the sheet.
 */
public interface Sheet {

    /**
     * Sets the data for the sheet, including name, size, column width, and initial cell data.
     *
     * @param name        The name of the sheet.
     * @param rows        The number of rows in the sheet.
     * @param columns     The number of columns in the sheet.
     * @param columnWidth The width of each column in the sheet.
     * @param newCells    A map containing the initial cell data.
     * @throws Exception If the sheet size is invalid.
     */
    void setSheetData(String name, int rows, int columns, int columnWidth, Map<String, CellImpl> newCells) throws Exception;

    /**
     * Updates the value of a specific cell, handles versioning, and recalculates dependencies.
     *
     * @param cellId   The ID of the cell to update.
     * @param newValue The new value to set in the cell.
     * @throws NullValueException If the new value is null or invalid.
     */
    void updateCell(String cellId, String newValue) throws NullValueException, InvalidFunctionInputException, OutOfBoundsException;

    /**
     * Retrieves the effective value of a specific cell.
     *
     * @param cellId The ID of the cell to retrieve the value from.
     * @return The effective value of the cell.
     */
    Object getCellValue(String cellId);

    /**
     * Returns the name of the sheet.
     *
     * @return The sheet's name.
     */
    String getName();

    /**
     * Returns the number of rows in the sheet.
     *
     * @return The number of rows in the sheet.
     */
    int getRows();

    /**
     * Returns the number of columns in the sheet.
     *
     * @return The number of columns in the sheet.
     */
    int getColumns();

    /**
     * Returns the width of each column in the sheet.
     *
     * @return The width of the columns.
     */
    int getColumnWidth();

    /**
     * Returns the current version number of the sheet.
     *
     * @return The current version number.
     */
    int getCurrentVersionNumber();

    /**
     * Retrieves the SheetDTO for a specific version.
     *
     * @param versionNumber The version number to retrieve.
     * @return The SheetDTO representing the specified version.
     */
    SheetDTO getVersionDTO(int versionNumber);

    /**
     * Returns the cells in the sheet.
     *
     * @return A map of cells in the sheet, keyed by cell ID.
     */
    Map<String, CellImpl> getCells();

    /**
     * Converts the current state of the sheet to a SheetDTO.
     *
     * @return The SheetDTO representing the current state of the sheet.
     */
    SheetDTO toSheetDTO();

    /**
     * Retrieves a specific cell by its ID.
     *
     * @param cellId The ID of the cell to retrieve.
     * @return The CellImpl representing the specified cell.
     */
    CellImpl getCell(String cellId);

    /**
     * Retrieves the CellDTO for a specific cell ID.
     *
     * @param cellId The ID of the cell to retrieve.
     * @return The CellDTO representing the specified cell.
     */
    CellDTO getCellDTO(String cellId);

    /**
     * Returns the version history of the sheet.
     *
     * @return A map representing the version history, keyed by version number.
     */
    Map<Integer, VersionImpl> getVersionHistory();
}
