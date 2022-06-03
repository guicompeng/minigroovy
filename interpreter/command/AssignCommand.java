package interpreter.command;

import interpreter.expr.Expr;
import interpreter.expr.SetExpr;
import interpreter.util.Utils;
import interpreter.value.NumberValue;
import interpreter.value.Value;

public class AssignCommand extends Command {

    public enum Op {
        StdOp,
        AddOp,
        SubOp,
        MulOp,
        DivOp,
        ModOp,
        PowerOp;
    }

    private SetExpr lhs;
    private Op op;
    private Expr rhs;

    public AssignCommand(int line, SetExpr lhs, Op op, Expr rhs) {
        super(line);

        this.lhs = lhs;
        this.op = op;
        this.rhs = rhs;
    }

    @Override
    public void execute() {
        switch (op) {
            case StdOp:
                stdOp();
                break;
            case AddOp:
                addOp();
                break;
            case SubOp:
                subOp();
                break;
            case MulOp:
                mulOp();
                break;
            case DivOp:
                divOp();
                break;
            case ModOp:
                modOp();
                break;
            case PowerOp:
                powerOp();
                break;
            default:
                Utils.abort(super.getLine());
        }
    }

    private void stdOp() {
        Value<?> rvalue = rhs.expr();
        lhs.setValue(rvalue);
    }

    private void addOp() {
        Value<?> lvalue = lhs.expr();
        Value<?> rvalue = rhs.expr();

        if (!(lvalue instanceof NumberValue) ||
            !(rvalue instanceof NumberValue))
            Utils.abort(super.getLine());

        NumberValue nvl = (NumberValue) lvalue;
        int lv = nvl.value();

        NumberValue nvr = (NumberValue) rvalue;
        int rv = nvr.value();

        NumberValue res = new NumberValue(lv + rv);
        lhs.setValue(res);
    }

    private void subOp() {
        Value<?> lvalue = lhs.expr();
        Value<?> rvalue = rhs.expr();

        if (!(lvalue instanceof NumberValue) ||
            !(rvalue instanceof NumberValue))
            Utils.abort(super.getLine());

        NumberValue nvl = (NumberValue) lvalue;
        int lv = nvl.value();

        NumberValue nvr = (NumberValue) rvalue;
        int rv = nvr.value();

        NumberValue res = new NumberValue(lv - rv);
        lhs.setValue(res);
    }

    private void mulOp() {
        Value<?> lvalue = lhs.expr();
        Value<?> rvalue = rhs.expr();

        if (!(lvalue instanceof NumberValue) ||
            !(rvalue instanceof NumberValue))
            Utils.abort(super.getLine());

        NumberValue nvl = (NumberValue) lvalue;
        int lv = nvl.value();

        NumberValue nvr = (NumberValue) rvalue;
        int rv = nvr.value();

        NumberValue res = new NumberValue(lv * rv);
        lhs.setValue(res);
    }

    private void divOp() {
        Value<?> lvalue = lhs.expr();
        Value<?> rvalue = rhs.expr();

        if (!(lvalue instanceof NumberValue) ||
            !(rvalue instanceof NumberValue))
            Utils.abort(super.getLine());

        NumberValue nvl = (NumberValue) lvalue;
        int lv = nvl.value();

        NumberValue nvr = (NumberValue) rvalue;
        int rv = nvr.value();

        NumberValue res = new NumberValue(lv / rv);
        lhs.setValue(res);
    }

    private void modOp() {
        Value<?> lvalue = lhs.expr();
        Value<?> rvalue = rhs.expr();

        if (!(lvalue instanceof NumberValue) ||
            !(rvalue instanceof NumberValue))
            Utils.abort(super.getLine());

        NumberValue nvl = (NumberValue) lvalue;
        int lv = nvl.value();

        NumberValue nvr = (NumberValue) rvalue;
        int rv = nvr.value();

        NumberValue res = new NumberValue(lv % rv);
        lhs.setValue(res);
    }

    private void powerOp() {
        Value<?> lvalue = lhs.expr();
        Value<?> rvalue = rhs.expr();

        if (!(lvalue instanceof NumberValue) ||
            !(rvalue instanceof NumberValue))
            Utils.abort(super.getLine());

        NumberValue nvl = (NumberValue) lvalue;
        int lv = nvl.value();

        NumberValue nvr = (NumberValue) rvalue;
        int rv = nvr.value();

        NumberValue res = new NumberValue((int) Math.pow(lv, rv));
        lhs.setValue(res);
    }

}
