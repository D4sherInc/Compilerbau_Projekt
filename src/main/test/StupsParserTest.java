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
        setUpStupsParser(Path.of("src/main/test/cs/parsertests/AddInt0_parseError.cs"));
        try {
            stupsParser.parse();
            fail("missing ParseException");
        } catch (ParserException e) {
            assertTrue(e.getMessage().contains("found 'void', expected: : bool, "));
        }
    }

    @Test
    public void addInt2() throws IOException, LexerException {
        setUpStupsParser(Path.of("src/main/test/cs/parsertests/AddInt1_parseError.cs"));
        try {
            stupsParser.parse();
            fail("missing ParseException");
        } catch (ParserException e) {
            assertTrue(e.getMessage().contains("found 'void', expected: : bool, "));
        }
    }

    @Test
    public void emptyWhile() throws IOException, LexerException {
        setUpStupsParser(Path.of("src/main/test/cs/parsertests/empty_while.cs"));
        try{
            stupsParser.parse();
            fail("missing ParsingException");
        } catch (ParserException e) {
            assertTrue(e.getMessage().contains("LINE 10: found ')', expected:"));
        }

    }

    //TODO: more tests
    // ExpressionEqualBool
    // ExpressionUnequalBool
    // FunctionCall
    // FunctionCallWithArgs
    // IfNoElse
    // IfNoElse2
    // IfWhileIf
    // IfWhileIf2
    // IfWhileIf3
    // IfWhileIf4
    // IfWithElse
    // IfWithElseNoBlock
    // IfWithIntExpression
    // IfWithIntExpression2
    // IfWithIntExpression3
    // IfWithStringExpression
    // IfWithStringExpression2
    // NestedCalls                  !
    // WhileIf
    // WhileIf2
    // WhileIf3
    // WhileIf4
    // WhileWithBoolExpression
    // WhileWithIntExpression
    // WhileWithIntExpression2
    //

}