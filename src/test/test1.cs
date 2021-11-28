using System;

namespace HelloWorld
{
    public class Program
    {
        public static void Main(string[] args)
        {
            int i1 = 1, i2 = 22, i3, i, number;
            Console.WriteLine("this is a test String");
            // this is a test comment with test = successful?
            Console.WriteLine(i1+" "+i2+" "); //printing i0 and i1
            number = 10;
			for (i=2; i<number; ++i) //loop starts from 2 because 0 and 1 are printed
            /*
            this is a
            test block comment
           */
            {
            i3 = i1+i2;
            Console.WriteLine(i3+" ");
            i1=i2;
            i2=i3;
            }
        }
    }
}