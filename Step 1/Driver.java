import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Driver {
    public static void main(String[] args) throws IOException {
        String filename = args[0];
        CharStream input = CharStreams.fromFileName(filename);
        File outputs = new File("outputs.txt"); //never used
        FileWriter writer = new FileWriter("outputs.txt");
        Little lexer = new Little(input);
        Token t = lexer.nextToken();
        Vocabulary v = lexer.getVocabulary();
        while (t.getType() != Little.EOF) {
            writer.write("Token Type: " + v.getSymbolicName(t.getType()));
            writer.write("\nValue: "+ t.getText()+ "\n");
            t = lexer.nextToken();
        }
        writer.close();
    }
}
