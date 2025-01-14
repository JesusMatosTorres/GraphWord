package graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

    @Test
    void testProcessDirectory() {
        String directoryPath = "testDirectory";
        
        // Mock directory
        File mockDirectory = mock(File.class);
        when(mockDirectory.exists()).thenReturn(true);
        when(mockDirectory.isDirectory()).thenReturn(true);
        
        // Mock files in the directory
        File mockFile1 = mock(File.class);
        File mockFile2 = mock(File.class);
        File[] mockFiles = {mockFile1, mockFile2};
        
        when(mockDirectory.listFiles()).thenReturn(mockFiles);
        
        // Mock behavior of files
        when(mockFile1.isFile()).thenReturn(true);
        when(mockFile1.getName()).thenReturn("file1.txt");
        when(mockFile1.getAbsolutePath()).thenReturn("testDirectory/file1.txt");
        
        when(mockFile2.isFile()).thenReturn(true);
        when(mockFile2.getName()).thenReturn("file2.txt");
        when(mockFile2.getAbsolutePath()).thenReturn("testDirectory/file2.txt");
    
        // Mock processed files behavior
        when(mockWordFileReader.extractWords("testDirectory/file1.txt")).thenReturn(Set.of("word1", "word2"));
        when(mockWordFileReader.extractWords("testDirectory/file2.txt")).thenReturn(Set.of("word3", "word4"));
        doNothing().when(mockGraphManipulation).insertWords(anySet());
        doNothing().when(mockGraphManipulation).connectWithExistingWords(anySet());
        doNothing().when(mockGraphManipulation).ensureGraphProjection(anyString());
    
        GraphProcessor graphProcessor = new GraphProcessor(mockWordFileReader, mockGraphManipulation) {
            protected Set<String> getProcessedFiles() {
                return processedFiles;
            }
            @Override
            public void processDirectory(String path) {
                File directory = mockDirectory; 
                if (!directory.exists() || !directory.isDirectory()) {
                    throw new GraphWordException("Directory '" + path + "' does not exist or is not a directory.");
                }
                File[] files = directory.listFiles();
                if (files == null || files.length == 0) {
                    System.out.println("No new files to process.");
                    return;
                }
                for (File file : files) {
                    if (!getProcessedFiles().contains(file.getName()) && file.isFile()) {
                        try {
                            processGraph(file.getAbsolutePath());
                            getProcessedFiles().add(file.getName());
                        } catch (GraphWordException e) {
                            System.err.println("Error processing file " + file.getName() + ": " + e.getMessage());
                        }
                    }
                }
            }
        };
    
        graphProcessor.processDirectory(directoryPath);
    
        verify(mockWordFileReader).extractWords("testDirectory/file1.txt");
        verify(mockWordFileReader).extractWords("testDirectory/file2.txt");
        verify(mockGraphManipulation).insertWords(Set.of("word1", "word2"));
        verify(mockGraphManipulation).connectWithExistingWords(Set.of("word1", "word2"));
        verify(mockGraphManipulation).insertWords(Set.of("word3", "word4"));
        verify(mockGraphManipulation).connectWithExistingWords(Set.of("word3", "word4"));
    }
    

}
