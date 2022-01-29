using System;

namespace HelloWorld
{
    class Program
    {
        static void Main(string[] args)
        {
            int a = 3;
            int b = 9;
            int c = add(a,b);
            Console.WriteLine(c);
        }

        static int add(int x, int y)
        {
            return x + y;
        }

    }
}