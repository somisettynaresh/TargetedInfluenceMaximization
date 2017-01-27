package edu.iastate.research.graph.utilities;

import java.util.HashSet;
import java.util.Stack;

/**
 * Created by Naresh on 1/23/2017.
 */
public class UniqueStack<T> extends Stack<T> {
    private HashSet<T> tempSet = new HashSet<>();

    @Override
    public T push(T item) {
        if (!tempSet.contains(item)) {
            addElement(item);
            tempSet.add(item);
        }
        return item;
    }

    @Override
    public synchronized T pop() {
        T obj;
        int len = size();
        obj = peek();
        removeElementAt(len - 1);
        tempSet.remove(obj);
        return obj;
    }

    public static void main(String[] args) {
        Stack<Integer> stack = new UniqueStack<>();
        stack.push(1);
        stack.push(1);
        stack.push(2);
        stack.push(2);
        stack.push(3);
        System.out.println(stack.pop());
        System.out.println(stack.pop());
        System.out.println(stack.pop());

    }
}
