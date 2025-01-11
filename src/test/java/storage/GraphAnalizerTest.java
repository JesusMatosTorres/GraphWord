package org.example.storage;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GraphAnalyzerTest {

    @Test
    void testAnalyzeGraph() {
        GraphAnalyzer analyzer = new GraphAnalyzer();
        boolean result = analyzer.analyzeGraph("testGraph");
        assertTrue(result, "El análisis del grafo debe ser exitoso.");
    }
}
