package com.example.util;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class HeapMax<T extends Comparable<T>> {
    private final List<T> items;

    public HeapMax() {
        items = new ArrayList<>();
    }

    /** Returns and does not remove the current maximum. */
    public T peek() {
        if (items.isEmpty()) {
            throw new NoSuchElementException("Heap is empty");
        }
        return items.get(0);
    }

    /** Removes and returns the maximum element. */
    public T popMax() {
        if (items.isEmpty()) {
            throw new NoSuchElementException("Heap is empty");
        }
        T max = items.get(0);
        int last = items.size() - 1;
        // restore the heap:
        items.set(0, items.get(last));
        items.remove(last);
        if (!items.isEmpty()) {
            heapifyDown(0);
        }
        return max;
    }

    /** Adds a new element into the heap, restoring the heap property. */
    public void add(T element) {
        items.add(element);
        heapifyUp(items.size() - 1);
    }

    // A recursive method to heapify a subtree with the root at the given index
    // assumes that the subtrees are already heapified.
    private void heapifyDown(int i) {
        int largest = i;
        int l = left(i), r = right(i);

        if (l < items.size() && items.get(l).compareTo(items.get(largest)) > 0) {
            largest = l;
        }
        if (r < items.size() && items.get(r).compareTo(items.get(largest)) > 0) {
            largest = r;
        }
        if (largest != i) {
            swap(i, largest);
            heapifyDown(largest);
        }
    }

    /** Restore max‑heap property by sifting the element up. */
    private void heapifyUp(int i) {
        if(i < 0 || i >= items.size())
            throw new IllegalStateException("Invalid index in heapifyUp");
        while (i != 0 && items.get(parent(i)).compareTo(items.get(i)) < 0) {
            swap(i, parent(i));
            i = parent(i);
        }
    }

    /**
     * Finds the index of the given item in the heap, or -1 if not present.
     * Uses List.indexOf (which calls equals()).
     */
    public int indexOf(T item) {
        return items.indexOf(item);
    }



    /** sift item up into its correct position.*/
    public void heapifyUp(T item) {
        int i = indexOf(item);
        if (i < 0) {
            throw new IllegalArgumentException("Item not found in heap");
        }
        heapifyUp(i);
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

    public int size() {
        return items.size();
    }
    public boolean isEmpty(){
        return items.isEmpty();
    }
}
