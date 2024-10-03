package functions.binaryfunctions;

import expression.api.Expression;
import expression.impl.BinaryExpression;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.EffectiveValueImpl;
import spreadsheet.impl.SheetImpl;

public class DivideBinaryFunction extends BinaryExpression {

    public DivideBinaryFunction(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    protected EffectiveValue apply(EffectiveValue leftValue, EffectiveValue rightValue) {
        validateNumericOperands(leftValue, rightValue);
        try {
            double divisor = rightValue.extractValueWithExpectation(Double.class);
            if (divisor == 0) {
                return new EffectiveValueImpl(CellType.STRING, "NaN");
            }
            else
            {
                double result = leftValue.extractValueWithExpectation(Double.class) / divisor;
                return new EffectiveValueImpl(CellType.NUMERIC, result);
            }

        } catch (Exception e) {
            throw new ArithmeticException("Error performing division: " + e.getMessage());
        }
    }

    @Override
    public EffectiveValue eval(SheetImpl sheet) {
        return apply(this.left.eval(sheet),this.right.eval(sheet));
    }
}