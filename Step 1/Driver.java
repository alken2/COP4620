import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import java.io.IOException;

public class Driver {

    public static void main(String[] args) {
        String filename = args[0];
        System.out.println("Parsing: " + filename);
        CharStream input = null;
        try {
            input = CharStreams.fromFileName(filename);
        }
        catch (IOException e) {
            System.out.println("File not found: " + filename);
            System.exit(1);
        }
        Little lexer = new Little(input);
        Token token = lexer.nextToken();
        Vocabulary v = lexer.getVocabulary();
        while (token.getType() != Little.EOF) {
            System.out.println("Token Type: " + v.getSymbolicName(token.getType()));
            System.out.println("Value: "+ token.getText());
            token = lexer.nextToken();
        }
    }
}
