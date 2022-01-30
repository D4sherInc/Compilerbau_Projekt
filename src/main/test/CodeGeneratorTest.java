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
                    "\t.limit locals 10\n" +
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

    @Test
    public void testInvoke() {
        path_to_file = Path.of(directory  + "/invoke.cs");
        testfile = setUpCodeGenerator(path_to_file);

        assertEquals("invoke.j", testfile.getName());

//        int c = add(a,b);
//        static int add(int x, int y)
//        {
//            return x + y;
//        }
        try {
            String content = Files.readString(Path.of(String.valueOf(testfile)));

            // add(int x, int y)
            String arguments = "iload 1\n\tiload 3";
            String invoke = "invokestatic invoke/add(II)I";

            assertTrue(content.contains(arguments));
            assertTrue(content.contains(invoke));

            assertTrue(content.lastIndexOf(arguments) < content.lastIndexOf(invoke));

            // allTypes(int i, double d, bool b, String s
            String args = "ldc 1 \n\tldc2_w 2.0 \n\ticonst_1\n\tldc \"all works\"";
            String method_call = "invokestatic invoke/allTypes(IDILjava/lang/String;)I";

            assertTrue(content.contains(args));
            assertTrue(content.contains(method_call));

            assertTrue(content.lastIndexOf(args) < content.lastIndexOf(method_call));


            // AssignStringFunctionCall
            String arg_string = "ldc \"333\"";
            String method_call2 = "invokestatic invoke/AssignStringFunctionCall(Ljava/lang/String;)Ljava/lang/String;";

            assertTrue(content.contains(arg_string));
            assertTrue(content.contains(method_call2));

            assertTrue(content.lastIndexOf(arg_string) < content.lastIndexOf(method_call2));

            // AssignDoubleFunctionCall
            String arg_double = "ldc2_w 2.9";
            String method_call3 = "invokestatic invoke/AssignDoubleFunctionCall(D)D";

            assertTrue(content.contains(arg_double));
            assertTrue(content.contains(method_call3));

            assertTrue(content.lastIndexOf(arg_double) < content.lastIndexOf(method_call3));

            // AssignIntFunctionCall
            String arg_int = "ldc 7";
            String method_call4 = "invokestatic invoke/AssignIntFunctionCall(I)I";

            assertTrue(content.contains(arg_int));
            assertTrue(content.contains(method_call4));

            assertTrue(content.lastIndexOf(arg_int) < content.lastIndexOf(method_call4));

            // CallInIf
            String if_label = "ifeq Else1";
            String if_true = "ldc 6 \n\tinvokestatic invoke/calledInIf(I)I\n\tistore";
            String if_true_end_goto = "goto L1";
            String else_label = "Else1:\n\tldc 3 \n\tinvokestatic invoke/calledInElse(I)I\n\tistore";
            String end_label = "L1:";

            assertTrue(content.contains(if_label));
            assertTrue(content.contains(if_true));
            assertTrue(content.contains(if_true_end_goto));
            assertTrue(content.contains(else_label));
            assertTrue(content.contains(end_label));

            assertTrue(content.lastIndexOf(if_label) < content.lastIndexOf(if_true));
            assertTrue(content.lastIndexOf(if_true) < content.lastIndexOf(if_true_end_goto));
            assertTrue(content.lastIndexOf(if_true_end_goto) < content.lastIndexOf(else_label));
            assertTrue(content.lastIndexOf(else_label) < content.lastIndexOf(end_label));

            // CallInWhile
            String while_label = "While2:";
            String while_condition = "ifeq Done2";
            String while_end = "goto While2\nDone2:";

            assertTrue(content.contains(while_label));
            assertTrue(content.contains(while_condition));
            assertTrue(content.contains(while_end));

            assertTrue(content.lastIndexOf(while_label) < content.lastIndexOf(while_condition));
            assertTrue(content.lastIndexOf(while_condition) < content.lastIndexOf(while_end));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFibonacci() {
        path_to_file = Path.of(directory + "/fibonacci.cs");
        testfile = setUpCodeGenerator(path_to_file);

        assertEquals("fibonacci.j", testfile.getName());

        try {
            String content = Files.readString(Path.of(String.valueOf(testfile)));


            String method_call_1 =  "ldc 5 \n\tinvokestatic fibonacci/Fib(I)I";
            String method_call_2 = "ldc 10 \n\tinvokestatic fibonacci/Fib(I)I";
            String recursive_call1 = "\tiload 0\n\tldc 1 \n\tisub\n\tinvokestatic fibonacci/Fib(I)I";
            String recursive_call2 = "\tiload 0\n\tldc 2 \n\tisub\n\tinvokestatic fibonacci/Fib(I)I";

            assertTrue(content.contains(method_call_1));
            assertTrue(content.contains(method_call_2));
            assertTrue(content.contains(recursive_call1));
            assertTrue(content.contains(recursive_call2));

            assertTrue(content.indexOf(method_call_1) < content.indexOf(method_call_2));
            assertTrue(content.indexOf(method_call_2) < content.indexOf(recursive_call1));
            assertTrue(content.indexOf(recursive_call1) < content.indexOf(recursive_call2));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTypecast() {
        path_to_file = Path.of(directory + "/typecast.cs") ;
        setUpCodeGenerator(path_to_file);

        testfile = codeGenerator.getJasmin();

        assertEquals("typecast.j", testfile.getName());

        try {
            String content = Files.readString(Path.of(String.valueOf(testfile)));

            // AssignDoubleToInt
            String load_double = "ldc2_w 2.0";
            String convert_double_to_integer = "d2i";
            String store_double_in_int = "istore ";

            assertTrue(content.contains(load_double));
            assertTrue(content.contains(convert_double_to_integer));
            assertTrue(content.contains(store_double_in_int));

            assertTrue(content.lastIndexOf(load_double) < content.lastIndexOf(convert_double_to_integer));
            assertTrue(content.lastIndexOf(convert_double_to_integer) < content.lastIndexOf(store_double_in_int));

            // AssignIntToDouble
            String load_integer = "ldc 3";
            String convert_integer_to_double = "i2d";
            String store_integer_in_double = "dstore ";

            assertTrue(content.contains(load_integer));
            assertTrue(content.contains(convert_integer_to_double));
            assertTrue(content.contains(store_integer_in_double));

            assertTrue(content.lastIndexOf(load_integer) < content.lastIndexOf(convert_integer_to_double));
            assertTrue(content.lastIndexOf(convert_integer_to_double) < content.lastIndexOf(store_integer_in_double));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testNot() {
        path_to_file = Path.of(directory + "/not.cs") ;
        setUpCodeGenerator(path_to_file);

        testfile = codeGenerator.getJasmin();

        assertEquals("not.j", testfile.getName());

        try {
            String content = Files.readString(Path.of(String.valueOf(testfile)));

            String negation = "ifeq Not0\n\ticonst_0\n\tgoto L0\nNot0:\n\ticonst_1";

            assertTrue(content.contains(negation));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testOr() {
        path_to_file = Path.of(directory + "/or.cs") ;
        setUpCodeGenerator(path_to_file);

        testfile = codeGenerator.getJasmin();

        assertEquals("or.j", testfile.getName());

        try {
            String content = Files.readString(Path.of(String.valueOf(testfile)));

            String or = "iload 1\n\ticonst_0\n\tior";

            assertTrue(content.contains(or));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testAnd() {
        path_to_file = Path.of(directory + "/and.cs") ;
        setUpCodeGenerator(path_to_file);

        testfile = codeGenerator.getJasmin();

        assertEquals("and.j", testfile.getName());

        try {
            String content = Files.readString(Path.of(String.valueOf(testfile)));

            String and = "iload 1\n\ticonst_0\n\tiand";

            assertTrue(content.contains(and));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCompare_strings() {
        path_to_file = Path.of(directory + "/compare_strings.cs");
        setUpCodeGenerator(path_to_file);

        testfile = codeGenerator.getJasmin();

        assertEquals("compare_strings.j", testfile.getName());

        try {
            String content = Files.readString(Path.of(String.valueOf(testfile)));

            String if_equals = "aload 3\n\taload 4\n\tif_acmpeq Else0";
            String if_not_equals = "aload 3\n\tldc \"no\" \n\tif_acmpne Else1";

            assertTrue(content.contains(if_equals));
            assertTrue(content.contains(if_not_equals));

            assertTrue(content.indexOf(if_equals) < content.indexOf(if_not_equals));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Test
    public void testCompare_booleans() {
        path_to_file = Path.of(directory + "/compare_booleans.cs");
        setUpCodeGenerator(path_to_file);

        testfile = codeGenerator.getJasmin();

        assertEquals("compare_booleans.j", testfile.getName());

        try {
            String content = Files.readString(Path.of(String.valueOf(testfile)));

            String if_equals = "iload 1\n\tiload 2\n\tiand";
            String if_not_equals = "iload 1\n\ticonst_0\n\tior";

            assertTrue(content.contains(if_equals));
            assertTrue(content.contains(if_not_equals));

            assertTrue(content.indexOf(if_equals) < content.indexOf(if_not_equals));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}