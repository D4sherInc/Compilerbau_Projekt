import exceptions.TypeCheckerException;
import lexer.LexerException;
import node.Start;
import org.junit.Before;
import org.junit.Test;
import parser.ParserException;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class StupsTypeCheckerTest {

    private String sub_directory = "src/main/test/cs/";
    private String test_directory = sub_directory + "/typecheckertests";
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
        setUpTypeChecker(Path.of(sub_directory + "/AddInt_allValid.cs"));
        try {
            stupsTypeChecker.typechecking();
        } catch (TypeCheckerException e) {
            fail("TypeCheckerException thrown where non was expected");
        }
    }

    @Test
    // assign int = int + bool (fail)
    public void addInt() throws ParserException, IOException, LexerException {
        setUpTypeChecker(Path.of(test_directory + "/AddInt2_typeError.cs"));

        try{
            stupsTypeChecker.typechecking();
            fail("missing TypeCheckerException");
        } catch (TypeCheckerException e) {
            assertTrue(e.getMessage().contains("Expected Type: INTEGER; actual: BOOLEAN"));
        }
    }

    @Test
    public void testInvoke_wrong_argument_type() throws ParserException, IOException, LexerException {
        setUpTypeChecker(Path.of(test_directory + "/invoke.cs"));

        try {
            stupsTypeChecker.typechecking();
            fail("missing TypecheckerException: wrong type in Invoke");
        } catch (TypeCheckerException e) {
            assertTrue(e.getMessage().contains("Expected Type: INTEGER; actual: STRING"));
        }
    }

    @Test
    public void testCompare_equalString() throws ParserException, IOException, LexerException {
        setUpTypeChecker(Path.of(test_directory + "/compare_strings.cs"));
        try {
            stupsTypeChecker.typechecking();
        } catch (TypeCheckerException e) {
            fail("Wrong TypecheckerException: string and string should be comparable");
        }
    }

    //TODO: more tests
    // PutDoubleInInt

}