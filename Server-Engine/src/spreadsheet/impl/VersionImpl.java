package spreadsheet.impl;

import spreadsheet.api.Version;
import spreadsheet.cell.impl.CellImpl;
import java.util.Map;

public class VersionImpl implements Version {
    private Map<String, CellImpl> data;
    private int changedCellsCount;
    private String name;

    // Constructor
    public VersionImpl(Map<String, CellImpl> data, int changedCellsCount, String name) {
        this.data = data;
        this.changedCellsCount = changedCellsCount;  // Don't hardcode to 0, use passed argument
        this.name = name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    // Getters and Setters
    @Override
    public Map<String, CellImpl> getData() {
        return data;
    }

    @Override
    public void setData(Map<String, CellImpl> data) {
        this.data = data;
    }

    @Override
    public int getChangedCellsCount() {
        return changedCellsCount;
    }

    @Override
    public void setChangedCellsCount(int changedCellsCount) {
        this.changedCellsCount = changedCellsCount;
    }
}
