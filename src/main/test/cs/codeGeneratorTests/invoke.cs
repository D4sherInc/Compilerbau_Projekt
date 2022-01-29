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
            bool d = allTypes(1, 2.0, true, "all works");
        }

        static int add(int x, int y)
        {
            return x + y;
        }

        static bool allTypes(int i, double d, bool b, string s)
        {
            Console.WriteLine(i);
            Console.WriteLine(d);
            Console.WriteLine(b);
            Console.WriteLine(s);
            return true;
        }

    }
}