package functions.trinaryfunctions;

import expression.api.Expression;
import expression.impl.TernaryExpression;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.EffectiveValueImpl;
import spreadsheet.impl.SheetImpl;

public class IfTernaryFunction extends TernaryExpression {

    public IfTernaryFunction(Expression condition, Expression trueValue, Expression falseValue) {
        super(condition, trueValue, falseValue);
    }

    @Override
    protected EffectiveValue apply(EffectiveValue conditionValue, EffectiveValue trueValue, EffectiveValue falseValue) {
        if (conditionValue.getCellType() == CellType.BOOLEAN&&conditionValue.getValue()!="UNKNOWN") {
            boolean condition = conditionValue.extractValueWithExpectation(Boolean.class);
            return condition ? trueValue : falseValue;
        }
        return new EffectiveValueImpl(CellType.STRING, "UNKNOWN");
    }

    @Override
    public EffectiveValue eval(SheetImpl sheet) {
        return apply(this.first.eval(sheet), this.second.eval(sheet), this.third.eval(sheet));
    }
}
