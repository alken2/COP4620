import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;

public class Driver {

    public static void main(String[] args) throws Exception {
        System.out.println("Parsing: " + args[0]);


        String t = args[0];
        CharStream input = CharStreams.fromFileName(t);
        Little lexer = new Little(input);

        Token token = lexer.nextToken();
        while (token.getType() != Little.EOF) {
            System.out.println("Token Type: " + getTokenType(token.getType()) + "\nValue: " + token.getText());
            token = lexer.nextToken();
        }
    }
    private static String getTokenType(int tokenType) {
        switch (tokenType) {
            case Little.OPEATORS:
                return "OPERATORS";
            case Little.COMMENT:
                return "COMMENT";
            case Little.KEYWORD:
                return "KEYWORD";
            case Little.STRINGLITERAL:
                return "STRINGLITERAL";
            case Little.FLOATLITERAL:
                return "FLOATLITERAL";
            case Little.INTLITERAL:
                return "INTLITERAL";
            case Little.IDENTIFIER:
                return "IDENTIFIER";
            case Little.WS:
                return "WS";
            default:
                return "OTHER";
        }
    }
}

