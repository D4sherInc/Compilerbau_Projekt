using System;

public class Program
{
	public void Main()
	{
		// this is a one line comment
		Console.WriteLine(calc(3));
		/*
		this is a block comment
		*/
	}

	public static double calc (int integer)
	{
		int i = (3+5);
		return (integer + i);
	}
}
