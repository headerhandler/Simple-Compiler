package com.compiler.arithmetic;
import com.compiler.main.SimpletronSimulatorFile;
import com.compiler.misc.Stack;
import com.compiler.parser.ConvertionToSML;
import com.compiler.storage.TableEntry;

public class PostfixWithHook
{
    private static Stack<Integer> stack = new Stack<Integer>(); // stack used in calculation
    private static int x, y, currentPosition, prec; // variables used in calculation with operators
    private static String[] tokens;
    public static void evaluatePostfix(String value)
    {
        if (value.split(" ").length == 1)
            value += " 0 + )"; // prevent redundancy and optimize code to support single value assignment i.e 'let x = 32' or let x = z
        else
            value += " )"; // append a right bracket to string passed in
        tokens = value.split(" "); // split into tokens
        currentPosition = -1; // counter to loop through value from left to right
        outer: while (true) // labelled while loop
        {
            String current = tokens[++currentPosition]; // current token being processed
            switch(current)
            {
                case ")":
                    ConvertionToSML.printer.printf("%02d %s\n", ConvertionToSML.smlCountFront++, 21*1000+ ConvertionToSML.letLocate + "");
                    break outer;
                default:
                    if (isOperator(current)) // if an operator is encountered pop elements and calculate
                    {
                        y = stack.pop();
                        x = stack.pop();
                        stack.push(evaluate(current));
                    }
                    else if (isNumber(current)) // parse numbers encountered
                    {
                        double cur = Double.parseDouble(current);
                        if (!ConvertionToSML.symbol.searchConstant(cur))
                        {
                            ConvertionToSML.symbol.addEntry(new TableEntry(cur, 'C', --ConvertionToSML.smlCountBack)); // add multi-digit number as a constant
                            SimpletronSimulatorFile.addConstant(ConvertionToSML.smlCountBack, cur);
                            stack.push(ConvertionToSML.smlCountBack);
                        }
                        else
                            stack.push(ConvertionToSML.symbol.getLocationCon(cur));
                    }
                    else if (isLetter(current))
                    {
                        char cur = current.charAt(0);
                        if(!ConvertionToSML.symbol.searchKey(cur))
                        {
                            ConvertionToSML.symbol.addEntry(new TableEntry((int)value.charAt(0), 'V', --ConvertionToSML.smlCountBack)); // add variable to symbol array
                            stack.push(ConvertionToSML.smlCountBack);
                        }
                        else
                            stack.push(ConvertionToSML.symbol.getLocationVar((int)cur));
                    }
                    else if (isArrayElement(current))
                    {
                        int p = 1;
                        String k = "";
                        while (true)
                        {
                            if (current.charAt(++p) != ']')
                                k += Character.toString(current.charAt(p));
                            else
                                break;
                        }
                        p = Integer.parseInt(k);
                        stack.push(ConvertionToSML.symbol.getLocationVar((int)current.charAt(0)) + ConvertionToSML.arrayLength - p); // get position of array index
                    }
                    else
                    {
                        System.out.println("Invalid character in Postfix string");
                        return;
                        //System.exit(1);
                    }
                    break;
            }
        } 
    }
    public static int evaluate(String b)
    {
        int evaluation = 0;
        switch(b)
        {
            case "+":
                if (prec != x) // check redundancy status
                    ConvertionToSML.printer.printf("%02d %s\n", ConvertionToSML.smlCountFront++, 20*1000+x+"");
                ConvertionToSML.printer.printf("%02d %s\n", ConvertionToSML.smlCountFront++, 30*1000+y+"");
                if ((!tokens[currentPosition+1].equals(")")) && (!isOperator(tokens[currentPosition+2]))) // avoid redundancy
                    ConvertionToSML.printer.printf("%02d %s\n", ConvertionToSML.smlCountFront++, 21*1000+--ConvertionToSML.smlCountBack+"");
                else
                    prec = ConvertionToSML.smlCountBack;
                evaluation = ConvertionToSML.smlCountBack;
                break;
            case "-":
                if (prec != x) // check redundancy status
                    ConvertionToSML.printer.printf("%02d %s\n", ConvertionToSML.smlCountFront++, 20*1000+x+"");
                ConvertionToSML.printer.printf("%02d %s\n", ConvertionToSML.smlCountFront++, 31*1000+y+"");
                if ((!tokens[currentPosition+1].equals(")")) && (!isOperator(tokens[currentPosition+2]))) // avoid redundancy
                    ConvertionToSML.printer.printf("%02d %s\n", ConvertionToSML.smlCountFront++, 21*1000+--ConvertionToSML.smlCountBack+"");
                else
                    prec = ConvertionToSML.smlCountBack;
                evaluation = ConvertionToSML.smlCountBack;
                break;
            case "*":
                if (prec != x) // check redundancy status
                    ConvertionToSML.printer.printf("%02d %s\n", ConvertionToSML.smlCountFront++, 20*1000+x+"");
                ConvertionToSML.printer.printf("%02d %s\n", ConvertionToSML.smlCountFront++, 33*1000+y+"");
                if ((!tokens[currentPosition+1].equals(")")) && (!isOperator(tokens[currentPosition+2]))) // avoid redundancy
                    ConvertionToSML.printer.printf("%02d %s\n", ConvertionToSML.smlCountFront++, 21*1000+--ConvertionToSML.smlCountBack+"");
                else
                    prec = ConvertionToSML.smlCountBack;
                evaluation = ConvertionToSML.smlCountBack;
                break;
            case "/":
                if (prec != x) // check redundancy status
                    ConvertionToSML.printer.printf("%02d %s\n", ConvertionToSML.smlCountFront++, 20*1000+x+"");
                ConvertionToSML.printer.printf("%02d %s\n", ConvertionToSML.smlCountFront++, 32*1000+y+"");
                if ((!tokens[currentPosition+1].equals(")")) && (!isOperator(tokens[currentPosition+2]))) // avoid redundancy
                    ConvertionToSML.printer.printf("%02d %s\n", ConvertionToSML.smlCountFront++, 21*1000+--ConvertionToSML.smlCountBack+"");
                else
                    prec = ConvertionToSML.smlCountBack;
                evaluation = ConvertionToSML.smlCountBack;
                break;
            case "^":
                if (prec != x) // check redundancy status
                    ConvertionToSML.printer.printf("%02d %s\n", ConvertionToSML.smlCountFront++, 20*1000+x+"");
                ConvertionToSML.printer.printf("%02d %s\n", ConvertionToSML.smlCountFront++, 35*1000+y+"");
                if ((!tokens[currentPosition+1].equals(")")) && (!isOperator(tokens[currentPosition+2]))) // avoid redundancy
                    ConvertionToSML.printer.printf("%02d %s\n", ConvertionToSML.smlCountFront++, 21*1000+--ConvertionToSML.smlCountBack+"");
                else
                    prec = ConvertionToSML.smlCountBack;
                evaluation = ConvertionToSML.smlCountBack;
                break;
            case "%":
                if (prec != x) // check redundancy status
                    ConvertionToSML.printer.printf("%02d %s\n", ConvertionToSML.smlCountFront++, 20*1000+x+"");
                ConvertionToSML.printer.printf("%02d %s\n", ConvertionToSML.smlCountFront++, 34*1000+y+"");
                if ((!tokens[currentPosition+1].equals(")")) && (!isOperator(tokens[currentPosition+2]))) // avoid redundancy
                    ConvertionToSML.printer.printf("%02d %s\n", ConvertionToSML.smlCountFront++, 21*1000+--ConvertionToSML.smlCountBack+"");
                else
                    prec = ConvertionToSML.smlCountBack;
                evaluation = ConvertionToSML.smlCountBack;
                break;
            default:
                System.out.println("Invalid arithmetic operator");
                //System.exit(1);
                break;
        }
        return evaluation;
    }
    public static boolean isOperator(String d)
    {
        boolean operator;
        switch(d)
        {
            case "+":
            case "*":
            case "-":
            case "/":
            case "%":
            case "^":
                operator = true;
                break;
            default:
                operator = false;
                break;
        }
        return operator;
    }
    public static boolean isLetter(String val)
    {
        if (val.length() == 1)
        {
            return Character.isLetter(val.charAt(0));
        }
        return false;
    }
    public static boolean isNumber(String D)
    {
        try
        {
            Double.parseDouble(D);
            return true;
        }
        catch(Exception e){}
        return false;
    }
    public static boolean isArrayElement(String D)
    {
        if (D.length() >= 4)
        {
            if (isLetter(Character.toString(D.charAt(0))))
            {
                if (D.charAt(1) == '[')
                {
                    if (isNumber(Character.toString(D.charAt(2))))
                    {
                        if (D.charAt(D.length()-1) == ']')
                            return true;
                    }
                }
            }
        }
        return false;
    }
}