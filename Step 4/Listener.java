import gen.*;

import java.util.*;

public class Listener extends LittleBaseListener {
    private static int blockNum = 0;
    private final Stack<String> scopeStack = new Stack<>();
    private final Stack<BinaryNode> nodeStack = new Stack<>();
    private final LinkedHashMap<String, SymbolTable> nestedST = new LinkedHashMap<>();
    private ScopeNode root;
    private ScopeNode syntaxTree;

    public Listener() {}

    public LinkedHashMap<String, SymbolTable> getSymbolTable() {
        return nestedST;
    }
    public AbstractNode getSyntaxTree() {
        return syntaxTree;
    }

    private void addScope(String scope, ArrayList<String> funcStrings) {
        nestedST.put(scope, new SymbolTable(scope));
        scopeStack.push(scope);
        if (funcStrings != null) {
            syntaxTree.addChild(new ScopeNode(funcStrings.get(0) + ":" + funcStrings.get(1) + ";" + funcStrings.get(2)));
        }
        else {
            syntaxTree.addChild(new ScopeNode(scope));
        }
        syntaxTree = (ScopeNode)syntaxTree.getChild(syntaxTree.numChildren() - 1);
    }

    private void removeScope() {
        scopeStack.pop();
        String scope = scopeStack.peek();
        Stack<Integer> indexStack = new Stack<>();
        indexStack.push(0);
        while (!syntaxTree.getElement().equals(scope)) {
            for (int i = indexStack.peek() + 1; i < syntaxTree.numChildren(); i++) {
                if (syntaxTree.getChild(i).getElement().contains(scope)) {
                    syntaxTree = (ScopeNode)syntaxTree.getChild(i);
                    break;
                }
                if (syntaxTree.getChild(i) instanceof ScopeNode) {
                    indexStack.push(-1);
                    syntaxTree = (ScopeNode)syntaxTree.getChild(i);
                    break;
                }
                if (!indexStack.empty()) {
                    indexStack.pop();
                }
            }
            /*
            if (indexStack.empty()) {
                System.out.println("Fatal error: compilation failed at " + syntaxTree.getElement());
                System.exit(1);
            }
            */
            if (indexStack.empty()) {
                return;
            }
        }
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
        String scope = "GLOBAL";
        nestedST.put(scope, new SymbolTable(scope));
        scopeStack.push(scope);
        syntaxTree = root = new ScopeNode(scope);
    }

    @Override public void exitProgram(LittleParser.ProgramContext ctx) {
        scopeStack.pop();
        //BinaryNode pgm = nodeStack.pop();
        nodeStack.pop();
        syntaxTree = root;
        while (!nodeStack.empty()) {
            System.out.println("Error: nodeStack is not empty!");
            nodeStack.pop();
        }
    }

    @Override public void exitId(LittleParser.IdContext ctx) {
        Stack<String> stringStack = new Stack<>();
        ArrayList<String> symbol = nestedST.get(scopeStack.peek()).getValue(ctx.getChild(0).getText());
        String tableName = scopeStack.peek();
        while ((symbol == null && !scopeStack.empty()) && !ctx.getChild(0).getText().equals(tableName)) {
            stringStack.push(scopeStack.pop());
            symbol = nestedST.get(stringStack.peek()).getValue(ctx.getChild(0).getText());
            if (!scopeStack.empty()) {
                tableName = scopeStack.peek();
            }
        }
        while (!stringStack.empty()) {
            scopeStack.push(stringStack.pop());
        }
        BinaryNode bn;
        if (symbol != null) {
            bn = new BinaryNode(symbol.get(1) + " " + symbol.get(0) + " " + symbol.get(2));
            nodeStack.push(bn);
        }
        else if (!tableName.equals(ctx.getChild(0).getText())) {
            bn = new BinaryNode( "id:" + ctx.getChild(0).getText());
            nodeStack.push(bn);
        }
        //bn = new BinaryNode( "FUNCTION:" + ctx.getChild(0).getText());
        //just have the function declaration deal with this and return
    }

    @Override public void enterString_decl(LittleParser.String_declContext ctx) {
        insertSymbol(ctx.getChild(1).getText(), ctx.getChild(0).getText(), ctx.getChild(3).getText());
    }

    @Override public void exitString_decl(LittleParser.String_declContext ctx) {
        //nodeStack.pop();
        syntaxTree.addChild(nodeStack.pop());
    }

    @Override public void enterVar_decl(LittleParser.Var_declContext ctx) {
        String varNames = ctx.getChild(1).getText();
        for (String varName : varNames.split(",")) {
            varName = varName.replace(";", "");
            insertSymbol(varName, ctx.getChild(0).getText(), null);
        }
    }

    @Override public void exitVar_decl(LittleParser.Var_declContext ctx) {
        //nodeStack.pop();
        //syntaxTree.addChild(nodeStack.pop()); //uncomment if exitId_list is uncommented
        BinaryNode id_tail = nodeStack.pop();
        BinaryNode id = nodeStack.pop();
        syntaxTree.addChild(new BinaryNode("var_decl", id, id_tail));
    }

    @Override public void exitId_list(LittleParser.Id_listContext ctx) {
        /*
        BinaryNode id = nodeStack.pop();
        BinaryNode id_tail = nodeStack.pop();
        BinaryNode newNode = new BinaryNode("id_list", id, id_tail);
        nodeStack.push(newNode);
        */
    }

    @Override public void exitId_tail(LittleParser.Id_tailContext ctx) {
        if (ctx.getChildCount() == 3) {
            BinaryNode id_tail = nodeStack.pop();
            BinaryNode id = nodeStack.pop();
            nodeStack.push(new BinaryNode("id_tail", id, id_tail));
            /*
            BinaryNode newNode;
            Stack<String> stringStack = new Stack<>();
            ArrayList<String> symbol = nestedST.get(scopeStack.peek()).getValue(ctx.getChild(1).getText());
            while (symbol == null && !scopeStack.empty()) {
                stringStack.push(scopeStack.pop());
                symbol = nestedST.get(stringStack.peek()).getValue(ctx.getChild(1).getText());
            }
            while (!stringStack.empty()) {
                scopeStack.push(stringStack.pop());
            }
            if (symbol != null) {
                BinaryNode bn = new BinaryNode("id:" + ctx.getChild(1).getText());
                newNode = new BinaryNode("id_tail", bn, id_tail);
            }
            else {
                newNode = new BinaryNode("id_tail", id, id_tail);
            }
            nodeStack.push(newNode);
            */
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
            BinaryNode func_decl = nodeStack.pop();
            BinaryNode func_declarations = nodeStack.pop();
            nodeStack.push(new BinaryNode("function_declarations", func_decl, func_declarations));
        }
        else {
            nodeStack.push(new BinaryNode(""));
        }
    }

    @Override public void enterFunc_decl(LittleParser.Func_declContext ctx) {
        ArrayList<String> funcStrings = new ArrayList<>();
        funcStrings.add(ctx.getChild(1).getText());
        funcStrings.add(ctx.getChild(2).getText());
        funcStrings.add(ctx.getChild(4).getText()/*.replaceAll("\\(\\)","")*/); // Doesn't work, parameters aren't loaded
        addScope(ctx.getChild(2).getText(), funcStrings);
    }

    @Override public void exitFunc_decl(LittleParser.Func_declContext ctx) {
        /*
        removeScope();
        //TBD
        BinaryNode id = nodeStack.pop();
        BinaryNode func_body = nodeStack.pop();
        nodeStack.push(new BinaryNode("FUNCTION " + ctx.getChild(1).getText() + " " + ctx.getChild(2).getText(), id, func_body));
        */
    }

    @Override public void exitStmt_list(LittleParser.Stmt_listContext ctx) {
        /*
        if (ctx.getChildCount() == 0) {
            nodeStack.push(new BinaryNode(""));
        }
        else {
            BinaryNode stmt = nodeStack.pop();
            BinaryNode stmt_list = nodeStack.pop();
            nodeStack.push(new BinaryNode("statement_list", stmt, stmt_list));
        }
        */
    }

    @Override public void exitAssign_expr(LittleParser.Assign_exprContext ctx) {
        BinaryNode expr = nodeStack.pop();
        BinaryNode id = nodeStack.pop();
        syntaxTree.addChild(new BinaryNode("assign_expr", id, expr));
        /*
        Stack<String> stringStack = new Stack<>();
        ArrayList<String> symbol = nestedST.get(scopeStack.peek()).getValue(ctx.getChild(0).getText());
        while (symbol == null && !scopeStack.empty()) {
            stringStack.push(scopeStack.pop());
            symbol = nestedST.get(stringStack.peek()).getValue(ctx.getChild(0).getText());
        }
        while (!stringStack.empty()) {
            scopeStack.push(stringStack.pop());
        }
        if (symbol != null) {
            BinaryNode bn = new BinaryNode("id:" + ctx.getChild(0).getText());
            syntaxTree.addChild(new BinaryNode("assign_expr", bn, expr));
        }
        else {
            syntaxTree.addChild(new BinaryNode("assign_expr", id, expr));
        }
        */
        //nodeStack.push(new BinaryNode("assign_expr", id, expr));
    }

    @Override public void exitRead_stmt(LittleParser.Read_stmtContext ctx) {
        BinaryNode id_tail = nodeStack.pop();
        String[] strings = nodeStack.pop().getElement().split(" ");
        BinaryNode bn = new BinaryNode("id:" + strings[1]);
        syntaxTree.addChild(new BinaryNode("READ", bn, id_tail));
        //nodeStack.push(new BinaryNode("READ", id, id_tail));
    }

    @Override public void exitWrite_stmt(LittleParser.Write_stmtContext ctx) {
        BinaryNode id_tail = nodeStack.pop();
        String[] strings = nodeStack.pop().getElement().split(" ");
        BinaryNode bn = new BinaryNode("id:" + strings[1]);
        syntaxTree.addChild(new BinaryNode("WRITE", bn, id_tail));
        //nodeStack.push(new BinaryNode("WRITE", id, id_tail));
    }

    @Override public void exitExpr(LittleParser.ExprContext ctx) {
        /*
        if (ctx.getChild(0).getText().equals("")) {
            BinaryNode factor = nodeStack.pop();
            nodeStack.pop();
            nodeStack.push(factor);
        }
        else {
            BinaryNode expr_prefix = nodeStack.pop();
            BinaryNode factor = nodeStack.pop();
            nodeStack.push(new BinaryNode("expr", expr_prefix, factor));
        }
        */
        if (!ctx.getChild(0).getText().equals("")) {
            BinaryNode right = nodeStack.pop();
            String addop = nodeStack.pop().getElement();
            BinaryNode left = nodeStack.pop();
            nodeStack.push(new BinaryNode(addop, left, right));
        }
    }

    @Override public void exitExpr_prefix(LittleParser.Expr_prefixContext ctx) {
        /*
        if (ctx.getChildCount() == 0) {
            nodeStack.push(new BinaryNode(""));
        }
        else {
            BinaryNode expr_prefix = nodeStack.pop();
            BinaryNode factor = nodeStack.pop();
            nodeStack.push(new BinaryNode(ctx.getChild(2).getText(), expr_prefix, factor));
        }
        */
    }

    @Override public void exitFactor(LittleParser.FactorContext ctx) {
        /*
        if (ctx.getChild(0).getText().equals("")) {
            BinaryNode postfix_expr = nodeStack.pop();
            nodeStack.pop();
            nodeStack.push(postfix_expr);
        }
        else {
        */
        if (!ctx.getChild(0).getText().equals("")) {
            BinaryNode right = nodeStack.pop();
            String mulop = nodeStack.pop().getElement();
            BinaryNode left = nodeStack.pop();
            nodeStack.push(new BinaryNode(mulop, left, right));
        }
    }

    @Override public void enterFactor_prefix(LittleParser.Factor_prefixContext ctx) {
        /*
        if (ctx.getChildCount() == 0) {
            nodeStack.push(new BinaryNode(""));
        }
        else {
            BinaryNode postfix_expr = nodeStack.pop();
            BinaryNode factor_prefix = nodeStack.pop();
            nodeStack.push(new BinaryNode(ctx.getChild(2).getText(), factor_prefix, postfix_expr));
        }
        */
    }

    @Override public void exitPrimary(LittleParser.PrimaryContext ctx) {
        if (ctx.getChildCount() == 1) {
            if (ctx.getChild(0).getText().contains(".")) {
                nodeStack.push(new BinaryNode("FLOAT:" + ctx.getChild(0).getText()));
            }
            else if (!ctx.getChild(0).getText().matches("[a-zA-Z][a-zA-Z0-9]*")) {
                nodeStack.push(new BinaryNode("INT:" + ctx.getChild(0).getText()));
            }
        }
    }

    @Override public void exitAddop(LittleParser.AddopContext ctx) {
        nodeStack.push(new BinaryNode(ctx.getChild(0).getText()));
    }

    @Override public void exitMulop(LittleParser.MulopContext ctx) {
        nodeStack.push(new BinaryNode(ctx.getChild(0).getText()));
    }
    
    @Override public void enterIf_stmt(LittleParser.If_stmtContext ctx) {
        blockNum++;
        addScope("BLOCK " + blockNum, null);
        nodeStack.push(new BinaryNode("IF"));
    }

    @Override public void exitIf_stmt(LittleParser.If_stmtContext ctx) {
        removeScope();
        //TBD - Work-In-Progress
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
            addScope("BLOCK " + blockNum, null);
        }
        nodeStack.push(new BinaryNode("ELSE"));
    }

    @Override public void exitElse_part(LittleParser.Else_partContext ctx) {
        if (ctx.getChildCount() > 0) {
            removeScope();
        }
        //TBD - Work-In-Progress
        BinaryNode else_stmt = nodeStack.pop();
        BinaryNode bn = new BinaryNode("");
        while (!else_stmt.getElement().equals("ELSE")) {
            bn = new BinaryNode("else_stmt", new BinaryNode(""), else_stmt);
            else_stmt = nodeStack.pop();
        }
        nodeStack.push(bn);
    }

    @Override public void exitCond(LittleParser.CondContext ctx) {
        BinaryNode exprLeft = nodeStack.pop();
        BinaryNode exprRight = nodeStack.pop();
        BinaryNode compop = new BinaryNode(ctx.getChild(1).getText(), exprLeft, exprRight);
        nodeStack.push(compop);
    }

    @Override public void enterWhile_stmt(LittleParser.While_stmtContext ctx) {
        blockNum++;
        addScope("BLOCK " + blockNum, null);
        nodeStack.push(new BinaryNode("WHILE"));
    }

    @Override public void exitWhile_stmt(LittleParser.While_stmtContext ctx) {
        removeScope();
        //TBD - Work-In-Progress
        BinaryNode while_stmt = nodeStack.pop();
        BinaryNode bn = new BinaryNode("");
        while (!while_stmt.getElement().equals("WHILE")) {
            bn = new BinaryNode("while_stmt", new BinaryNode(""), while_stmt);
            while_stmt = nodeStack.pop();
        }
        nodeStack.push(bn);
    }
}
