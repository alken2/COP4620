import gen.*;

import java.util.*;

public class Listener extends LittleBaseListener {
    private static int blockNum = 0;
    private final Stack<String> scopeStack = new Stack<>();
    private final Stack<Map<BinaryNode, String>> nodeStack = new Stack<>();
    private final LinkedHashMap<String, SymbolTable> nestedST = new LinkedHashMap<>();
    private BinaryNode syntaxTree;

    public Listener() {}

    public LinkedHashMap<String, SymbolTable> getSymbolTable() {
        return nestedST;
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
        scopeStack.pop();
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
        Map<BinaryNode, String> m = new HashMap<>();
        m.put(bn, "id");
        nodeStack.push(m);
    }

    @Override public void enterString_decl(LittleParser.String_declContext ctx) {
        insertSymbol(ctx.getChild(1).getText(), ctx.getChild(0).getText(), ctx.getChild(3).getText());
    }

    @Override public void enterVar_decl(LittleParser.Var_declContext ctx) {
        String varNames = ctx.getChild(1).getText();
        for (String varName : varNames.split(",")) {
            varName = varName.replace(";", "");
            insertSymbol(varName, ctx.getChild(0).getText(), null);
        }
    }

    @Override public void enterParam_decl(LittleParser.Param_declContext ctx) {
        insertSymbol(ctx.getChild(1).getText(), ctx.getChild(0).getText(), null);
    }

    @Override public void enterFunc_decl(LittleParser.Func_declContext ctx) {
        addScope(ctx.getChild(2).getText());
    }

    @Override public void exitFunc_decl(LittleParser.Func_declContext ctx) {
        scopeStack.pop();
    }

    @Override public void exitPrimary(LittleParser.PrimaryContext ctx) {
        String temp1 = ctx.getChild(2).getText();
        String temp2 = ctx.getChild(3).getText();
    }

    @Override public void enterIf_stmt(LittleParser.If_stmtContext ctx) {
        blockNum++;
        addScope("BLOCK " + blockNum);
    }

    @Override public void exitIf_stmt(LittleParser.If_stmtContext ctx) {
        scopeStack.pop();
    }

    @Override public void enterElse_part(LittleParser.Else_partContext ctx) {
        if (ctx.getChildCount() > 0) {
            blockNum++;
            addScope("BLOCK " + blockNum);
        }
    }

    @Override public void exitElse_part(LittleParser.Else_partContext ctx) {
        if (ctx.getChildCount() > 0) {
            scopeStack.pop();
        }
    }

    @Override public void enterWhile_stmt(LittleParser.While_stmtContext ctx) {
        blockNum++;
        addScope("BLOCK " + blockNum);
    }

    @Override public void exitWhile_stmt(LittleParser.While_stmtContext ctx) {
        scopeStack.pop();
    }
}
