package storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.neo4j.driver.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

public class GraphAnalyzerTest {

    private GraphAnalyzer graphAnalyzer;
    private Driver mockDriver;
    private Session mockSession;
    private Result mockResult;

    @BeforeEach
    void setUp() {
        mockDriver = mock(Driver.class);
        mockSession = mock(Session.class);
        mockResult = mock(Result.class);

        when(mockDriver.session()).thenReturn(mockSession);
        graphAnalyzer = new GraphAnalyzer(mockDriver);
    }

    @Test
    void testFindShortestPath() {
        assertDoesNotThrow(() -> {
            when(mockSession.run(anyString(), anyMap())).thenReturn(mockResult);
            when(mockResult.hasNext()).thenReturn(true);
            when(mockResult.next()).thenReturn(mock(Record.class));

            List<String> path = graphAnalyzer.findShortestPath("node1", "node2");
            verify(mockSession, times(1)).run(anyString(), anyMap());
        });
    }

    @Test
    void testFindAllPaths() {
        assertDoesNotThrow(() -> {
            when(mockSession.run(anyString(), anyMap())).thenReturn(mockResult);
            when(mockResult.hasNext()).thenReturn(true);
            when(mockResult.next()).thenReturn(mock(Record.class));

            List<List<String>> paths = graphAnalyzer.findAllPaths("node1", "node2");
            verify(mockSession, times(1)).run(anyString(), anyMap());
        });
    }

    @Test
    void testFindMaximumDistance() {
        assertDoesNotThrow(() -> {
            when(mockSession.run(anyString())).thenReturn(mockResult);
            when(mockResult.hasNext()).thenReturn(true);
            when(mockResult.next()).thenReturn(mock(Record.class));

            int maxDistance = graphAnalyzer.findMaximumDistance();
            verify(mockSession, times(1)).run(anyString());
        });
    }

    @Test
    void testFindCommunities() {
        assertDoesNotThrow(() -> {
            when(mockSession.run(anyString())).thenReturn(mockResult);
            when(mockResult.hasNext()).thenReturn(true);
            when(mockResult.next()).thenReturn(mock(Record.class));

            List<List<String>> communities = graphAnalyzer.findCommunities();
            verify(mockSession, times(1)).run(anyString());
        });
    }

    @Test
    void testFindIsolatedNodes() {
        assertDoesNotThrow(() -> {
            when(mockSession.run(anyString())).thenReturn(mockResult);
            when(mockResult.hasNext()).thenReturn(true);
            when(mockResult.next()).thenReturn(mock(Record.class));

            List<String> isolatedNodes = graphAnalyzer.findIsolatedNodes();
            verify(mockSession, times(1)).run(anyString());
        });
    }

    @Test
    void testFindHighConnectivityNodes() {
        assertDoesNotThrow(() -> {
            when(mockSession.run(anyString(), anyMap())).thenReturn(mockResult);
            when(mockResult.hasNext()).thenReturn(true);
            when(mockResult.next()).thenReturn(mock(Record.class));

            List<String> highConnectivityNodes = graphAnalyzer.findHighConnectivityNodes(3);
            verify(mockSession, times(1)).run(anyString(), anyMap());
        });
    }

    @Test
    void testFindNodesByDegree() {
        assertDoesNotThrow(() -> {
            when(mockSession.run(anyString(), anyMap())).thenReturn(mockResult);
            when(mockResult.hasNext()).thenReturn(true);
            when(mockResult.next()).thenReturn(mock(Record.class));

            List<String> nodes = graphAnalyzer.findNodesByDegree(3);
            verify(mockSession, times(1)).run(anyString(), anyMap());
        });
    }
}
