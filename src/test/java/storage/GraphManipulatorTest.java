package storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentMatchers;

import java.util.Set;
import java.util.Map;

public class GraphManipulatorTest {

    private GraphManipulator graphManipulator;
    private org.neo4j.driver.Driver mockDriver;
    private Session mockSession;

    @BeforeEach
    void setUp() {
        mockDriver = mock(org.neo4j.driver.Driver.class);
        mockSession = mock(Session.class);

        when(mockDriver.session()).thenReturn(mockSession);
        graphManipulator = new GraphManipulator(mockDriver);
    }

    // Método de ayuda para configurar el mock de run()
    private void setupRunMock() {
        Answer<Result> answer = invocation -> {
            return mock(Result.class);
        };
        
        doAnswer(answer).when(mockSession).run(anyString(), any(Map.class));
    }

    @Test
    void testEnsureGraphProjection() {
        String graphName = "testGraph";

        assertDoesNotThrow(() -> {
            setupRunMock();

            graphManipulator.ensureGraphProjection(graphName);

            verify(mockSession).run(
                eq("CALL gds.graph.drop($graphName)"),
                eq(Values.parameters("graphName", graphName))
            );
            verify(mockSession).run(
                eq("CALL gds.graph.project($graphName, ['Word'], {CONNECTED: {type: 'CONNECTED', orientation: 'UNDIRECTED'}})"),
                eq(Values.parameters("graphName", graphName))
            );
        });
    }

    @Test
    void testInsertWords() {
        Set<String> words = Set.of("word1", "word2", "word3");

        assertDoesNotThrow(() -> {
            when(mockSession.writeTransaction(any())).thenReturn(null);

            graphManipulator.insertWords(words);

            verify(mockSession, times(1)).writeTransaction(any());
        });
    }

    @Test
    void testConnectWithExistingWords() {
        Set<String> newWords = Set.of("word1", "word2");

        assertDoesNotThrow(() -> {
            setupRunMock();

            graphManipulator.connectWithExistingWords(newWords);

            verify(mockSession, times(1)).run(
                anyString(),
                any(Map.class)
            );
        });
    }

    @Test
    void testConnectWords() {
        Set<String> words = Set.of("word1", "word2");

        assertDoesNotThrow(() -> {
            setupRunMock();

            graphManipulator.connectWords(words);

            verify(mockSession, atLeastOnce()).run(
                eq("""
                   MERGE (w1:Word {name: $word1})
                   MERGE (w2:Word {name: $word2})
                   MERGE (w1)-[:CONNECTED]-(w2)
                   """),
                argThat(map -> map.containsKey("word1") && map.containsKey("word2") && !map.get("word1").equals(map.get("word2")))
            );
        });
    }

    @Test
    void testIsOneLetterDifference() {
        assertDoesNotThrow(() -> {
            boolean result = graphManipulator.isOneLetterDifference("word", "ward");
            assert result;
        });
    }
}
