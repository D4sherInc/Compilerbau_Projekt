import lexer.LexerException;
import node.Start;
import parser.ParserException;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StupsCompiler {
    public static void main(String[] args) throws IOException, LexerException, ParserException {

        // java StupsCompiler <filepath>
        // java StupsCompiler minimal.cs
        Path path_to_file = Paths.get(args[0]);

        // if wrong path: IOException in StupsLexer
        StupsLexer stupsLexer = new StupsLexer(path_to_file);
        stupsLexer.lex();

        //
        StupsParser stupsParser = new StupsParser(path_to_file);
        Start tree = stupsParser.parse();

        StupsTypeChecker stupsTypeChecker = new StupsTypeChecker(tree);
        stupsTypeChecker.typechecking();
        SymbolTable st = stupsTypeChecker.getSymbolTable();

        //parse(input);

        //TODO:
        // write a visitor to go over the parse tree
        // --> codegenerator

        // create jasmine file
        CodeGenerator jasmineMaker = new CodeGenerator(tree, "jasminCode.j");
        jasmineMaker.generateCode();

    }
}