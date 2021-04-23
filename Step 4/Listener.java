import gen.*;

import java.util.*;

public class Listener extends LittleBaseListener {
    private static int blockNum = 0;
    private final Stack<String> scopeStack = new Stack<>();
    private final Stack<BinaryNode> nodeStack = new Stack<>();;
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
            syntaxTree.addChild(new ScopeNode(funcStrings.get(0) + ":" + funcStrings.get(1) + ";"));
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
    }

    @Override public void enterString_decl(LittleParser.String_declContext ctx) {
        insertSymbol(ctx.getChild(1).getText(), ctx.getChild(0).getText(), ctx.getChild(3).getText());
    }

    @Override public void exitString_decl(LittleParser.String_declContext ctx) {
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
        BinaryNode id_tail = nodeStack.pop();
        BinaryNode id = nodeStack.pop();
        syntaxTree.addChild(new BinaryNode("var_decl", id, id_tail));
    }

    @Override public void exitId_tail(LittleParser.Id_tailContext ctx) {
        if (ctx.getChildCount() == 3) {
            BinaryNode id_tail = nodeStack.pop();
            BinaryNode id = nodeStack.pop();
            nodeStack.push(new BinaryNode("id_tail", id, id_tail));
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
        addScope(ctx.getChild(2).getText(), funcStrings);
    }

    @Override public void exitFunc_decl(LittleParser.Func_declContext ctx) {
        removeScope();
    }

    @Override public void exitAssign_expr(LittleParser.Assign_exprContext ctx) {
        BinaryNode expr = nodeStack.pop();
        BinaryNode id = nodeStack.pop();
        syntaxTree.addChild(new BinaryNode("assign_expr", id, expr));
    }

    @Override public void exitRead_stmt(LittleParser.Read_stmtContext ctx) {
        BinaryNode id_tail = nodeStack.pop();
        String[] strings = nodeStack.pop().getElement().split(" ");
        BinaryNode bn = new BinaryNode("id:" + strings[1]);
        syntaxTree.addChild(new BinaryNode("READ", bn, id_tail));
    }

    @Override public void exitWrite_stmt(LittleParser.Write_stmtContext ctx) {
        BinaryNode id_tail = nodeStack.pop();
        String[] strings = nodeStack.pop().getElement().split(" ");
        BinaryNode bn = new BinaryNode("id:" + strings[1]);
        syntaxTree.addChild(new BinaryNode("WRITE", bn, id_tail));
    }

    @Override public void exitExpr(LittleParser.ExprContext ctx) {
        if (!ctx.getChild(0).getText().equals("")) {
            BinaryNode right = nodeStack.pop();
            String addop = nodeStack.pop().getElement();
            BinaryNode left = nodeStack.pop();
            nodeStack.push(new BinaryNode(addop, left, right));
        }
    }

    @Override public void exitFactor(LittleParser.FactorContext ctx) {
        if (!ctx.getChild(0).getText().equals("")) {
            BinaryNode right = nodeStack.pop();
            String mulop = nodeStack.pop().getElement();
            BinaryNode left = nodeStack.pop();
            nodeStack.push(new BinaryNode(mulop, left, right));
        }
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
        //No test cases with if_stmt? No problem!
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
        //No test cases with else_part? No problem!
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
        //No test cases with while_stmt? No problem!
    }

    @Override public void exitWhile_stmt(LittleParser.While_stmtContext ctx) {
        removeScope();
        //No test cases with while_stmt? No problem!
    }
}
