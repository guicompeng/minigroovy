package interpreter.command;

import java.util.ArrayList;

import interpreter.expr.Expr;
import interpreter.expr.Variable;
import interpreter.value.Value;

public class DeclarationType1Command extends DeclarationCommand {

    private ArrayList<Variable> lhs;

    public DeclarationType1Command(int line, ArrayList<Variable> lhs, ArrayList<Expr> rhs) {
        super(line, rhs);

        this.lhs = lhs;
    }

    @Override
    public void execute() {
        for (int i = 0; i < lhs.size(); i++) {
            Value<?> v = (rhs.get(i) != null ? rhs.get(i).expr() : null);
            lhs.get(i).setValue(v);
        }
        
    }

}
