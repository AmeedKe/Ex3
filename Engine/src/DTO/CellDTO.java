package DTO;

import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.CellImpl;
import spreadsheet.cell.impl.EffectiveValueImpl;

import java.util.HashSet;
import java.util.Set;

public class CellDTO {
    private final String originalValue;
    private final EffectiveValue effectiveValue;
    private final int lastUpdatedVersion;
    private final Set<String> dependencies;
    private final Set<String> influences;
    private final String cellId; // Added the cellId member

    // Constructors
    public CellDTO(String originalValue, EffectiveValue effectiveValue, int lastUpdatedVersion, Set<String> dependencies, Set<String> influences, String cellId) {
        this.originalValue = originalValue;
        this.effectiveValue = effectiveValue;
        this.lastUpdatedVersion = lastUpdatedVersion;
        this.dependencies = dependencies;
        this.influences = influences;
        this.cellId = cellId; // Initialize the cellId
    }

    public CellDTO(CellImpl cellImpl) {
        try {
            this.originalValue = cellImpl.getOriginalValue();
            this.effectiveValue = new EffectiveValueImpl(cellImpl.getEffectiveValueObject().getCellType(), cellImpl.getEffectiveValueObject().getValue());
            this.lastUpdatedVersion = cellImpl.getLastUpdatedVersion();
            this.dependencies = deepCopySet(cellImpl.getDependencies());
            this.influences = deepCopySet(cellImpl.getInfluences());
            this.cellId = cellImpl.getCellId();
        } catch (Exception e) {
            // Handle or log the exception
            throw new RuntimeException("Error creating CellDTO: " + e.getMessage(), e);
        }
    }


    // Method to deep copy a Set<String>
    private static Set<String> deepCopySet(Set<String> originalSet) {
        return originalSet == null ? null : new HashSet<>(originalSet);
    }

    // Getters
    public String getOriginalValue() {
        return originalValue;
    }

    public EffectiveValue getEffectiveValue() {
        return effectiveValue;
    }

    public int getLastUpdatedVersion() {
        return lastUpdatedVersion;
    }

    public Set<String> getDependencies() {
        return dependencies;
    }

    public Set<String> getInfluences() {
        return influences;
    }

    public String getCellId() {
        return cellId;
    }
}
