import lexer.LexerException;
import node.Start;
import parser.ParserException;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class StupsCompiler {
    public static void main(String[] args) throws IOException, LexerException, ParserException {

        switch(args[0]) {
            case ("-compile"):

                System.out.println("start compiling:\n");

                // java StupsCompiler <filepath>
                // java StupsCompiler minimal.cs
                Path path_to_file = Paths.get(args[1]);
                // java StupsCompiler <filepath>
                // java StupsCompiler minimal.cs

                String filename = path_to_file.toString();
                filename = filename.substring(filename.lastIndexOf("/")+1);

                // if wrong path: IOException in StupsLexer
                StupsLexer stupsLexer = new StupsLexer(path_to_file);
                stupsLexer.lex();

                StupsParser stupsParser = new StupsParser(path_to_file);
                Start tree = stupsParser.parse();

                StupsTypeChecker stupsTypeChecker = new StupsTypeChecker(tree);
                stupsTypeChecker.typechecking();
                SymbolTable st = stupsTypeChecker.getSymbolTable();

                // create jasmine file
                CodeGenerator jasmineMaker = new CodeGenerator(st, tree, filename);
                File jasmin = jasmineMaker.getJasmin();

        //        Scanner input = new Scanner(jasmin);
        //        System.out.println("\n\nGenerated Jasmin file: \n");
        //        while(input.hasNextLine()){
        //            System.out.println(input.nextLine());
        //        }

            break;

            case ("-liveness"):
                System.out.println("performing liveness analysis:");
                //TODO: implement liveness analysis
                break;

            default:
                System.out.println("missing option '-compile' or '-liveness'");
        }
    }
}