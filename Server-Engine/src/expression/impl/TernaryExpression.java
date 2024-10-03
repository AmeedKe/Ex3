package expression.impl;

import expression.api.Expression;
import spreadsheet.api.Sheet;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.impl.SheetImpl;

public abstract class TernaryExpression implements Expression {

    protected final Expression first;
    protected final Expression second;
    protected final Expression third;

    public TernaryExpression(Expression first, Expression second, Expression third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    public EffectiveValue eval(SheetImpl sheet) {
        EffectiveValue firstValue = first.eval(sheet);
        EffectiveValue secondValue = second.eval(sheet);
        EffectiveValue thirdValue = third.eval(sheet);

        // Validate that all operands are strings
        validateStringOperands(firstValue, secondValue, thirdValue);

        return apply(firstValue, secondValue, thirdValue);
    }

    /**
     * Validate that all operands are of string type.
     */
    protected void validateStringOperands(EffectiveValue firstValue, EffectiveValue secondValue, EffectiveValue thirdValue) {
        if (firstValue.getCellType() != CellType.STRING ||
                secondValue.getCellType() != CellType.STRING ||
                thirdValue.getCellType() != CellType.STRING) {
            throw new IllegalArgumentException("All operands must be strings");
        }
    }

    /**
     * Abstract method to apply the ternary operation.
     * Subclasses must define how the three string values are combined.
     */
    protected abstract EffectiveValue apply(EffectiveValue firstValue, EffectiveValue secondValue, EffectiveValue thirdValue);
}
