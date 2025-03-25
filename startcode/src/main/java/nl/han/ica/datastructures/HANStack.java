package nl.han.ica.datastructures;

import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.ASTNode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class HANStack implements IHANStack<ASTNode> {

    private final List<ASTNode> data;
    private int top;

    public HANStack() {
        this.data = new LinkedList<>();
        top = -1;
    }

    @Override
    public void push(ASTNode value) {
        data.add(value);
        top++;
    }

    @Override
    public ASTNode pop() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }

        ASTNode value = data.get(top);
        data.remove(top);
        top--;
        return value;
    }

    @Override
    public ASTNode peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }

        return data.get(top);
    }

    public boolean isEmpty() {
        return top == -1;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        // Iterate through the stack without modifying it
        for (int i = 0; i <= top; i++) {
            sb.append(data.get(i));
            if (i < top) sb.append(",");  // Avoid trailing comma
        }

        sb.append("]");

        return sb.toString();
    }
}
