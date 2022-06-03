package interpreter.expr;

import interpreter.util.Memory;
import interpreter.value.Value;

public class Variable extends SetExpr {

    private String name;

    public Variable(int line, String name) {
        super(line);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public Value<?> expr() {
        Value<?> v = Memory.read(name);
        return v;
    }

    @Override
    public void setValue(Value<?> value) {
        Memory.write(name, value);
    }
    
}
