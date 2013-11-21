package com.compiler.main;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JOptionPane;

import com.compiler.parser.ConvertionToSML;
public class SimpletronSimulatorFile
{
    private static String[] memory = new String[1000]; // memory of 1000 objects
    private static int instructionCounter = 0; // initialize variable used to keep track of instructions being executed
    private static double accumulator; 
    private static int enterCount = 0; // counter/index for values saved in memory
    private static int operationCode, operand, instructionRegister; // variables used in execution
    private static ArrayList<Integer> ints = new ArrayList<Integer>(); // used to store line numbers read from sml file
    private static ArrayList<String> strings = new ArrayList<String>(); // used to store instructions read from sml file
    public static boolean dumpTicked = false;
    public static Object stringEnt, valueEnt;
    public static String stringEntered;
    private static PrintStream printer; // printerstream to store output in text file
    private File fileLoad;
    public SimpletronSimulatorFile(File fileLoad)
    {
        try
        {
            printer = new PrintStream(new NullOutputStream());
        } 
        catch(Exception exceptError){}
        for (int counter = 0; counter < 1000; counter++)
            memory[counter] = "0"; // initialize memory objects to 0
        this.fileLoad = fileLoad;
    }
    public static void addConstant(int location, double value)
    {
        memory[location] = value + "";
    }
    public void startSML() // read values from sml file and execute instructions after reaching end of file
    {
        try
        {
            Scanner input = new Scanner(fileLoad);
            while (input.hasNext())
            {
                {
                    ints.add(input.nextInt());
                    strings.add(input.nextLine());
                }
            }            
        }
        catch(Exception error){}
        for (String string : strings)
            memory[enterCount++] = Integer.toHexString(Integer.decode(string.trim())); // convert instructions to hex values
        while (instructionCounter < 1000)
        {
            executeCommand(instructionCounter);
            instructionCounter++;
        }
    }
    public static void executeCommand(int counter)
    {
        instructionRegister = Integer.parseInt(memory[counter], 16); // convert hex value of instruction back to decimal
        operationCode = instructionRegister / 1000; 
        operand = instructionRegister % 1000;
        double valueEntered = 100000;
        switch(operationCode)
        {
            case 0:
                System.out.println("An unknown error");
                instructionCounter = 999;
                break;
            case 10:
                while (valueEntered < -99999 || valueEntered > 99999)
                {
                    valueEnt = JOptionPane.showInputDialog(null, "Enter an integer between -99999 and 99999 >>  ");
                    if (valueEnt == null)
                    {
                        System.out.println("Program execution cancelled");
                        System.out.println("Please restart the application to try again.");
                        instructionCounter = 999;  // to prevent further processing
                        break;
                    }
                    else
                    {
                        try{
                        valueEntered = Double.parseDouble((String)valueEnt);
                        valueEnt = null;
                        }
                        catch(NumberFormatException e)
                        {
                            System.out.println("Please enter a valid number");
                        }
                    }
                }
                memory[operand] = valueEntered + "";
                break;
            case 11:
                System.out.printf("%s", memory[operand]);
                printer.printf("%s", memory[operand]);
                break;
            case 20:
                accumulator = Double.parseDouble(memory[operand]);
                break;
            case 21:
                memory[operand] = accumulator + "";
                break;
            case 30:
                if ((accumulator + Double.parseDouble(memory[operand])) > 99999 || (accumulator + Double.parseDouble(memory[operand])) < -99999)
                {
                    System.out.printf("\n%s\n%s\n", "*** Accumulator Overflow: Value not within number range. ***",
                                      "*** Simpletron execution abnormally terminated ***");
                    printer.printf("\n%s\n%s\n", "*** Accumulator Overflow: Value not within number range. ***",
                                      "*** Simpletron execution abnormally terminated ***");
                    printDump();
                }
                else
                    accumulator = Double.parseDouble(String.format("%.2f", accumulator += Double.parseDouble(memory[operand])));
                break;
            case 31:
                if ((accumulator - Double.parseDouble(memory[operand])) > 99999 || (accumulator - Double.parseDouble(memory[operand])) < -99999)
                {
                    System.out.printf("\n%s\n%s\n", "*** Accumulator Overflow: Value not within number range. ***",
                                      "*** Simpletron execution abnormally terminated ***");
                    printer.printf("\n%s\n%s\n", "*** Accumulator Overflow: Value not within number range. ***",
                                      "*** Simpletron execution abnormally terminated ***");
                    printDump();
                }
                else
                    accumulator = Double.parseDouble(String.format("%.2f", accumulator -= Double.parseDouble(memory[operand])));
                break;
            case 32:
                if (Double.parseDouble(memory[operand]) == 0)
                {
                    System.out.printf("\n%s\n%s\n", "*** Attempt to divide by zero ***",
                                      "*** Simpletron execution abnormally terminated ***");
                    printer.printf("\n%s\n%s\n", "*** Attempt to divide by zero ***",
                                      "*** Simpletron execution abnormally terminated ***");
                    printDump();
                }
                else if ((accumulator / Double.parseDouble(memory[operand])) > 99999 || (accumulator / Double.parseDouble(memory[operand])) < -99999)
                {
                    System.out.printf("\n%s\n%s\n", "*** Accumulator Overflow: Value not within number range. ***",
                                      "*** Simpletron execution abnormally terminated ***");
                    printer.printf("\n%s\n%s\n", "*** Accumulator Overflow: Value not within number range. ***",
                                      "*** Simpletron execution abnormally terminated ***");
                    printDump();
                }
                else
                    accumulator = Double.parseDouble(String.format("%.2f", accumulator /= Double.parseDouble(memory[operand])));
                break;
            case 33:
                if ((accumulator * Double.parseDouble(memory[operand])) > 99999 || (accumulator * Double.parseDouble(memory[operand])) < -99999)
                {
                    System.out.printf("\n%s\n%s\n", "*** Accumulator Overflow: Value not within number range. ***",
                                      "*** Simpletron execution abnormally terminated ***");
                    printer.printf("\n%s\n%s\n", "*** Accumulator Overflow: Value not within number range. ***",
                                      "*** Simpletron execution abnormally terminated ***");
                    printDump();
                }
                else
                {
                    accumulator = Double.parseDouble(String.format("%.2f", accumulator *= Double.parseDouble(memory[operand])));
                }
                break;
            case 34:
                if ((accumulator % Double.parseDouble(memory[operand])) > 99999 || (accumulator % Double.parseDouble(memory[operand])) < -99999)
                {
                    System.out.printf("\n%s\n%s\n", "*** Accumulator Overflow: Value not within number range. ***",
                                      "*** Simpletron execution abnormally terminated ***");
                    printer.printf("\n%s\n%s\n", "*** Accumulator Overflow: Value not within number range. ***",
                                      "*** Simpletron execution abnormally terminated ***");
                    printDump();
                }
                else
                    accumulator = Double.parseDouble(String.format("%.2f", accumulator %= Double.parseDouble(memory[operand])));
                break;
            case 35:
                if (Math.pow(accumulator,Double.parseDouble(memory[operand])) > 99999 || Math.pow(accumulator,Double.parseDouble(memory[operand])) < -99999)
                {
                    System.out.printf("\n%s\n%s\n", "*** Accumulator Overflow: Value not within number range. ***",
                                      "*** Simpletron execution abnormally terminated ***");
                    printer.printf("\n%s\n%s\n", "*** Accumulator Overflow: Value not within number range. ***",
                                      "*** Simpletron execution abnormally terminated ***");
                    printDump();
                }
                else
                    accumulator = Double.parseDouble(String.format("%.2f", accumulator = Math.pow(accumulator,Double.parseDouble(memory[operand]))));
                break;
            case 40:
                instructionCounter = operand-1; // branch to location. 1 is subracted since variable will be incremented by 1
                break;
            case 41:
                if (accumulator < 0)  // branch to location if negative
                    instructionCounter = operand-1;
                break;
            case 42:
                if (accumulator == 0)  // branch to location if equal
                    instructionCounter = operand-1;
                break;
            case 43:
                System.out.println("\n\n*** Simpletron execution terminated ***");
                printer.println("\n\n*** Simpletron execution terminated ***");
                printer.close();
                printDump();
                System.out.println("\nPlease restart the application to try again.");
                instructionCounter = 999;  // to prevent further processing
                break;
            case 50: // new line, value after 50 (operand) specifies number of lines, max is 999
                for (int ter = 0; ter < operand; ter++)
                System.out.print("\n");
                printer.print("\n");
                break;
            case 51: // handle string input
                storeString();
                break;
            case 52: // display string starting from operand position
                String b = displayString();
                System.out.printf("\n%s\n", b);
                printer.printf("\n%s\n", b);
                break;
            default:
                System.out.println("Invalid operation code");
                printer.println("Invalid operation code");
                printDump();
                break;
        }
    }
    public static void printDump()  // dump print
    {
        if (dumpTicked)
        {
            System.out.println("\nREGISTERS:");
            printer.println("\nREGISTERS:");
            System.out.printf("%-19s\t%04f\n%-19s\t%02d\n%-19s\t%04d\n%-19s\t%02d\n%-19s\t%02d\n",
                              "accumulator", accumulator, "instructionCounter", instructionCounter,
                              "instructionRegister", instructionRegister, "operationCode", operationCode,
                              "operand", operand);
            printer.printf("%-19s\t%04f\n%-19s\t%02d\n%-19s\t%04d\n%-19s\t%02d\n%-19s\t%02d\n",
                           "accumulator", accumulator, "instructionCounter", instructionCounter,
                           "instructionRegister", instructionRegister, "operationCode", operationCode,
                           "operand", operand);
            System.out.println("\nMEMORY");
            printer.println("\nMEMORY");
            System.out.printf("%14d%14d%14d%14d%14d%14d%14d%14d%14d%14d\n", 0,1,2,3,4,5,6,7,8,9);
            printer.printf("%14d%12d%12d%12d%12d%12d%12d%12d%12d%12d\n", 0,1,2,3,4,5,6,7,8,9);
            for (int counter = 0; counter < 1000; counter=counter+10)
            {
                System.out.printf("%03d ", counter);
                printer.printf("%03d ", counter);
                for (int count = 0; count < 10; count++)
                {
                    System.out.printf("%8s", memory[counter+count]);
                    printer.printf("%4s     ", memory[counter+count]);
                }
                System.out.println();
                printer.println();
            }
        }
    }
    public static String decrypt(int val)
    {
        return Character.toString((char) val);
    }
    public static void storeString()  // store string variable in memory array
    {
        stringEntered = "";
        do
        {
            stringEnt = JOptionPane.showInputDialog(null, "Enter string less than 300 characters >>  ");
            if (stringEnt == null)
            {
                System.out.println("Program execution cancelled");
                System.out.println("\nPlease restart the application to try again.");
                instructionCounter = 999;  // to prevent further processing
                return;
            }
            else
            {
                stringEntered = (String)stringEnt;
                stringEnt = null;
            }
        }while (stringEntered.equals(""));
        char[] charValue = stringEntered.toCharArray();
        if (stringEntered.length() < 10)
            memory[operand++] = stringEntered.length() * 1000 + (int)charValue[0] + "";
        if (stringEntered.length() > 9 && stringEntered.length() < 100)
            memory[operand++] = stringEntered.length() * 1000 + (int)charValue[0] + "";
        if (stringEntered.length() > 99 && stringEntered.length() < 1000)
            memory[operand++] = stringEntered.length() * 1000 + (int)charValue[0] + "";
        for (int counter = 1; counter < stringEntered.length(); counter = counter+2)
        {
            try
            {
                memory[operand++] = (int)charValue[counter] * 1000 + (int)charValue[counter+1] + "";
            }
            catch(ArrayIndexOutOfBoundsException arrayEx)
            {
                memory[operand-1] = (int)charValue[counter]*1000 + "";
            }
        }
        ConvertionToSML.smlCountMiddle = ++operand;
    }
    public static String displayString() // convert stored string from memory to string variable
    {
        int start = Integer.parseInt(memory[operand++]);
        int start1 = start/1000 - 1;
        char[] charValue = new char[start / 1000];
        charValue[0] = (char)(start % 1000);
        if ((start/1000) % 2 == 1)
        {
            for (int counter = 1; counter < start1; counter = counter+2)
            {
                start = Integer.parseInt(memory[operand++]);
                charValue[counter] = (char)(start / 1000);
                charValue[counter+1] = (char)(start % 1000);
            }
        }
        else
        {
            for (int counter = 1; counter < start1; counter = counter+2)
            {
                start = Integer.parseInt(memory[operand++]);
                charValue[counter] = (char)(start / 1000);
                charValue[counter+1] = (char)(start % 1000);
            }
            start = Integer.parseInt(memory[operand]);
            charValue[start1] = (char)(start / 1000);
        }
        return new String(charValue);
    }
    private class NullOutputStream extends OutputStream
    {
        public void write(int i) throws IOException { }
    }
}