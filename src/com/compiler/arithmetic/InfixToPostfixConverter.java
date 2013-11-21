package com.compiler.arithmetic;
import com.compiler.misc.*;
import java.util.Scanner;
public class InfixToPostfixConverter
{
    private static Stack<Character> stack; // stack variable to help in conversion
    private static final char[][] precede = {{'+', '-'},{'*', '^', '/', '%'}}; // array ordering precedence
    public static void main(String[] args)
    {
        Scanner input = new Scanner(System.in);
        System.out.printf("\n%s", convertToPostfix(new String(input.nextLine()))); // convert input to postfix
    }
    public static String convertToPostfix(String param)
    {
        String[] characters = param.split(" ");
        String param2 = "";
        for (String string : characters)
            param2 += string;
        StringBuilder postfix = new StringBuilder(15); // postfix string
        stack = new Stack<Character>();
        StringBuilder infix = new StringBuilder(param2); // convert argument to stringbuilder
        infix.append(')'); // a step in the shunting yard algorithm
        char[] charArray = new String(infix).toCharArray(); // get characters of infix string
        int infixLoop = -1; // counter for looping through elements of infix from left to right
        stack.push('('); // another step in the shunting yard algorithm
        while (!stack.isEmpty()) // loop until all elements of stack have been popped
        {
            char current = charArray[++infixLoop]; // current character being processed
            switch(current)
            {
                case '(':
                    stack.push(current);
                    break;
                case ')': 
                    while (stack.peek() != '(')
                    { 
                        if (isOperator(stack.peek())) // corresponding space to be placed after operator
                            postfix.append(' ');
                        postfix.append(stack.pop());
                    }
                    stack.pop();
                    break;
                default:
                    if (isOperator(current))
                    {
                        while (isOperator(stack.peek())) // pops operators with higher precedence first
                        {
                            char de = precedence(stack.peek(), current); // precedence method
                            if (de == stack.peek())
                            {
                                postfix.append(' '); // corresponding space to be placed before operator
                                postfix.append(stack.pop());
                            }
                            else
                                break;
                        }
                        postfix.append(' '); // space placed in postfix string
                        stack.push(current); // push operator after popping operators with higher precedence, if any
                    }
                    else if ((Character.isDigit(current)) | current == '.') // if current character is a digit or dot
                        postfix.append(current);
                    else if ((Character.isLetter(current)))
                        postfix.append(current);
                    else if (current == '[' | current == ']')
                        postfix.append(current);
                    else
                    {
                        System.out.println("Invalid Character Command" + " ----->  " + current);
                        //System.exit(1);
                    }
                    break;
            }
        } 
        return new String(postfix);
    }
    public static boolean isOperator(char d) // determine if it's an operator
    {
        boolean operator;
        switch(d)
        {
            case '+':
            case '*':
            case '-':
            case '/':
            case '%':
            case '^':
                operator = true;
                break;
            default:
                operator = false;
                break;
        }
        return operator;
    }
    public static char precedence(char b, char c) // determine precedence by checking position in precedence array
    {
        int bRow = 0;
        int cRow = 0;
        for (int counter = 0; counter < precede.length; counter++)
        {
            for (int count = 0; count < precede[counter].length; count++)
            {
                if (b == precede[counter][count])
                    bRow = counter;
                if (c == precede[counter][count])
                    cRow = counter;
            }
        }
        if (bRow >= cRow)
            return b;
        return c;
    }
}