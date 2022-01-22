package helpingstuff;

import java.io.File;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.Scanner;

import parser.Parser;
import parser.ParserException;

import lexer.Lexer;
import lexer.LexerException;
import node.Start;

public class MainProgramPrinter {


	public static void main(String[] args) throws
			LexerException, IOException, ParserException {

		String input = new Scanner(new File("src/main/test/cs/AddInt2.cs")).useDelimiter("\\Z").next();
		parse(input);

		//Sally false *(true + false)
		//Sally 33 ok+7 ok *(5 ok +1 ok )
		//New intro1.start(name.name1,expr.expra) 33 ok+7 ok *(5 ok +1 ok )
	}

	private static void parse(String input) throws ParserException,
			LexerException, IOException {
		StringReader reader = new StringReader(input);
		PushbackReader r = new PushbackReader(reader, 100);
		Lexer l = new Lexer(r);
		Parser parser = new Parser(l);
		Start start = parser.parse();
		ASTPrinter printer = new ASTPrinter();
		start.apply(printer);
	}
}