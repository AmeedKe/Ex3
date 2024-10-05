package functions.binaryfunctions;

import expression.api.Expression;
import expression.impl.BinaryExpression;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.EffectiveValueImpl;
import spreadsheet.impl.SheetImpl;

public class PercentBinaryFunction extends BinaryExpression {

    public PercentBinaryFunction(Expression part, Expression whole) {
        super(part, whole);
    }

    @Override
    protected EffectiveValue apply(EffectiveValue partValue, EffectiveValue wholeValue) {
        validateNumericOperands(partValue, wholeValue);
        try {
            double part = partValue.extractValueWithExpectation(Double.class);
            double whole = wholeValue.extractValueWithExpectation(Double.class);
            double result = (part * whole) / 100.0;
            return new EffectiveValueImpl(CellType.NUMERIC, result);
        } catch (Exception e) {
            return new EffectiveValueImpl(CellType.STRING, "NaN");
        }
    }

    @Override
    public EffectiveValue eval(SheetImpl sheet) {
        return apply(this.left.eval(sheet), this.right.eval(sheet));
    }
}
