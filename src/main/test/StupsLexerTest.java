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

    @Test
    public void file_not_exisiting() {
        try{
            StupsLexer stupsLexer = new StupsLexer(Path.of(""));
            fail("missing IOException: 'file not found'");
        } catch (IOException e) {
            assertTrue(e.getMessage().contains("ERROR: no such file found, try another path. Path was:"));
        }
    }

    @Test
    public void lex_unknownToken() throws IOException {
        StupsLexer stupsLexer = new StupsLexer(Path.of("src/main/test/cs/lexertests/lex_unknownToken.cs"));
        try{
            stupsLexer.lex();
            fail("missing LexerException: 'Unknown Token'");
        }
        catch (LexerException le) {
            assertTrue(le.getMessage().contains("Unknown token:"));
        }
    }

    //TODO: more tests
}