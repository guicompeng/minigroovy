package interpreter.command;

import java.util.ArrayList;

import interpreter.expr.Expr;

public abstract class DeclarationCommand extends Command {
    
    protected ArrayList<Expr> rhs;

    public DeclarationCommand(int line, ArrayList<Expr> rhs) {
        super(line);
        this.rhs = rhs;
    }

    @Override
    public abstract void execute();
}
