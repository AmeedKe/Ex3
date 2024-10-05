package spreadsheet.api;

import spreadsheet.cell.impl.CellImpl;
import java.util.Map;

public interface Version {
    Map<String, CellImpl> getData();
    void setData(Map<String, CellImpl> data);

    String getName();
    void setName(String name);
    int getChangedCellsCount();
    void setChangedCellsCount(int changedCellsCount);
}
