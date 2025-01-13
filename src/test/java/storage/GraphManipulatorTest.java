package storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.neo4j.driver.*;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

public class GraphManipulatorTest {

    private GraphManipulator graphManipulator;

    @Mock
    private Driver mockDriver;
    @Mock
    private Session mockSession;
    @Mock
    private Transaction mockTransaction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        graphManipulator = new GraphManipulator(mockDriver);
    }

    @Test
    void testEnsureGraphProjection() {
        when(mockDriver.session()).thenReturn(mockSession);
        when(mockSession.run(anyString(), anyMap())).thenReturn(mock(Result.class));

        assertDoesNotThrow(() -> graphManipulator.ensureGraphProjection("testGraph"));
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
    void testConnectWithExistingWords() {
        Set<String> newWords = Set.of("word1");

        when(mockDriver.session()).thenReturn(mockSession);
        when(mockSession.run(anyString())).thenReturn(mock(Result.class));
        when(mockSession.writeTransaction(any())).thenAnswer(invocation -> {
            TransactionWork<Void> work = invocation.getArgument(0);
            return work.execute(mockTransaction);
        });

        assertDoesNotThrow(() -> graphManipulator.connectWithExistingWords(newWords));
        verify(mockTransaction, atLeastOnce()).run(anyString(), anyMap());
    }

    @Test
    void testConnectWords() {
        Set<String> words = Set.of("word1", "word2");

        when(mockDriver.session()).thenReturn(mockSession);
        when(mockSession.run(anyString(), anyMap())).thenReturn(mock(Result.class));

        assertDoesNotThrow(() -> graphManipulator.connectWords(words));
        verify(mockSession, atLeastOnce()).run(anyString(), anyMap());
    }

    @Test
    void testIsOneLetterDifference() {
        assertDoesNotThrow(() -> {
            boolean result = graphManipulator.isOneLetterDifference("word", "ward");
            assertTrue(result);
        });
    }
}
