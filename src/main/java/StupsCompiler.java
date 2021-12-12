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
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class StupsCompiler {
    public static void main(String[] args) throws IOException, LexerException {
        // java StupsCompiler
        // java StupsCompiler ../resources/minimal.cs
        Path path_to_file = Paths.get(args[0]);
        lex(path_to_file);

    }

    private static void lex(Path path) throws IOException {

        List<String> input;

        // try-catch for IOException: wrong filepath
        try{
            input = Files.lines(path).collect(Collectors.toList());
        }
        catch (IOException e) {
            System.err.println("ERROR: no such file found, try another path");
            input = new LinkedList<>();
        }

        int lineCounter = 1;
        boolean lexErrorFound = false;
        for (String line : input) {
            StringReader reader = new StringReader(line);
            PushbackReader r = new PushbackReader(reader);
            Lexer l = new Lexer(r);
            Token token;
            do {

                // try-catch for lexical error: token not found --> print "unknown token: *token*"
                // add Whitespace token (gets ignored), --> keep going
                try{
                    token = l.next();
                }
                catch (LexerException e) {
                    System.out.println("ERROR ON LINE " + lineCounter + " " + e.getMessage());
                    token = new TWhitespace(" ");
                    lexErrorFound = true;
                }
            } while (!(token instanceof EOF));
            lineCounter++;
        }

        if (!lexErrorFound) System.out.println("lexing successful");
    }
}