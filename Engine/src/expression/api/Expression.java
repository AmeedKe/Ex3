package expression.api;

import spreadsheet.api.Sheet;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.CellImpl;
import spreadsheet.impl.SheetImpl;

import java.util.Map;
@FunctionalInterface
public interface Expression {
    EffectiveValue eval(SheetImpl sheet);
}
