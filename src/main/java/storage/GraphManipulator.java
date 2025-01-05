package storage;

import org.neo4j.driver.*;
import utils.GraphWordException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GraphManipulator implements GraphManipulation, AutoCloseable {

    private final Driver driver;

    public GraphManipulator(String uri, String user, String password) {
        this.driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    @Override
    public void ensureGraphProjection(String graphName) {
        try (Session session = driver.session()) {
            // Delete and project the graph
            session.run(
                        "CALL gds.graph.drop($graphName)",
                        org.neo4j.driver.Values.parameters("graphName", graphName)
            );
            session.run(
                    "CALL gds.graph.project($graphName, ['Word'], " +
                            "{CONNECTED: {type: 'CONNECTED', orientation: 'UNDIRECTED'}})",
                    Values.parameters("graphName", graphName)
            );

        } catch (Exception e) {
            throw new GraphWordException("Failed to project the graph: " + e.getMessage(), e);
        }
    }


    @Override
    public void insertWords(Set<String> words) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                for (String word : words) {
                    tx.run("MERGE (w:Word {name: $word})", Values.parameters("word", word));
                }
                return null;
            });
        } catch (Exception e) {
            throw new GraphWordException("Failed to insert words: " + e.getMessage(), e);
        }
    }

    @Override
    public void connectWithExistingWords(Set<String> newWords) {
        try (Session session = driver.session()) {
            // Comprobar si el grafo está vacío
            var result = session.run("MATCH (n) RETURN count(n) AS nodeCount");
            if (result.hasNext() && result.next().get("nodeCount").asInt() == 0) {
                System.out.println("Graph is empty. Connecting new words among themselves.");
                connectWords(newWords);
                return;
            }

            // Conectar palabras con las existentes
            for (String newWord : newWords) {
                session.writeTransaction(tx -> {
                    var queryResult = tx.run("""
                    MATCH (existing:Word)
                    WHERE apoc.text.levenshteinDistance(existing.name, $newWord) = 1
                    RETURN existing.name AS existingWord
                    """, Values.parameters("newWord", newWord));

                    while (queryResult.hasNext()) {
                        var record = queryResult.next();
                        String existingWord = record.get("existingWord").asString();
                        tx.run("""
                        MATCH (w1:Word {name: $newWord}), (w2:Word {name: $existingWord})
                        MERGE (w1)-[:CONNECTED]-(w2)
                        """, Values.parameters("newWord", newWord, "existingWord", existingWord));
                    }
                    return null;
                });
            }
        }
    }


    public void connectWords(Set<String> words) {
        List<String> wordList = new ArrayList<>(words);
        try (Session session = driver.session()) {
            for (int i = 0; i < wordList.size(); i++) {
                for (int j = i + 1; j < wordList.size(); j++) {
                    String word1 = wordList.get(i);
                    String word2 = wordList.get(j);
                    if (isOneLetterDifference(word1, word2)) {
                        session.run("""
                            MERGE (w1:Word {name: $word1})
                            MERGE (w2:Word {name: $word2})
                            MERGE (w1)-[:CONNECTED]-(w2)
                            """, org.neo4j.driver.Values.parameters("word1", word1, "word2", word2));

                    }
                }
            }
        }
    }

    // Auxiliary method to check the difference of a letter
    private boolean isOneLetterDifference(String word1, String word2) {
        if (word1.length() != word2.length()) return false;
        int diffCount = 0;
        for (int i = 0; i < word1.length(); i++) {
            if (word1.charAt(i) != word2.charAt(i)) {
                diffCount++;
                if (diffCount > 1) return false;
            }
        }
        return diffCount == 1;
    }

    @Override
    public void close() {
        driver.close();
    }
}
