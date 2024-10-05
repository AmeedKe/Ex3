package DTO;

import java.util.Map;
import java.util.HashMap;

public class SheetDTO {
    private final String name;
    private final int rows;
    private final int columns;
    private final int columnWidth;
    private final Map<String, CellDTO> cells;
    private final int versionNumber;  // Field to store the version number
    private int ChangedCells = 0;

    public SheetDTO(String name, int rows, int columns, int columnWidth, int versionNumber) {
        this.name = name;
        this.rows = rows;
        this.columns = columns;
        this.columnWidth = columnWidth;
        this.versionNumber = versionNumber;
        this.cells = new HashMap<>();
    }

    // Getters

    public int getChangedCells()
    {
        return ChangedCells;
    }

    public void setChangedCells(int changedCells) {
        ChangedCells = changedCells;
    }

    public String getName() {
        return name;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public int getColumnWidth() {
        return columnWidth;
    }

    public Map<String, CellDTO> getCells() {
        return new HashMap<>(cells); // Return a copy to maintain immutability
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    // Method to add a cell to the map
    public void addCell(String cellId, CellDTO cellDTO) {
        this.cells.put(cellId, cellDTO);
    }

    // Method to retrieve a cell by its ID
    public CellDTO getCell(String cellId) {
        return this.cells.get(cellId);
    }

    // Method to update a cell in the map
    public void updateCell(String cellId, CellDTO cellDTO) {
        this.cells.put(cellId, cellDTO);
    }

    // Method to remove a cell by its ID
    public void removeCell(String cellId) {
        this.cells.remove(cellId);
    }

    // Method to clear all cells
    public void clearCells() {
        this.cells.clear();
    }
}
