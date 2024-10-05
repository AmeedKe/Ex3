package spreadsheet.impl;

/**
 * Class representing a range of cells.
 */
public class Range {
    private final String name;
    private final String topLeftCellId;
    private final String bottomRightCellId;

    public Range(String name, String topLeftCellId, String bottomRightCellId) {
        this.name = name;
        this.topLeftCellId = topLeftCellId;
        this.bottomRightCellId = bottomRightCellId;
    }

    public String getName() {
        return name;
    }

    public String getTopLeftCellId() {
        return topLeftCellId;
    }

    public String getBottomRightCellId() {
        return bottomRightCellId;
    }

    /**
     * Method to check if a cell is within the range.
     */
    public boolean containsCell(String cellId) {
        // Logic to check if a cell is within the range defined by top-left and bottom-right cells
        return false;  // Implement based on your cell ID format
    }

    @Override
    public String toString() {
        return name + ": " + topLeftCellId + ".." + bottomRightCellId;
    }
}
