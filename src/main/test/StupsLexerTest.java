import lexer.Lexer;
import lexer.LexerException;
import org.junit.Before;
import org.junit.Test;
import parser.ParserException;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class StupsLexerTest {

    @Before
    public void setUp() {
    }

    @Test(expected = IOException.class)
    public void file_not_exisiting() throws IOException {
        try{
            StupsLexer stupsLexer = new StupsLexer(Path.of(""));
        } catch (IOException e) {
            assertTrue(e.getMessage().contains("ERROR: no such file found, try another path. Path was:"));
            throw e;
        }
    }

    @Test(expected = LexerException.class)
    public void lex_unknownToken() throws LexerException, IOException {
        StupsLexer stupsLexer = new StupsLexer(Path.of("src/main/test/cs/lex_unknownToken.cs"));
        try{
            stupsLexer.lex();
        }
        catch (LexerException le) {
            assertTrue(le.getMessage().contains("Unknown token:"));
            throw le;
        }
    }
}