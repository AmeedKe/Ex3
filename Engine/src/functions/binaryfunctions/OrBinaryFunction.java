package functions.binaryfunctions;

import expression.api.Expression;
import expression.impl.BinaryExpression;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.EffectiveValueImpl;
import spreadsheet.impl.SheetImpl;

public class OrBinaryFunction extends BinaryExpression {

    public OrBinaryFunction(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    protected EffectiveValue apply(EffectiveValue leftValue, EffectiveValue rightValue) {
        // Check if both values are valid boolean values
        if (isValidBoolean(leftValue) && isValidBoolean(rightValue)) {
            boolean leftBool = convertToBoolean(leftValue);
            boolean rightBool = convertToBoolean(rightValue);
            return new EffectiveValueImpl(CellType.BOOLEAN, leftBool || rightBool);
        }

        // If either of the values is not a valid boolean, return "UNKNOWN"
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
        // Default to false if not a valid boolean (this is guarded by isValidBoolean check)
        return false;
    }

    @Override
    public EffectiveValue eval(SheetImpl sheet) {
        return apply(this.left.eval(sheet), this.right.eval(sheet));
    }
}
