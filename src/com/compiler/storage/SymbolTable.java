package com.compiler.storage;
import com.compiler.parser.ConvertionToSML;

public class SymbolTable
{
    private TableEntry[] entries;
    private int count = -1;
    public SymbolTable()
    {
        entries = new TableEntry[1000];
    }
    public void addEntry(TableEntry next)
    {
        if ((count+1) < 100)
            entries[++count] = next;
    }
    public boolean searchKey(int searchKey)
    {
        if (entries != null)
        {
            for (int count = 0; count <= this.count; count++)
            {
                if (entries[count].getSymbol() == searchKey && entries[count].getType() == 'L')
                    return true;
            }
        }
        return false;
    }
    public int reverseSearch(int searchKey)
    {
        if (entries != null)
        {
            for (int count : ConvertionToSML.lineNumbers)
            {
                if (getLocation(count) == searchKey)
                    return count;
            }
        }
        return 0;
    }
    public boolean searchKey(char searchKey)
    {
        if (entries != null)
        {
            for (int count = 0; count <= this.count; count++)
            {
                if ((char)entries[count].getSymbol() == searchKey && entries[count].getType() == 'V')
                    return true;
            }
        }
        return false;
    }
    public boolean searchString(char searchKey)
    {
        if (entries != null)
        {
            for (int count = 0; count <= this.count; count++)
            {
                if ((char)entries[count].getSymbol() == searchKey && entries[count].getType() == 'S')
                    return true;
            }
        }
        return false;
    }
    public int getLocation(int searchKey)
    {
        if (entries != null)
        {
            for (int count = 0; count <= this.count; count++)
            {
                if (entries[count].getSymbol() == searchKey && entries[count].getType() == 'L')
                {
                    return entries[count].getLocation();
                }
            }
        }
        return 0;
    }
    public int getLocationVar(int searchKey)
    {
        if (entries != null)
        {
            for (int count = 0; count <= this.count; count++)
            {
                if (entries[count].getSymbol() == searchKey && entries[count].getType() == 'V')
                    return entries[count].getLocation();
            }
        }
        return 0;
    }
    public int getLocationString(int searchKey)
    {
        if (entries != null)
        {
            for (int count = 0; count <= this.count; count++)
            {
                if (entries[count].getSymbol() == searchKey && entries[count].getType() == 'S')
                    return entries[count].getLocation();
            }
        }
        return 0;
    }
    public boolean searchConstant(double searchKey)
    {
        if (entries != null)
        {
            for (int count = 0; count <= this.count; count++)
            {
                if (entries[count].getSymbol() == searchKey && entries[count].getType() == 'C')
                    return true;
            }
        }
        return false;
    }
    public int getLocationCon(double searchKey)
    {
        if (entries != null)
        {
            for (int count = 0; count <= this.count; count++)
            {
                if (entries[count].getSymbol() == searchKey && entries[count].getType() == 'C')
                    return entries[count].getLocation();
            }
        }
        return 0;
    }
}