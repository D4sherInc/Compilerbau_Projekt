import lexer.Lexer;
import lexer.LexerException;
import node.EOF;
import node.TWhitespace;
import node.Token;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class StupsLexer {

    private final List<String> inputAsList;

    public StupsLexer(Path path_to_file) throws IOException {

        // try-catch for IOException: wrong filepath
        try{
            inputAsList = Files.lines(path_to_file).collect(Collectors.toList());
        }
        catch (UncheckedIOException | IOException e) {
            throw new IOException(String.format("ERROR: no such file found, try another path. Path was: %s%n", path_to_file.toString()));
        }
    }

    public void lex() throws LexerException {

        System.out.println("start lexing...");

        int lineCounter = 1;
        for (String line : inputAsList) {
            StringReader reader = new StringReader(line);
            PushbackReader r = new PushbackReader(reader);
            Lexer l = new Lexer(r);
            Token token;
            do {

                // try-catch for lexical error: token not found --> print "unknown token: 't'"
                // add Whitespace token (gets ignored), --> keep going
                try{
                    token = l.next();
                }
                catch (LexerException | IOException e) {
                    throw new LexerException(String.format("ERROR ON LINE %d:%s%n" ,lineCounter, e.getMessage().substring(e.getMessage().indexOf("]") + 1)));

                }
            } while (!(token instanceof EOF));
            lineCounter++;
        }

        System.out.println("lexing successful!\n");
    }
}
