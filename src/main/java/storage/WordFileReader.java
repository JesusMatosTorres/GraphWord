package storage;

import java.util.Set;

public interface WordFileReader {
    Set<String> extractWords(String pathOrKey);
}

