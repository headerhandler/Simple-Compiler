package com.compiler.storage;
public class TableEntry
{
    private double symbol;
    private char type;
    private int location;
    public TableEntry(double symbol, char type, int location)
    {
        this.symbol = symbol;
        this.type = type;
        this.location = location;
    }
    public void setSymbol(double symbol)
    {
        this.symbol = symbol;
    }
    public void setType(char type)
    {
        this.type = type;
    }
    public void setLocation(int location)
    {
        this.location = location;
    }
    public double getSymbol()
    {
        return symbol;
    }
    public char getType()
    {
        return type;
    }
    public int getLocation()
    {
        return location;
    }
}