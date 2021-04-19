import gen.*;

import java.util.*;

public class Listener extends LittleBaseListener {
    private static int blockNum = 0;
    private final Stack<String> scopeStack = new Stack<>();
    private final Stack<BinaryNode> nodeStack = new Stack<>();
    private final LinkedHashMap<String, SymbolTable> nestedST = new LinkedHashMap<>();
    private BinaryNode syntaxTree;

    public Listener() {}

    public LinkedHashMap<String, SymbolTable> getSymbolTable() {
        return nestedST;
    }
    public BinaryNode getSyntaxTree() {
        return syntaxTree;
    }

    private void addScope(String scope) {
        nestedST.put(scope, new SymbolTable(scope));
        scopeStack.push(scope);
    }

    private void insertSymbol(String name, String type, String value) {
        if (nestedST.get(scopeStack.peek()).search(name)) {
            System.out.println("DECLARATION ERROR " + name);
            System.exit(0);
        } else {
            nestedST.get(scopeStack.peek()).insert(name, type, value);
        }
    }

    @Override public void enterProgram(LittleParser.ProgramContext ctx) {
        addScope("GLOBAL");
    }

    @Override public void exitProgram(LittleParser.ProgramContext ctx) {
        BinaryNode pgm = nodeStack.pop();
        nodeStack.pop();
        syntaxTree = pgm;
        while (!nodeStack.empty()) {
            System.out.println("Error: nodeStack is not empty!");
            nodeStack.pop();
        }
    }

    @Override public void exitId(LittleParser.IdContext ctx) {
        System.out.println(ctx);
        ArrayList<String> symbol = nestedST.get(scopeStack.peek()).getValue(scopeStack.peek());
        if (symbol == null) {
            symbol = nestedST.get("GLOBAL").getValue(ctx.getChild(0).getText());
        }
        BinaryNode bn;
        if (symbol != null) {
            bn = new BinaryNode(symbol.get(0) + " " + symbol.get(1) + " " + symbol.get(2) + ":" + ctx.getChild(0).getText());
        }
        else {
            bn = new BinaryNode(ctx.getChild(0).getText());
        }
        nodeStack.push(bn);
    }

    @Override public void enterString_decl(LittleParser.String_declContext ctx) {
        insertSymbol(ctx.getChild(1).getText(), ctx.getChild(0).getText(), ctx.getChild(3).getText());
    }

    @Override public void exitString_decl(LittleParser.String_declContext ctx) {
        nodeStack.pop();
    }

    @Override public void enterVar_decl(LittleParser.Var_declContext ctx) {
        String varNames = ctx.getChild(1).getText();
        for (String varName : varNames.split(",")) {
            varName = varName.replace(";", "");
            insertSymbol(varName, ctx.getChild(0).getText(), null);
        }
    }

    @Override public void exitVar_decl(LittleParser.Var_declContext ctx) {
        nodeStack.pop();
    }
    @Override public void exitId_list(LittleParser.Id_listContext ctx) {
        BinaryNode id_tail = nodeStack.pop();
        BinaryNode id = nodeStack.pop();
        BinaryNode newNode = new BinaryNode("id_list", id, id_tail);
        nodeStack.push(newNode);
    }

    @Override public void exitId_tail(LittleParser.Id_tailContext ctx) {
        if (ctx.getChildCount() == 3) {
            BinaryNode id_tail = nodeStack.pop();
            BinaryNode id = nodeStack.pop();
            BinaryNode newNode = new BinaryNode("id_tail", id, id_tail);
            nodeStack.push(newNode);
        }
        else {
            nodeStack.push(new BinaryNode(""));
        }
    }

    @Override public void enterParam_decl(LittleParser.Param_declContext ctx) {
        insertSymbol(ctx.getChild(1).getText(), ctx.getChild(0).getText(), null);
    }

    @Override public void exitFunc_declarations(LittleParser.Func_declarationsContext ctx) {
        if (ctx.getChildCount() == 2) {
            BinaryNode func_declarations = nodeStack.pop();
            BinaryNode func_decl = nodeStack.pop();
            nodeStack.push(new BinaryNode("function_declarations", func_decl, func_declarations));
        }
        else {
            nodeStack.push(new BinaryNode(""));
        }
    }

    @Override public void enterFunc_decl(LittleParser.Func_declContext ctx) {
        addScope(ctx.getChild(2).getText());
    }

    @Override public void exitFunc_decl(LittleParser.Func_declContext ctx) {
        scopeStack.pop();
        BinaryNode func_body = nodeStack.pop();
        BinaryNode id = nodeStack.pop();
        nodeStack.push(new BinaryNode("FUNCTION", id, func_body));
    }

    @Override public void exitStmt_list(LittleParser.Stmt_listContext ctx) {
        if (ctx.getChildCount() == 0) {
            nodeStack.push(new BinaryNode(""));
        }
        else {
            BinaryNode stmt_list = nodeStack.pop();
            BinaryNode stmt = nodeStack.pop();
            nodeStack.push(new BinaryNode("statement_list", stmt, stmt_list));
        }
    }

    @Override public void exitAssign_expr(LittleParser.Assign_exprContext ctx) {
        BinaryNode expr = nodeStack.pop();
        BinaryNode id = nodeStack.pop();
        nodeStack.push(new BinaryNode(":=", id, expr));
    }

    @Override public void exitRead_stmt(LittleParser.Read_stmtContext ctx) {
        BinaryNode id_list = nodeStack.pop();
        BinaryNode read = nodeStack.pop();
        nodeStack.push(new BinaryNode("READ", read, id_list));
    }

    @Override public void exitWrite_stmt(LittleParser.Write_stmtContext ctx) {
        BinaryNode id_list = nodeStack.pop();
        BinaryNode write = nodeStack.pop();
        nodeStack.push(new BinaryNode("WRITE", write, id_list));
    }

    @Override public void exitExpr(LittleParser.ExprContext ctx) {
        if (ctx.getChild(0).getText().equals("")) {
            BinaryNode factor = nodeStack.pop();
            nodeStack.pop();
            nodeStack.push(factor);
        }
        else {
            BinaryNode factor = nodeStack.pop();
            BinaryNode expr_prefix = nodeStack.pop();
            nodeStack.push(new BinaryNode("expr", expr_prefix, factor));
        }
    }

    @Override public void exitExpr_prefix(LittleParser.Expr_prefixContext ctx) {
        if (ctx.getChildCount() == 0) {
            nodeStack.push(new BinaryNode(""));
        }
        else {
            BinaryNode factor = nodeStack.pop();
            BinaryNode expr_prefix = nodeStack.pop();
            nodeStack.push(new BinaryNode(ctx.getChild(2).getText(), expr_prefix, factor));
        }
    }

    @Override public void exitFactor(LittleParser.FactorContext ctx) {
        if (ctx.getChild(0).getText().equals("")) {
            BinaryNode postfix_expr = nodeStack.pop();
            nodeStack.pop();
            nodeStack.push(postfix_expr);
        }
        else {
            BinaryNode postfix_expr = nodeStack.pop();
            BinaryNode factor_prefix = nodeStack.pop();
            nodeStack.push(new BinaryNode("factor", factor_prefix, postfix_expr));
        }
    }

    @Override public void enterFactor_prefix(LittleParser.Factor_prefixContext ctx) {
        if (ctx.getChildCount() == 0) {
            nodeStack.push(new BinaryNode(""));
        }
        else {
            BinaryNode postfix_expr = nodeStack.pop();
            BinaryNode factor_prefix = nodeStack.pop();
            nodeStack.push(new BinaryNode(ctx.getChild(2).getText(), factor_prefix, postfix_expr));
        }
    }

    @Override public void exitPrimary(LittleParser.PrimaryContext ctx) {
        if (ctx.getChildCount() == 1) {
            if (ctx.getChild(0).getText().contains(".")) {
                nodeStack.push(new BinaryNode("FLOAT:" + ctx.getChild(0).getText()));
            }
            else {
                nodeStack.push(new BinaryNode("INT:" + ctx.getChild(0).getText()));
            }
        }
    }

    @Override public void enterIf_stmt(LittleParser.If_stmtContext ctx) {
        blockNum++;
        addScope("BLOCK " + blockNum);
        nodeStack.push(new BinaryNode("IF"));
    }

    @Override public void exitIf_stmt(LittleParser.If_stmtContext ctx) {
        scopeStack.pop();
        BinaryNode if_stmt = nodeStack.pop();
        BinaryNode bn = new BinaryNode("");
        while (!if_stmt.getElement().equals("IF")) {
            bn = new BinaryNode("if_stmt", new BinaryNode(""), if_stmt);
            if_stmt = nodeStack.pop();
        }
        nodeStack.push(bn);
    }

    @Override public void enterElse_part(LittleParser.Else_partContext ctx) {
        if (ctx.getChildCount() > 0) {
            blockNum++;
            addScope("BLOCK " + blockNum);
        }
        nodeStack.push(new BinaryNode("ELSE"));
    }

    @Override public void exitElse_part(LittleParser.Else_partContext ctx) {
        if (ctx.getChildCount() > 0) {
            scopeStack.pop();
        }
        BinaryNode else_stmt = nodeStack.pop();
        BinaryNode bn = new BinaryNode("");
        while (!else_stmt.getElement().equals("ELSE")) {
            bn = new BinaryNode("else_stmt", new BinaryNode(""), else_stmt);
            else_stmt = nodeStack.pop();
        }
        nodeStack.push(bn);
    }

    @Override public void exitCond(LittleParser.CondContext ctx) {
        BinaryNode exprRight = nodeStack.pop();
        BinaryNode exprLeft = nodeStack.pop();
        BinaryNode compop = new BinaryNode(ctx.getChild(1).getText(), exprLeft, exprRight);
        nodeStack.push(compop);
    }

    @Override public void enterWhile_stmt(LittleParser.While_stmtContext ctx) {
        blockNum++;
        addScope("BLOCK " + blockNum);
        nodeStack.push(new BinaryNode("WHILE"));
    }

    @Override public void exitWhile_stmt(LittleParser.While_stmtContext ctx) {
        scopeStack.pop();
        BinaryNode while_stmt = nodeStack.pop();
        BinaryNode bn = new BinaryNode("");
        while (!while_stmt.getElement().equals("WHILE")) {
            bn = new BinaryNode("while_stmt", new BinaryNode(""), while_stmt);
            while_stmt = nodeStack.pop();
        }
        nodeStack.push(bn);
    }
}
