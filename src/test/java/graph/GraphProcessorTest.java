package org.example.graph;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GraphProcessorTest {

    @Test
    void testProcessGraphValidInput() {
        GraphProcessor processor = new GraphProcessor();
        String result = processor.processGraph("validGraphInput");
        assertEquals("expectedResult", result, "El resultado no es el esperado.");
    }

    @Test
    void testProcessGraphInvalidInput() {
        GraphProcessor processor = new GraphProcessor();
        assertThrows(IllegalArgumentException.class, () -> {
            processor.processGraph(null);
        }, "Debe lanzar una excepción si la entrada es nula.");
    }
}
