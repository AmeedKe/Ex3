package expression.impl;

import expression.api.Expression;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.impl.SheetImpl;

import java.util.Set;

public class RangeExpression implements Expression {

    private final String rangeName;

    public RangeExpression(String rangeName) {
        this.rangeName = rangeName;
    }

    @Override
    public EffectiveValue eval(SheetImpl sheet) {
        // Get the set of cells within the range using the range name
        Set<String> cellIds = sheet.getRanges().get(rangeName);

        // Check if the range exists, return UNDEFINED if it doesn't
        if (cellIds == null || cellIds.isEmpty()) {
            throw new IllegalArgumentException("Range not found or empty: " + rangeName);
        }

        // This is the point where you can decide what to do with the range of cells
        // For example, returning the sum or average would require further logic here
        // For now, we just return the range name as a placeholder (to be replaced in subclasses)
        return null; // To be handled by subclasses (e.g., SUM or AVERAGE)
    }

    public String getRangeName() {
        return rangeName;
    }
}
