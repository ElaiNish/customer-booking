package com.example.util;

import java.util.LinkedList;

public class ComparableQueue<T> extends LinkedList<T> implements Comparable<ComparableQueue<T>> {
    @Override
    public int compareTo(ComparableQueue<T> other) {
        return other == null ? 1 : Integer.compare(this.size(), other.size());
    }
}
