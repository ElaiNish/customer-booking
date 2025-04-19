package com.example.util;

import java.util.ArrayList;
import java.util.List;

public class AdjacencyMatrix<T> {
    private byte[][] matrix;
    private T[] array;

    @SuppressWarnings("unchecked")
    public AdjacencyMatrix(int size) {
        matrix = new byte[size][size];
        array = (T[]) new Object[size];
    }

    public AdjacencyMatrix(List<T> list) {
        int size = list.size();
        array = (T[]) list.toArray();
        matrix = new byte[size][size];
    }

    public T[] getArray() {
        return array.clone();
    }

    private int getIndex(T item) {
        if (item == null) return -1;
        for (int i = 0; i < array.length; i++)
            if (item.equals(array[i]))
                return i;
        return -1;
    }

    public List<T> getAdjacent(T item) {
        int index = getIndex(item);
        List<T> result = new ArrayList<>();
        if (index == -1) return result;
        for (int i = 0; i < array.length; i++)
            if (matrix[index][i] == 1)
                result.add(array[i]);
        return result;
    }

    public List<T> getSpanningBFS(T item) {
        int index = getIndex(item);
        List<T> result = new ArrayList<>();
        if (index == -1) return result;
        List<T> queue = new ArrayList<>();
        queue.add(item);
        result.add(item);
        while (!queue.isEmpty()) {
            T current = queue.remove(0);
            for (T adj : getAdjacent(current))
                if (!result.contains(adj)) {
                    queue.add(adj);
                    result.add(adj);
                }
        }
        result.remove(item);
        return result;
    }

    private boolean setAdjacent(T item1, T item2, byte mode) {
        int ind1 = getIndex(item1);
        int ind2 = getIndex(item2);
        if (ind1 == -1 || ind2 == -1) return false;
        matrix[ind1][ind2] = mode;
        matrix[ind2][ind1] = mode;
        return true;
    }

    public boolean makeAdjacent(T item1, T item2) {
        return setAdjacent(item1, item2, (byte)1);
    }

    public boolean makeNotAdjacent(T item1, T item2) {
        return setAdjacent(item1, item2, (byte)0);
    }

    public List<T> getVertices() {
        List<T> list = new ArrayList<>();
        for (T t : array)
            list.add(t);
        return list;
    }
}
