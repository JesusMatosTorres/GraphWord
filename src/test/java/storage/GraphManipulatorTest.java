package storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import java.util.Set;

public class GraphManipulatorTest {

    private GraphManipulator graphManipulator;
    private Driver mockDriver;
    private Session mockSession;

    @BeforeEach
    void setUp() {
        mockDriver = mock(Driver.class);
        mockSession = mock(Session.class, RETURNS_SMART_NULLS);

        when(mockDriver.session()).thenReturn(mockSession);
        graphManipulator = new GraphManipulator(mockDriver);
    }

    private void setupMockResult(Result mockResult, boolean hasNext, org.neo4j.driver.Record mockRecord) {
        when(mockResult.hasNext()).thenReturn(hasNext);
        when(mockResult.next()).thenReturn(mockRecord);
    }

    @Test
    void testEnsureGraphProjection() {
        String graphName = "testGraph";
        Result mockResult = mock(Result.class);
        setupMockResult(mockResult, false, null);

        when(mockSession.run(anyString(), any(Value.class))).thenReturn(mockResult);

        assertDoesNotThrow(() -> {
            graphManipulator.ensureGraphProjection(graphName);
            verify(mockSession, times(2)).run(anyString(), any(Value.class));
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
        Set<String> words = Set.of("word1", "word2");

        Result mockSearchResult = mock(Result.class);
        Result mockCountResult = mock(Result.class);
        org.neo4j.driver.Record mockRecord = mock(org.neo4j.driver.Record.class);
        org.neo4j.driver.Record mockCountRecord = mock(org.neo4j.driver.Record.class);

        when(mockCountRecord.get("nodeCount")).thenReturn(Values.value(1));
        when(mockCountResult.hasNext()).thenReturn(true);
        when(mockCountResult.next()).thenReturn(mockCountRecord);
        when(mockSession.run("MATCH (n) RETURN count(n) AS nodeCount"))
                .thenReturn(mockCountResult);

        when(mockRecord.get("name")).thenReturn(Values.value("word1"));
        when(mockSearchResult.hasNext()).thenReturn(true, false);
        when(mockSearchResult.next()).thenReturn(mockRecord);
        when(mockSession.run(
                "MATCH (w:Word) WHERE w.name IN $words RETURN w.name as name",
                Values.parameters("words", words)
        )).thenReturn(mockSearchResult);

        assertDoesNotThrow(() -> {
            graphManipulator.connectWithExistingWords(words);
        });
    }

    @Test
    void testConnectWords() {
        Set<String> words = Set.of("word1", "word2");
        Result mockResult = mock(Result.class);

        when(mockResult.hasNext()).thenReturn(false);
        when(mockSession.run(anyString(), any(Value.class))).thenReturn(mockResult);

        assertDoesNotThrow(() -> {
            graphManipulator.connectWords(words);
            verify(mockSession, atLeastOnce()).run(anyString(), any(Value.class));
        });
    }

    @Test
    void testIsOneLetterDifference() {
        String word1 = "word";
        String word2 = "lord";
        assertTrue(graphManipulator.isOneLetterDifference(word1, word2), 
                  "Should be true for one letter difference (word vs lord)");
        
        assertTrue(graphManipulator.isOneLetterDifference("word", "ward"), 
                  "Should be true for one letter difference");
        assertTrue(graphManipulator.isOneLetterDifference("cat", "hat"), 
                  "Should be true for one letter difference");
        assertFalse(graphManipulator.isOneLetterDifference("word", "word"), 
                   "Should be false for identical words");
        assertFalse(graphManipulator.isOneLetterDifference("word", "words"), 
                   "Should be false for different lengths");
        assertFalse(graphManipulator.isOneLetterDifference("word", "cold"), 
                   "Should be false for two letter differences");
    }
}
