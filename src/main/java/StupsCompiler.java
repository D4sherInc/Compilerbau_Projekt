import lexer.Lexer;
import lexer.LexerException;
import node.EOF;
import node.Token;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class StupsCompiler {
    public static void main(String[] args) throws IOException, LexerException {
        Path path = Paths.get("./test1.cs");
        List<String> input = Files.lines(path).collect(Collectors.toList());
        int lineCounter = 1;
        for (String line : input) {
            StringReader reader = new StringReader(line);
            PushbackReader r = new PushbackReader(reader);
            Lexer l = new Lexer(r);
            Token token;
            System.out.println("LINE " + lineCounter);
            do {
                token = l.next();
                System.out.println(token.getClass().getSimpleName() + " " + token.getText());
            } while (!(token instanceof EOF));
            lineCounter++;
        }
    }
}