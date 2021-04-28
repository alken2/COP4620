import org.antlr.v4.runtime.*;
import java.io.FileInputStream;
import java.io.InputStream;

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
        parser.program();
        int i = parser.getNumberOfSyntaxErrors();
        
        if (i == 0) {
            System.out.println("Accepted");
        }
        else {
            System.out.println("Not accepted");
        }
    }
}
