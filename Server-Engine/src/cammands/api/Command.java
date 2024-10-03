package cammands.api;

import ExceptionHandler.InvalidFunctionInputException;
import ExceptionHandler.NullValueException;
import ExceptionHandler.OutOfBoundsException;

/**
 * Interface representing a command that can be executed on a spreadsheet.
 */
public interface Command {
     void execute() throws NullValueException, InvalidFunctionInputException, OutOfBoundsException;
    // Method to retrieve the command name
    CommandsNames getName();
}