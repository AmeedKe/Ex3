package functions.unaryfunctions;

import expression.impl.UnaryExpression;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.CellImpl;
import spreadsheet.cell.impl.EffectiveValueImpl;
import spreadsheet.impl.SheetImpl;

public class RefUnaryFunction extends UnaryExpression {

    private final String referencedCellId;

    public RefUnaryFunction(String referencedCellId) {
        super(null); // Since the operand in RefUnaryFunction is the referenced cell, pass null to UnaryExpression
        this.referencedCellId = referencedCellId;
    }

    @Override
    protected EffectiveValue apply(EffectiveValue operandValue) {
        // This method is not used in RefUnaryFunction since the logic is different.
        throw new UnsupportedOperationException("apply method is not used in RefUnaryFunction");
    }



    @Override
    public EffectiveValue eval(SheetImpl sheet) {
        // Get the referenced cell from the sheet
        CellImpl referencedCell = sheet.getCell(referencedCellId);

        // If the cell is null, return an undefined EffectiveValue
        if (referencedCell == null) {
            return new EffectiveValueImpl(CellType.STRING, "");
        }

        // Return the actual effective value of the referenced cell
        return referencedCell.getEffectiveValueObject();
    }
}