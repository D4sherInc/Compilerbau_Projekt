using System;

namespace HelloWorld
{
    class Program
    {
        static void Main(string[] args)
        {
            int i = 3;

            // short if
            if (i == 3)
            {
                Console.WriteLine("i is 3");
            }

            // short if with parantheses
            if (i == 3) Console.WriteLine("i is still 3");

            i = i + 1;

            //short if with else
            if (i < 5) Console.WriteLine("i is still less than 5");
            else Console.WriteLine("i is now atleast 5");

            i = 6;

            // short if into short if with else with else
            if (i != 5)
                if (i == 4) Console.WriteLine("if1 if2 true");
                else Console.WriteLine("if1 if2 false");
            else Console.WriteLine("if1 false");
        }
    }
}