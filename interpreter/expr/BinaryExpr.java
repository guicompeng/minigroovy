package interpreter.expr;

import interpreter.util.Utils;
import interpreter.value.BooleanValue;
import interpreter.value.NumberValue;
import interpreter.value.TextValue;
import interpreter.value.Value;

public class BinaryExpr extends Expr {

    public enum Op {
        AndOp,
        OrOp,
        EqualOp,
        NotEqualOp,
        LowerThanOp,
        LowerEqualOp,
        GreaterThanOp,
        GreaterEqualOp,
        ContainsOp,
        NotContainsOp,
        AddOp,
        SubOp,
        MulOp,
        DivOp,
        ModOp,
        PowerOp;
    }

    private Expr left;
    private Op op;
    private Expr right;

    public BinaryExpr(int line, Expr left, Op op, Expr right) {
        super(line);

        this.left = left;
        this.op = op;
        this.right = right;
    }

    @Override
    public Value<?> expr() {
        Value<?> v = null;
        switch (op) {
            case AndOp:
                v = andOp();
                break;
            case OrOp:
                v = orOp();
                break;
            case EqualOp:
                v = equalOp();
                break;
            case NotEqualOp:
                v = notEqualOp();
                break;
            case LowerThanOp:
                v = lowerThanOp();
                break;
            case LowerEqualOp:
                v = lowerEqualOp();
                break;
            case GreaterThanOp:
                v = greaterThanOp();
                break;
            case GreaterEqualOp:
                v = greaterEqualOp();
                break;
            case ContainsOp:
                v = containsOp();
                break;
            case NotContainsOp:
                v = notContainsOp();
                break;
            case AddOp:
                v = addOp();
                break;
            case SubOp:
                v = subOp();
                break;
            case MulOp:
                v = mulOp();
                break;
            case DivOp:
                v = divOp();
                break;
            case ModOp:
                v = modOp();
                break;
            case PowerOp:
                v = powerOp();
                break;
            default:
                Utils.abort(super.getLine());
        }

        return v;
    }

    private Value<?> andOp() {
        Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();

        if (!(lvalue instanceof NumberValue) ||
            !(rvalue instanceof NumberValue))
            Utils.abort(super.getLine());

        NumberValue nvl = (NumberValue) lvalue;
        int lv = nvl.value();

        NumberValue nvr = (NumberValue) rvalue;
        int rv = nvr.value();

        NumberValue res = lv != 0 && rv != 0 ? new NumberValue(1) : new NumberValue(0);

        return res;
    }

    private Value<?> orOp() {
        Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();

        if (!(lvalue instanceof NumberValue) ||
            !(rvalue instanceof NumberValue))
            Utils.abort(super.getLine());

        NumberValue nvl = (NumberValue) lvalue;
        int lv = nvl.value();

        NumberValue nvr = (NumberValue) rvalue;
        int rv = nvr.value();

        NumberValue res = lv != 0 || rv != 0 ? new NumberValue(1) : new NumberValue(0);

        return res;
    }

    private Value<?> equalOp() {
        Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();

        NumberValue res = new NumberValue(0);

        if(lvalue == null && rvalue == null) {
            res = new NumberValue(1);
        }
        else if((lvalue instanceof TextValue) && (rvalue instanceof TextValue)) {
            TextValue nvl = (TextValue) lvalue;
            String lv = nvl.value();

            TextValue nvr = (TextValue) rvalue;
            String rv = nvr.value();

            if(lv.equals(rv))
                res = new NumberValue(1);
            
        } else if((lvalue instanceof NumberValue) && (rvalue instanceof NumberValue)) {
            NumberValue nvl = (NumberValue) lvalue;
            int lv = nvl.value();

            NumberValue nvr = (NumberValue) rvalue;
            int rv = nvr.value();

            if(lv == rv)
                res =  new NumberValue(1);

        } else if((lvalue instanceof BooleanValue) && (rvalue instanceof BooleanValue)) {
            BooleanValue nvl = (BooleanValue) lvalue;
            boolean lv = nvl.value();

            BooleanValue nvr = (BooleanValue) rvalue;
            boolean rv = nvr.value();

            if(lv == rv)
                res =  new NumberValue(1);
        }

        return res;
    }

    private Value<?> notEqualOp() {
        Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();

        NumberValue res = new NumberValue(1);

        if(lvalue == null && rvalue == null) {
            res = new NumberValue(0);
        }
        else if((lvalue instanceof TextValue) && (rvalue instanceof TextValue)) {
            TextValue nvl = (TextValue) lvalue;
            String lv = nvl.value();

            TextValue nvr = (TextValue) rvalue;
            String rv = nvr.value();

            if(lv.equals(rv))
                res = new NumberValue(0);
            
        } else if((lvalue instanceof NumberValue) && (rvalue instanceof NumberValue)) {
            NumberValue nvl = (NumberValue) lvalue;
            int lv = nvl.value();

            NumberValue nvr = (NumberValue) rvalue;
            int rv = nvr.value();

            if(lv == rv)
                res =  new NumberValue(0);

        } else if((lvalue instanceof BooleanValue) && (rvalue instanceof BooleanValue)) {
            BooleanValue nvl = (BooleanValue) lvalue;
            boolean lv = nvl.value();

            BooleanValue nvr = (BooleanValue) rvalue;
            boolean rv = nvr.value();

            if(lv == rv)
                res =  new NumberValue(0);
        }

        return res;
    }

    private Value<?> lowerThanOp() {
        Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();

        if (!(lvalue instanceof NumberValue) ||
            !(rvalue instanceof NumberValue))
            Utils.abort(super.getLine());

        NumberValue nvl = (NumberValue) lvalue;
        int lv = nvl.value();

        NumberValue nvr = (NumberValue) rvalue;
        int rv = nvr.value();

        NumberValue res = lv < rv ? new NumberValue(1) : new NumberValue(0);

        return res;
    }

    private Value<?> lowerEqualOp() {
        Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();

        if (!(lvalue instanceof NumberValue) ||
            !(rvalue instanceof NumberValue))
            Utils.abort(super.getLine());

        NumberValue nvl = (NumberValue) lvalue;
        int lv = nvl.value();

        NumberValue nvr = (NumberValue) rvalue;
        int rv = nvr.value();

        NumberValue res = lv <= rv ? new NumberValue(1) : new NumberValue(0);

        return res;
    }

    private Value<?> greaterThanOp() {
        Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();

        if (!(lvalue instanceof NumberValue) ||
            !(rvalue instanceof NumberValue))
            Utils.abort(super.getLine());

        NumberValue nvl = (NumberValue) lvalue;
        int lv = nvl.value();

        NumberValue nvr = (NumberValue) rvalue;
        int rv = nvr.value();

        NumberValue res = lv > rv ? new NumberValue(1) : new NumberValue(0);

        return res;
    }

    private Value<?> greaterEqualOp() {
        Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();

        if (!(lvalue instanceof NumberValue) ||
            !(rvalue instanceof NumberValue))
            Utils.abort(super.getLine());

        NumberValue nvl = (NumberValue) lvalue;
        int lv = nvl.value();

        NumberValue nvr = (NumberValue) rvalue;
        int rv = nvr.value();

        NumberValue res = lv >= rv ? new NumberValue(1) : new NumberValue(0);

        return res;
    }

    private Value<?> containsOp() {
        Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();

        if (!(lvalue instanceof NumberValue) ||
            !(rvalue instanceof NumberValue))
            Utils.abort(super.getLine());

        NumberValue nvl = (NumberValue) lvalue;
        int lv = nvl.value();

        NumberValue nvr = (NumberValue) rvalue;
        int rv = nvr.value();

        NumberValue res = lv == rv ? new NumberValue(1) : new NumberValue(0);

        return res;
    }

    private Value<?> notContainsOp() {
        Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();

        if (!(lvalue instanceof NumberValue) ||
            !(rvalue instanceof NumberValue))
            Utils.abort(super.getLine());

        NumberValue nvl = (NumberValue) lvalue;
        int lv = nvl.value();

        NumberValue nvr = (NumberValue) rvalue;
        int rv = nvr.value();

        NumberValue res = lv != rv ? new NumberValue(1) : new NumberValue(0);

        return res;
    }

    private Value<?> addOp() {
        Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();

        if((lvalue instanceof TextValue) && (rvalue instanceof TextValue)) {
            TextValue nvl = (TextValue) lvalue;
            String lv = nvl.value();

            TextValue nvr = (TextValue) rvalue;
            String rv = nvr.value();

            TextValue res = new TextValue(lv + rv);
            return res;
        }
        else if((lvalue instanceof TextValue) && (rvalue instanceof NumberValue)) {
            TextValue nvl = (TextValue) lvalue;
            String lv = nvl.value();

            NumberValue nvr = (NumberValue) rvalue;
            int rv = nvr.value();

            TextValue res = new TextValue(lv + rv);
            return res;
        }
        else if (!(lvalue instanceof NumberValue) ||
            !(rvalue instanceof NumberValue)) {
            Utils.abort(super.getLine());
            return null;
        } else {
            NumberValue nvl = (NumberValue) lvalue;
            int lv = nvl.value();

            NumberValue nvr = (NumberValue) rvalue;
            int rv = nvr.value();

            NumberValue res = new NumberValue(lv + rv);
            return res;
        }
    }

    private Value<?> subOp() {
        Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();

        if (!(lvalue instanceof NumberValue) ||
            !(rvalue instanceof NumberValue))
            Utils.abort(super.getLine());

        NumberValue nvl = (NumberValue) lvalue;
        int lv = nvl.value();

        NumberValue nvr = (NumberValue) rvalue;
        int rv = nvr.value();

        NumberValue res = new NumberValue(lv - rv);
        return res;
    }

    private Value<?> mulOp() {
        Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();

        if (!(lvalue instanceof NumberValue) ||
            !(rvalue instanceof NumberValue))
            Utils.abort(super.getLine());

        NumberValue nvl = (NumberValue) lvalue;
        int lv = nvl.value();

        NumberValue nvr = (NumberValue) rvalue;
        int rv = nvr.value();

        NumberValue res = new NumberValue(lv * rv);
        return res;
    }

    private Value<?> divOp() {
        Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();

        if (!(lvalue instanceof NumberValue) ||
            !(rvalue instanceof NumberValue))
            Utils.abort(super.getLine());

        NumberValue nvl = (NumberValue) lvalue;
        int lv = nvl.value();

        NumberValue nvr = (NumberValue) rvalue;
        int rv = nvr.value();

        NumberValue res = new NumberValue(lv / rv);
        return res;
    }

    private Value<?> modOp() {
        Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();

        if (!(lvalue instanceof NumberValue) ||
            !(rvalue instanceof NumberValue))
            Utils.abort(super.getLine());

        NumberValue nvl = (NumberValue) lvalue;
        int lv = nvl.value();

        NumberValue nvr = (NumberValue) rvalue;
        int rv = nvr.value();

        NumberValue res = new NumberValue(lv % rv);
        return res;
    }

    private Value<?> powerOp() {
        Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();

        if (!(lvalue instanceof NumberValue) ||
            !(rvalue instanceof NumberValue))
            Utils.abort(super.getLine());

        NumberValue nvl = (NumberValue) lvalue;
        int lv = nvl.value();

        NumberValue nvr = (NumberValue) rvalue;
        int rv = nvr.value();

        NumberValue res = new NumberValue((int) Math.pow(lv, rv));
        return res;
    }
    
}
