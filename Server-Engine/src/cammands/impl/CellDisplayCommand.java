package cammands.impl;

import DTO.CellDTO;
import cammands.api.Command;
import cammands.api.CommandsNames;
import spreadsheet.api.Sheet;
//import NotForExc2.Main.view.CellView;

/**
 * Command to display information about a specific cell.
 */
public class CellDisplayCommand implements Command {
    private final Sheet sheet;
    private final String cellId;
    private final CommandsNames name = CommandsNames.DISPLAY_CELL;

    public CellDisplayCommand(Sheet sheet, String cellId) {
        this.sheet = sheet;
        this.cellId = cellId;
    }

    @Override
    public void execute() {
        CellDTO cellDTO = sheet.getCellDTO(cellId);
       // CellView.displayCellInfo(cellDTO);
    }

    @Override
    public CommandsNames getName() {
        return name;
    }
}
