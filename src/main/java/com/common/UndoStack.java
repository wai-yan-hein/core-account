package com.common;
import java.util.Stack;

public class UndoStack {
    private Stack<UndoItem> stack = new Stack<>();

    public void addUndo(UndoItem item) {
        stack.clear();
        stack.push(item);
    }

    public UndoItem undo() {
        if (!stack.isEmpty()) {
            return stack.pop();
        }
        return null;
    }
}
