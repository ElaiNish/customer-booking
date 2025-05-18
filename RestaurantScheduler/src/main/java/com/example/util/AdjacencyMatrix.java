package com.example.util;

import java.lang.reflect.Array;
import java.util.*;

public class AdjacencyMatrix<T> {
    private final byte[][] matrix;
    private final T[] array;

    @SuppressWarnings("unchecked")
    public AdjacencyMatrix(int size, Class<T> componentType) {
        matrix = new byte[size][size];
        array  = (T[]) Array.newInstance(componentType, size);
    }


    @SuppressWarnings("unchecked")
    public AdjacencyMatrix(List<T> vertices) {
        Objects.requireNonNull(vertices, "vertices");
        int n = vertices.size();

        // create true T[] instance
        T sample = vertices.isEmpty() ? null : vertices.get(0);
        Class<?> type = (sample == null) ? Object.class : sample.getClass();
        array = (T[]) Array.newInstance(type, n);
        vertices.toArray(array);

        matrix = new byte[n][n];    // initially all zeros
    }


    public T[] getArray() {
        return array.clone();
    }

    public List<T> getVertices() {
        return List.of(array);
    }

    private int indexOf(T item) {
        if (item == null) return -1;
        for (int i = 0; i < array.length; i++) {
            if (item.equals(array[i])) return i;
        }
        return -1;
    }

    public List<T> getAdjacent(T item) {
        int index = indexOf(item);
        if (index == -1) return Collections.emptyList();

        List<T> res = new ArrayList<>();
        for (int i = 0; i < array.length; i++)
            if (matrix[index][i] == 1) res.add(array[i]);
        return res;
    }

    public List<T> getSpanningBFS(T item) {
        int idx = indexOf(item);
        if (idx == -1) return Collections.emptyList();

        List<T> visited = new ArrayList<>();
        Queue<T> q = new ArrayDeque<>();
        q.add(item);
        visited.add(item);

        while (!q.isEmpty()) {
            for (T adj : getAdjacent(q.poll())) {
                if (!visited.contains(adj)) {
                    visited.add(adj);
                    q.add(adj);
                }
            }
        }
        visited.remove(0);               // drop original
        return visited;
    }

    private boolean setAdjacent(T a, T b, byte mode) {
        int i = indexOf(a);
        int j = indexOf(b);
        if (i < 0 || j < 0) {
            return false;   // one or both vertices not in the matrix
        }
        // undirected graph: symmetric assignment
        matrix[i][j] = mode;
        matrix[j][i] = mode;
        return true;
    }

    public boolean makeAdjacent(T a, T b)    {
        return setAdjacent(a, b, (byte)1);
    }

    public boolean makeNotAdjacent(T a, T b) {
        return setAdjacent(a, b, (byte)0);
    }
}
