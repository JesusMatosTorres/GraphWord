package storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class GraphManipulatorTest {

    private GraphManipulator graphManipulator;

    @BeforeEach
    void setUp() {
        String uri = System.getenv("NEO4J_URI");
        String user = System.getenv("NEO4J_USER");
        String password = System.getenv("NEO4J_PASSWORD");

        if (uri == null || user == null || password == null) {
            throw new IllegalStateException("Las variables de entorno NEO4J_URI, NEO4J_USER y NEO4J_PASSWORD deben estar configuradas.");
        }

        graphManipulator = new GraphManipulator(uri, user, password);
    }

    @Test
    void testInsertWords() {
        Set<String> words = Set.of("word1", "word2");
        assertDoesNotThrow(() -> graphManipulator.insertWords(words), "Inserting words should not throw an exception.");
    }

    @Test
    void testConnectWords() {
        Set<String> words = Set.of("word1", "word2", "word3");
        assertDoesNotThrow(() -> graphManipulator.connectWords(words), "Connecting words should not throw an exception.");
    }

    @Test
    void testEnsureGraphProjection() {
        String graphName = "testGraph";
        assertDoesNotThrow(() -> graphManipulator.ensureGraphProjection(graphName), "Ensuring graph projection should not throw an exception.");
    }
}
