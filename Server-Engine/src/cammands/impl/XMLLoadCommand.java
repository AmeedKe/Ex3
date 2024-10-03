package cammands.impl;

import cammands.api.Command;
import cammands.api.CommandsNames;
import engineimpl.EngineImpl;
import ExceptionHandler.UnvalidPathException;
import ExceptionHandler.InvalidXmlContentException;
import spreadsheet.impl.SheetImpl;

public class XMLLoadCommand implements Command {
    private final EngineImpl engine;
    private final String xmlFilePath;
    private final CommandsNames name = CommandsNames.READ_XML; // Define the command name

    public XMLLoadCommand(EngineImpl engine, String xmlFilePath) {
        this.engine = engine;
        this.xmlFilePath = xmlFilePath;
    }

    @Override
    public void execute() {
        try {
            if (engine.getSheet()==null)
            {
                engine.setSheet(new SheetImpl());
            }
            engine.loadSheetFromXML(xmlFilePath);
            System.out.println("Sheet loaded successfully from " + xmlFilePath);
        } catch (UnvalidPathException | InvalidXmlContentException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while loading the sheet: " + e.getMessage());
        }
    }

    @Override
    public CommandsNames getName() {
        return name;
    }
}
