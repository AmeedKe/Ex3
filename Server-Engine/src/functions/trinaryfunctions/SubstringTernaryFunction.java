package functions.trinaryfunctions;

import expression.api.Expression;
import expression.impl.TernaryExpression;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.EffectiveValueImpl;
import spreadsheet.impl.SheetImpl;

public class SubstringTernaryFunction extends TernaryExpression {

    public SubstringTernaryFunction(Expression source, Expression startIndex, Expression endIndex) {
        super(source, startIndex, endIndex);
    }

    @Override
    public EffectiveValue eval(SheetImpl sheet) {
        EffectiveValue sourceValue = first.eval(sheet);
        EffectiveValue startValue = second.eval(sheet);
        EffectiveValue endValue = third.eval(sheet);

        if (sourceValue == null || startValue == null || endValue == null) {
            throw new IllegalArgumentException("Operands cannot be null");
        }

        validateStringOperand(sourceValue);
        validateNumericOperand(startValue);
        validateNumericOperand(endValue);

        try {
            String source = sourceValue.extractValueWithExpectation(String.class);
            int start = startValue.extractValueWithExpectation(Double.class).intValue();
            int end = endValue.extractValueWithExpectation(Double.class).intValue();

            if (start < 0 || end < 0 || start > end || end >= source.length()) {
                return new EffectiveValueImpl(CellType.STRING, "!UNDEFINED!");
            }

            String result = source.substring(start, end + 1);
            return new EffectiveValueImpl(CellType.STRING, result);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error performing substring operation: " + e.getMessage());
        }
    }

    /**
     * Validate that the source operand is a string.
     */
    private void validateStringOperand(EffectiveValue value) {
        if (value.getCellType() != CellType.STRING) {
            throw new IllegalArgumentException("Source must be a string");
        }
    }

    /**
     * Validate that the index operands are numeric.
     */
    private void validateNumericOperand(EffectiveValue value) {
        if (value.getCellType() != CellType.NUMERIC) {
            throw new IllegalArgumentException("Indices must be numeric");
        }
    }

    @Override
    protected EffectiveValue apply(EffectiveValue firstValue, EffectiveValue secondValue, EffectiveValue thirdValue) {
        // Not used directly since eval() is overridden
        return null;
    }
}
