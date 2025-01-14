package storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
        Result mockResult = mock(Result.class);
        when(mockSession.run(
                eq("MATCH path = shortestPath((start:Word {name: $source})-[:CONNECTED*]-(end:Word {name: $target})) " +
                "RETURN [node IN nodes(path) | node.name] AS path"),
                eq(Values.parameters("source", "node1", "target", "node2"))
        )).thenReturn(mockResult);

        assertDoesNotThrow(() -> {
            graphAnalyzer.findShortestPath("node1", "node2");
            verify(mockSession).run(anyString(), any(Value.class));
        });
    }

    @Test
    void testFindAllPaths() {
        Result mockResult = mock(Result.class);
        when(mockSession.run(
                eq("MATCH path = (start:Word {name: $source})-[*]-(end:Word {name: $target}) " +
                "WHERE ALL(node IN nodes(path) WHERE single(x IN nodes(path) WHERE x = node)) " +
                "RETURN [node IN nodes(path) | node.name] AS path, size(relationships(path)) AS length ORDER BY length ASC LIMIT 10"),
                eq(Values.parameters("source", "node1", "target", "node2"))
        )).thenReturn(mockResult);

        assertDoesNotThrow(() -> {
            graphAnalyzer.findAllPaths("node1", "node2");
            verify(mockSession).run(anyString(), any(Value.class));
        });
    }

    @Test
    void testFindMaximumDistance() {
        assertDoesNotThrow(() -> {
            when(mockSession.run(
                eq("CALL gds.allShortestPaths.stream('myGraph') " +
                   "YIELD sourceNodeId, targetNodeId, distance " +
                   "RETURN max(distance) AS maxDistance")
            )).thenReturn(mockResult);

            when(mockResult.hasNext()).thenReturn(true);
            when(mockResult.next()).thenReturn(mock(org.neo4j.driver.Record.class));

            int maxDistance = graphAnalyzer.findMaximumDistance();
            verify(mockSession, times(1)).run(anyString());
        });
    }

    @Test
    void testFindCommunities() {
        assertDoesNotThrow(() -> {
            when(mockSession.run(
                eq("CALL gds.louvain.stream('myGraph') " +
                   "YIELD communityId, nodeId " +
                   "MATCH (n:Word) WHERE id(n) = nodeId " +
                   "RETURN communityId, collect(n.name) AS members " +
                   "ORDER BY communityId")
            )).thenReturn(mockResult);

            when(mockResult.hasNext()).thenReturn(true);
            when(mockResult.next()).thenReturn(mock(org.neo4j.driver.Record.class));

            List<List<String>> communities = graphAnalyzer.findCommunities();
            verify(mockSession, times(1)).run(anyString());
        });
    }

    @Test
    void testFindIsolatedNodes() {
        assertDoesNotThrow(() -> {
            when(mockSession.run(
                eq("MATCH (w:Word) WHERE NOT (w)-[]-() RETURN w.name AS name")
            )).thenReturn(mockResult);

            when(mockResult.hasNext()).thenReturn(true);
            when(mockResult.next()).thenReturn(mock(org.neo4j.driver.Record.class));

            List<String> isolatedNodes = graphAnalyzer.findIsolatedNodes();
            verify(mockSession, times(1)).run(anyString());
        });
    }

    @Test
    void testFindHighConnectivityNodes() {
        Result mockResult = mock(Result.class);
        when(mockSession.run(
                eq("CALL gds.degree.stream('myGraph')\n" +
                "YIELD nodeId, score\n" +
                "WHERE score >= $minDegree\n" +
                "MATCH (n:Word) WHERE id(n) = nodeId\n" +
                "RETURN n.name AS name, score\n" +
                "ORDER BY score DESC\n"),
                eq(Values.parameters("minDegree", 3))
        )).thenReturn(mockResult);

        assertDoesNotThrow(() -> {
            graphAnalyzer.findHighConnectivityNodes(3);
            verify(mockSession).run(anyString(), any(Value.class));
        });
    }

    @Test
    void testFindNodesByDegree() {
        Result mockResult = mock(Result.class);
        when(mockSession.run(
                eq("MATCH (n:Word)-[r]-()\n" +
                "WITH n, COUNT(r) AS degree\n" +
                "WHERE degree = $degree\n" +
                "RETURN n.name AS name\n"),
                eq(Values.parameters("degree", 3))
        )).thenReturn(mockResult);

        assertDoesNotThrow(() -> {
            graphAnalyzer.findNodesByDegree(3);
            verify(mockSession).run(anyString(), any(Value.class));
        });
    }
}
