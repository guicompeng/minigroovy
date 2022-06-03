package interpreter.expr;

import java.util.Scanner;

import interpreter.util.Utils;
import interpreter.value.BooleanValue;
import interpreter.value.NumberValue;
import interpreter.value.TextValue;
import interpreter.value.Value;

public class UnaryExpr extends Expr {

    public enum Op {
        NotOp,
        NegOp,
        ReadOp,
        EmptyOp,
        SizeOp,
        KeysOp,
        ValuesOp;
    }
    private static Scanner input = new Scanner(System.in);

    private Expr expr;
    private Op op;

    public UnaryExpr(int line, Expr expr, Op op) {
        super(line);
        
        this.expr = expr;
        this.op = op;
    }

    @Override
    public Value<?> expr() {
        Value<?> v = null;
        switch (op) {
            case NotOp:
                v = notOp();
                break;
            case NegOp:
                v = negOp();
                break;
            case ReadOp:
                v = readOp();
                break;
            case EmptyOp:
                v = emptyOp();
                break;
            case SizeOp:
                v = sizeOp();
                break;
            case KeysOp:
                v = keysOp();
                break;
            case ValuesOp:
                v = valuesOp();
                break;
            default:
                Utils.abort(super.getLine());
        }

        return v;
    }

    private Value<?> notOp() {
        Value<?> v = expr.expr();
        boolean b = v == null ? false : v.eval();
        BooleanValue bv = new BooleanValue(!b);
        return bv;
    }

    private Value<?> negOp() {
        Value<?> v = expr.expr();
        if (!(v instanceof NumberValue))
            Utils.abort(super.getLine());

        NumberValue nv = (NumberValue) v;
        int n = nv.value();

        NumberValue res = new NumberValue(-n);
        return res;
    }

    private Value<?> readOp() {
        Value<?> v = expr.expr();
        System.out.print(v == null ? "null" : v.toString());

        String line = input.nextLine();
        TextValue tv = new TextValue(line);
        return tv;
    }

    private Value<?> emptyOp() {
        return null;
    }

    private Value<?> sizeOp() {
        return null;
    }

    private Value<?> keysOp() {
        return null;
    }

    private Value<?> valuesOp() {
        return null;
    }

}
