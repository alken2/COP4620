import gen.*;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class Driver {
    public static void main(String[] args) throws Exception {
        String inputfile = null;
        ANTLRInputStream input;
        InputStream is = System.in;
        if (args.length > 0) {
            inputfile = args[0];
        }
        if (inputfile != null) {
            is = new FileInputStream(inputfile);
        }

        input = new ANTLRInputStream(is);
        LittleLexer lexer = new LittleLexer(input);
        CommonTokenStream to = new CommonTokenStream(lexer);
        LittleParser parser = new LittleParser(to);

        parser.removeErrorListeners();

        Listener listener = new Listener();
        ParseTreeWalker walker = new ParseTreeWalker();
        ParseTree tree = parser.program();
        walker.walk(listener,tree);
        LinkedHashMap<String, SymbolTable> map = listener.getSymbolTable();
        AbstractNode ast = listener.getSyntaxTree();
        ast.print();
        //IRGenerator irGenerator = new IRGenerator();
        AssemblyGenerator asmGenerator = new AssemblyGenerator();
        //irGenerator.createIR((ScopeNode)ast);
        asmGenerator.createAssembly((ScopeNode)ast, map);
    }
}
