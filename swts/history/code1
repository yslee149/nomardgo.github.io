package org.yslee.history;

import java.util.LinkedHashMap;
import java.util.Map;

class Item {
    int id;
    String name;

    public Item(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Item{id=" + id + ", name='" + name + "'}";
    }
}

public class HistoryTest {

    public static void main(String[] args) {
        Map<Integer, Item> historyMap = new LinkedHashMap<>();

        int key = 0;
        historyMap.put(key++, new Item(0, "name-0"));
        historyMap.put(key++, new Item(1, "name-1"));
        historyMap.put(key++, new Item(2, "name-2"));
        historyMap.put(key++, new Item(3, "name-3"));
        historyMap.put(key++, new Item(4, "name-4"));

        // Start from the last element
//        int currentKey = historyMap.size() - 1;
        int currentKey = historyMap.size() ;

        // Undo operation
        currentKey = undo(historyMap, currentKey);  // Move to previous state
        currentKey = undo(historyMap, currentKey);  // Move to previous state

        // Redo operation
        currentKey = redo(historyMap, currentKey);  // Move to next state
    }

    public static int undo(Map<Integer, Item> historyMap, int currentKey) {
        if (currentKey > 0) {
            currentKey--;  // Move one step back
            System.out.println("Undo: " + historyMap.get(currentKey));
        } else {
            System.out.println("No more undo steps available.");
        }
        return currentKey;
    }

    public static int redo(Map<Integer, Item> historyMap, int currentKey) {
        if (currentKey < historyMap.size() - 1) {
            currentKey++;  // Move one step forward
            System.out.println("Redo: " + historyMap.get(currentKey));
        } else {
            System.out.println("No more redo steps available.");
        }
        return currentKey;
    }
}
