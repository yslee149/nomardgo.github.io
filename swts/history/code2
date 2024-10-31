package org.yslee.history;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class DataManager {
    private List<String> data;
    private Stack<List<String>> undoStack;
    private Stack<List<String>> redoStack;

    public DataManager(List<String> initialData) {
        // Initialize data with a deep copy to prevent external modifications
        this.data = new ArrayList<>(initialData);
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
    }

    /**
     * Changes the first occurrence of oldValue to newValue.
     * @param oldValue The value to be replaced.
     * @param newValue The new value to replace with.
     * @return true if the change was successful, false otherwise.
     */
    public boolean changeByValue(String oldValue, String newValue) {
        int index = data.indexOf(oldValue);
        if (index != -1) {
            // Push current state to undo stack
            undoStack.push(new ArrayList<>(data));
            // Clear redo stack as new action invalidates future redos
            redoStack.clear();
            // Apply the change
            data.set(index, newValue);
            return true;
        } else {
            System.out.println("Value \"" + oldValue + "\" not found. No change made.");
            return false;
        }
    }

    /**
     * Undoes the last change.
     * @return true if undo was successful, false otherwise.
     */
    public boolean undo() {
        if (!undoStack.isEmpty()) {
            // Push current state to redo stack
            redoStack.push(new ArrayList<>(data));
            // Pop the last state from undo stack and set as current data
            data = undoStack.pop();
            return true;
        } else {
            System.out.println("No actions to undo.");
            return false;
        }
    }

    /**
     * Redoes the last undone change.
     * @return true if redo was successful, false otherwise.
     */
    public boolean redo() {
        if (!redoStack.isEmpty()) {
            // Push current state to undo stack
            undoStack.push(new ArrayList<>(data));
            // Pop the last state from redo stack and set as current data
            data = redoStack.pop();
            return true;
        } else {
            System.out.println("No actions to redo.");
            return false;
        }
    }

    /**
     * Returns a copy of the current data.
     * @return List of current data elements.
     */
    public List<String> getData() {
        return new ArrayList<>(data);
    }

    /**
     * Utility method to print the current data in a formatted way.
     */
    public void printData() {
        System.out.println(data);
    }

    public static void main(String[] args) {
        // Step 1: Initialize data with A, B, C, D
        List<String> initialData = new ArrayList<>();
        initialData.add("A");
        initialData.add("B");
        initialData.add("C");
        initialData.add("D");

        DataManager manager = new DataManager(initialData);
        System.out.print("1. Initial data: ");
        manager.printData(); // A, B, C, D

        // Step 2: Change B to F
        manager.changeByValue("B", "F");
        System.out.print("2. After changing B to F: ");
        manager.printData(); // A, F, C, D

        manager.changeByValue("C", "G");
        System.out.print("2. After changing B to F: ");
        manager.printData(); // A, F, C, D

        manager.changeByValue("D", "I");
        System.out.print("2. After changing B to F: ");
        manager.printData(); // A, F, C, D

        manager.undo();
        System.out.print("4. After undo: ");
        manager.printData();

        manager.undo();
        System.out.print("4. After undo: ");
        manager.printData();

        manager.undo();
        System.out.print("4. After undo: ");
        manager.printData();

        manager.redo();
        System.out.print("11. After second redo: ");
        manager.printData(); // A, F, G, D

        manager.redo();
        System.out.print("11. After second redo: ");
        manager.printData(); // A, F, G, D

        manager.redo();
        System.out.print("11. After second redo: ");
        manager.printData(); // A, F, G, D

        manager.undo();
        System.out.print("4. After undo: ");
        manager.printData();

//        // Step 3: Undo
//        manager.undo();
//        System.out.print("4. After undo: ");
//        manager.printData(); // A, B, C, D
//
//        // Step 4: Redo
//        manager.redo();
//        System.out.print("5. After redo: ");
//        manager.printData(); // A, F, C, D
//
//        // Step 5: Change C to G
//        manager.changeByValue("C", "G");
//        System.out.print("7. After changing C to G: ");
//        manager.printData(); // A, F, G, D
//
//        // Step 6: Undo (C to G)
//        manager.undo();
//        System.out.print("8. After first undo: ");
//        manager.printData(); // A, F, C, D
//
//        // Step 7: Undo (B to F)
//        manager.undo();
//        System.out.print("9. After second undo: ");
//        manager.printData(); // A, B, C, D
//
//        // Step 8: Redo (B to F)
//        manager.redo();
//        System.out.print("10. After first redo: ");
//        manager.printData(); // A, F, C, D
//
//        // Step 9: Redo (C to G)
//        manager.redo();
//        System.out.print("11. After second redo: ");
//        manager.printData(); // A, F, G, D
    }
}
