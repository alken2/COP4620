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
        CharStream input = null;
        File outputs = new File("outputs.txt");
        FileWriter myWriter = new FileWriter("outputs.txt");
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
            myWriter.write("Token Type: " + v.getSymbolicName(token.getType()));
            myWriter.write("\nValue: "+ token.getText()+ "\n");
            token = lexer.nextToken();
        }
        myWriter.close();
    }
}
