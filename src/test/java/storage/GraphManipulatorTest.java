package storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.neo4j.driver.*;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GraphManipulatorTest {

    private GraphManipulator graphManipulator;

    @Mock
    private Driver mockDriver;
    @Mock
    private Session mockSession;
    @Mock
    private Transaction mockTransaction;
    @Mock
    private Result mockResult;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        graphManipulator = new GraphManipulator(mockDriver);
    }

    @Test
    void testEnsureGraphProjection() {
        String graphName = "testGraph";

        when(mockDriver.session()).thenReturn(mockSession);
        when(mockSession.run(anyString(), anyMap())).thenReturn(mockResult);

        assertDoesNotThrow(() -> graphManipulator.ensureGraphProjection(graphName));
        verify(mockSession, times(2)).run(anyString(), anyMap());
    }

    @Test
    void testInsertWords() {
        Set<String> words = Set.of("word1", "word2");

        when(mockDriver.session()).thenReturn(mockSession);
        when(mockSession.writeTransaction(any())).thenAnswer(invocation -> {
            TransactionWork<Void> work = invocation.getArgument(0);
            return work.execute(mockTransaction);
        });

        assertDoesNotThrow(() -> graphManipulator.insertWords(words));
        verify(mockTransaction, times(2)).run(anyString(), anyMap());
    }

    @Test
    void testConnectWithExistingWords_EmptyGraph() {
        Set<String> newWords = Set.of("word1");

        when(mockDriver.session()).thenReturn(mockSession);
        when(mockSession.run(anyString())).thenReturn(mockResult);
        when(mockResult.hasNext()).thenReturn(true);
        Record record = mock(Record.class);
        when(mockResult.next()).thenReturn(record);
        when(record.get("nodeCount")).thenReturn(Values.value(0));

        assertDoesNotThrow(() -> graphManipulator.connectWithExistingWords(newWords));
        verify(mockSession, times(1)).run(anyString());
    }

    @Test
    void testConnectWithExistingWords_NonEmptyGraph() {
        Set<String> newWords = Set.of("word1");

        when(mockDriver.session()).thenReturn(mockSession);
        when(mockSession.run(anyString())).thenReturn(mockResult);
        when(mockResult.hasNext()).thenReturn(true, false);
        Record record = mock(Record.class);
        when(mockResult.next()).thenReturn(record);
        when(record.get("nodeCount")).thenReturn(Values.value(1));
        when(mockSession.writeTransaction(any())).thenAnswer(invocation -> {
            TransactionWork<Void> work = invocation.getArgument(0);
            return work.execute(mockTransaction);
        });

        assertDoesNotThrow(() -> graphManipulator.connectWithExistingWords(newWords));
        verify(mockSession, times(1)).run(anyString());
        verify(mockTransaction, times(1)).run(anyString(), anyMap());
    }

    @Test
    void testConnectWords() {
        Set<String> words = Set.of("word1", "word2");

        when(mockDriver.session()).thenReturn(mockSession);
        when(mockSession.run(anyString(), anyMap())).thenReturn(mockResult);

        assertDoesNotThrow(() -> graphManipulator.connectWords(words));
        verify(mockSession, times(1)).run(anyString(), anyMap());
    }

    @Test
    void testIsOneLetterDifference_True() {
        String word1 = "word";
        String word2 = "ward";

        boolean result = graphManipulator.isOneLetterDifference(word1, word2);

        assertTrue(result);
    }

    @Test
    void testIsOneLetterDifference_False_DifferentLengths() {
        String word1 = "word";
        String word2 = "words";

        boolean result = graphManipulator.isOneLetterDifference(word1, word2);

        assertFalse(result);
    }

    @Test
    void testIsOneLetterDifference_False_MoreThanOneDifference() {
        String word1 = "word";
        String word2 = "work";

        boolean result = graphManipulator.isOneLetterDifference(word1, word2);

        assertFalse(result);
    }
}
