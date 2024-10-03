package engineimpl;

import ExceptionHandler.*;
import engineapi.Engine;
import spreadsheet.cell.impl.CellImpl;
import spreadsheet.impl.SheetImpl;

import java.util.Map;

public class EngineImpl implements Engine {
    private SheetImpl sheet = new SheetImpl();

    // Constructor with XML file path
    public EngineImpl(String xmlFilePath) {
        loadSheetFromXML(xmlFilePath);
        sheet.initializeCells();
       // sheet.setx(sheet.getx()+1);
    //    evaluateAllCells();  // Recalculate all cells after loading and initializing
    }

    public EngineImpl() {}

    @Override
    public SheetImpl getSheet() {
        return sheet;
    }

    @Override
    public void setSheet(SheetImpl sheet) {
        this.sheet = sheet;
    }

    @Override
    public void updateCell(String cellId, String newValue) throws NullValueException, InvalidFunctionInputException, OutOfBoundsException {
        sheet.updateCell(cellId, newValue);
    }

    @Override
    public void setSheetData(String name, int rows, int columns, int columnWidth, Map<String, CellImpl> newCells) throws Exception {
        sheet.setSheetData(name, rows, columns, columnWidth, newCells);
    }

    @Override
    public void evaluateAllCells() {
        sheet.getCells().forEach((id, cell) -> {
            try {
                cell.updateValue(cell.getOriginalValue(), sheet.getCurrentVersionNumber());
            } catch (NullValueException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // Method to load sheet data from an XML file
    public void loadSheetFromXML(String xmlFilePath) {
        XMLRead xmlRead = new XMLRead(xmlFilePath, sheet);
        try {
            xmlRead.execute();  // This will load the data into the `sheet`
        } catch (Exception e) {
            sheet = null;  // Set sheet to null to indicate failure
            throw e;
        }
    }
}
