package org.yslee.sort;

import java.util.Comparator;

public class GenericComparator<T> implements Comparator<T> {

    private Comparator<T> comparator;

    public GenericComparator(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    @Override
    public int compare(T o1, T o2) {
        return comparator.compare(o1, o2);
    }
}
