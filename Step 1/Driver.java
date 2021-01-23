import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;

public class Driver {

    public static void main(String[] args) throws Exception {

        String filename = args[0];
        System.out.println("Parsing: " + filename);
        CharStream input = CharStreams.fromFileName(filename);
        Little lexer = new Little(input);

        Token token = lexer.nextToken();
        while (token.getType() != Little.EOF) {
            System.out.println("\t" + getTokenType(token.getType()) + "\t\t" + token.getText());
            token = lexer.nextToken();
        }
    }

    private static String getTokenType(int tokenType) {
        switch (tokenType) {
            case Little.KEYWORD:
                return "KEYWORD";
            case Little.OPEATOR:
                return "OPERATOR";
            /*
            case Little.COMMENT:
                return "COMMENT";
            case Little.WS:
                return "WS";
            */
            case Little.IDENTIFIER:
                return "IDENTIFIER";
            case Little.INTLITERAL:
                return "INTLITERAL";
            case Little.FLOATLITERAL:
                return "FLOATLITERAL";
            case Little.STRINGLITERAL:
                return "STRINGLITERAL";
            default:
                return "OTHER";
        }
    }
}
