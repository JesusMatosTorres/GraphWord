package storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

public class GraphManipulatorTest {

    private GraphManipulator mockGraphManipulator;

    @BeforeEach
    void setUp() {
        mockGraphManipulator = mock(GraphManipulator.class);
    }

    @Test
    void testInsertWords() {
        Set<String> words = Set.of("word1", "word2", "word3");

        doNothing().when(mockGraphManipulator).insertWords(words);

        assertDoesNotThrow(() -> mockGraphManipulator.insertWords(words),
                "Inserting words should not throw an exception.");
    }

    @Test
    void testConnectWords() {
        Set<String> words = Set.of("word1", "word2", "word3");

        doNothing().when(mockGraphManipulator).connectWords(words);

        assertDoesNotThrow(() -> mockGraphManipulator.connectWords(words),
                "Connecting words should not throw an exception.");
    }

    @Test
    void testEnsureGraphProjection() {
        String graphName = "testGraph";

        doNothing().when(mockGraphManipulator).ensureGraphProjection(graphName);

        assertDoesNotThrow(() -> mockGraphManipulator.ensureGraphProjection(graphName),
                "Ensuring graph projection should not throw an exception.");
    }
}
