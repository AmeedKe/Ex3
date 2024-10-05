package functions.unaryfunctions;

import expression.api.Expression;
import expression.impl.UnaryExpression;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.EffectiveValueImpl;
import spreadsheet.impl.SheetImpl;

public class NotUnaryFunction extends UnaryExpression {

    public NotUnaryFunction(Expression operand) {
        super(operand);
    }

    @Override
    protected EffectiveValue apply(EffectiveValue operandValue) {
        // Check if the operand is a valid boolean
        if (isValidBoolean(operandValue)) {
            boolean value = convertToBoolean(operandValue);
            return new EffectiveValueImpl(CellType.BOOLEAN, !value);
        }
        // If not a valid boolean, return "UNKNOWN"
        return new EffectiveValueImpl(CellType.BOOLEAN, "UNKNOWN");
    }

    // Helper method to determine if a value can be considered a boolean
    private boolean isValidBoolean(EffectiveValue value) {
        if (value.getCellType() == CellType.BOOLEAN) {
            return true;
        } else if (value.getCellType() == CellType.STRING) {
            String stringValue = value.extractValueWithExpectation(String.class).toLowerCase();
            return stringValue.equals("true") || stringValue.equals("false");
        }
        return false;
    }

    // Helper method to convert both BOOLEAN and valid STRING values to boolean
    private boolean convertToBoolean(EffectiveValue value) {
        if (value.getCellType() == CellType.BOOLEAN) {
            return value.extractValueWithExpectation(Boolean.class);
        } else if (value.getCellType() == CellType.STRING) {
            String stringValue = value.extractValueWithExpectation(String.class).toLowerCase();
            return stringValue.equals("true");
        }
        // Default to false if not a valid boolean (guarded by isValidBoolean check)
        return false;
    }

    @Override
    public EffectiveValue eval(SheetImpl sheet) {
        return apply(this.operand.eval(sheet));
    }
}
