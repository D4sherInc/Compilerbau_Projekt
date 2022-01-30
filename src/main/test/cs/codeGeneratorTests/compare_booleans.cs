using System;

namespace HelloWorld
{
    class Program
    {
        static void Main(string[] args)
        {
            bool a = true;
            bool b = false;
            bool c = a && b;
            bool d = a || false;
            Console.WriteLine(c);
            Console.WriteLine(d);
        }
    }
}