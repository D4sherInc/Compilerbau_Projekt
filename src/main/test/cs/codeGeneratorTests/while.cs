using System;

namespace HelloWorld
{
    class Program
    {
        static void Main(string[] args)
        {
            int i = 0;
            int j;

            //base while
            while (i < 15)
            {
                Console.WriteLine(i);
                i = i + 1;
            }

            //while bool_const (works)

            while (false)
            {
                i = i + 1;
                j = 9;
            }

            //while if-else with nested short if-else
            while (i <30)
            {
                if (i < 25)
                    if (i > 20) Console.WriteLine("if if");
                    else Console.WriteLine("if if else");
                else Console.WriteLine("if (if) else");
                i =i + 1;
            }

            while (i < 55)
            {
                if (i > 52) Console.WriteLine("last steps");
                else Console.WriteLine("a few more steps");
                i = i + 1;
            }

        }
    }
}