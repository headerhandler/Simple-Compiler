package com.compiler.parser;

import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.compiler.arithmetic.InfixToPostfixConverter;
import com.compiler.arithmetic.PostfixWithHook;
import com.compiler.main.FileString;
import com.compiler.main.SimpletronSimulatorFile;
import com.compiler.misc.ErrorStatement;
import com.compiler.storage.SymbolTable;
import com.compiler.storage.TableEntry;

import java.io.PrintStream;
public class ConvertionToSML
{
    public static SymbolTable symbol = new SymbolTable(); // symbol table object
    public static File fileUsed; // file containing simple instructions
    public static int smlCountFront;
    public static int arrayLength;
    public static int smlCountBack;
    public static int smlCountMiddle;
    private static int forReal;
    private static char forVariable;
    private static double forReal2, forReal3;
    public static int[] flags = new int[1000];
    private static Scanner input;
    public static int letLocate, goSubLocate = 0;
    public static int temp0;
    public static SimpletronSimulatorFile simulator;
    public static PrintStream printer, printe;
    public static File firstPass;
    private static ErrorStatement errorVal = new ErrorStatement();
    public static ArrayList<Integer> lineNumbers = new ArrayList<Integer>();
    public static ArrayList<String> stringVar = new ArrayList<String>(); // arraylist to store lines of string in simple file
    private static String[] stringTokens; // arraylist to store tokens 
    public static File getSMLFile()
    {
        return firstPass;
    }
    public static SimpletronSimulatorFile getSimulator()
    {
        return simulator;
    }
    public static void firstPass(String param)
    {
        try
        {
            firstPass =  File.createTempFile("pref", ".tmp"); 
            simulator = new SimpletronSimulatorFile(firstPass);
            printer =  new PrintStream(firstPass); // printstream for file generated for sml commands
            param = param.trim();
            input = new Scanner(param); // scanner to loop through simple file
        }
        catch (Exception scanExcept)
        {
            JOptionPane.showMessageDialog(null, "Error initializing files used in the compilation process");
        }
        finally
        {
            smlCountFront = 0; // counter for sml line number
            smlCountBack = 1000; // counter for variables
            smlCountMiddle = 300;
        }
        for (int counter = 0; counter < 1000; counter++) // initialize elements of flags array to zero
            flags[counter] = -1;
        while (input.hasNext())
        {
            String nextLine = input.nextLine();
            char[] nextChars = nextLine.toCharArray();
            for (int cd = 0; cd < nextChars.length; cd++)
            {
                if (Character.isUpperCase(nextChars[cd]))
                    nextChars[cd] = Character.toLowerCase(nextChars[cd]);
            }
            nextLine = new String(nextChars);
            stringVar.add(nextLine); // read lines and store as strings in stringVar array 
        }
        for (int str = 0; str < stringVar.size(); str++)
        {
            temp0 = str;
            stringTokens = stringVar.get(str).split("\\s");
            ArrayList<String> stringTok = new ArrayList<String>();
            int mar = 0;
            for (String string : stringTokens)
            {
                if (!string.equals(""))
                {
                    stringTok.add(string);
                    mar++;
                }
            }
            stringTokens = new String[mar];
            stringTokens = stringTok.toArray(stringTokens);
            processAdd(stringTokens);
        }
        printer.close();
        secondPass();
        simulator.startSML();
    }
    public static void secondPass()
    {
        ArrayList<String> fileList = new ArrayList<String>(); // arraylist to store instructions
        for (int counter = 0; counter < 1000; counter++) // loop through elements of flags array
        {
            fileList.clear(); 
            if (flags[counter] != -1) // if value needs to be changed
            {
                try
                {   
                    input = new Scanner(firstPass); // scanner to loop through simple file
                }
                catch (Exception scanExcept)
                {
                    scanExcept.printStackTrace();
                }
                while (input.hasNext())
                {
                    String next = input.nextLine(); // read lines and store as strings
                    if (Integer.parseInt(next.split(" ")[0]) != counter)
                    {
                        fileList.add(next);
                    }
                    else // if value needs to be modified
                    {
                        next = String.format("%02d %s", Integer.parseInt(next.split(" ")[0]),
                           Integer.parseInt(next.split(" ")[1])/1000 * 1000 + symbol.getLocation(flags[counter]) + ""); // modify goto position to move to location
                        fileList.add(next);
                    }
                }
                printer.close();
                try{
                    printe = new PrintStream(firstPass);}
                catch(Exception er)
                {
                    System.out.println("er error");
                }
                for (int nter = 0; nter < fileList.size(); nter++) // loop through elements of arraylist and print to file stream
                {
                    if (nter == fileList.size() - 1)
                        printe.print(fileList.get(nter));
                    else
                        printe.println(fileList.get(nter));
                }
                printe.close();
            }
        }
    }
    public static void processAdd(String[] stringTokens)
    {
        for (int counter = 0; counter < 2; counter++) // pick first two tokens
        {
            int line;
            if (counter == 0)
            {
                try
                {
                    line = Integer.parseInt(stringTokens[counter]);
                    if (!symbol.searchKey(line)) // search symbol entry for existence
                        symbol.addEntry(new TableEntry(line, 'L', smlCountFront));
                    lineNumbers.add(line);
                }
                catch(Exception error)
                {
                    errorVal.lineError();
                }
            }
            else
            {
                switch(stringTokens[counter])
                {
                    case "rem":
                        break;
                    case "input":
                        String tem = "";
                        for (int cds = counter+1; cds < stringTokens.length; cds++)
                            tem += stringTokens[cds];
                        if (tem.equals(""))
                            errorVal.variableError(false, 'c');
                        char[] temArray = tem.toCharArray();
                        //for (int cds = 0; cds < temArray.length; cds=cds+2)
                        int cda = 0;
                        while (cda < temArray.length)
                        {
                            try
                            {
                                if ((Character.isLetter(temArray[cda]) && temArray[cda+1] == ','))
                                {
                                    if (!symbol.searchKey(temArray[cda])) // variable after input
                                    {
                                        symbol.addEntry(new TableEntry((int)temArray[cda], 'V', --smlCountBack)); // add variable to symbol entry
                                        printer.printf("%02d %s\n", smlCountFront++, 10*1000 + smlCountBack + "");
                                        cda = cda+2;
                                    }
                                    else
                                    {
                                        printer.printf("%02d %s\n", smlCountFront++, 10*1000 + 
                                                       symbol.getLocationVar((int)temArray[cda]) + "");
                                        cda = cda+2;
                                    }
                                }
                                else if (Character.isLetter(temArray[cda]) & (Character.isLetter(temArray[cda+1])
                                                                             | !Character.isLetter(temArray[cda+1])))
                                    errorVal.variableError(false, temArray[cda]);
                                    
                                else if (temArray[cda] == '(' & temArray[cda+1] == 's' & temArray[cda+2] == ')'
                                             & Character.isLetter(temArray[cda+3]) & temArray[cda+4] == ',')
                                {
                                    if (!symbol.searchKey(temArray[cda+3])) // variable after input
                                    {
                                        smlCountMiddle += 50;
                                        symbol.addEntry(new TableEntry((int)temArray[cda+3], 'S', smlCountMiddle)); // add variable to symbol entry
                                        printer.printf("%02d %s\n", smlCountFront++, 51*1000 + smlCountMiddle + "");
                                        cda = cda+5;
                                    }
                                    else
                                    {
                                        printer.printf("%02d %s\n", smlCountFront++, 51*1000 + 
                                                       symbol.getLocationVar((int)temArray[cda]) + "");
                                        cda = cda+5;
                                    }
                                }
                                else
                                    errorVal.variableError(false, temArray[cda]);
                            }
                            catch (ArrayIndexOutOfBoundsException errore)
                            {
                                if ((Character.isLetter(temArray[cda])) && temArray.length == cda+1)
                                {
                                    if (!symbol.searchKey(temArray[cda])) // variable after input
                                    {
                                        symbol.addEntry(new TableEntry((int)temArray[cda], 'V', --smlCountBack)); // add variable to symbol entry
                                        printer.printf("%02d %s\n", smlCountFront++, 10*1000 + smlCountBack + "");
                                        cda = cda + 2;
                                    }
                                    else
                                    {
                                        printer.printf("%02d %s\n", smlCountFront++, 10*1000 + 
                                                       symbol.getLocationVar((int)temArray[cda]) + "");
                                        cda = cda+2;
                                    }
                                }
                                else if (!Character.isLetter(temArray[cda]) & temArray[cda] != '(')
                                    errorVal.variableError(false, temArray[cda]);
                                else if (temArray[cda] == '(' & temArray[cda+1] == 's' & temArray[cda+2] == ')')
                                {
                                    if (!symbol.searchKey(temArray[cda+3])) // variable after input
                                    {
                                        smlCountMiddle += 50;
                                        symbol.addEntry(new TableEntry((int)temArray[cda+3], 'S', smlCountMiddle)); // add variable to symbol entry
                                        printer.printf("%02d %s\n", smlCountFront++, 51*1000 + smlCountMiddle + "");
                                        cda = cda + 5;
                                    }
                                    else
                                    {
                                        printer.printf("%02d %s\n", smlCountFront++, 51*1000 + 
                                                       symbol.getLocationVar((int)temArray[cda]) + "");
                                        cda = cda+5;
                                    }
                                }
                                else
                                    errorVal.variableError(false, temArray[cda]);
                            }
                        }
                        break;
                    case "print":
                        String tem2 = "";
                        for (int cds = counter+1; cds < stringTokens.length; cds++)
                            tem2 += stringTokens[cds];
                        if (tem2.equals(""))
                            errorVal.variableError(false, 'c');
                        char[] temArray2 = tem2.toCharArray();                    
                        int cds = 0;
                        while (cds < temArray2.length)
                        {
                            try
                            {
                                if ((Character.isLetter(temArray2[cds]) && temArray2[cds+1] == ','))
                                {
                                    if (symbol.searchKey(temArray2[cds]))
                                    {
                                        printer.printf("%02d %s\n", smlCountFront++, 11*1000 + 
                                                       symbol.getLocationVar((int)temArray2[cds]) + "");
                                        printer.printf("%02d %s\n", smlCountFront++, 50*1000 + 1); // new line output
                                        cds = cds + 2;
                                    }
                                    else if (symbol.searchString(temArray2[cds])) // variable after print is a string
                                    {
                                        printer.printf("%02d %s\n", smlCountFront++, 52*1000 + symbol.getLocationString((int)temArray2[cds]) + "");
                                        printer.printf("%02d %s\n", smlCountFront++, 50*1000 + 1); // new line output
                                        cds = cds + 2;
                                    }
                                    else
                                        errorVal.variableError(true, temArray2[cds]);
                                }
                                else if (Character.isLetter(temArray2[cds]) & (Character.isLetter(temArray2[cds+1])
                                                                             | !Character.isLetter(temArray2[cds+1])))
                                    errorVal.variableError(false, temArray2[cds]);
                                else
                                    errorVal.variableError(false, temArray2[cds]);
                            }
                            catch(ArrayIndexOutOfBoundsException err)
                            {
                                if ((Character.isLetter(temArray2[cds])) &  temArray2.length == cds+1)
                                {
                                    if (symbol.searchKey(temArray2[cds]))
                                    {
                                        printer.printf("%02d %s\n", smlCountFront++, 11*1000 + 
                                                       symbol.getLocationVar((int)temArray2[cds]) + "");
                                        printer.printf("%02d %s\n", smlCountFront++, 50*1000 + 1); // new line output
                                        cds = cds + 2;
                                    }
                                    else if (symbol.searchString(temArray2[cds])) // variable after input
                                    { 
                                        printer.printf("%02d %s\n", smlCountFront++, 52*1000 + symbol.getLocationString((int)temArray2[cds]) + "");
                                        printer.printf("%02d %s\n", smlCountFront++, 50*1000 + 1); // new line output
                                        cds = cds+2;
                                    }
                                    else
                                        errorVal.variableError(true, temArray2[cds]);
                                }
                                else
                                    errorVal.variableError(false, temArray2[cds]);
                            }
                        }
                        break;
                    case "end":
                        printer.printf("%02d %s\n", smlCountFront++, 43*1000 + "");
                        break;
                    case "if":
                        String[] ifStringToken = stringTokens;
                        processIfOperator(stringTokens[counter+2], stringTokens[counter+5], ifStringToken); // second and last token(after goto)
                        break;
                    case "goto":
                        try{
                        Integer.parseInt(stringTokens[counter+1]);}
                        catch(NumberFormatException e)
                        {
                            JOptionPane.showMessageDialog(null, "Only line numbers are permitted after a goto/gosub command");
                            FileString.restart();
                        }
                        if (!symbol.searchKey(Integer.parseInt(stringTokens[counter+1])))
                        {
                            flags[smlCountFront] = Integer.parseInt(stringTokens[counter+1]);
                            printer.printf("%02d %s\n", smlCountFront++, 40*1000 + ""); // goto position to move to location
                        }
                        else
                            printer.printf("%02d %s\n", smlCountFront++, 40*1000 + symbol.getLocation(Integer.parseInt(stringTokens[counter+1])) + ""); // goto position to move to location for !=
                        break;
                    case "gosub":
                        try{
                        Integer.parseInt(stringTokens[counter+1]);}
                        catch(NumberFormatException e)
                        {
                            JOptionPane.showMessageDialog(null, "Only line numbers are permitted after a goto/gosub command");
                            FileString.restart();
                        }
                        if (!symbol.searchKey(Integer.parseInt(stringTokens[counter+1])))
                        {
                            flags[smlCountFront] = Integer.parseInt(stringTokens[counter+1]);
                            printer.printf("%02d %s\n", smlCountFront++, 40*1000 + ""); // goto position to move to location
                            goSubLocate = smlCountFront;
                        }
                        else
                        {
                            printer.printf("%02d %s\n", smlCountFront++, 40*1000 + 
                              symbol.getLocation(Integer.parseInt(stringTokens[counter+1])) + ""); // goto position to move to location for !=
                            goSubLocate = smlCountFront;
                        }
                        break;
                    case "return":
                        if (goSubLocate != 0)
                        {
                            printer.printf("%02d %s\n", smlCountFront++, 40*1000 + goSubLocate + ""); // goto position to move to statement after gosub
                            goSubLocate = 0;
                        }
                        break;
                    case "for":
                        String forString = "";
                        try
                        {
                            if ((stringTokens[stringTokens.length-2]).equals("step"))
                            {
                                try{
                                    forReal3 = Double.parseDouble(stringTokens[stringTokens.length-1]);}
                                catch(NumberFormatException e) { errorVal.forError(); }
                            }
                            else
                                forReal3 = 1;
                        }
                        catch(Exception e)
                        {
                            errorVal.forError();
                        }
                        try
                        {
                            for (int ta = 2; ta < stringTokens.length; ta++) // worst case scenario( spaces btw for [variable] [=] and initial value
                                forString += stringTokens[ta];
                        }
                        catch(ArrayIndexOutOfBoundsException e)
                        {
                            errorVal.forError();
                        }
                        char[] forArray2 = forString.toCharArray();
                        int forInit = 0, forLimit = 0;
                        for (int b = 1; b < forArray2.length-1; b++)
                        {
                            if (forArray2[b-1] == 't' & forArray2[b] == 'o')
                            {
                                forInit = b-2;
                                forLimit = b+1;
                                break;
                            }
                        }
                        char[] forArray = new char[forInit+1];
                        char[] forLimArray = new char[forArray2.length-forLimit];
                        System.arraycopy(forArray2, 0, forArray, 0,forArray.length); // copy values up to to the 'to' token
                        System.arraycopy(forArray2, forLimit, forLimArray, 0, forLimArray.length); // copy values from starting after the 'to' token
                        double forNum = 0;
                        if (forArray[1] == '=')
                        {
                            String forNum1 = "";
                            for (int d = 2; d < forArray.length;d++)
                                forNum1 += Character.toString(forArray[d]);
                            int pe = -1;
                            String forReal2String = "";
                            try
                            {
                                while (Character.isDigit(forLimArray[++pe]) | forLimArray[pe] == '.')
                                     forReal2String += Character.toString(forLimArray[pe]);
                            }
                            catch(ArrayIndexOutOfBoundsException er){}
                            try
                            {
                                forNum = Double.parseDouble(forNum1);
                                forReal2 = Double.parseDouble(forReal2String);
                            }
                            catch(Exception e)
                            {
                                errorVal.forError();
                            }
                        }
                        else
                            errorVal.forError();
                        if (Character.isLetter(forArray[0]))
                        {
                            forVariable = forArray[0];
                            processAdd(String.format("9200 let %c = %f", forArray[0], forNum).split(" ")); // recursive call to let process
                            forReal = smlCountFront;
                        }
                        else
                            errorVal.forError();
                        break;
                    case "next":
                        processAdd(String.format("9200 let %c = %c+%f", forVariable, forVariable, forReal3).split(" ")); // recursive call to let process
                        processAdd(String.format("9201 if %c <= %f goto %d", forVariable, forReal2, symbol.reverseSearch(forReal)).split(" ")); // recursive call to if process
                        break;
                    case "let":
                        String varType = "";
                        boolean incrementLength = false;
                        for (int let = 2; let < stringTokens.length; let++)
                            varType += stringTokens[let];
                        if (varType.length() < 4)
                        {
                            varType += " arb";  // just to increase length in order to avoid array index out of bounds exception in next if statement
                            incrementLength = true;
                        }
                        char[] letChar = varType.toCharArray();
                        if (Character.isLetter(letChar[0]) & ((letChar[1] == '=') | ((letChar[1] == '[')
                             & (letChar[2] == ']') & (letChar[3] == '='))))
                        {
                            if (letChar[1] == '=')
                            {
                                if (!symbol.searchKey(letChar[0])) // variable after let
                                {
                                    symbol.addEntry(new TableEntry((int)letChar[0], 'V', --smlCountBack)); // add variable to symbol entry
                                    letLocate = smlCountBack;
                                }
                                else
                                    letLocate = symbol.getLocationVar((int)letChar[0]);
                                String infix = ""; // infix variable
                                for (int ta = 2; ta < letChar.length; ta++)
                                {
                                    if (!incrementLength)
                                        infix += Character.toString(letChar[ta]);
                                    else
                                    {
                                        if (!(letChar[ta] == ' ' & letChar[ta+1] == 'a' & letChar[ta+2] == 'r'
                                                & letChar[ta+3] == 'b'))
                                            infix += Character.toString(letChar[ta]);
                                        else
                                            break;
                                    }
                                }
                                char[] stringChar = infix.toCharArray();
                                int ssd = 0;
                                while (ssd < stringChar.length)
                                {
                                    int pe = ssd;
                                    String num = "";
                                    if ((Character.isDigit(stringChar[ssd])) || (stringChar[ssd] == '.'))
                                    {
                                        num += Character.toString(stringChar[ssd]);
                                        try
                                        {
                                            while (Character.isDigit(stringChar[++pe]) | stringChar[pe] == '.')
                                                num += Character.toString(stringChar[pe]);
                                        }
                                        catch(ArrayIndexOutOfBoundsException exs){}
                                        double pj = 0;
                                        try
                                        {
                                            pj = Double.parseDouble(num);
                                        }
                                        catch(Exception e)
                                        {
                                            errorVal.letError();
                                        }
                                        ssd = pe;
                                        if (!symbol.searchConstant(pj))
                                        {
                                            symbol.addEntry(new TableEntry(pj, 'C', --smlCountBack));
                                            SimpletronSimulatorFile.addConstant(smlCountBack, pj);
                                        }
                                    }
                                    else
                                        ssd++;
                                }
                                String postfix = InfixToPostfixConverter.convertToPostfix(infix.trim()); // convert infix to postfix
                                PostfixWithHook.evaluatePostfix(postfix);
                            }
                            else
                            {
                                arrayLength = 0;
                                if (letChar[4] == '{' & letChar[letChar.length-1] == '}')
                                { // process array values
                                    int ssd = 5;
                                    while (ssd != letChar.length-1)
                                    {
                                        int pe = ssd; // start from the fifth character, the first digit of the first element of the array
                                        String num = "";
                                        if ((Character.isDigit(letChar[ssd])) | (letChar[ssd] == '.'))
                                        {
                                            num += Character.toString(letChar[ssd]);
                                            try
                                            {
                                                while (Character.isDigit(letChar[++pe]) | letChar[pe] == '.')
                                                    num += Character.toString(letChar[pe]);
                                            }
                                            catch(ArrayIndexOutOfBoundsException exs){}
                                            double pj = 0;
                                            try
                                            {
                                                pj = Double.parseDouble(num);
                                            }
                                            catch(Exception e)
                                            {
                                                errorVal.letError();
                                            }
                                            ssd = pe;
                                            symbol.addEntry(new TableEntry(pj, 'C', --smlCountBack));
                                            SimpletronSimulatorFile.addConstant(smlCountBack, pj);
                                            arrayLength++;
                                        }
                                        else if (letChar[ssd] == ',')
                                            ++ssd;
                                        else
                                            errorVal.arrayError();
                                    }
                                    if (!symbol.searchKey(letChar[0])) // variable after let
                                    {
                                        symbol.addEntry(new TableEntry((int)letChar[0], 'V', --smlCountBack)); // add variable to symbol entry
                                        SimpletronSimulatorFile.addConstant(smlCountBack, arrayLength);
                                    }
                                }
                                else
                                    errorVal.arrayError();
                            }
                        }
                        else
                            errorVal.letError();
                        break;
                    default:
                        errorVal.statementError();
                        break;
                }
            }
        }
    }
    public static void processIf(String value, int position)
    {
        switch(value.length()) // determine if next value is a variable or a number
        {
            case 1: // if it's a variable or a number with a single digit
                if (Character.isLetter(value.charAt(0))) // if it's a variable
                {
                    if (!symbol.searchKey(value.charAt(0)))
                    {
                        symbol.addEntry(new TableEntry((int)value.charAt(0), 'V', --smlCountBack)); // add variable to symbol array
                        if (position == 1)
                            printer.printf("%02d %s\n", smlCountFront++, 20*1000 + smlCountBack + "");
                        else
                            printer.printf("%02d %s\n", smlCountFront++, 31*1000 + smlCountBack + "");
                    }
                    else
                    {
                        if (position == 1)
                            printer.printf("%02d %s\n", smlCountFront++, 20*1000 +
                                           symbol.getLocationVar(value.charAt(0)) + "");
                        else
                            printer.printf("%02d %s\n", smlCountFront++, 31*1000 +
                                           symbol.getLocationVar(value.charAt(0)) + "");
                    }
                }
                if (Character.isDigit(value.charAt(0))) // if it's a single digit number
                {
                    if (!symbol.searchConstant((int)value.charAt(0)))
                    {
                        symbol.addEntry(new TableEntry((int)value.charAt(0), 'C', --smlCountBack)); // add constant digit to symbol array
                        SimpletronSimulatorFile.addConstant(smlCountBack, (int)value.charAt(0));
                        if (position == 1)
                            printer.printf("%02d %s\n", smlCountFront++, 20*1000 + smlCountBack + "");
                        else
                            printer.printf("%02d %s\n", smlCountFront++, 31*1000 + smlCountBack + "");
                    }
                    else // if constant is already stored in symbol table
                    {
                        int locat = symbol.getLocationCon((double)value.charAt(0));
                        if (position == 1)
                            printer.printf("%02d %s\n", smlCountFront++, 20*1000 + locat + "");
                        else
                            printer.printf("%02d %s\n", smlCountFront++, 31*1000 + locat + "");
                    }
                }
                break;
            default:
                try
                {
                    if (!symbol.searchConstant(Double.parseDouble(value)))
                    {
                        symbol.addEntry(new TableEntry(Double.parseDouble(value), 'C', --smlCountBack)); // add multi-digit number as a constant
                        SimpletronSimulatorFile.addConstant(smlCountBack, Double.parseDouble(value));
                        if (position == 1)
                            printer.printf("%02d %s\n", smlCountFront++, 20*1000 + smlCountBack + "");
                        else
                            printer.printf("%02d %s\n", smlCountFront++, 31*1000 + smlCountBack + "");
                    }
                    else
                    {
                        int locat = symbol.getLocationCon(Double.parseDouble(value));
                        if (position == 1)
                            printer.printf("%02d %s\n", smlCountFront++, 20*1000 + locat + "");
                        else
                            printer.printf("%02d %s\n", smlCountFront++, 31*1000 + locat + "");
                    }
                }
                catch(Exception error)
                {
                    return;
                }
                break;
        }
    }
    public static void processIfOperator(String value, String position, String[] string)
    {
        switch(value)
        {
            case "==":
                processIf(string[2], 1); // first token after if statement
                processIf(string[4], 2); // third token
                if (!symbol.searchKey(Integer.parseInt(position)))
                {
                    flags[smlCountFront] = Integer.parseInt(position);
                    printer.printf("%02d %s\n", smlCountFront++, 42000 + ""); // leave goto position for second pass
                }
                else
                    printer.printf("%02d %s\n", smlCountFront++, 42*1000 + symbol.getLocation(Integer.parseInt(position)) + ""); // add goto position
                break;
            case "<":
                processIf(string[2], 1); // first token after if statement
                processIf(string[4], 2); // third token
                if (!symbol.searchKey(Integer.parseInt(position)))
                {
                    flags[smlCountFront] = Integer.parseInt(position);
                    printer.printf("%02d %s\n", smlCountFront++, 41000 + ""); // leave goto position for second pass
                }
                else
                    printer.printf("%02d %s\n", smlCountFront++, 41*1000 + symbol.getLocation(Integer.parseInt(position)) + ""); // add goto position
                break;
            case ">":
                processIf(string[4], 1); // third token loaded first into memory
                processIf(string[2], 2); // first token loaded second into memory
                if (!symbol.searchKey(Integer.parseInt(position)))
                {
                    flags[smlCountFront] = Integer.parseInt(position);
                    printer.printf("%02d %s\n", smlCountFront++, 41000 + ""); // leave goto position for second pass
                }
                else
                    printer.printf("%02d %s\n", smlCountFront++, 41*1000 + symbol.getLocation(Integer.parseInt(position)) + ""); // add goto position
                break;
            case "<=":
                processIf(string[2], 1); // first token after if statement
                processIf(string[4], 2); // third token
                if (!symbol.searchKey(Integer.parseInt(position)))
                {
                    flags[smlCountFront] = Integer.parseInt(position);
                    printer.printf("%02d %s\n", smlCountFront++, 42000 + ""); // leave goto position for second pass (equal)
                    flags[smlCountFront] = Integer.parseInt(position);
                    printer.printf("%02d %s\n", smlCountFront++, 41000 + ""); // leave goto position for second pass (less than)
                }
                else
                {
                    printer.printf("%02d %s\n", smlCountFront++, 42*1000 + symbol.getLocation(Integer.parseInt(position)) + ""); // add goto position (equal)
                    printer.printf("%02d %s\n", smlCountFront++, 41*1000 + symbol.getLocation(Integer.parseInt(position)) + ""); // add goto position (less than)
                }
                break;
            case ">=":
                processIf(string[4], 1); // third token loaded first into memory
                processIf(string[2], 2); // first token loaded second into memory
                if (!symbol.searchKey(Integer.parseInt(position)))
                {
                    flags[smlCountFront] = Integer.parseInt(position);
                    printer.printf("%02d %s\n", smlCountFront++, 42000 + ""); // leave goto position for second pass (equal)
                    flags[smlCountFront] = Integer.parseInt(position);
                    printer.printf("%02d %s\n", smlCountFront++, 41000 + ""); // leave goto position for second pass (greater than)
                }
                else
                {
                    printer.printf("%02d %s\n", smlCountFront++, 42*1000 + symbol.getLocation(Integer.parseInt(position)) + ""); // add goto position (equal)
                    printer.printf("%02d %s\n", smlCountFront++, 41*1000 + symbol.getLocation(Integer.parseInt(position)) + ""); // add goto position (> than)
                }
                break;
            case "!=": // procedure here is skip one line if first line evaluates to 0, if not goto position required for !=
                processIf(string[2], 1); // first token after if statement
                processIf(string[4], 2); // third token
                if (!symbol.searchKey(Integer.parseInt(position)))
                {
                    printer.printf("%02d %s\n", smlCountFront++, 42*1000 + smlCountFront+1 + ""); // branch to two lines after current line to skip goto in next line
                    flags[smlCountFront] = Integer.parseInt(position);
                    printer.printf("%02d %s\n", smlCountFront++, 40*1000 + ""); // goto position to move to location for !=
                }
                else
                {
                    printer.printf("%02d %s\n", smlCountFront++, 42*1000 + smlCountFront+1 + ""); // branch to two lines after current line to skip goto in next line
                    printer.printf("%02d %s\n", smlCountFront++, 40*1000 + symbol.getLocation(Integer.parseInt(position)) + ""); // goto position to move to location for !=
                }
                break;
            default:
                errorVal.ifError();
                break;
        }
    }
}