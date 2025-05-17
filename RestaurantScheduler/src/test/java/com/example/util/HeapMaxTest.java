package com.example.util;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class HeapMaxTest {

    private HeapMax<Integer> heap;

    @BeforeEach
    void setUp() {
        heap = new HeapMax<>();
    }

    @Test
    void testAddAndPeek() {
        heap.add(5);
        heap.add(3);
        heap.add(9);
        assertEquals(9, heap.peek());
    }

    @Test
    void testDeleteMax() {
        heap.add(2);
        heap.add(7);
        heap.add(4);
        int max = heap.popMax();
        assertEquals(7, max);
        assertEquals(4, heap.peek());
    }

    @Test
    void testIsEmpty() {
        assertTrue(heap.isEmpty());
        heap.add(1);
        assertFalse(heap.isEmpty());
    }
}
