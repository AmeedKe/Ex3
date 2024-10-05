package spreadsheet.cell.impl;

import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;

public class EffectiveValueImpl implements EffectiveValue {

    private final CellType cellType;
    private final Object value;

    public EffectiveValueImpl(CellType cellType, Object value) {
        this.cellType = cellType;
        this.value = value;
    }

    @Override
    public CellType getCellType() {
        return cellType;
    }

    @Override
    public Object getValue() {
        return value;
    }
    @Override
    public String toString() {
        return value != null ? value.toString():"";
    }

    @Override
    public <T> T extractValueWithExpectation(Class<T> type) {
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        throw new ClassCastException("Expected type " + type.getName() + " but found " + value.getClass().getName());
    }
}