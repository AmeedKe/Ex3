package functions.api;

import expression.api.Expression;
import functions.binaryfunctions.*;
import functions.rangefunctions.AverageRangeFunction;
import functions.rangefunctions.SumRangeFunction;
import functions.trinaryfunctions.IfTernaryFunction;
import functions.trinaryfunctions.SubstringTernaryFunction;
import functions.unaryfunctions.*;
import spreadsheet.impl.SheetImpl;

import java.util.List;

public enum FunctionType {
    // Mathematical functions
    PLUS(2) {
        @Override
        public Expression createExpression(List<Expression> operands, SheetImpl sheet) {
            return new PlusBinaryFunction(operands.get(0), operands.get(1));
        }
    },
    MINUS(2) {
        @Override
        public Expression createExpression(List<Expression> operands, SheetImpl sheet) {
            return new MinusBinaryFunction(operands.get(0), operands.get(1));
        }
    },
    TIMES(2) {
        @Override
        public Expression createExpression(List<Expression> operands, SheetImpl sheet) {
            return new TimesBinaryFunction(operands.get(0), operands.get(1));
        }
    },
    DIVIDE(2) {
        @Override
        public Expression createExpression(List<Expression> operands, SheetImpl sheet) {
            return new DivideBinaryFunction(operands.get(0), operands.get(1));
        }
    },
    MOD(2) {
        @Override
        public Expression createExpression(List<Expression> operands, SheetImpl sheet) {
            return new ModBinaryFunction(operands.get(0), operands.get(1));
        }
    },
    POW(2) {
        @Override
        public Expression createExpression(List<Expression> operands, SheetImpl sheet) {
            return new PowerBinaryFunction(operands.get(0), operands.get(1));
        }
    },
    ABS(1) {
        @Override
        public Expression createExpression(List<Expression> operands, SheetImpl sheet) {
            return new AbsUnaryFunction(operands.get(0));
        }
    },
    PERCENT(2) {
        @Override
        public Expression createExpression(List<Expression> operands, SheetImpl sheet) {
            return new PercentBinaryFunction(operands.get(0), operands.get(1));
        }
    },

    SUM(1) {
        @Override
        public Expression createExpression(List<Expression> operands, SheetImpl sheet) {
            return new SumRangeFunction(operands.get(0).eval(sheet).extractValueWithExpectation(String.class));
        }
    },
    AVERAGE(1) {
        public Expression createExpression(List<Expression> operands, SheetImpl sheet) {
            return new AverageRangeFunction(operands.get(0).eval(sheet).extractValueWithExpectation(String.class));
        }
    },


    // String functions
    CONCAT(2) {
        @Override
        public Expression createExpression(List<Expression> operands, SheetImpl sheet) {
            return new ConcatBinaryFunction(operands.get(0), operands.get(1));
        }
    },
    SUB(3) {
        @Override
        public Expression createExpression(List<Expression> operands, SheetImpl sheet) {
            return new SubstringTernaryFunction(operands.get(0), operands.get(1), operands.get(2));
        }
    },

    // Logical functions
    EQUAL(2) {
        @Override
        public Expression createExpression(List<Expression> operands, SheetImpl sheet) {
            return new EqualBinaryFunction(operands.get(0), operands.get(1));
        }
    },
    NOT(1) {
        @Override
        public Expression createExpression(List<Expression> operands, SheetImpl sheet) {
            return new NotUnaryFunction(operands.get(0));
        }
    },
    OR(2) {
        @Override
        public Expression createExpression(List<Expression> operands, SheetImpl sheet) {
            return new OrBinaryFunction(operands.get(0), operands.get(1));
        }
    },
    AND(2) {
        @Override
        public Expression createExpression(List<Expression> operands, SheetImpl sheet) {
            return new AndBinaryFunction(operands.get(0), operands.get(1));
        }
    },
    BIGGER(2) {
        @Override
        public Expression createExpression(List<Expression> operands, SheetImpl sheet) {
            return new BiggerBinaryFunction(operands.get(0), operands.get(1));
        }
    },
    LESS(2) {
        @Override
        public Expression createExpression(List<Expression> operands, SheetImpl sheet) {
            return new LessBinaryFunction(operands.get(0), operands.get(1));
        }
    },
    IF(3) {
        @Override
        public Expression createExpression(List<Expression> operands, SheetImpl sheet) {
            return new IfTernaryFunction(operands.get(0), operands.get(1), operands.get(2));
        }
    },

    // System functions
    REF(1) {
        @Override
        public Expression createExpression(List<Expression> operands, SheetImpl sheet) {
            return new RefUnaryFunction(operands.get(0).eval(sheet).toString());
        }
    };

    private final int operandCount;

    FunctionType(int operandCount) {
        this.operandCount = operandCount;
    }

    public int getOperandCount() {
        return operandCount;
    }

    public abstract Expression createExpression(List<Expression> operands, SheetImpl sheet);
}
