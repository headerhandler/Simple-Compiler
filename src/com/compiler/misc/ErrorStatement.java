package com.compiler.misc;
import javax.swing.JOptionPane;

import com.compiler.main.FileString;
import com.compiler.parser.ConvertionToSML;
public class ErrorStatement
{
    public void lineError()
    {
        JOptionPane.showMessageDialog(null, String.format("Error while processing line: \n%s\n%s",
                          ConvertionToSML.stringVar.get(ConvertionToSML.temp0), "Please check that line number is first token in statement"));
        FileString.restart();
    }
    public void statementError()
    {
        JOptionPane.showMessageDialog(null, String.format("Error while processing line: \n\t%s\n%s",
                          ConvertionToSML.stringVar.get(ConvertionToSML.temp0), "Please ensure that line statement contains valid simple statements"));
        FileString.restart();
    }
    public void forError()
    { 
        JOptionPane.showMessageDialog(null, String.format("Error while processing line: \n\t%s\n%s",
                          ConvertionToSML.stringVar.get(ConvertionToSML.temp0), 
                          "Please see documentation for correct format of [for] statements"));
        FileString.restart();
    }
    public void letError()
    {
        JOptionPane.showMessageDialog(null, String.format("Error while processing line: \n\t%s\n%s",
                          ConvertionToSML.stringVar.get(ConvertionToSML.temp0), 
                          "Please see documentation for correct format of [let] statements"));
        FileString.restart();
    }
    public void ifError()
    {
        JOptionPane.showMessageDialog(null, String.format("Error while processing line: \n\t%s\n%s",
                          ConvertionToSML.stringVar.get(ConvertionToSML.temp0),
                          "Please see documentation for correct format of if statements"));
        FileString.restart();
    }
    public void inputError()
    {
        JOptionPane.showMessageDialog(null, String.format("Error while processing line: \n\t%s\n%s",
                          ConvertionToSML.stringVar.get(ConvertionToSML.temp0),
                          "Please see documentation for correct format of input statements"));
        FileString.restart();
    }
    public void variableError(boolean b, char param)
    {
        if (b == true)
            JOptionPane.showMessageDialog(null, String.format("Error while processing line: \n\t%s\n%s",
               ConvertionToSML.stringVar.get(ConvertionToSML.temp0), "Variable: " + Character.toString(param)
               + " not yet assigned"));
        else
            JOptionPane.showMessageDialog(null, String.format("Error while processing line: \n\t%s\n%s",
               ConvertionToSML.stringVar.get(ConvertionToSML.temp0), "Only variables are allowed after a print/input "
               + "statement. Please separate multiple variables with a comma"));
        FileString.restart();
    }
    public void gotoError()
    {
        JOptionPane.showMessageDialog(null, String.format("Error while processing line: \n\t%s\n%s",
                          ConvertionToSML.stringVar.get(ConvertionToSML.temp0), 
                          "Please see documentation for correct format of goto statements"));
        FileString.restart();
    }
    public void arrayError()
    {
        JOptionPane.showMessageDialog(null, String.format("Error while processing line: \n\t%s\n%s",
                          ConvertionToSML.stringVar.get(ConvertionToSML.temp0), "Array variables should be enclosed in braces"));
        FileString.restart();
    }
}