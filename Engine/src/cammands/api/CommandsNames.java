package cammands.api;

/**
 * Enum for supported command numbers.
 */
public enum CommandsNames {
    READ_XML(1),
    DISPLAY_SHEET(2),
    DISPLAY_CELL(3),
    UPDATE_CELL(4),
    VIEW_VERSIONS(5),
    EXIT(6);

    private final int commandNumber;

    CommandsNames(int commandNumber) {
        this.commandNumber = commandNumber;
    }

    public int getCommandNumber() {
        return commandNumber;
    }

    public static CommandsNames fromNumber(int number) {
        for (CommandsNames commandName : CommandsNames.values()) {
            if (commandName.getCommandNumber() == number) {
                return commandName;
            }
        }
        return null; // Handle unknown command cases
    }
}
