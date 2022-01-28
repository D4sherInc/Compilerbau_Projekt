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
            String if_start1 = "\tifeq False1\n";
            String if_false1 = "\tFalse1:\n";
            assertTrue(content
                    .contains(if_start1));
            assertTrue(content
                    .contains(if_false1));
            
            // check if labels are in the right order
            assertTrue(content.lastIndexOf(if_start1) < content.lastIndexOf(if_false1));
            

            // short if with else
            String if_start2 = "ifeq Else5\n";
            String if_true2_end = "\tgoto L5\n\tElse5:\n";
            String if_false2_end = "\tL5:";

            assertTrue(content.contains(if_start2));
            assertTrue(content.contains(if_true2_end));
            assertTrue(content.contains(if_false2_end));

            assertTrue(content.lastIndexOf(if_start2) < content.lastIndexOf(if_true2_end));
            assertTrue(content.lastIndexOf(if_true2_end) < content.lastIndexOf(if_false2_end));


            //TODO
            // short if with short if-else with else
            String if_start3 = "ifeq Else7\n";
            String if_start4_nested = "\tifeq Else8\n";
            String if_true4_end_nested = "\tgoto L8\n\tElse8:\n";
            String if4_end = "\tL8:\n:";
            String if_false4_end_nested = "\tgoto L9\n\tElse9:\n";
            String if_true3_end = "\tgoto L7\n\tElse7:\n";
            String if_false3_end = "\tL7:";

            assertTrue(content
                    .contains("ifeq Else7\n" +
                            "\tiload 1\n" +
                            "\ti2d\n" +
                            "\tldc 4 \n" +
                            "\ti2d\n" +
                            "\tdcmpg\n" +
                            "\tifeq Else8\n" +
                            "\ticonst_0\n" +
                            "\tgoto L8\n" +
                            "\tElse8:\n" +
                            "\ticonst_1\n" +
                            "\tL8:\n" +
                            "\tifeq Else9\n" +
                            "\tgetstatic java/lang/System/out Ljava/io/PrintStream;\n" +
                            "\tldc \"if1 if2 true\" \n" +
                            "\tinvokevirtual java/io/PrintStream/println(Ljava/lang/String;)V\n" +
                            "\tgoto L9\n" +
                            "\tElse9:\n" +
                            "\tgetstatic java/lang/System/out Ljava/io/PrintStream;\n" +
                            "\tldc \"if1 if2 false\" \n" +
                            "\tinvokevirtual java/io/PrintStream/println(Ljava/lang/String;)V\n" +
                            "\tL9:\n" +
                            "\tgoto L7\n" +
                            "\tElse7:\n" +
                            "\tgetstatic java/lang/System/out Ljava/io/PrintStream;\n" +
                            "\tldc \"if1 false\" \n" +
                            "\tinvokevirtual java/io/PrintStream/println(Ljava/lang/String;)V\n" +
                            "\tL7:"));
            /* assertTrue(content
                    .contains());
            assertTrue(content
                    .contains());
            assertTrue(content
                    .contains());
            assertTrue(content
                    .contains());
            assertTrue(content
                    .contains());
            assertTrue(content
                    .contains());
*/


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
            assertTrue(content
                    .contains("While0:\n" +
                            "\tiload 1\n" +
                            "\ti2d\n" +
                            "\tldc 15 \n" +
                            "\ti2d\n" +
                            "\tdcmpg\n" +
                            "\tiflt Else0\n" +
                            "\ticonst_0\n" +
                            "\tgoto L0\n" +
                            "\tElse0:\n" +
                            "\ticonst_1\n" +
                            "\tL0:\n" +
                            "\tifeq Done0\n" +
                            "\tgetstatic java/lang/System/out Ljava/io/PrintStream;\n" +
                            "\tiload 1\n" +
                            "\tinvokevirtual java/io/PrintStream/println(I)V\n" +
                            "\tiload 1\n" +
                            "\tldc 1 \n" +
                            "\tiadd\n" +
                            "\tistore 1\n" +
                            "\tgoto While0\n" +
                            "\tDone0:"));

//            while (i <30)
//            {
//                if (i < 25)
//                    if (i > 20) Console.WriteLine("if if");
//                    else Console.WriteLine("if if else");
//                else Console.WriteLine("if (if) else");
//                i =i + 1;
//            }
            assertTrue(content
            .contains("While3:\n" +
                    "\tiload 1\n" +
                    "\ti2d\n" +
                    "\tldc 30 \n" +
                    "\ti2d\n" +
                    "\tdcmpg\n" +
                    "\tiflt Else3\n" +
                    "\ticonst_0\n" +
                    "\tgoto L3\n" +
                    "\tElse3:\n" +
                    "\ticonst_1\n" +
                    "\tL3:\n" +
                    "\tifeq Done3\n" +
                    "\tiload 1\n" +
                    "\ti2d\n" +
                    "\tldc 25 \n" +
                    "\ti2d\n" +
                    "\tdcmpg\n" +
                    "\tiflt Else4\n" +
                    "\ticonst_0\n" +
                    "\tgoto L4\n" +
                    "\tElse4:\n" +
                    "\ticonst_1\n" +
                    "\tL4:\n" +
                    "\tifeq Else5\n" +
                    "\tiload 1\n" +
                    "\ti2d\n" +
                    "\tldc 20 \n" +
                    "\ti2d\n" +
                    "\tdcmpg\n" +
                    "\tifgt Else6\n" +
                    "\ticonst_0\n" +
                    "\tgoto L6\n" +
                    "\tElse6:\n" +
                    "\ticonst_1\n" +
                    "\tL6:\n" +
                    "\tifeq Else7\n" +
                    "\tgetstatic java/lang/System/out Ljava/io/PrintStream;\n" +
                    "\tldc \"if if\" \n" +
                    "\tinvokevirtual java/io/PrintStream/println(Ljava/lang/String;)V\n" +
                    "\tgoto L7\n" +
                    "\tElse7:\n" +
                    "\tgetstatic java/lang/System/out Ljava/io/PrintStream;\n" +
                    "\tldc \"if if else\" \n" +
                    "\tinvokevirtual java/io/PrintStream/println(Ljava/lang/String;)V\n" +
                    "\tL7:\n" +
                    "\tgoto L5\n" +
                    "\tElse5:\n" +
                    "\tgetstatic java/lang/System/out Ljava/io/PrintStream;\n" +
                    "\tldc \"if (if) else\" \n" +
                    "\tinvokevirtual java/io/PrintStream/println(Ljava/lang/String;)V\n" +
                    "\tL5:\n" +
                    "\tiload 1\n" +
                    "\tldc 1 \n" +
                    "\tiadd\n" +
                    "\tistore 1\n" +
                    "\tgoto While3\n" +
                    "\tDone3:"));


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}