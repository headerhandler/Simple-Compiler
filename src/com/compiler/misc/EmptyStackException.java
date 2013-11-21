package com.compiler.misc;
public class EmptyStackException extends RuntimeException
{
    public EmptyStackException()
    {
        this ("Stack");
    }
    public EmptyStackException(String name)
    {
        super(name + " is empty");
    }
}