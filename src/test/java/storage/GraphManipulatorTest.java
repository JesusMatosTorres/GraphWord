package storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.neo4j.driver.*;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

public class GraphManipulatorTest {

    private GraphManipulator graphManipulator;
    private Driver mockDriver;
    private Session mockSession;
    private Transaction mockTransaction;

    @BeforeEach
    void setUp() {
        mockDriver = mock(Driver.class);
        mockSession = mock(Session.class);
        mockTransaction = mock(Transaction.class);

        when(mockDriver.session()).thenReturn(mockSession);
        when(mockSession.beginTransaction()).thenReturn(mockTransaction);
        graphManipulator = new GraphManipulator(mockDriver);
    }

    @Test
    void testEnsureGraphProjection() {
        String graphName = "testGraph";

        assertDoesNotThrow(() -> {
            when(mockSession.run(anyString(), anyMap())).thenReturn(null);
            graphManipulator.ensureGraphProjection(graphName);
            verify(mockSession, times(2)).run(anyString(), anyMap());
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
            when(mockSession.run(anyString())).thenReturn(mock(Result.class));
            graphManipulator.connectWithExistingWords(newWords);
            verify(mockSession, times(1)).run(anyString());
        });
    }

    @Test
    void testConnectWords() {
        Set<String> words = Set.of("word1", "word2");

        assertDoesNotThrow(() -> {
            when(mockSession.run(anyString(), anyMap())).thenReturn(null);
            graphManipulator.connectWords(words);
            verify(mockSession, atLeastOnce()).run(anyString(), anyMap());
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
