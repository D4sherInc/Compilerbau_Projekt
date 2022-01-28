import node.Start;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CodeGeneratorTest {

    private final Path directory = Path.of("src/main/test/cs/codeGeneratorTests/");
    private Path path_to_file;
    private File testfile;

    private CodeGenerator codeGenerator;

    private File setUpCodeGenerator(Path path_to_file) {
        try {
            StupsParser stupsParser = new StupsParser(path_to_file);
            Start tree = stupsParser.parse();
            StupsTypeChecker stupsTypeChecker = new StupsTypeChecker(tree);
            stupsTypeChecker.typechecking();
            SymbolTable st = stupsTypeChecker.getSymbolTable();
            this.codeGenerator = new CodeGenerator(st, tree, path_to_file.toString());
            return codeGenerator.getJasmin();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new File("error");
    }

    @Test
    public void testBaseCase() {

        path_to_file = Path.of(directory + "/basecase.cs");
        testfile = setUpCodeGenerator(path_to_file);

        assertEquals("basecase.j", testfile.getName());

        try {
            String content = Files.readString(Path.of(String.valueOf(testfile)));

            assertTrue(content
                    .contains(".method public static main([Ljava/lang/String;)V\n" +
                    "\t.limit stack 20\n" +
                    "\t.limit locals 1\n" +
                    "\treturn\n" +
                    "\t.end method"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testIfElse(){

        path_to_file = Path.of(directory + "/if_else.cs") ;
        setUpCodeGenerator(path_to_file);

        testfile = codeGenerator.getJasmin();

        assertEquals("if_else.j", testfile.getName());
        
        try {
            String content = Files.readString(Path.of(String.valueOf(testfile)));

            // base if
            String if_1 = "ifeq False1";
            String if_false1 = "False1:";
            assertTrue(content
                    .contains(if_1));
            assertTrue(content
                    .contains(if_false1));
            
            // check if labels are in the right order
            assertTrue(content.lastIndexOf(if_1) < content.lastIndexOf(if_false1));

            // short if with else
            String if_start2 = "ifeq Else5";
            String if_true2_end = "goto L5\nElse5:";
            String if_false2_end = "L5:";

            assertTrue(content.contains(if_start2));
            assertTrue(content.contains(if_true2_end));
            assertTrue(content.contains(if_false2_end));

            assertTrue(content.lastIndexOf(if_start2) < content.lastIndexOf(if_true2_end));
            assertTrue(content.lastIndexOf(if_true2_end) < content.lastIndexOf(if_false2_end));


//            if (i != 5)
//                if (i == 4) Console.WriteLine("nested if yes");
//                else Console.WriteLine("nested if no");
//            else Console.WriteLine("outer if no");

            String if_3 = "ifeq Else7";
            // nested if
            String if_4_nested = "ifeq Else9";
            String if_true4_end_nested = "goto L9\nElse9:";
            String if_false4_end_nested = "L9:\n";
            // end of first if
            String if_true3_end = "goto L7\nElse7:";
            String if_false3_end = "L7:";

            assertTrue(content.contains(if_3));
            assertTrue(content.contains(if_4_nested));
            assertTrue(content.contains(if_true4_end_nested));
            assertTrue(content.contains(if_false4_end_nested));
            assertTrue(content.contains(if_true3_end));
            assertTrue(content.contains(if_false3_end));

            assertTrue(content.lastIndexOf(if_3) < content.lastIndexOf(if_4_nested));
            assertTrue(content.lastIndexOf(if_4_nested) < content.lastIndexOf(if_true4_end_nested));
            assertTrue(content.lastIndexOf(if_true4_end_nested) < content.lastIndexOf(if_false4_end_nested));
            assertTrue(content.lastIndexOf(if_false4_end_nested) < content.lastIndexOf(if_true3_end));
            assertTrue(content.lastIndexOf(if_true3_end) < content.lastIndexOf(if_false3_end));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testWhile(){
        path_to_file = Path.of(directory + "/while.cs") ;
        testfile = setUpCodeGenerator(path_to_file);

        assertEquals("while.j", testfile.getName());

        try {
            String content = Files.readString(Path.of(String.valueOf(testfile)));

//             while (i < 15)
//              {
//                  Console.WriteLine(i);
//                  i = i + 1;
//              }
            String while1 = "While0:";
            String condition1 = "ifeq Done0";
            String while1_backjump ="goto While0";
            String while1_end = "Done0:";

            assertTrue(content.contains(while1));
            assertTrue(content.contains(condition1));
            assertTrue(content.contains(while1_backjump));
            assertTrue(content.contains(while1_end));

            // assert right order of labels and jumps
            assertTrue(content.lastIndexOf(while1) < content.lastIndexOf(condition1));
            assertTrue(content.lastIndexOf(condition1) < content.lastIndexOf(while1_backjump));
            assertTrue(content.lastIndexOf(while1_backjump) < content.lastIndexOf(while1_end));

//            while (i <30)
//            {
//                if (i < 25)
//                    if (i > 20) Console.WriteLine("if if");
//                    else Console.WriteLine("if if else");
//                else Console.WriteLine("if (if) else");
//                i =i + 1;
//            }
            String while2 = "While3:";
            String condition2 = "iflt Else3";

            String if_3 = "ifeq Else5";
            // nested if
            String if_4_nested = "ifeq Else7";
            String if_true4_end_nested = "goto L7\nElse7:";
            String if_false4_end_nested = "L7:";
            // end of first if
            String if_true3_end = "goto L5\nElse5:";
            String if_false3_end = "L5:";

            String while2_backjump = "goto While3";
            String while2_end = "Done3";

            assertTrue(content.lastIndexOf(while2) < content.lastIndexOf(condition2));
            assertTrue(content.lastIndexOf(condition2) < content.lastIndexOf(if_3));
            assertTrue(content.lastIndexOf(if_3) < content.lastIndexOf(if_4_nested));
            assertTrue(content.lastIndexOf(if_4_nested) < content.lastIndexOf(if_true4_end_nested));
            assertTrue(content.lastIndexOf(if_true4_end_nested) < content.lastIndexOf(if_false4_end_nested));
            assertTrue(content.lastIndexOf(if_false4_end_nested) < content.lastIndexOf(if_true3_end));
            assertTrue(content.lastIndexOf(if_true3_end) < content.lastIndexOf(if_false3_end));
            assertTrue(content.lastIndexOf(if_false3_end) < content.lastIndexOf(while2_backjump));
            assertTrue(content.lastIndexOf(while2_backjump) < content.lastIndexOf(while2_end));

//            while (i < 55)
//            {
//                if (i > 52) Console.WriteLine("last steps");
//                else Console.WriteLine("a few more steps");
//                i = i + 1;
//            }

            String while5 = "While9:";
            String condition5 = "ifeq Done9";

            String if6 = "ifeq Else11";
            String if_true6_end = "goto L11\nElse11:";
            String if_false6_end = "L11";

            String while5_backjump = "goto While9";
            String while5_end = "Done9:";

            assertTrue(content.contains(while5));
            assertTrue(content.contains(condition5));
            assertTrue(content.contains(if6));
            assertTrue(content.contains(if_true6_end));
            assertTrue(content.contains(if_false6_end));
            assertTrue(content.contains(while5_backjump));
            assertTrue(content.contains(while5_end));

            assertTrue(content.lastIndexOf(while5) < content.lastIndexOf(condition5));
            assertTrue(content.lastIndexOf(condition5) < content.lastIndexOf(if6));
            assertTrue(content.lastIndexOf(if6) < content.lastIndexOf(if_true6_end));
            assertTrue(content.lastIndexOf(if_true6_end) < content.lastIndexOf(if_false6_end));
            assertTrue(content.lastIndexOf(if_false6_end) < content.lastIndexOf(while5_backjump));
            assertTrue(content.lastIndexOf(while5_backjump) < content.lastIndexOf(while5_end));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}