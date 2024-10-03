package functions.binaryfunctions;

import expression.api.Expression;
import expression.impl.BinaryExpression;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.EffectiveValueImpl;
import spreadsheet.impl.SheetImpl;

public class LessBinaryFunction extends BinaryExpression {

    public LessBinaryFunction(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    protected EffectiveValue apply(EffectiveValue leftValue, EffectiveValue rightValue) {
        // Ensure both are numeric
        if (leftValue.getCellType() != CellType.NUMERIC || rightValue.getCellType() != CellType.NUMERIC) {
            return new EffectiveValueImpl(CellType.BOOLEAN, "UNKNOWN");
        }

        double leftNum = leftValue.extractValueWithExpectation(Double.class);
        double rightNum = rightValue.extractValueWithExpectation(Double.class);
        return new EffectiveValueImpl(CellType.BOOLEAN, leftNum <= rightNum);
    }

    @Override
    public EffectiveValue eval(SheetImpl sheet) {
        return apply(this.left.eval(sheet), this.right.eval(sheet));
    }
}
