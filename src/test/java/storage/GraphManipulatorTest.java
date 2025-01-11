package org.example.storage;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GraphManipulatorTest {

    @Test
    void testManipulateGraph() {
        GraphManipulator manipulator = new GraphManipulator();
        String updatedGraph = manipulator.manipulateGraph("testGraph");
        assertNotNull(updatedGraph, "El grafo manipulado no debe ser nulo.");
    }
}