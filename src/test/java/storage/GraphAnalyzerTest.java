package storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GraphAnalyzerTest {

    private GraphAnalyzer graphAnalyzer;

    @BeforeEach
    void setUp() {
        String uri = System.getenv("NEO4J_URI");
        String user = System.getenv("NEO4J_USER");
        String password = System.getenv("NEO4J_PASSWORD");

        if (uri == null || user == null || password == null) {
            throw new IllegalStateException("Las variables de entorno NEO4J_URI, NEO4J_USER y NEO4J_PASSWORD deben estar configuradas.");
        }

        graphAnalyzer = new GraphAnalyzer(uri, user, password);
    }

    @Test
    void testFindShortestPath() {
        assertDoesNotThrow(() -> {
            List<String> path = graphAnalyzer.findShortestPath("node1", "node2");
            assertNotNull(path, "Shortest path should not be null.");
        }, "Finding the shortest path should not throw an exception.");
    }

    @Test
    void testFindCommunities() {
        assertDoesNotThrow(() -> {
            List<List<String>> communities = graphAnalyzer.findCommunities();
            assertNotNull(communities, "Communities should not be null.");
        }, "Finding communities should not throw an exception.");
    }

    @Test
    void testFindIsolatedNodes() {
        assertDoesNotThrow(() -> {
            List<String> isolatedNodes = graphAnalyzer.findIsolatedNodes();
            assertNotNull(isolatedNodes, "Isolated nodes should not be null.");
        }, "Finding isolated nodes should not throw an exception.");
    }

    @Test
    void testFindMaximumDistance() {
        assertDoesNotThrow(() -> {
            int maxDistance = graphAnalyzer.findMaximumDistance();
            assertNotNull(maxDistance, "Maximum distance should not be null.");
        }, "Finding the maximum distance should not throw an exception.");
    }
}
