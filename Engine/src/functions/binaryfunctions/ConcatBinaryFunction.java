package functions.binaryfunctions;

import expression.api.Expression;
import expression.impl.BinaryExpression;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.EffectiveValueImpl;
import spreadsheet.impl.SheetImpl;

public class ConcatBinaryFunction extends BinaryExpression {

    public ConcatBinaryFunction(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    protected EffectiveValue apply(EffectiveValue leftValue, EffectiveValue rightValue) {
        // Check if both operands are strings
        if (leftValue.getCellType() != CellType.STRING || rightValue.getCellType() != CellType.STRING) {
            // If not, return "!UNDEFINED!"
            return new EffectiveValueImpl(CellType.STRING, "!UNDEFINED!");
        }

        // Perform concatenation if both operands are strings
        String result = leftValue.extractValueWithExpectation(String.class) + rightValue.extractValueWithExpectation(String.class);
        return new EffectiveValueImpl(CellType.STRING, result);
    }

    @Override
    public EffectiveValue eval(SheetImpl sheet) {
        return apply(this.left.eval(sheet), this.right.eval(sheet));
    }
}
