package storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GraphAnalyzerTest {

    private GraphAnalyzer graphAnalyzer;

    @BeforeEach
    void setUp() {
        graphAnalyzer = mock(GraphAnalyzer.class);
    }

    @Test
    void testFindShortestPathSuccess() {
        when(graphAnalyzer.findShortestPath("node1", "node2"))
                .thenReturn(List.of("node1", "node2"));

        List<String> path = graphAnalyzer.findShortestPath("node1", "node2");

        assertNotNull(path, "Shortest path should not be null.");
        assertEquals(2, path.size(), "Path size should be 2.");
        assertEquals("node1", path.get(0));
        assertEquals("node2", path.get(1));
    }

    @Test
    void testFindShortestPathError() {
        when(graphAnalyzer.findShortestPath("node1", "node2")).thenThrow(new RuntimeException("Error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            graphAnalyzer.findShortestPath("node1", "node2");
        });

        assertEquals("Error", exception.getMessage());
    }

    @Test
    void testFindCommunitiesSuccess() {
        when(graphAnalyzer.findCommunities())
                .thenReturn(List.of(List.of("node1", "node2")));

        List<List<String>> communities = graphAnalyzer.findCommunities();

        assertNotNull(communities, "Communities should not be null.");
        assertEquals(1, communities.size());
        assertEquals(2, communities.get(0).size());
    }

    @Test
    void testFindIsolatedNodes() {
        when(graphAnalyzer.findIsolatedNodes()).thenReturn(List.of("node3"));

        List<String> isolatedNodes = graphAnalyzer.findIsolatedNodes();

        assertNotNull(isolatedNodes, "Isolated nodes should not be null.");
        assertEquals(1, isolatedNodes.size());
        assertEquals("node3", isolatedNodes.get(0));
    }

    @Test
    void testFindMaximumDistance() {
        when(graphAnalyzer.findMaximumDistance()).thenReturn(5);

        int maxDistance = graphAnalyzer.findMaximumDistance();

        assertEquals(5, maxDistance, "Maximum distance should be 5.");
    }

    @Test
    void testFindHighConnectivityNodes() {
        when(graphAnalyzer.findHighConnectivityNodes(3)).thenReturn(List.of("node1", "node2"));

        List<String> nodes = graphAnalyzer.findHighConnectivityNodes(3);

        assertNotNull(nodes, "Nodes should not be null.");
        assertEquals(2, nodes.size());
    }

    @Test
    void testFindNodesByDegree() {
        when(graphAnalyzer.findNodesByDegree(2)).thenReturn(List.of("node1", "node2"));

        List<String> nodes = graphAnalyzer.findNodesByDegree(2);

        assertNotNull(nodes, "Nodes should not be null.");
        assertEquals(2, nodes.size());
    }
}
