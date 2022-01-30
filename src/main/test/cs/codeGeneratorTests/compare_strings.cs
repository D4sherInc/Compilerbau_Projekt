using System;

namespace HelloWorld
{
    class Program
    {
        static void Main(string[] args)
        {
            string s1 = "yes";
            string s2 = "no";
            bool b = s1 == s2;
            bool c = s1 != "no";

            Console.WriteLine(b);
            Console.WriteLine(c);
        }
    }
}