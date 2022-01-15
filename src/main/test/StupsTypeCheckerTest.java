import lexer.LexerException;
import node.Start;
import org.junit.Before;
import org.junit.Test;
import parser.ParserException;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class StupsTypeCheckerTest {

    private StupsTypeChecker stupsTypeChecker;

    @Before
    public void setUp() {
    }

    private void setUpTypeChecker(Path path_to_file) throws IOException, ParserException, LexerException {
        StupsParser stupsParser = new StupsParser(path_to_file);
        Start tree = stupsParser.parse();

        this.stupsTypeChecker = new StupsTypeChecker(tree);
    }

    @Test
    // all valid int operations in one go
    public void addInt_all_correct() throws ParserException, IOException, LexerException {
        setUpTypeChecker(Path.of("src/main/test/cs/AddInt_allValid.cs"));
        try {
            stupsTypeChecker.typechecking();
        } catch (TypeCheckerException e) {
            fail("TypeCheckerException thrown where non was expected");
        }
    }

    @Test
    // assign int = int + bool (fail)
    public void addInt() throws ParserException, IOException, LexerException {
        setUpTypeChecker(Path.of("src/main/test/cs/AddInt2_typeError.cs"));

        try{
            stupsTypeChecker.typechecking();
            fail("missing TypeCheckerException");
        } catch (TypeCheckerException e) {
            assertTrue(e.getMessage().contains("Expected Type: INTEGER; actual: BOOLEAN"));
        }
    }

}