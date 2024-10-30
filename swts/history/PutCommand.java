package com.history;

import java.util.Map;

public class PutCommand<K, V> implements Command {
    private Map<K, V> map;
    private K key;
    private V newValue;
    private V oldValue;
    private boolean hadOldValue;

    public PutCommand(Map<K, V> map, K key, V value) {
        this.map = map;
        this.key = key;
        this.newValue = value;
    }

    @Override
    public void execute() {
        if (map.containsKey(key)) {
            oldValue = map.get(key);
            hadOldValue = true;
        } else {
            hadOldValue = false;
        }
        map.put(key, newValue);
    }

    @Override
    public void undo() {
        if (hadOldValue) {
            map.put(key, oldValue);
        } else {
            map.remove(key);
        }
    }
}

