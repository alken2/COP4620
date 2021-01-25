import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import java.io.IOException;

public class Driver {
    public static void main(String[] args) {
        String filename = args[0];
        CharStream input = null;
        try {
            input = CharStreams.fromFileName(filename);
        }
        catch (IOException e) {
            System.out.println("File not found: " + filename);
            System.exit(1);
        }
        Little lexer = new Little(input);
        Token t = lexer.nextToken();
        Vocabulary v = lexer.getVocabulary();
        while (t.getType() != Little.EOF) {
            System.out.println("Token Type: " + v.getSymbolicName(t.getType()));
            System.out.println("Value: "+ t.getText());
            t = lexer.nextToken();
        }
    }
}
