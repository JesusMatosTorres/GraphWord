package storage;

import java.util.Set;

public interface GraphManipulation {
    void ensureGraphProjection(String graphName);

    void insertWords(Set<String> words);

    void connectWithExistingWords(Set<String> words);

    void close();
}




