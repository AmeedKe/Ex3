package functions.unaryfunctions;

import expression.api.Expression;
import expression.impl.UnaryExpression;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.EffectiveValueImpl;
import spreadsheet.impl.SheetImpl;

public class AbsUnaryFunction extends UnaryExpression {

    public AbsUnaryFunction(Expression operand) {
        super(operand);
    }

    @Override
    protected EffectiveValue apply(EffectiveValue operandValue) {
        if (operandValue == null || operandValue.getCellType() != CellType.NUMERIC) {
            throw new IllegalArgumentException("Operand must be a numeric value");
        }

        try {
            double result = Math.abs(operandValue.extractValueWithExpectation(Double.class));
            return new EffectiveValueImpl(CellType.NUMERIC, result);
        } catch (Exception e) {
            throw new ArithmeticException("Error performing absolute value operation: " + e.getMessage());
        }
    }

    @Override
    public EffectiveValue eval(SheetImpl sheet) {
        return apply(this.operand.eval(sheet));
    }
}
