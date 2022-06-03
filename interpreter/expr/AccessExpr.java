package interpreter.expr;

import java.util.List;
import java.util.Map;

import interpreter.util.Utils;
import interpreter.value.ArrayValue;
import interpreter.value.MapValue;
import interpreter.value.NumberValue;
import interpreter.value.TextValue;
import interpreter.value.Value;

public class AccessExpr extends SetExpr {

    private SetExpr base;
    private Expr index;

    public AccessExpr(int line, SetExpr base, Expr index) {
        super(line);

        this.base = base;
        this.index = index;
    }

    @Override
    public Value<?> expr() {
        Value<?> bvalue = base.expr();
        if (bvalue instanceof ArrayValue) {
            ArrayValue av = (ArrayValue) bvalue;

            Value<?> ivalue = index.expr();
            if (ivalue instanceof NumberValue) {
                NumberValue iv = (NumberValue) ivalue;

                List<Value<?>> list = av.value();
                int idx = iv.value();

                if (idx >= 0 && idx < list.size())
                    return list.get(idx);
                else
                    return null;
            } else {
                Utils.abort(super.getLine());
            }
        } else if (bvalue instanceof MapValue) {
            MapValue mv = (MapValue) bvalue;

            Value<?> ivalue = index.expr();
            if (ivalue instanceof TextValue) {
                TextValue sv = (TextValue) ivalue;

                Map<String, Value<?>> map = mv.value();
                String idx = sv.value();

                if (map.containsKey(idx)) {
                    return map.get(idx);
                } else {
                    return null;
                }
            } else {
                Utils.abort(super.getLine());
            }
        } else {
            Utils.abort(super.getLine());
        }

        return null;
    }

    @Override
    public void setValue(Value<?> value) {
        System.out.println("Fazer a atribuição do acesso da expressão");
    }
}