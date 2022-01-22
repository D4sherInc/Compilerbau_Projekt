package helpingstuff;

import lexer.Lexer;
import lexer.LexerException;
import node.EOF;
import node.TWhitespace;
import node.Token;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class LineLexer {
    public static void main(String[] args) throws IOException, LexerException {
        Path path = Paths.get("src/main/cs/fibonacci.cs");
        List<String> input = Files.lines(path).collect(Collectors.toList());
        int lineCounter = 1;
        for (String line : input) {
            StringReader reader = new StringReader(line);
            PushbackReader r = new PushbackReader(reader);
            Lexer l = new Lexer(r);
            Token token;
            System.out.println("\nLINE " + lineCounter);
            do {

                // try-catch for lexical error: token not found --> print "unknown token: *token*"
                // add Whitespace token (gets ignored), --> keep going
                try{
                    token = l.next();
                }
                catch (LexerException e) {
                    System.out.println("LINE " + lineCounter + " " + e.getMessage());
                    token = new TWhitespace(" ");
                }
                if (!token.getClass().getSimpleName().equals("TWhitespace")) System.out.println(token.getClass().getSimpleName() + " " + token.getText());
            } while (!(token instanceof EOF));
            lineCounter++;
        }
    }
}