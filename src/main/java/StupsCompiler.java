import lexer.Lexer;
import lexer.LexerException;
import node.EOF;
import node.Start;
import node.TWhitespace;
import node.Token;
import parser.Parser;
import parser.ParserException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class StupsCompiler {
    public static void main(String[] args) throws IOException, LexerException, ParserException {
        // java StupsCompiler <filepath>
        // java StupsCompiler minimal.cs
        Path path_to_file = Paths.get(args[0]);
        List<String> inputAsList;

        // try-catch for IOException: wrong filepath
        try{
            inputAsList = Files.lines(path_to_file).collect(Collectors.toList());
        }
        catch (IOException e) {
            System.err.println("ERROR: no such file found, try another path. Path was: " + path_to_file);
            return;
        }

        String input = new Scanner(new File(String.valueOf(path_to_file))).useDelimiter("\\Z").next();

        boolean success = lex(inputAsList);
        if (success) parse(input);
    }

    private static boolean lex(List<String> input) throws IOException {
        boolean lexErrorFound = false;
        int lineCounter = 1;
        for (String line : input) {
            StringReader reader = new StringReader(line);
            PushbackReader r = new PushbackReader(reader);
            Lexer l = new Lexer(r);
            Token token;
            do {

                // try-catch for lexical error: token not found --> print "unknown token: 't'"
                // add Whitespace token (gets ignored), --> keep going
                try{
                    token = l.next();
                }
                catch (LexerException e) {
                    System.err.printf("ERROR ON LINE %d:%s%n" ,lineCounter, e.getMessage().substring(e.getMessage().indexOf("]") + 1));
                    System.err.println(e.getMessage());
                    token = new TWhitespace(" ");
                    lexErrorFound = true;
                }
            } while (!(token instanceof EOF));
            lineCounter++;
        }

        if (!lexErrorFound) System.out.println("lexing successful");
        return !lexErrorFound;
    }

    private static void parse(String input) throws IOException, ParserException, LexerException, TypeCheckerException {
            StringReader reader = new StringReader(input);
            PushbackReader r = new PushbackReader(reader, 100);
            Lexer l = new Lexer(r);
            Parser parser = new Parser(l);

            boolean parseErrorFound = false;
            boolean typeCheckErrorFound = false;

            try {
                Start tree = parser.parse();

                SymbolTable symbolTable = new SymbolTable();
                TypeChecker typeChecker = new TypeChecker(symbolTable);
                tree.apply(typeChecker);

            } catch (ParserException e) {
                System.err.printf("LINE %d: found '%s', expected: %s%n", e.getToken().getLine(), e.getToken().getText(), e.getMessage().substring(e.getMessage().indexOf('\'')));
                parseErrorFound = true;
                System.exit(1);
            } catch (TypeCheckerException | SymbolTableException e) {
                System.err.println(e.getMessage());
                typeCheckErrorFound = true;
            }

        if (!parseErrorFound) System.out.println("parsing successful");
        if (!typeCheckErrorFound) System.out.println("typecheck successful");
    }

}