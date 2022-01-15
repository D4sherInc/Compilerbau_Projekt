import lexer.LexerException;
import org.junit.Before;
import org.junit.Test;
import parser.ParserException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class StupsParserTest {

    private StupsParser stupsParser;

    @Before
    public void setUp() {
    }

    private void setUpStupsParser(Path path_to_file) throws FileNotFoundException {
        this.stupsParser = new StupsParser(path_to_file);
    }

    @Test
    public void baseCase() throws IOException, ParserException, LexerException {
        setUpStupsParser(Path.of("src/main/test/cs/basecase.cs"));
        stupsParser.parse();

    }

    @Test
    public void addInt() throws IOException, LexerException {
        setUpStupsParser(Path.of("src/main/test/cs/AddInt0_parseError.cs"));
        try {
            stupsParser.parse();
            fail("missing ParseException");
        } catch (ParserException e) {
            assertTrue(e.getMessage().contains("found 'void', expected: : bool, "));
        }
    }

    @Test
    public void addInt2() throws IOException, LexerException {
        setUpStupsParser(Path.of("src/main/test/cs/AddInt1_parseError.cs"));
        try {
            stupsParser.parse();
            fail("missing ParseException");
        } catch (ParserException e) {
            assertTrue(e.getMessage().contains("found 'void', expected: : bool, "));
        }
    }

    //TODO: more tests

}