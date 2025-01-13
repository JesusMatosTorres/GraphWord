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

    // Método de ayuda para verificar la llamada a run()
    private void verifyRunWithParams(String query, Value params) {
        verify(mockSession).run(eq(query), eq(params));
    }

    // Método de ayuda para configurar el mock de run()
    private void setupRunMock() {
    Result mockResult = mock(Result.class);
    // Configuramos el comportamiento específico para cada tipo de parámetro
    when(mockSession.run(anyString(), any(Value.class))).thenReturn(mockResult);
    // Aseguramos que el método run() con TransactionConfig no se llame
    when(mockSession.run(anyString(), any(TransactionConfig.class))).thenThrow(new IllegalArgumentException("Ambiguous call"));
    }


    @Test
    void testEnsureGraphProjection() {
        String graphName = "testGraph";

        assertDoesNotThrow(() -> {
            setupRunMock();

            graphManipulator.ensureGraphProjection(graphName);

            verifyRunWithParams(
                "CALL gds.graph.drop($graphName)",
                Values.parameters("graphName", graphName)
            );
            verifyRunWithParams(
                "CALL gds.graph.project($graphName, ['Word'], {CONNECTED: {type: 'CONNECTED', orientation: 'UNDIRECTED'}})",
                Values.parameters("graphName", graphName)
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

            verify(mockSession, times(1)).run(anyString(), any(Value.class));
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
                argThat(value -> {
                    Map<String, Object> params = value.asMap();
                    return params.containsKey("word1") && 
                           params.containsKey("word2") && 
                           !params.get("word1").equals(params.get("word2"));
                })
            );
        });
    }
}
