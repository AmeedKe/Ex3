package expression.impl;

import expression.api.Expression;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.impl.SheetImpl;

public abstract class BinaryExpression implements Expression {

    protected final Expression left;
    protected final Expression right;

    public BinaryExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public EffectiveValue eval(SheetImpl sheet) {
        EffectiveValue leftValue = left.eval(sheet);
        EffectiveValue rightValue = right.eval(sheet);

        if (leftValue == null || rightValue == null) {
            throw new IllegalArgumentException("Operands cannot be null");
        }

        return apply(leftValue, rightValue);
    }

    protected void validateNumericOperands(EffectiveValue leftValue, EffectiveValue rightValue) {
        if (leftValue.getCellType() != CellType.NUMERIC || rightValue.getCellType() != CellType.NUMERIC) {
            throw new IllegalArgumentException("Operands must be numeric");
        }
    }

    protected void validateStringOperands(EffectiveValue leftValue, EffectiveValue rightValue) {
        if (leftValue.getCellType() != CellType.STRING || rightValue.getCellType() != CellType.STRING) {
            throw new IllegalArgumentException("Operands must be strings");
        }
    }

    protected void validateBooleanOperands(EffectiveValue leftValue, EffectiveValue rightValue) {
        if (leftValue.getCellType() != CellType.BOOLEAN || rightValue.getCellType() != CellType.BOOLEAN) {
            throw new IllegalArgumentException("Operands must be boolean");
        }
    }

    protected abstract EffectiveValue apply(EffectiveValue leftValue, EffectiveValue rightValue);
}
