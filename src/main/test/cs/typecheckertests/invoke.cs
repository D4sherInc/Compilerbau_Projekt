using System;

namespace HelloWorld
{
    class Program
    {
        static void Main(string[] args)
        {
            int i = 3;
            string j = "3.0";
            int k = add(i, j);
        }

        static int add(int x, int y)
        {
            return x + y;
        }
    }
}