package storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GraphManipulatorTest {

    private GraphManipulator graphManipulator;

    @BeforeEach
    void setUp() {
        graphManipulator = mock(GraphManipulator.class);
    }

    @Test
    void testInsertWords() {
        Set<String> words = Set.of("word1", "word2", "word3");

        doNothing().when(graphManipulator).insertWords(words);

        assertDoesNotThrow(() -> graphManipulator.insertWords(words));
        verify(graphManipulator, times(1)).insertWords(words);
    }

    @Test
    void testConnectWords() {
        Set<String> words = Set.of("word1", "word2", "word3");

        doNothing().when(graphManipulator).connectWords(words);

        assertDoesNotThrow(() -> graphManipulator.connectWords(words));
        verify(graphManipulator, times(1)).connectWords(words);
    }

    @Test
    void testEnsureGraphProjectionSuccess() {
        String graphName = "testGraph";

        doNothing().when(graphManipulator).ensureGraphProjection(graphName);

        assertDoesNotThrow(() -> graphManipulator.ensureGraphProjection(graphName));
        verify(graphManipulator, times(1)).ensureGraphProjection(graphName);
    }

    @Test
    void testEnsureGraphProjectionError() {
        String graphName = "testGraph";

        doThrow(new RuntimeException("Error")).when(graphManipulator).ensureGraphProjection(graphName);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            graphManipulator.ensureGraphProjection(graphName);
        });

        assertEquals("Error", exception.getMessage());
    }

    @Test
    void testConnectWithExistingWords() {
        Set<String> words = Set.of("word1", "word2");

        doNothing().when(graphManipulator).connectWithExistingWords(words);

        assertDoesNotThrow(() -> graphManipulator.connectWithExistingWords(words));
        verify(graphManipulator, times(1)).connectWithExistingWords(words);
    }
}
