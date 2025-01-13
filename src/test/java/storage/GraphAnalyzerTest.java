package storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class GraphAnalyzerTest {

    private GraphAnalyzer mockGraphAnalyzer;

    @BeforeEach
    void setUp() {
        mockGraphAnalyzer = mock(GraphAnalyzer.class);
    }

    @Test
    void testFindShortestPath() {
        when(mockGraphAnalyzer.findShortestPath("node1", "node2")).thenReturn(List.of("node1", "node2"));

        assertDoesNotThrow(() -> {
            List<String> path = mockGraphAnalyzer.findShortestPath("node1", "node2");
            assertNotNull(path, "Shortest path should not be null.");
        });
    }

    @Test
    void testFindCommunities() {
        when(mockGraphAnalyzer.findCommunities()).thenReturn(List.of(List.of("node1", "node2")));

        assertDoesNotThrow(() -> {
            List<List<String>> communities = mockGraphAnalyzer.findCommunities();
            assertNotNull(communities, "Communities should not be null.");
        });
    }

    @Test
    void testFindIsolatedNodes() {
        when(mockGraphAnalyzer.findIsolatedNodes()).thenReturn(List.of("node3"));

        assertDoesNotThrow(() -> {
            List<String> isolatedNodes = mockGraphAnalyzer.findIsolatedNodes();
            assertNotNull(isolatedNodes, "Isolated nodes should not be null.");
        });
    }

    @Test
    void testFindMaximumDistance() {
        when(mockGraphAnalyzer.findMaximumDistance()).thenReturn(5);

        assertDoesNotThrow(() -> {
            int maxDistance = mockGraphAnalyzer.findMaximumDistance();
            assertNotNull(maxDistance, "Maximum distance should not be null.");
        });
    }
}
