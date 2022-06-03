package interpreter.command;

import java.util.ArrayList;

import interpreter.expr.Expr;
import interpreter.value.Value;

public class ForCommand extends Command {

    private ArrayList<DeclarationCommand> leftDecl;
    private ArrayList<AssignCommand> leftAssign;
    private Expr expr;
    private ArrayList<AssignCommand> right;
    private Command cmds;

    public ForCommand(int line, ArrayList<DeclarationCommand> leftDecl, ArrayList<AssignCommand> leftAssign, Expr expr, ArrayList<AssignCommand> right, Command cmds) {
        super(line);

        this.leftDecl = leftDecl;
        this.leftAssign = leftAssign;
        this.expr = expr;
        this.right = right;
        this.cmds = cmds;
    }

    @Override
    public void execute() {
        int i;

        for (i = 0; i < leftDecl.size(); i++)
            leftDecl.get(i).execute();

        for (i = 0; i < leftAssign.size(); i++)
            leftAssign.get(i).execute();
        

        if(expr != null) {
            do {
                Value<?> v = expr.expr();
                if (v != null && v.eval()) {
                    cmds.execute();
                    for (i = 0; i < right.size(); i++) 
                        right.get(i).execute();
                }
                else
                    break;
            } while (true);
        }
        
    }
    
}
