package syntatic;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import interpreter.command.AssignCommand;
import interpreter.command.BlocksCommand;
import interpreter.command.Command;
import interpreter.command.DeclarationCommand;
import interpreter.command.DeclarationType1Command;
import interpreter.command.DeclarationType2Command;
import interpreter.command.ForCommand;
import interpreter.command.IfCommand;
import interpreter.command.PrintCommand;
import interpreter.command.WhileCommand;
import interpreter.expr.BinaryExpr;
import interpreter.expr.CastExpr;
import interpreter.expr.ConstExpr;
import interpreter.expr.Expr;
import interpreter.expr.SetExpr;
import interpreter.expr.UnaryExpr;
import interpreter.expr.Variable;
import interpreter.util.Utils;
import interpreter.value.BooleanValue;
import interpreter.value.NumberValue;
import interpreter.value.TextValue;
import interpreter.value.Value;
import lexical.Lexeme;
import lexical.LexicalAnalysis;
import lexical.TokenType;

public class SyntaticAnalysis {

    private LexicalAnalysis lex;
    private Lexeme current;
    private Stack<Lexeme> history;
    private Stack<Lexeme> queued;

    public SyntaticAnalysis(LexicalAnalysis lex) {
        this.lex = lex;
        this.current = lex.nextToken();
        this.history = new Stack<Lexeme>();
        this.queued = new Stack<Lexeme>();
    }

    public Command start() {
        Command cmd = procCode();
        eat(TokenType.END_OF_FILE);
        return cmd;
    }

    private void rollback() {
        assert !history.isEmpty();

        // System.out.println("Rollback (\"" + current.token + "\", " +
        //     current.type + ")");
        queued.push(current);
        current = history.pop();
    }

    private void advance() {
        // System.out.println("Advanced (\"" + current.token + "\", " +
        //     current.type + ")");
        history.add(current);
        current = queued.isEmpty() ? lex.nextToken() : queued.pop();
    }

    private void eat(TokenType type) {
        // System.out.println("Expected (..., " + type + "), found (\"" + 
        //     current.token + "\", " + current.type + ")");
        if (type == current.type) {
            history.add(current);
            current = queued.isEmpty() ? lex.nextToken() : queued.pop();
        } else {
            showError();
        }
    }

    private void showError() {
        System.out.printf("%02d: ", lex.getLine());

        switch (current.type) {
            case INVALID_TOKEN:
                System.out.printf("Lexema inválido [%s]\n", current.token);
                break;
            case UNEXPECTED_EOF:
            case END_OF_FILE:
                System.out.printf("Fim de arquivo inesperado\n");
                break;
            default:
                System.out.printf("Lexema não esperado [%s]\n", current.token);
                break;
        }

        System.exit(1);
    }

    // <code> ::= { <cmd> }
    private BlocksCommand procCode() {
        int line = lex.getLine();

        List<Command> cmds = new ArrayList<Command>();
        while (current.type == TokenType.DEF ||
            current.type == TokenType.PRINT ||
            current.type == TokenType.PRINTLN ||
            current.type == TokenType.IF ||
            current.type == TokenType.WHILE ||
            current.type == TokenType.FOR ||
            current.type == TokenType.FOREACH ||
            current.type == TokenType.NOT ||
            current.type == TokenType.SUB ||
            current.type == TokenType.OPEN_PAR ||
            current.type == TokenType.NULL ||
            current.type == TokenType.FALSE ||
            current.type == TokenType.TRUE ||
            current.type == TokenType.NUMBER ||
            current.type == TokenType.TEXT ||
            current.type == TokenType.READ ||
            current.type == TokenType.EMPTY ||
            current.type == TokenType.SIZE ||
            current.type == TokenType.KEYS ||
            current.type == TokenType.VALUES ||
            current.type == TokenType.SWITCH ||
            current.type == TokenType.OPEN_BRA ||
            current.type == TokenType.NAME) {
            Command c = procCmd();
            cmds.add(c);
        }

        BlocksCommand bc = new BlocksCommand(line, cmds);
        return bc;
    }

    // <cmd> ::= <decl> | <print> | <if> | <while> | <for> | <foreach> | <assign>
    private Command procCmd() {
        Command cmd = null;
        switch (current.type) {
            case DEF:
                DeclarationCommand dc = procDecl();
                cmd = dc;
                break;
            case PRINT:
            case PRINTLN:
                PrintCommand pc = procPrint();
                cmd = pc;
                break;
            case IF:
                IfCommand pi = procIf();
                cmd = pi;
                break;
            case WHILE:
                WhileCommand wc = procWhile();
                cmd = wc;
                break;
            case FOR:
                ForCommand fc = procFor();
                cmd = fc;
                break;
            case FOREACH:
                procForeach();
                break;
            case NOT:
            case SUB:
            case OPEN_PAR:
            case NULL:
            case FALSE:
            case TRUE:
            case NUMBER:
            case TEXT:
            case READ:
            case EMPTY:
            case SIZE:
            case KEYS:
            case VALUES:
            case SWITCH:
            case OPEN_BRA:
            case NAME:
                AssignCommand ac = procAssign();
                cmd = ac;
                break;
            default:
                showError();
        }

        return cmd;
    }

    // <decl> ::= def ( <decl-type1> | <decl-type2> )
    private DeclarationCommand procDecl() {
        eat(TokenType.DEF);

        DeclarationCommand dc = null;
        if (current.type == TokenType.NAME) {
            dc = procDeclType1();
        } else {
            dc = procDeclType2();
        }

        return dc;
    }

    // <decl-type1> ::= <name> [ '=' <expr> ] { ',' <name> [ '=' <expr> ] }
    private DeclarationType1Command procDeclType1() {

        ArrayList<Variable> lhs = new ArrayList<Variable>();
        lhs.add(procName());

        int line = lex.getLine();

        ArrayList<Expr> rhs = new ArrayList<Expr>();
        if (current.type == TokenType.ASSIGN) {
            advance();
            rhs.add(procExpr());
        } else {
            rhs.add(null);
        }

        while (current.type == TokenType.COMMA) {
                advance();
                lhs.add(procName());
                if (current.type == TokenType.ASSIGN) {
                    advance();
                    rhs.add(procExpr());
                } else {
                    rhs.add(null);
                }
        }

        DeclarationType1Command dt1c = new DeclarationType1Command(line, lhs, rhs);
        return dt1c;
       
    }

    // <decl-type2> ::= '(' <name> { ',' <name> } ')' = <expr>
    private DeclarationType2Command procDeclType2() {

        ArrayList<Variable> lhs = new ArrayList<Variable>();

        eat(TokenType.OPEN_PAR);
        lhs.add(procName());

        int line = lex.getLine();

        while (current.type == TokenType.COMMA) {
            advance();
            lhs.add(procName());
        }

        eat(TokenType.CLOSE_PAR);
        eat(TokenType.ASSIGN);
        eat(TokenType.OPEN_BRA);

        ArrayList<Expr> rhs = new ArrayList<Expr>();
        // se houver ao menos um item na lista do lado direito
        if(current.type != TokenType.CLOSE_BRA) {
            rhs.add(procExpr());
            while (current.type == TokenType.COMMA) {
                advance();
                rhs.add(procExpr());
            }
            eat(TokenType.CLOSE_BRA);
        }

        DeclarationType2Command dt2c = new DeclarationType2Command(line, lhs, rhs);
        return dt2c;
    }

    // <print> ::= (print | println) '(' <expr> ')'
    private PrintCommand procPrint() {
        boolean newline = false;
        if (current.type == TokenType.PRINT) {
            advance();
        } else if (current.type == TokenType.PRINTLN) {
            newline = true;
            advance();
        } else {
            showError();
        }

        int line = lex.getLine();
        eat(TokenType.OPEN_PAR);
        Expr expr = procExpr();
        eat(TokenType.CLOSE_PAR);

        PrintCommand pc = new PrintCommand(line, newline, expr);
        return pc;
    }

    // <if> ::= if '(' <expr> ')' <body> [ else <body> ]
    private IfCommand procIf() {
        eat(TokenType.IF);
        int line = lex.getLine();
        eat(TokenType.OPEN_PAR);
        Expr expr = procExpr();
        eat(TokenType.CLOSE_PAR);
        Command cmdsIf = procBody();

        if (current.type == TokenType.ELSE) {
            advance();
            Command cmdsElse = procBody();
            IfCommand ic = new IfCommand(line, expr, cmdsIf, cmdsElse);
            return ic;
        } else {
            IfCommand ic = new IfCommand(line, expr, cmdsIf);
            return ic;

        }

    }

    // <while> ::= while '(' <expr> ')' <body>
    private WhileCommand procWhile() {
        eat(TokenType.WHILE);
        int line = lex.getLine();
        eat(TokenType.OPEN_PAR);
        Expr expr = procExpr();
        eat(TokenType.CLOSE_PAR);
        Command cmds = procBody();

        WhileCommand wc = new WhileCommand(line, expr, cmds);
        return wc;
    }

    // <for> ::= for '(' [ ( <def> | <assign> ) { ',' ( <def> | <assign> ) } ] ';' [ <expr> ] ';' [ <assign> { ',' <assign> } ] ')' <body>
    private ForCommand procFor() {
        eat(TokenType.FOR);
        int line = lex.getLine();
        eat(TokenType.OPEN_PAR);

        // parte da esquerda: def ou assign ou nada (proximo ;)
        ArrayList<DeclarationCommand> dc = new ArrayList<DeclarationCommand>();
        ArrayList<AssignCommand> ac = new ArrayList<AssignCommand>();

        if(current.type != TokenType.SEMI_COLON) {

            if(current.type == TokenType.DEF) {
                dc.add(procDecl());
            } else {
                ac.add(procAssign());
            }
            while (current.type == TokenType.COMMA) {
                advance();
                if(current.type == TokenType.DEF) {
                    dc.add(procDecl());
                } else {
                    ac.add(procAssign());
                }
            }
        }

        eat(TokenType.SEMI_COLON);

        // meio: condicao
        Expr expr = null;
        if(current.type != TokenType.SEMI_COLON) {
            expr = procExpr();
        }

        eat(TokenType.SEMI_COLON);

        // direita: assign
        ArrayList<AssignCommand> acRight = new ArrayList<AssignCommand>();

        if(current.type != TokenType.CLOSE_PAR) {
            acRight.add(procAssign());
            while (current.type == TokenType.COMMA) {
                advance();
                acRight.add(procAssign());
            }
        }

        eat(TokenType.CLOSE_PAR);

        Command cmds = procBody();
        ForCommand wc = new ForCommand(line, dc, ac, expr, acRight, cmds);

        return wc;
    }

    // <foreach> ::= foreach '(' [ def ] <name> in <expr> ')' <body>
    private void procForeach() {
    }

    // <body> ::= <cmd> | '{' <code> '}'
    private Command procBody() {
        Command cmd;
        if (current.type == TokenType.OPEN_CUR) {
            advance();
            cmd = procCode();
            eat(TokenType.CLOSE_CUR);
        } else {
            cmd = procCmd();
        }

        return cmd;
    }

    // <assign> ::= <expr>  ( '=' | '+=' | '-=' | '*=' | '/=' | '%=' | '**=') <expr>
    private AssignCommand procAssign() {
        Expr left = procExpr();
        if (!(left instanceof SetExpr))
            Utils.abort(lex.getLine());

        AssignCommand.Op op = null;
        switch (current.type) {
            case ASSIGN:
                op = AssignCommand.Op.StdOp;
                break;
            case ASSIGN_ADD:
                op = AssignCommand.Op.AddOp;
                break;
            case ASSIGN_SUB:
                op = AssignCommand.Op.SubOp;
                break;
            case ASSIGN_MUL:
                op = AssignCommand.Op.MulOp;
                break;
            case ASSIGN_DIV:
                op = AssignCommand.Op.DivOp;
                break;
            case ASSIGN_MOD:
                op = AssignCommand.Op.ModOp;
                break;
            case ASSIGN_POWER:
                op = AssignCommand.Op.PowerOp;
                break;
            default:
                showError();
        }
        advance();
        int line = lex.getLine();

        Expr right = procExpr();
        
        AssignCommand ac = new AssignCommand(line, (SetExpr) left, op, right);
        return ac;
    }

    // <expr> ::= <rel> { ('&&' | '||') <rel> }
    private Expr procExpr() {
        Expr left = procRel();

        while (current.type == TokenType.AND ||
                current.type == TokenType.OR) {
            BinaryExpr.Op op = null;
            switch (current.type) {
                case AND:
                    advance();
                    op = BinaryExpr.Op.AndOp;
                    break;
                case OR:
                default:
                    advance();
                    op = BinaryExpr.Op.OrOp;
                    break;
            }
            int line = lex.getLine();

            Expr right = procRel();

            BinaryExpr bexpr = new BinaryExpr(line, left, op, right);
            left = bexpr;
        }

        return left;
    }

    // <rel> ::= <cast> [ ('<' | '>' | '<=' | '>=' | '==' | '!=' | in | '!in') <cast> ]

    private Expr procRel() {
        Expr left = procCast();

        if (
            current.type == TokenType.LOWER ||
            current.type == TokenType.GREATER ||
            current.type == TokenType.LOWER_EQUAL ||
            current.type == TokenType.GREATER_EQUAL ||
            current.type == TokenType.EQUALS ||
            current.type == TokenType.NOT_EQUAL ||
            current.type == TokenType.CONTAINS ||
            current.type == TokenType.NOT_CONTAINS
        ) {

            BinaryExpr.Op op = null;
            switch (current.type) {
                case LOWER:
                    advance();
                    op = BinaryExpr.Op.LowerThanOp;
                    break;
                case GREATER:
                    advance();
                    op = BinaryExpr.Op.GreaterThanOp;
                    break;
                case LOWER_EQUAL:
                    advance();
                    op = BinaryExpr.Op.LowerEqualOp;
                    break;
                case GREATER_EQUAL:
                    advance();
                    op = BinaryExpr.Op.GreaterEqualOp;
                    break;
                case EQUALS:
                    advance();
                    op = BinaryExpr.Op.EqualOp;
                    break;
                case NOT_EQUAL:
                    advance();
                    op = BinaryExpr.Op.NotEqualOp;
                    break;
                case CONTAINS:
                    advance();
                    op = BinaryExpr.Op.ContainsOp;
                    break;
                case NOT_CONTAINS:
                    advance();
                    op = BinaryExpr.Op.NotContainsOp;
                    break;
                default:
                    advance();
                    op = BinaryExpr.Op.EqualOp;
                    break;
            }
            int line = lex.getLine();

            Expr right = procCast();

            BinaryExpr bexpr = new BinaryExpr(line, left, op, right);
            left = bexpr;
        }

        return left;
    }

    // <cast> ::= <arith> [ as ( Boolean | Integer | String) ]
    private Expr procCast() {
        Expr expr = procArith();

        if(current.type == TokenType.AS) {
            advance();
            CastExpr.Op op = null;
            switch (current.type) {
                case BOOLEAN:
                    op = CastExpr.Op.toBoolean;
                    break;
                case INTEGER:
                    op = CastExpr.Op.toInteger;
                    break;
                case STRING:
                    op = CastExpr.Op.toStr;
                    break;
                default:
                    showError();
                    break;
            }
            int line = lex.getLine();
            CastExpr bexpr = new CastExpr(line, expr, op);
            expr = bexpr;

            advance();

        }
        return expr;
    }

    // <arith> ::= <term> { ('+' | '-') <term> }
    private Expr procArith() {
        Expr left = procTerm();

        while (current.type == TokenType.ADD ||
                current.type == TokenType.SUB) {
            BinaryExpr.Op op = null;
            switch (current.type) {
                case ADD:
                    advance();
                    op = BinaryExpr.Op.AddOp;
                    break;
                case SUB:
                default:
                    advance();
                    op = BinaryExpr.Op.SubOp;
                    break;
            }
            int line = lex.getLine();

            Expr right = procTerm();

            BinaryExpr bexpr = new BinaryExpr(line, left, op, right);
            left = bexpr;
        }

        return left;
    }

    // <term> ::= <power> { ('*' | '/' | '%') <power> }
    private Expr procTerm() {
        Expr left = procPower();

        while (current.type == TokenType.MUL ||
                current.type == TokenType.DIV ||
                current.type == TokenType.MOD) {
            BinaryExpr.Op op = null;
            switch (current.type) {
                case MUL:
                    advance();
                    op = BinaryExpr.Op.MulOp;
                    break;
                case DIV:
                    advance();
                    op = BinaryExpr.Op.DivOp;
                    break;
                case MOD:
                default:
                    advance();
                    op = BinaryExpr.Op.ModOp;
                    break;
            }
            int line = lex.getLine();

            Expr right = procPower();

            BinaryExpr bexpr = new BinaryExpr(line, left, op, right);
            left = bexpr;
        }

        return left;
    }

    // <power> ::= <factor> { '**' <factor> }
    private Expr procPower() {
        Expr left = procFactor();

        while (current.type == TokenType.POWER) {
            BinaryExpr.Op op = null;
            switch (current.type) {
                case POWER:
                    advance();
                    op = BinaryExpr.Op.PowerOp;
                    break;
                default:
                    advance();
                    op = BinaryExpr.Op.PowerOp;
                    break;
            }
            int line = lex.getLine();

            Expr right = procFactor();

            BinaryExpr bexpr = new BinaryExpr(line, left, op, right);
            left = bexpr;
        }

        return left;
    }

    // <factor> ::= [ '!' | '-' ] ( '(' <expr> ')' | <rvalue> )
    private Expr procFactor() {
        Expr expr = null;

        UnaryExpr.Op op = null;
        if (current.type == TokenType.NOT) {
            advance();
            op = UnaryExpr.Op.NotOp;
        } else if (current.type == TokenType.SUB) {
            advance();
            op = UnaryExpr.Op.NegOp;
        }
        int line = lex.getLine();

        if (current.type == TokenType.OPEN_PAR) {
            advance();
            expr = procExpr();
            eat(TokenType.CLOSE_PAR);
        } else {
            expr = procRValue();
        }

        if (op != null) {
            UnaryExpr uexpr = new UnaryExpr(line, expr, op);
            expr = uexpr;
        }

        return expr;
    }

    // <lvalue> ::= <name> { '.' <name> | '[' <expr> ']' }
    private Variable procLValue() {
        Variable var = procName();

        // TODO: Implement me!

        return var;
    }

    // <rvalue> ::= <const> | <function> | <switch> | <struct> | <lvalue>
    private Expr procRValue() {
        Expr expr = null;
        switch (current.type) {
            case NULL:
            case FALSE:
            case TRUE:
            case NUMBER:
            case TEXT:
                Value<?> v = procConst();
                int line = lex.getLine();
                ConstExpr cexpr = new ConstExpr(line, v);
                expr = cexpr;
                break;
            case READ:
            case EMPTY:
            case SIZE:
            case KEYS:
            case VALUES:
                UnaryExpr uexpr = procFunction();
                expr = uexpr;
                break;
            case SWITCH:
                procSwitch();
                break;
            case OPEN_BRA:
                procStruct();
                break;
            case NAME:
                Variable var = procLValue();
                expr = var;
                break;
            default:
                showError();
        }

        return expr;
    }

    // <const> ::= null | false | true | <number> | <text>
    private Value<?> procConst() {
        Value<?> v = null;
        if (current.type == TokenType.NULL) {
            advance();
        } else if (current.type == TokenType.FALSE) {
            advance();
            BooleanValue bv = new BooleanValue(false);
            v = bv;
        } else if (current.type == TokenType.TRUE) {
            advance();
            BooleanValue bv = new BooleanValue(true);
            v = bv;
        } else if (current.type == TokenType.NUMBER) {
            NumberValue nv = procNumber();
            v = nv;
        } else if (current.type == TokenType.TEXT) {
            TextValue tv = procText();
            v = tv;
        } else {
            showError();
        }

        return v;
    }

    // <function> ::= (read | empty | size | keys | values) '(' <expr> ')'
    private UnaryExpr procFunction() {
        UnaryExpr.Op op = null;
        switch (current.type) {
            case READ:
                op = UnaryExpr.Op.ReadOp;
                break;
            case EMPTY:
                op = UnaryExpr.Op.EmptyOp;
                break;
            case SIZE:
                op = UnaryExpr.Op.SizeOp;
                break;
            case KEYS:
                op = UnaryExpr.Op.KeysOp;
                break;
            case VALUES:
                op = UnaryExpr.Op.ValuesOp;
                break;
            default:
                showError();
        }
        advance();
        int line = lex.getLine();

        eat(TokenType.OPEN_PAR);
        Expr expr = procExpr();
        eat(TokenType.CLOSE_PAR);

        UnaryExpr uexpr = new UnaryExpr(line, expr, op);
        return uexpr;
    }

    // <switch> ::= switch '(' <expr> ')' '{' { case <expr> '->' <expr> } [ default '->' <expr> ] '}'
    private void procSwitch() {
        eat(TokenType.SWITCH);
        eat(TokenType.OPEN_PAR);
        procExpr();
        eat(TokenType.CLOSE_PAR);
        eat(TokenType.OPEN_CUR);
        while (current.type == TokenType.CASE) {
            advance();
            procExpr();
            eat(TokenType.ARROW);
            procExpr();
        }

        if (current.type == TokenType.DEFAULT) {
            advance();
            eat(TokenType.ARROW);
            procExpr();
        }

        eat(TokenType.CLOSE_CUR);
    }

    // <struct> ::= '[' [ ':' | <expr> { ',' <expr> } | <name> ':' <expr> { ',' <name> ':' <expr> } ] ']'
    private void procStruct() {
        eat(TokenType.OPEN_BRA);

        if (current.type == TokenType.COLON) {
            advance();
        } else if (current.type == TokenType.CLOSE_BRA) {
            // Do nothing.
        } else {
            Lexeme prev = current;
            advance();

            if (prev.type == TokenType.NAME &&
                    current.type == TokenType.COLON) {
                rollback();

                procName();
                eat(TokenType.COLON);
                procExpr();

                while (current.type == TokenType.COMMA) {
                    advance();

                    procName();
                    eat(TokenType.COLON);
                    procExpr();
                }
            } else {
                rollback();

                procExpr();

                while (current.type == TokenType.COMMA) {
                    advance();
                    procExpr();
                }
            }
        }

        eat(TokenType.CLOSE_BRA);
    }

    private Variable procName() {
        String tmp = current.token;
        eat(TokenType.NAME);
        int line = lex.getLine();

        Variable var = new Variable(line, tmp);
        return var;
    }

    private NumberValue procNumber() {
        String tmp = current.token;
        eat(TokenType.NUMBER);

        int v;
        try {
            v = Integer.parseInt(tmp);
        } catch (Exception e) {
            v = 0;
        }

        NumberValue nv = new NumberValue(v);
        return nv;
    }

    private TextValue procText() {
        String tmp = current.token;

        eat(TokenType.TEXT);

        TextValue tv = new TextValue(tmp);
        return tv;
    }
 
}
