import lexer.Lexer;
import lexer.LexerException;
import node.Start;
import parser.Parser;
import parser.ParserException;

import java.io.*;
import java.nio.file.Path;
import java.util.Scanner;

public class StupsParser {

    private String input;

    public StupsParser(Path path_to_file) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(String.valueOf(path_to_file)));
        scanner.useDelimiter("\\Z");
        input = scanner.next();
    }

    public Start parse() throws ParserException, LexerException, IOException {

        System.out.println("parsing...");

        StringReader reader = new StringReader(input);
        PushbackReader r = new PushbackReader(reader, 100);
        Lexer l = new Lexer(r);
        Parser parser = new Parser(l);
        Start tree;

        try {
            tree = parser.parse();

        } catch (ParserException e) {
            //throw new ParserException(e.getToken(), String.format("LINE %d: found '%s', expected: %s%n", e.getToken().getLine(), e.getToken().getText(), e.getMessage().substring(e.getMessage().indexOf('\''))));
            throw new ParserException(e.getToken(), String.format("LINE %d: found '%s', expected: %s%n", e.getToken().getLine(), e.getToken().getText(), e.getMessage().substring(e.getMessage().indexOf(':'))));
        }

        System.out.println("parsing successful!");
        return tree;
    }
}
 