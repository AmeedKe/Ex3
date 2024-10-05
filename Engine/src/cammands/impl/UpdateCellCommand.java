package cammands.impl;

import DTO.CellDTO;
import ExceptionHandler.InvalidFunctionInputException;
import ExceptionHandler.NullValueException;
import ExceptionHandler.OutOfBoundsException;
import cammands.api.Command;
import cammands.api.CommandsNames;
import spreadsheet.api.Sheet;
//import NotForExc2.Main.view.CellView;

/**
 * Command to update the value of a specific cell.
 */
public class UpdateCellCommand implements Command {
    private final Sheet sheet;
    private final String cellId;
    private final String newValue;
    private final CommandsNames name = CommandsNames.UPDATE_CELL;  // Command name member

    public UpdateCellCommand(Sheet sheet, String cellId, String newValue) {
        this.sheet = sheet;
        this.cellId = cellId;
        this.newValue = newValue;
    }

    @Override
    public void execute() throws NullValueException, InvalidFunctionInputException, OutOfBoundsException {
        // Update the cell value


        sheet.updateCell(cellId, newValue);

        // Display the updated cell information
        CellDTO cell = sheet.getCellDTO(cellId);
       //CellView.displayCellInfo(cell);

    }

    @Override
    public CommandsNames getName() {
        return name;
    }
}