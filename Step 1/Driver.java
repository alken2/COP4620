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
        Little lexer = new Little(input);
        Token t = lexer.nextToken();
        Vocabulary v = lexer.getVocabulary();
        while (t.getType() != t.EOF) {
            System.out.println("Token Type: " + v.getSymbolicName(t.getType()));
            System.out.println("Value: " + t.getText());
            t = lexer.nextToken();
        }
    }
}
