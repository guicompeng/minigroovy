package interpreter.expr;

import interpreter.util.Utils;
import interpreter.value.BooleanValue;
import interpreter.value.NumberValue;
import interpreter.value.TextValue;
import interpreter.value.Value;

public class CastExpr extends Expr {

    public enum Op {
        toBoolean,
        toInteger,
        toStr;
    }

    private Expr left;
    private Op op;

    public CastExpr(int line, Expr left, Op op) {
        super(line);

        this.left = left;
        this.op = op;
    }

    @Override
    public Value<?> expr() {
        Value<?> v = null;
        switch (op) {
            case toBoolean:
                v = toBoolean();
                break;
            case toInteger:
                v = toInteger();
                break;
            case toStr:
                v = toStr();
                break;
            default:
                Utils.abort(super.getLine());
        }

        return v;
    }


    private Value<?> toBoolean() {
        Value<?> lvalue = left.expr();

        BooleanValue res = new BooleanValue(false);

        if(lvalue == null) {
            res = new BooleanValue(false);
        } else if((lvalue instanceof NumberValue) && ((int) lvalue.value() == 0)) {
            res = new BooleanValue(false);
        } else if((lvalue instanceof BooleanValue) && ((boolean) lvalue.value() == false)) {
            res = new BooleanValue(false);
        } else if((lvalue instanceof TextValue) && (String)lvalue.value() == "") {
            res = new BooleanValue(false);
        } else {
            res = new BooleanValue(true);
        }

        return res;
    }

    private Value<?> toInteger() {
        Value<?> lvalue = left.expr();

        NumberValue res = new NumberValue(0);

        if(lvalue == null) {
            res = new NumberValue(0);
        } else if((lvalue instanceof NumberValue)) {
            res = (NumberValue)lvalue;
        } else if((lvalue instanceof BooleanValue) && ((boolean) lvalue.value() == false)) {
            res = new NumberValue(0);
        } else if((lvalue instanceof BooleanValue) && ((boolean) lvalue.value() == true)) {
            res = new NumberValue(1);
        } else if((lvalue instanceof TextValue)) {
            int aux;
            try {
                aux = Integer.parseInt((String)lvalue.value());
             }
             catch (NumberFormatException e) {
                aux = 0;
             }
            res = new NumberValue(aux);
        } else {
            res = new NumberValue(0);

        }

        return res;
    }

    private Value<?> toStr() {
        Value<?> lvalue = left.expr();
        
        String aux;

        if(lvalue == null) {
            aux = "null";
        } else if((lvalue instanceof BooleanValue) && ((boolean) lvalue.value() == false)) {
            aux = "false";
        } else if((lvalue instanceof BooleanValue) && ((boolean) lvalue.value() == true)) {
            aux = "true";
        } else {
            aux = "" + lvalue.value();
        }
         
        TextValue res = new TextValue(aux);
        return res;
    }
    
}
