package com.compiler.misc;
class StackNode<D>
{
    D element;
    StackNode<D> nextNode;
    public StackNode(D element, StackNode<D> nextNode)
    {
        this.element = element;
        this.nextNode = nextNode;
    }
    public StackNode(D object)
    {
        this(object, null);
    }
    public D getElement()
    {
        return element;
    }
    public StackNode<D> getNextNode()
    {
        return nextNode;
    } 
}

public class Stack<D>
{
    private StackNode<D> first;
    public StackNode<D> last;
    private String stackName;
    public Stack()
    {
        this("Stack");
    }
    public Stack(String name)
    {
        stackName = name;
        first = last = null;
    }
    public boolean isEmpty()
    {
        return first == null;
    }
    public void push (D object)
    {
        if (isEmpty())
            first = last = new StackNode<D>(object);
        else
            first = new StackNode<D>(object, first);
    }
    public D pop() throws EmptyStackException
    {
        if (isEmpty())
            throw new EmptyStackException(stackName);
        D removedItem = first.element;
        if (first == last)
            first = last = null;
        else
            first = first.nextNode;
        return removedItem;
    }
    public void print()
    {
        if (isEmpty())
        {
            System.out.println("Empty Stack");
            return;
        }
        
        System.out.printf("\nThe %s is ", stackName);
        StackNode<D> current = first;
        while (current != null)
        {
            System.out.printf("%s ", current.element);
            current = current.nextNode;
        }
        System.out.println("\n");
    }
    public D peek()
    {
        return first.element;
    }
}