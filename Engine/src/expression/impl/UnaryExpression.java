package expression.impl;

import expression.api.Expression;
import spreadsheet.api.Sheet;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.impl.SheetImpl;

public abstract class UnaryExpression implements Expression {

    protected final Expression operand;

    public UnaryExpression(Expression operand) {
        this.operand = operand;
    }

    @Override
    public EffectiveValue eval(SheetImpl sheet) {
        EffectiveValue value = operand.eval(sheet);

        if (value == null) {
            throw new IllegalArgumentException("Operand cannot be null");
        }

        return apply(value);
    }

    protected void validateNumericOperand(EffectiveValue value) {
        if (value.getCellType() != CellType.NUMERIC) {
            throw new IllegalArgumentException("Operand must be numeric");
        }
    }

    protected void validateStringOperand(EffectiveValue value) {
        if (value.getCellType() != CellType.STRING) {
            throw new IllegalArgumentException("Operand must be a string");
        }
    }

    protected void validateBooleanOperand(EffectiveValue value) {
        if (value.getCellType() != CellType.BOOLEAN) {
            throw new IllegalArgumentException("Operand must be boolean");
        }
    }

    protected abstract EffectiveValue apply(EffectiveValue value);
}
