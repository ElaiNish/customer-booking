package com.example.util;

import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AdjacencyMatrixTest {

    private AdjacencyMatrix<String> mat;

    @BeforeEach
    void init() {
        mat = new AdjacencyMatrix<>(List.of("A","B","C"));
        mat.makeAdjacent("A","B");
        mat.makeAdjacent("B","C");
    }

    @Test
    void testGetAdjacent() {
        List<String> adjA = mat.getAdjacent("A");
        assertEquals(List.of("B"), adjA);
        List<String> adjB = mat.getAdjacent("B");
        assertTrue(adjB.contains("A"));
        assertTrue(adjB.contains("C"));
    }

    @Test
    void testSpanningBFS() {
        // from "A" should reach B and C
        var bfs = mat.getSpanningBFS("A");
        assertEquals(2, bfs.size());
        assertTrue(bfs.containsAll(List.of("B","C")));
    }

//    @Test
//    void testGetVertices() {
//        var verts = mat.getVertecies();
//        assertEquals(List.of("A","B","C"), verts);
//    }
}
