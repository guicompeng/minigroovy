package interpreter.command;

import java.util.ArrayList;

import interpreter.expr.Expr;
import interpreter.expr.Variable;
import interpreter.value.Value;

public class DeclarationType2Command extends DeclarationCommand {

    private ArrayList<Variable> lhs;

    public DeclarationType2Command(int line, ArrayList<Variable> lhs, ArrayList<Expr> rhs) {
        super(line, rhs);

        this.lhs = lhs;
    }

    @Override
    public void execute() {
        int rhsSize = rhs.size();
        for (int i = 0; i < lhs.size(); i++) {
            if(i < rhsSize) {
                Value<?> v = (rhs.get(i) != null ? rhs.get(i).expr() : null);
                lhs.get(i).setValue(v);
            } else {
                lhs.get(i).setValue(null);
            }
        }
        
    }

}
