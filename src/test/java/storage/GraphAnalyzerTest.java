package storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.neo4j.driver.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

public class GraphAnalyzerTest {

    private GraphAnalyzer graphAnalyzer;

    @Mock
    private Driver mockDriver;
    @Mock
    private Session mockSession;
    @Mock
    private Result mockResult;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        graphAnalyzer = new GraphAnalyzer(mockDriver);
    }

    @Test
    void testFindShortestPath() {
        when(mockDriver.session()).thenReturn(mockSession);
        when(mockSession.run(anyString(), anyMap())).thenReturn(mockResult);

        assertDoesNotThrow(() -> {
            List<String> result = graphAnalyzer.findShortestPath("node1", "node2");
            assertNotNull(result);
        });
    }

    @Test
    void testFindAllPaths() {
        when(mockDriver.session()).thenReturn(mockSession);
        when(mockSession.run(anyString(), anyMap())).thenReturn(mockResult);

        assertDoesNotThrow(() -> {
            List<List<String>> result = graphAnalyzer.findAllPaths("node1", "node2");
            assertNotNull(result);
        });
    }

    @Test
    void testFindMaximumDistance() {
        when(mockDriver.session()).thenReturn(mockSession);
        when(mockSession.run(anyString())).thenReturn(mockResult);

        assertDoesNotThrow(() -> {
            int maxDistance = graphAnalyzer.findMaximumDistance();
            assertTrue(maxDistance >= 0);
        });
    }

    @Test
    void testFindCommunities() {
        when(mockDriver.session()).thenReturn(mockSession);
        when(mockSession.run(anyString())).thenReturn(mockResult);

        assertDoesNotThrow(() -> {
            List<List<String>> communities = graphAnalyzer.findCommunities();
            assertNotNull(communities);
        });
    }

    @Test
    void testFindIsolatedNodes() {
        when(mockDriver.session()).thenReturn(mockSession);
        when(mockSession.run(anyString())).thenReturn(mockResult);

        assertDoesNotThrow(() -> {
            List<String> nodes = graphAnalyzer.findIsolatedNodes();
            assertNotNull(nodes);
        });
    }
}
