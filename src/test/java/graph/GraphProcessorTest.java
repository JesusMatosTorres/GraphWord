package graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import storage.GraphManipulation;
import storage.WordFileReader;
import utils.GraphWordException;

import java.io.File;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
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

    void testProcessDirectory() {
        // Setup mocks for the directory and files
        File mockFile1 = mock(File.class);
        File mockFile2 = mock(File.class);

        // Configure mock files behavior
        when(mockFile1.isFile()).thenReturn(true);
        when(mockFile1.getName()).thenReturn("file1.txt");
        when(mockFile1.getAbsolutePath()).thenReturn("validDirectory/file1.txt");

        when(mockFile2.isFile()).thenReturn(true);
        when(mockFile2.getName()).thenReturn("file2.txt");
        when(mockFile2.getAbsolutePath()).thenReturn("validDirectory/file2.txt");

        try (MockedStatic<File> mockedFileStatic = mockStatic(File.class)) {
            File mockDirectory = mock(File.class);

            // Mock static File creation and directory behavior
            mockedFileStatic.when(() -> new File("validDirectory")).thenReturn(mockDirectory);
            when(mockDirectory.exists()).thenReturn(true);
            when(mockDirectory.isDirectory()).thenReturn(true);
            when(mockDirectory.listFiles()).thenReturn(new File[]{mockFile1, mockFile2});

            // Mock WordFileReader and GraphManipulation
            when(mockWordFileReader.extractWords("validDirectory/file1.txt")).thenReturn(Set.of("word1", "word2"));
            when(mockWordFileReader.extractWords("validDirectory/file2.txt")).thenReturn(Set.of("word3", "word4"));

            // Act: Process the directory
            graphProcessor.processDirectory("validDirectory");

            // Verify interactions with mocked dependencies
            verify(mockWordFileReader).extractWords("validDirectory/file1.txt");
            verify(mockWordFileReader).extractWords("validDirectory/file2.txt");
            verify(mockGraphManipulation).insertWords(Set.of("word1", "word2"));
            verify(mockGraphManipulation).connectWithExistingWords(Set.of("word1", "word2"));
            verify(mockGraphManipulation).insertWords(Set.of("word3", "word4"));
            verify(mockGraphManipulation).connectWithExistingWords(Set.of("word3", "word4"));

            // Assert processed files are tracked
            assertTrue(graphProcessor.getProcessedFiles().contains("file1.txt"));
            assertTrue(graphProcessor.getProcessedFiles().contains("file2.txt"));
        }
    }
    

}
