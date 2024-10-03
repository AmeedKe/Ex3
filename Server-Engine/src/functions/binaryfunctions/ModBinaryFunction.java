package functions.binaryfunctions;

import expression.api.Expression;
import expression.impl.BinaryExpression;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.EffectiveValueImpl;
import spreadsheet.impl.SheetImpl;

public class ModBinaryFunction extends BinaryExpression {

    public ModBinaryFunction(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    protected EffectiveValue apply(EffectiveValue leftValue, EffectiveValue rightValue) {
        validateNumericOperands(leftValue, rightValue);
        try {
            int dividend = leftValue.extractValueWithExpectation(Double.class).intValue();
            int divisor = rightValue.extractValueWithExpectation(Double.class).intValue();
            if (divisor == 0) {
                throw new ArithmeticException("Modulo by zero is not allowed");
            }
            int result = dividend % divisor;
            return new EffectiveValueImpl(CellType.NUMERIC, result);
        } catch (Exception e) {
            throw new ArithmeticException("Error performing modulo operation: " + e.getMessage());
        }
    }

    @Override
    public EffectiveValue eval(SheetImpl sheet) {
        return apply(this.left.eval(sheet),this.right.eval(sheet));
    }
}
