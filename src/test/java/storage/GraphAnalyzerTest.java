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
            when(mockSession.run(
                eq("MATCH path = shortestPath((start:Word {name: $source})-[:CONNECTED*]-(end:Word {name: $target})) RETURN [node IN nodes(path) | node.name] AS path"),
                eq(Values.parameters("source", "node1", "target", "node2"))
            )).thenReturn(mockResult);

            when(mockResult.hasNext()).thenReturn(true);
            when(mockResult.next()).thenReturn(mock(org.neo4j.driver.Record.class)); // Cambiado aquí

            List<String> path = graphAnalyzer.findShortestPath("node1", "node2");
            verify(mockSession, times(1)).run(anyString(), anyMap());
        });
    }

    @Test
    void testFindAllPaths() {
        assertDoesNotThrow(() -> {
            when(mockSession.run(
                eq("MATCH path = (start:Word {name: $source})-[*]-(end:Word {name: $target}) " +
                   "WHERE ALL(node IN nodes(path) WHERE single(x IN nodes(path) WHERE x = node)) " +
                   "RETURN [node IN nodes(path) | node.name] AS path, size(relationships(path)) AS length " +
                   "ORDER BY length ASC LIMIT 10"),
                eq(Values.parameters("source", "node1", "target", "node2"))
            )).thenReturn(mockResult);

            when(mockResult.hasNext()).thenReturn(true);
            when(mockResult.next()).thenReturn(mock(org.neo4j.driver.Record.class)); // Cambiado aquí

            List<List<String>> paths = graphAnalyzer.findAllPaths("node1", "node2");
            verify(mockSession, times(1)).run(anyString(), anyMap());
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
            when(mockResult.next()).thenReturn(mock(org.neo4j.driver.Record.class)); // Cambiado aquí

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
            when(mockResult.next()).thenReturn(mock(org.neo4j.driver.Record.class)); // Cambiado aquí

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
            when(mockResult.next()).thenReturn(mock(org.neo4j.driver.Record.class)); // Cambiado aquí

            List<String> isolatedNodes = graphAnalyzer.findIsolatedNodes();
            verify(mockSession, times(1)).run(anyString());
        });
    }

    @Test
    void testFindHighConnectivityNodes() {
        assertDoesNotThrow(() -> {
            when(mockSession.run(
                eq("""
                   CALL gds.degree.stream('myGraph')
                   YIELD nodeId, score
                   WHERE score >= $minDegree
                   MATCH (n:Word) WHERE id(n) = nodeId
                   RETURN n.name AS name, score
                   ORDER BY score DESC
                   """),
                eq(Values.parameters("minDegree", 3))
            )).thenReturn(mockResult);

            when(mockResult.hasNext()).thenReturn(true);
            when(mockResult.next()).thenReturn(mock(org.neo4j.driver.Record.class)); // Cambiado aquí

            List<String> highConnectivityNodes = graphAnalyzer.findHighConnectivityNodes(3);
            verify(mockSession, times(1)).run(anyString(), anyMap());
        });
    }

    @Test
    void testFindNodesByDegree() {
        assertDoesNotThrow(() -> {
            when(mockSession.run(
                eq("""
                   MATCH (n:Word)-[r]-()
                   WITH n, COUNT(r) AS degree
                   WHERE degree = $degree
                   RETURN n.name AS name
                   """),
                eq(Values.parameters("degree", 3))
            )).thenReturn(mockResult);

            when(mockResult.hasNext()).thenReturn(true);
            when(mockResult.next()).thenReturn(mock(org.neo4j.driver.Record.class)); // Cambiado aquí

            List<String> nodes = graphAnalyzer.findNodesByDegree(3);
            verify(mockSession, times(1)).run(anyString(), anyMap());
        });
    }
}
