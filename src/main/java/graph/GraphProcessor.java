package graph;

import storage.GraphManipulation;
import storage.WordFileReader;
import utils.GraphWordException;
import utils.ValidationUtils;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class GraphProcessor {
    private final WordFileReader wordFileReader;
    private final GraphManipulation graphManipulation;
    private Set<String> processedFiles;

    public GraphProcessor(WordFileReader wordFileReader, GraphManipulation graphManipulation) {
        this.wordFileReader = wordFileReader;
        this.graphManipulation = graphManipulation;
        this.processedFiles = new HashSet<>();
    }

    public void processDirectory(String directoryPath) {
        // Validate that the directory is not null or empty
        ValidationUtils.validateNotEmpty(directoryPath, "Directory path cannot be empty.");

        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new GraphWordException("Directory '" + directoryPath + "' does not exist or is not a directory.");
        }

        File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("No new files to process.");
            return;
        }

        for (File file : files) {
            if (!processedFiles.contains(file.getName()) && file.isFile()) {
                try {
                    processGraph(file.getAbsolutePath());
                    processedFiles.add(file.getName());
                } catch (GraphWordException e) {
                    System.err.println("Error processing file " + file.getName() + ": " + e.getMessage());
                }
            }
        }

        System.out.println("Directory processing completed.");
    }

    public void processGraph(String filePath) {
        ValidationUtils.validateNotEmpty(filePath, "File path cannot be empty.");
        System.out.println("Processing graph from file: " + filePath);

        // Extract words from file
        Set<String> words;
        try {
            words = wordFileReader.extractWords(filePath);
        } catch (Exception e) {
            throw new GraphWordException("Failed to read words from file: " + filePath, e);
        }

        if (words.isEmpty()) {
            throw new GraphWordException("No valid words found in file: " + filePath);
        }

        System.out.println("Extracted words: " + words.size());

        // Insert words into the graph
        try {
            graphManipulation.insertWords(words);
        } catch (Exception e) {
            throw new GraphWordException("Failed to insert words into the graph storage.", e);
        }

        // Connect words
        try {
            graphManipulation.connectWithExistingWords(words);
        } catch (Exception e) {
            throw new GraphWordException("Failed to connect words with existing words in the graph storage.", e);
        }

        System.out.println("Graph successfully processed for file: " + filePath);

        // Project the graph
        try {
            graphManipulation.ensureGraphProjection("myGraph");
        } catch (Exception e) {
            throw new GraphWordException("Failed to project the graph.", e);
        }

        System.out.println("Graph successfully projected for file: " + filePath);
    }

    protected Set<String> getProcessedFiles() {
        return processedFiles;
    }

    public WordFileReader getWordFileReader() {
        return wordFileReader;
    }

    public GraphManipulation getGraphManipulation() {
        return graphManipulation;
    }
}
