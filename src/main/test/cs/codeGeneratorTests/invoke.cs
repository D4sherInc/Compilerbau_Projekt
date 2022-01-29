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
            string st = AssignStringFunctionCall("333");
            double e = AssignDoubleFunctionCall(2.9);
            int i = AssignIntFunctionCall(7);
            string j = AssignStringFunctionCall("teststring");

            //CallInIf
            int k = 1;
            if (i == 0) k = calledInIf(6);
            else k = calledInElse(3);

            //callInWhile
            while (k < 5)
            {
                k = whilePrint(k);
            }

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

        //AssignStringFunctionCall
        static string AssignStringFunctionCall(string s)
        {
            return s;
        }

        //AssignDoubleFunctionCall
        static double AssignDoubleFunctionCall(double d)
        {
            return d;
        }

        //AssignIntFunctionCall
        static int AssignIntFunctionCall(int i)
        {
            return i;
        }

        //CallInIf
        static int calledInIf(int u)
        {
            return u;
        }

        static int calledInElse(int v)
        {
            return v;
        }

        //callInWhile
        static int whilePrint(int k)
        {
            Console.WriteLine(k);
            return k + 1;
        }
    }
}