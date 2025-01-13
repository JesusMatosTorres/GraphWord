package storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.neo4j.driver.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
        when(mockResult.hasNext()).thenReturn(true, false);
        Record record = mock(Record.class);
        when(mockResult.next()).thenReturn(record);
        when(record.get("path")).thenReturn(Values.value(List.of("node1", "node2")));

        List<String> path = graphAnalyzer.findShortestPath("node1", "node2");

        assertNotNull(path);
        assertEquals(List.of("node1", "node2"), path);
    }

    @Test
    void testFindAllPaths() {
        when(mockDriver.session()).thenReturn(mockSession);
        when(mockSession.run(anyString(), anyMap())).thenReturn(mockResult);
        when(mockResult.hasNext()).thenReturn(true, true, false);
        Record record = mock(Record.class);
        when(mockResult.next()).thenReturn(record);
        when(record.get("path")).thenReturn(Values.value(List.of("node1", "node2")));

        List<List<String>> paths = graphAnalyzer.findAllPaths("node1", "node2");

        assertNotNull(paths);
        assertEquals(2, paths.size());
        assertEquals(List.of("node1", "node2"), paths.get(0));
    }

    @Test
    void testFindMaximumDistance() {
        when(mockDriver.session()).thenReturn(mockSession);
        when(mockSession.run(anyString())).thenReturn(mockResult);
        when(mockResult.hasNext()).thenReturn(true);
        Record record = mock(Record.class);
        when(mockResult.next()).thenReturn(record);
        when(record.get("maxDistance")).thenReturn(Values.value(10));

        int maxDistance = graphAnalyzer.findMaximumDistance();

        assertEquals(10, maxDistance);
    }

    @Test
    void testFindCommunities() {
        when(mockDriver.session()).thenReturn(mockSession);
        when(mockSession.run(anyString())).thenReturn(mockResult);
        when(mockResult.hasNext()).thenReturn(true, false);
        Record record = mock(Record.class);
        when(mockResult.next()).thenReturn(record);
        when(record.get("members")).thenReturn(Values.value(List.of("node1", "node2")));

        List<List<String>> communities = graphAnalyzer.findCommunities();

        assertNotNull(communities);
        assertEquals(1, communities.size());
        assertEquals(List.of("node1", "node2"), communities.get(0));
    }

    @Test
    void testFindIsolatedNodes() {
        when(mockDriver.session()).thenReturn(mockSession);
        when(mockSession.run(anyString())).thenReturn(mockResult);
        when(mockResult.hasNext()).thenReturn(true, false);
        Record record = mock(Record.class);
        when(mockResult.next()).thenReturn(record);
        when(record.get("name")).thenReturn(Values.value("node1"));

        List<String> isolatedNodes = graphAnalyzer.findIsolatedNodes();

        assertNotNull(isolatedNodes);
        assertEquals(1, isolatedNodes.size());
        assertEquals("node1", isolatedNodes.get(0));
    }

    @Test
    void testFindHighConnectivityNodes() {
        when(mockDriver.session()).thenReturn(mockSession);
        when(mockSession.run(anyString(), anyMap())).thenReturn(mockResult);
        when(mockResult.hasNext()).thenReturn(true, false);
        Record record = mock(Record.class);
        when(mockResult.next()).thenReturn(record);
        when(record.get("name")).thenReturn(Values.value("node1"));

        List<String> highConnectivityNodes = graphAnalyzer.findHighConnectivityNodes(3);

        assertNotNull(highConnectivityNodes);
        assertEquals(1, highConnectivityNodes.size());
        assertEquals("node1", highConnectivityNodes.get(0));
    }

    @Test
    void testFindNodesByDegree() {
        when(mockDriver.session()).thenReturn(mockSession);
        when(mockSession.run(anyString(), anyMap())).thenReturn(mockResult);
        when(mockResult.hasNext()).thenReturn(true, false);
        Record record = mock(Record.class);
        when(mockResult.next()).thenReturn(record);
        when(record.get("name")).thenReturn(Values.value("node1"));

        List<String> nodes = graphAnalyzer.findNodesByDegree(2);

        assertNotNull(nodes);
        assertEquals(1, nodes.size());
        assertEquals("node1", nodes.get(0));
    }
}
