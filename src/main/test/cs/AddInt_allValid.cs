using System;

namespace HelloWorld
{
    class Program
    {
        static void Main(string[] args)
        {
            int a;
            a = 9;
            int b = 0;
            a = b;
            int c = 0.1;
            int d = b + 4;
            int e = 5 + c;
            int f = d + e;
            int g = 1 * 2;
            int h = 1.0 * 2;
            int i = 2 * 5.1;
            int j = 1 % 3;
            int k = 8 / 7;
            int l = add(3, 4);
            int m = add(3.0, 4);
            int n = add(3, 5.8);
            int o = add(7.7, 4.6);
        }

        static int add(int x, int y)
        {
            return x + y;
        }
    }
}