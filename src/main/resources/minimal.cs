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
            return("7");
        }
    }
}
