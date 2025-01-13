package storage;

import org.neo4j.driver.*;

import java.util.ArrayList;
import java.util.List;

public class GraphAnalyzer implements GraphAnalysis, AutoCloseable {

    private final Driver driver;

    public GraphAnalyzer(String uri, String user, String password) {
        this.driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    public GraphAnalyzer(Driver driver) { // Constructor adicional para inyección de Driver
        this.driver = driver;
    }

    // mejorable
    @Override
    public List<String> findShortestPath(String source, String target) {
        List<String> path = new ArrayList<>();
        try (Session session = driver.session()) {
            var result = session.run(
                    "MATCH path = shortestPath((start:Word {name: $source})-[:CONNECTED*]-(end:Word {name: $target})) " +
                            "RETURN [node IN nodes(path) | node.name] AS path",
                    org.neo4j.driver.Values.parameters("source", source, "target", target));
            if (result.hasNext()) {
                path = result.next().get("path").asList(org.neo4j.driver.Value::asString);
            }
        } catch (Exception e) {
            System.err.println("Error in findShortestPath: " + e.getMessage());
        }
        return path;
    }

    // terminada
    @Override
    public List<List<String>> findAllPaths(String source, String target) {
        List<List<String>> allPaths = new ArrayList<>();
        try (Session session = driver.session()) {
            var result = session.run(
                    "MATCH path = (start:Word {name: $source})-[*]-(end:Word {name: $target}) " +
                            "WHERE ALL(node IN nodes(path) WHERE single(x IN nodes(path) WHERE x = node)) " +
                            "RETURN [node IN nodes(path) | node.name] AS path, size(relationships(path)) AS length " +
                            "ORDER BY length ASC " +
                            "LIMIT 10",
                    Values.parameters("source", source, "target", target)
            );

            while (result.hasNext()) {
                org.neo4j.driver.Record record = result.next(); // Uso explícito de Record
                List<String> path = record.get("path").asList(Value::asString);
                allPaths.add(path);
            }
        } catch (Exception e) {
            System.err.println("Error in findAllPaths: " + e.getMessage());
        }
        return allPaths;
    }

    // terminada
    @Override
    public int findMaximumDistance() {
        try (Session session = driver.session()) {
            var result = session.run(
                    "CALL gds.allShortestPaths.stream('myGraph') " +
                            "YIELD sourceNodeId, targetNodeId, distance " +
                            "RETURN max(distance) AS maxDistance"
            );

            if (result.hasNext()) {
                org.neo4j.driver.Record record = result.next(); // Uso explícito de Record
                return record.get("maxDistance").asInt();
            }
        } catch (Exception e) {
            System.err.println("Error in findMaximumDistance: " + e.getMessage());
        }
        return -1;
    }

    // terminada
    @Override
    public List<List<String>> findCommunities() {
        List<List<String>> communities = new ArrayList<>();
        try (Session session = driver.session()) {
            var result = session.run(
                    "CALL gds.louvain.stream('myGraph') " +
                            "YIELD communityId, nodeId " +
                            "MATCH (n:Word) WHERE id(n) = nodeId " +
                            "RETURN communityId, collect(n.name) AS members " +
                            "ORDER BY communityId"
            );

            while (result.hasNext()) {
                org.neo4j.driver.Record record = result.next(); // Uso explícito de Record
                communities.add(record.get("members").asList(Value::asString));
            }
        } catch (Exception e) {
            System.err.println("Error in findCommunities: " + e.getMessage());
        }
        return communities;
    }

    // terminada
    @Override
    public List<String> findIsolatedNodes() {
        List<String> isolatedNodes = new ArrayList<>();
        try (Session session = driver.session()) {
            var result = session.run(
                    "MATCH (w:Word) WHERE NOT (w)-[]-() RETURN w.name AS name");
            while (result.hasNext()) {
                org.neo4j.driver.Record record = result.next(); // Uso explícito de Record
                isolatedNodes.add(record.get("name").asString());
            }
        } catch (Exception e) {
            System.err.println("Error in findIsolatedNodes: " + e.getMessage());
        }
        return isolatedNodes;
    }

    @Override
    public List<String> findHighConnectivityNodes(int minDegree) {
        List<String> highConnectivityNodes = new ArrayList<>();
        try (Session session = driver.session()) {
            var result = session.run(
                    """
                    CALL gds.degree.stream('myGraph')
                    YIELD nodeId, score
                    WHERE score >= $minDegree
                    MATCH (n:Word) WHERE id(n) = nodeId
                    RETURN n.name AS name, score
                    ORDER BY score DESC
                    """,
                    Values.parameters("minDegree", minDegree)
            );

            while (result.hasNext()) {
                org.neo4j.driver.Record record = result.next(); // Uso explícito de Record
                highConnectivityNodes.add(record.get("name").asString());
            }
        } catch (Exception e) {
            System.err.println("Error in findHighConnectivityNodes: " + e.getMessage());
        }
        return highConnectivityNodes;
    }

    @Override
    public List<String> findNodesByDegree(int degree) {
        List<String> nodes = new ArrayList<>();
        try (Session session = driver.session()) {
            var result = session.run(
                    """
                    MATCH (n:Word)-[r]-()
                    WITH n, COUNT(r) AS degree
                    WHERE degree = $degree
                    RETURN n.name AS name
                    """,
                    org.neo4j.driver.Values.parameters("degree", degree)
            );

            while (result.hasNext()) {
                org.neo4j.driver.Record record = result.next(); // Uso explícito de Record
                nodes.add(record.get("name").asString());
            }
        } catch (Exception e) {
            System.err.println("Error in findNodesByDegree: " + e.getMessage());
            e.printStackTrace();
        }

        if (nodes.isEmpty()) {
            return List.of("Error: No nodes found with the specified degree.");
        }

        return nodes;
    }

    @Override
    public void close() {
        driver.close();
    }
}
