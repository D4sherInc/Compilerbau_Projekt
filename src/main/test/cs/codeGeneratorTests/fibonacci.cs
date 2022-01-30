using System;

namespace HelloWorld
{
    class Program
    {
        static void Main(string[] args)
        {
            Console.WriteLine(Fib(5));
            Console.WriteLine(Fib(10));
            int x = FibRec(15);
        }

        static int Fib(int n)
        {
            if (n > 0) {
                if (n <= 2) {
                    return 1;
                } else {
                    return Fib(n - 1) + Fib(n - 2);
                }
            } else {
            return 0;
            }
        }

        static int FibRec(int n)
        {
            int i = 0;
            while (i < n)
            {
                Console.WriteLine(Fib(i));
                i = i + 1;
            }
            return 0;
        }
    }
}