package interpreter.command;

import interpreter.expr.Expr;
import interpreter.value.Value;

public class IfCommand extends Command {

    private Expr expr;
    private Command cmdsIf;
    private Command cmdsElse;

    public IfCommand(int line, Expr expr, Command cmdsIf) {
        super(line);

        this.expr = expr;
        this.cmdsIf = cmdsIf;
    }

    public IfCommand(int line, Expr expr, Command cmdsIf, Command cmdsElse) {
        super(line);

        this.expr = expr;
        this.cmdsIf = cmdsIf;
        this.cmdsElse = cmdsElse;
    }

    @Override
    public void execute() {
        Value<?> v = expr.expr();
        if (v != null && v.eval())
            cmdsIf.execute();
        else {
            if(cmdsElse != null) {
                cmdsElse.execute(); 
            }
        }
    }
    
}
