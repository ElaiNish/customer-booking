package com.example.util;

import java.util.ArrayList;
import java.util.List;

public class HeapMax<T extends Comparable<T>> {
    private List<T> items;

    public HeapMax() {
        items = new ArrayList<>();
    }

    private int parent(int i) {
        return (i - 1) / 2;
    }

    private int left(int i) {
        return 2 * i + 1;
    }

    private int right(int i) {
        return 2 * i + 2;
    }

    private void swap(int i, int j) {
        T temp = items.get(i);
        items.set(i, items.get(j));
        items.set(j, temp);
    }

    public T peek() {
        if (items.isEmpty())
            throw new IllegalStateException("Heap is empty!");
        return items.get(0);
    }

    public T deleteMax() {
        if (items.isEmpty())
            throw new IllegalStateException("Heap is empty!");
        T root = items.get(0);
        items.set(0, items.get(items.size() - 1));
        items.remove(items.size() - 1);
        if (!items.isEmpty())
            heapify(0);
        return root;
    }

    public void add(T k) {
        items.add(k);
        heapifyUp(items.size() - 1);
    }

    // A recursive method to heapify a subtree with the root at the given index
    // assumes that the subtrees are already heapified.
    private void heapify(int i) {
        if(i < 0 || i >= items.size())
            throw new IllegalStateException("Invalid index in heapify");
        int l = left(i);
        int r = right(i);
        int largest = i;
        if (l < items.size() && items.get(l).compareTo(items.get(i)) > 0)
            largest = l;
        if (r < items.size() && items.get(r).compareTo(items.get(largest)) > 0)
            largest = r;
        if (largest != i) {
            swap(i, largest);
            heapify(largest);
        }
    }

    private void heapifyUp(int i) {
        if(i < 0 || i >= items.size())
            throw new IllegalStateException("Invalid index in heapifyUp");
        while (i != 0 && items.get(parent(i)).compareTo(items.get(i)) < 0) {
            swap(i, parent(i));
            i = parent(i);
        }
    }

    public int count() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
