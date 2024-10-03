package spreadsheet.cell.api;

import ExceptionHandler.NullValueException;

import java.util.Set;

public interface Cell {
    // Returns the effective value of the cell as a string.
    String getEffectiveValue();

    // Returns the effective value of the cell as an EffectiveValue
    public EffectiveValue getEffectiveValueObject();

    // Returns the original value of the cell as a string.
    String getOriginalValue();

    // Updates the cell's value and its version.
    void updateValue(String newValue, int currentVersion) throws NullValueException;

    // Returns the version number when the cell was last updated.
    int getLastUpdatedVersion();

    // Returns a set of cell IDs that this cell depends on.
    Set<String> getDependencies();

    // Adds a dependency to this cell.
    void addDependency(String cellId);

    // Clears all dependencies from this cell.
    void clearDependencies();

    // Returns a set of cell IDs that depend on this cell.
    Set<String> getInfluences();

    // Adds an influence to this cell.
    void addInfluence(String cellId);
}