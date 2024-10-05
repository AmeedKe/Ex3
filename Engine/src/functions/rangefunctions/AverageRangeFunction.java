package functions.rangefunctions;

import expression.impl.RangeExpression;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.EffectiveValueImpl;
import spreadsheet.impl.SheetImpl;

import java.util.Set;

public class AverageRangeFunction extends RangeExpression {

    private final String rangeName;

    public AverageRangeFunction(String rangeName) {
        super(rangeName);
        this.rangeName = rangeName; // Store the range name
    }

    @Override
    public EffectiveValue eval(SheetImpl sheet) {
        // Check if the range exists in the range map
        if (!sheet.getRanges().containsKey(rangeName)) {
            // If the range doesn't exist, return "NaN" as a string
            return new EffectiveValueImpl(CellType.STRING, "NaN");
        }

        Set<String> cellIds = sheet.getRanges().get(rangeName);

        if (cellIds == null || cellIds.isEmpty()) {
            // If the range is empty, return "NaN" as a string
            return new EffectiveValueImpl(CellType.STRING, "NaN");
        }

        double sum = 0;
        int count = 0;

        for (String cellId : cellIds) {
            EffectiveValue cellValue = sheet.getCell(cellId).getEffectiveValueObject();
            if (cellValue.getCellType() == CellType.NUMERIC) {
                sum += cellValue.extractValueWithExpectation(Double.class);
                count++;
            }
        }

        // If no numeric values were found, return "NaN"
        if (count == 0) {
            return new EffectiveValueImpl(CellType.STRING, "NaN");
        }

        double average = sum / count;
        return new EffectiveValueImpl(CellType.NUMERIC, average);
    }
}
