package api;

import com.google.gson.Gson;
import config.ConfigLoader;
import graph.GraphProcessor;
import storage.GraphAnalysis;
import utils.ErrorLogger;
import utils.GraphWordException;
import utils.ValidationUtils;

import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class GraphController {
    private final GraphProcessor graphProcessor;
    private final GraphAnalysis graphAnalysis;
    private final Gson gson;

    public GraphController(GraphProcessor graphProcessor, GraphAnalysis graphAnalysis) {
        this.graphProcessor = graphProcessor;
        this.graphAnalysis = graphAnalysis;
        this.gson = new Gson();
    }

    public void setupRoutes() {

        // Endpoint: Process all files in a directory
        post("/graph/process", (req, res) -> {
            try {
                String directoryPath = ConfigLoader.get("libros.directory");
                ValidationUtils.validateNotEmpty(directoryPath, "Configuration 'libros.directory' cannot be empty.");

                graphProcessor.processDirectory(directoryPath);
                res.status(200);
                return "All files processed successfully!";
            } catch (GraphWordException e) {
                ErrorLogger.logError("Error processing directory", e);
                res.status(400);
                return gson.toJson(Map.of("error", e.getMessage()));
            }
        });

        // Endpoint: Shortest path
        get("/graph/shortest-path", (req, res) -> {
            try {
                String source = req.queryParams("source");
                String target = req.queryParams("target");

                ValidationUtils.validateNotEmpty(source, "Parameter 'source' is required.");
                ValidationUtils.validateNotEmpty(target, "Parameter 'target' is required.");

                List<String> shortestPath = graphAnalysis.findShortestPath(source, target);
                res.type("application/json");
                return gson.toJson(shortestPath);
            } catch (GraphWordException e) {
                ErrorLogger.logError("Error finding shortest path", e);
                res.status(400);
                return gson.toJson(Map.of("error", e.getMessage()));
            }
        });

        // Endpoint: Communities
        get("/graph/communities", (req, res) -> {
            try {
                List<List<String>> communities = graphAnalysis.findCommunities();
                res.type("application/json");
                return gson.toJson(communities);
            } catch (GraphWordException e) {
                ErrorLogger.logError("Error retrieving communities", e);
                res.status(400);
                return gson.toJson(Map.of("error", e.getMessage()));
            }
        });

        // Endpoint: Isolated nodes
        get("/graph/isolated-nodes", (req, res) -> {
            try {
                List<String> isolatedNodes = graphAnalysis.findIsolatedNodes();
                res.type("application/json");
                return gson.toJson(isolatedNodes);
            } catch (GraphWordException e) {
                ErrorLogger.logError("Error retrieving isolated nodes", e);
                res.status(400);
                return gson.toJson(Map.of("error", e.getMessage()));
            }
        });

        // Endpoint: All paths between two nodes
        get("/graph/all-paths", (req, res) -> {
            try {
                String source = req.queryParams("source");
                String target = req.queryParams("target");

                ValidationUtils.validateNotEmpty(source, "Parameter 'source' is required.");
                ValidationUtils.validateNotEmpty(target, "Parameter 'target' is required.");

                var allPaths = graphAnalysis.findAllPaths(source, target);
                res.type("application/json");
                return gson.toJson(allPaths);
            } catch (GraphWordException e) {
                ErrorLogger.logError("Error retrieving all paths", e);
                res.status(400);
                return gson.toJson(Map.of("error", e.getMessage()));
            }
        });

        // Endpoint: Maximum distance in the graph
        get("/graph/maximum-distance", (req, res) -> {
            try {
                int maxDistance = graphAnalysis.findMaximumDistance();
                res.type("application/json");
                return gson.toJson(Map.of("maximumDistance", maxDistance));
            } catch (GraphWordException e) {
                ErrorLogger.logError("Error retrieving maximum distance", e);
                res.status(400);
                return gson.toJson(Map.of("error", e.getMessage()));
            }
        });

        // Endpoint: High connectivity nodes
        get("/graph/high-connectivity-nodes", (req, res) -> {
            try {
                String minDegreeParam = req.queryParams("minDegree");
                int minDegree = minDegreeParam != null ? Integer.parseInt(minDegreeParam) : 6;

                var highConnectivityNodes = graphAnalysis.findHighConnectivityNodes(minDegree);
                res.type("application/json");
                return gson.toJson(highConnectivityNodes);
            } catch (GraphWordException e) {
                ErrorLogger.logError("Error retrieving high connectivity nodes", e);
                res.status(400);
                return gson.toJson(Map.of("error", e.getMessage()));
            }
        });

        // Endpoint: Nodes with a specific degree of connectivity
        get("/graph/nodes-by-degree", (req, res) -> {
            try {
                String degreeParam = req.queryParams("degree");
                ValidationUtils.validateNotEmpty(degreeParam, "Parameter 'degree' is required.");

                int degree = Integer.parseInt(degreeParam);
                var nodes = graphAnalysis.findNodesByDegree(degree);
                res.type("application/json");
                return gson.toJson(nodes);
            } catch (GraphWordException e) {
                ErrorLogger.logError("Error retrieving nodes by degree", e);
                res.status(400);
                return gson.toJson(Map.of("error", e.getMessage()));
            }
        });
    }
}
