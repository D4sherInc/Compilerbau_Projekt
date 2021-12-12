using System;

namespace HelloWorld
{
    class Program
    {
        static void Main(string[] args)
        {
            // this is a one line comment
            Console.WriteLine(calc(3));

            /*
            this is a block comment
            */
        }

        static string calc(double number)
        {
            int counter = 0;
            while (counter < 5) {
                Console.WriteLine(counter);
                counter = counter + 1;
            }
            // short while
            while (counter < 10) Console.WriteLine("short while");

            // short if short else
            if (false) Console.WriteLine("short if short else yes");
            else Console.WriteLine("short if short else no");

            // short if long else
            if (false) Console.WriteLine("short if long else yes");
            else {
                counter = counter + 2;
                Console.WriteLine("short if long else no");
            }
            // long if
            if (true) {
                counter = counter + 4;
				Console.WriteLine("long if yes");
            }

            // long if short else
            if (false)
            {
                counter = counter + 4;
				Console.WriteLine("long if short else yes");
            }
            else Console.WriteLine("long if short else no");

			return("done"); &
		}
    }
}