package storage;

import java.util.List;

public interface GraphAnalysis {
    List<String> findShortestPath(String source, String target);

    List<List<String>> findCommunities();

    List<String> findIsolatedNodes();

    List<List<String>> findAllPaths(String source, String target);

    int findMaximumDistance();

    List<String> findHighConnectivityNodes(int minDegree);

    List<String> findNodesByDegree(int degree);

    void close();
}
