package functions.binaryfunctions;

import expression.api.Expression;
import expression.impl.BinaryExpression;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.EffectiveValueImpl;
import spreadsheet.impl.SheetImpl;

public class EqualBinaryFunction extends BinaryExpression {

    public EqualBinaryFunction(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    protected EffectiveValue apply(EffectiveValue leftValue, EffectiveValue rightValue) {
        // Compare the values if they are of the same type
        if (leftValue.getCellType() == rightValue.getCellType()) {
            boolean result = leftValue.getValue().equals(rightValue.getValue());
            return new EffectiveValueImpl(CellType.BOOLEAN, result);
        }
        // Return false for mismatched types
        return new EffectiveValueImpl(CellType.BOOLEAN, false);
    }

    @Override
    public EffectiveValue eval(SheetImpl sheet) {
        return apply(this.left.eval(sheet), this.right.eval(sheet));
    }
}
