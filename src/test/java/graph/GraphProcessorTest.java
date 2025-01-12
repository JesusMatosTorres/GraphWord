package graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import storage.GraphManipulation;
import storage.WordFileReader;
import utils.GraphWordException;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GraphProcessorTest {

    private WordFileReader mockWordFileReader;
    private GraphManipulation mockGraphManipulation;
    private GraphProcessor graphProcessor;

    @BeforeEach
    void setup() {
        mockWordFileReader = mock(WordFileReader.class);
        mockGraphManipulation = mock(GraphManipulation.class);
        graphProcessor = new GraphProcessor(mockWordFileReader, mockGraphManipulation);
    }

    @Test
    void testProcessGraphValidInput() {
        String validFilePath = "validFilePath";

        // Mock the behavior of WordFileReader and GraphManipulation
        when(mockWordFileReader.extractWords(validFilePath)).thenReturn(Set.of("word1", "word2"));
        doNothing().when(mockGraphManipulation).insertWords(anySet());
        doNothing().when(mockGraphManipulation).connectWithExistingWords(anySet());
        doNothing().when(mockGraphManipulation).ensureGraphProjection(anyString());

        // Act
        assertDoesNotThrow(() -> graphProcessor.processGraph(validFilePath));

        // Verify interactions with mocks
        verify(mockWordFileReader).extractWords(validFilePath);
        verify(mockGraphManipulation).insertWords(Set.of("word1", "word2"));
        verify(mockGraphManipulation).connectWithExistingWords(Set.of("word1", "word2"));
        verify(mockGraphManipulation).ensureGraphProjection("myGraph");
    }

    @Test
    void testProcessGraphInvalidInput() {
        // Case 1: Null file path
        assertThrows(GraphWordException.class, () -> graphProcessor.processGraph(null),
                "Should throw an exception if the file path is null.");

        // Case 2: Empty file path
        assertThrows(GraphWordException.class, () -> graphProcessor.processGraph(""),
                "Should throw an exception if the file path is empty.");
    }

    @Test
    void testProcessGraphNoWordsExtracted() {
        String filePath = "emptyFilePath";

        // Mock WordFileReader to return an empty set of words
        when(mockWordFileReader.extractWords(filePath)).thenReturn(Set.of());

        // Act & Assert
        GraphWordException exception = assertThrows(GraphWordException.class, 
                () -> graphProcessor.processGraph(filePath),
                "Should throw an exception if no words are extracted.");
        assertEquals("No valid words found in file: " + filePath, exception.getMessage());

        // Verify interactions with mocks
        verify(mockWordFileReader).extractWords(filePath);
        verifyNoInteractions(mockGraphManipulation); // No interactions with GraphManipulation
    }

    @Test
    void testProcessGraphInsertionFailure() {
        String filePath = "validFilePath";

        // Mock WordFileReader to return valid words
        when(mockWordFileReader.extractWords(filePath)).thenReturn(Set.of("word1", "word2"));

        // Mock GraphManipulation to throw an exception during insertion
        doThrow(new RuntimeException("Insertion failed")).when(mockGraphManipulation).insertWords(anySet());

        // Act & Assert
        GraphWordException exception = assertThrows(GraphWordException.class,
                () -> graphProcessor.processGraph(filePath),
                "Should throw an exception if word insertion fails.");
        assertEquals("Failed to insert words into the graph storage.", exception.getMessage());

        // Verify interactions with mocks
        verify(mockWordFileReader).extractWords(filePath);
        verify(mockGraphManipulation).insertWords(Set.of("word1", "word2"));
    }
}
