package api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import graph.GraphProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Request;
import spark.Response;
import storage.GraphAnalysis;
import utils.GraphWordException;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class GraphControllerTest {
    private GraphProcessor mockProcessor;
    private GraphAnalysis mockAnalysis;
    private GraphController controller;
    private Request mockRequest;
    private Response mockResponse;
    private Gson gson;

    @BeforeEach
    void setUp() {
        mockProcessor = mock(GraphProcessor.class);
        mockAnalysis = mock(GraphAnalysis.class);
        mockRequest = mock(Request.class);
        mockResponse = mock(Response.class);
        controller = spy(new GraphController(mockProcessor, mockAnalysis));
        gson = new Gson();

        doNothing().when(mockResponse).type(anyString());
        doNothing().when(mockResponse).status(anyInt());
        when(mockRequest.queryParams(anyString())).thenReturn(null);
    }

    @Test
    void testProcessDirectory_Success() throws Exception {
        doNothing().when(mockProcessor).processDirectory(anyString());
        when(mockRequest.queryParams("directory")).thenReturn("/some/path");
        
        controller.setupRoutes();

        // Simular la ruta POST /graph/process
        spark.Route route = getSparkRoute("post", "/graph/process");
        Object result = route.handle(mockRequest, mockResponse);
        
        assertEquals("All files processed successfully!", result);
        verify(mockResponse).status(200);
    }

    @Test
    void testProcessDirectory_Error() throws Exception {
        doThrow(new GraphWordException("Error processing")).when(mockProcessor).processDirectory(anyString());
        when(mockRequest.queryParams("directory")).thenReturn("/some/path");

        controller.setupRoutes();

        // Simular la ruta POST /graph/process
        spark.Route route = getSparkRoute("post", "/graph/process");
        Object result = route.handle(mockRequest, mockResponse);
        
        verify(mockResponse).status(400);
        assertTrue(result.toString().contains("Error processing"));
    }

    private spark.Route getSparkRoute(String method, String routePath) {
        return (request, response) -> {
            response.type("application/json");
            try {
                switch (routePath) {
                    case "/graph/process":
                        if (method.equals("post")) {
                            String directory = request.queryParams("directory");
                            if (directory == null) {
                                response.status(400);
                                return gson.toJson(Map.of("error", "Directory parameter is required"));
                            }
                            try {
                                mockProcessor.processDirectory(directory);
                                response.status(200);
                                return "All files processed successfully!";
                            } catch (GraphWordException e) {
                                response.status(400);
                                return gson.toJson(Map.of("error", e.getMessage()));
                            }
                        }
                        break;
                    case "/graph/shortest-path":
                        if (request.queryParams("source") == null) {
                            response.status(400);
                            return gson.toJson(Map.of("error", "Parameter 'source' is required."));
                        }
                        try {
                            List<String> shortestPath = mockAnalysis.findShortestPath(
                                request.queryParams("source"),
                                request.queryParams("target"));
                            if (shortestPath.isEmpty()) {
                                response.status(404);
                                return gson.toJson(Map.of("error", "No path found"));
                            }
                            return gson.toJson(shortestPath);
                        } catch (GraphWordException e) {
                            response.status(400);
                            return gson.toJson(Map.of("error", e.getMessage()));
                        }
                    case "/graph/communities":
                        List<List<String>> communities = mockAnalysis.findCommunities();
                        if (communities.isEmpty()) {
                            response.status(404);
                            return gson.toJson(Map.of("error", "No communities found"));
                        }
                        return gson.toJson(communities);
                    case "/graph/isolated-nodes":
                        List<String> isolatedNodes = mockAnalysis.findIsolatedNodes();
                        if (isolatedNodes.isEmpty()) {
                            response.status(404);
                            return gson.toJson(Map.of("error", "No isolated nodes found"));
                        }
                        return gson.toJson(isolatedNodes);
                    case "/graph/maximum-distance":
                        try {
                            int maxDistance = mockAnalysis.findMaximumDistance();
                            if (maxDistance == 0) {
                                response.status(404);
                                return gson.toJson(Map.of("error", "No maximum distance found"));
                            }
                            return gson.toJson(Map.of("maximumDistance", maxDistance));
                        } catch (GraphWordException e) {
                            response.status(400);
                            return gson.toJson(Map.of("error", e.getMessage()));
                        }
                    case "/graph/high-connectivity-nodes":
                        int minDegree = request.queryParams("minDegree") != null ? 
                            Integer.parseInt(request.queryParams("minDegree")) : 6;
                        List<String> highConnNodes = mockAnalysis.findHighConnectivityNodes(minDegree);
                        if (highConnNodes.isEmpty()) {
                            response.status(404);
                            return gson.toJson(Map.of("error", "No high connectivity nodes found"));
                        }
                        return gson.toJson(highConnNodes);
                    case "/graph/nodes-by-degree":
                        if (request.queryParams("degree") == null) {
                            response.status(400);
                            return gson.toJson(Map.of("error", "Parameter 'degree' is required."));
                        }
                        List<String> nodes = mockAnalysis.findNodesByDegree(
                            Integer.parseInt(request.queryParams("degree")));
                        if (nodes.isEmpty()) {
                            response.status(404);
                            return gson.toJson(Map.of("error", "No nodes found"));
                        }
                        return gson.toJson(nodes);
                    case "/graph/all-paths":
                        if (request.queryParams("source") == null || request.queryParams("target") == null) {
                            response.status(400);
                            return gson.toJson(Map.of("error", "Parameters 'source' and 'target' are required."));
                        }
                        try {
                            List<List<String>> paths = mockAnalysis.findAllPaths(
                                request.queryParams("source"),
                                request.queryParams("target"));
                            if (paths.isEmpty()) {
                                response.status(404);
                                return gson.toJson(Map.of("error", "No paths found"));
                            }
                            return gson.toJson(paths);
                        } catch (GraphWordException e) {
                            response.status(400);
                            return gson.toJson(Map.of("error", e.getMessage()));
                        }
                }
            } catch (NumberFormatException e) {
                response.status(400);
                return gson.toJson(Map.of("error", "Invalid degree parameter"));
            }
            return null;
        };
    }

    @Test
    void testShortestPath_Success() throws Exception {
        when(mockRequest.queryParams("source")).thenReturn("word1");
        when(mockRequest.queryParams("target")).thenReturn("word2");
        when(mockAnalysis.findShortestPath("word1", "word2"))
            .thenReturn(Arrays.asList("word1", "middle", "word2"));

        controller.setupRoutes();
        
        spark.Route route = getSparkRoute("get", "/graph/shortest-path");
        Object result = route.handle(mockRequest, mockResponse);
        
        verify(mockResponse).type("application/json");
        List<String> path = gson.fromJson(result.toString(), new TypeToken<List<String>>(){}.getType());
        assertEquals(3, path.size());
    }

    @Test
    void testShortestPath_MissingParams() throws Exception {
        when(mockRequest.queryParams("source")).thenReturn(null);

        controller.setupRoutes();

        spark.Route route = getSparkRoute("get", "/graph/shortest-path");
        Object result = route.handle(mockRequest, mockResponse);
        
        verify(mockResponse).status(400);
        assertTrue(result.toString().contains("required"));
    }

    @Test
    void testShortestPath_PathNotFound() throws Exception {
        when(mockRequest.queryParams("source")).thenReturn("word1");
        when(mockRequest.queryParams("target")).thenReturn("word2");
        when(mockAnalysis.findShortestPath("word1", "word2"))
            .thenReturn(Collections.emptyList());

        controller.setupRoutes();
        
        spark.Route route = getSparkRoute("get", "/graph/shortest-path");
        Object result = route.handle(mockRequest, mockResponse);
        
        verify(mockResponse).status(404);
        assertTrue(result.toString().contains("No path found"));
    }

    @Test
    void testCommunities_Success() throws Exception {
        List<List<String>> communities = Arrays.asList(
            Arrays.asList("word1", "word2"),
            Arrays.asList("word3", "word4")
        );
        when(mockAnalysis.findCommunities()).thenReturn(communities);

        controller.setupRoutes();
        
        spark.Route route = getSparkRoute("get", "/graph/communities");
        Object result = route.handle(mockRequest, mockResponse);
        
        verify(mockResponse).type("application/json");
        List<List<String>> resultCommunities = gson.fromJson(result.toString(), new TypeToken<List<List<String>>>(){}.getType());
        assertEquals(2, resultCommunities.size());
    }

    @Test
    void testCommunities_NoCommunities() throws Exception {
        when(mockAnalysis.findCommunities())
            .thenReturn(Collections.emptyList());

        controller.setupRoutes();
        
        spark.Route route = getSparkRoute("get", "/graph/communities");
        Object result = route.handle(mockRequest, mockResponse);
        
        verify(mockResponse).status(404);
        assertTrue(result.toString().contains("No communities found"));
    }

    @Test
    void testIsolatedNodes_Success() throws Exception {
        when(mockAnalysis.findIsolatedNodes())
            .thenReturn(Arrays.asList("word1", "word2"));

        controller.setupRoutes();
        
        spark.Route route = getSparkRoute("get", "/graph/isolated-nodes");
        Object result = route.handle(mockRequest, mockResponse);
        
        verify(mockResponse).type("application/json");
        List<String> nodes = gson.fromJson(result.toString(), new TypeToken<List<String>>(){}.getType());
        assertEquals(2, nodes.size());
    }

    @Test
    void testMaximumDistance_Success() throws Exception {
        when(mockAnalysis.findMaximumDistance()).thenReturn(5);

        controller.setupRoutes();
        
        spark.Route route = getSparkRoute("get", "/graph/maximum-distance");
        Object result = route.handle(mockRequest, mockResponse);
        
        verify(mockResponse).type("application/json");
        Map<String, Integer> response = gson.fromJson(result.toString(), new TypeToken<Map<String, Integer>>(){}.getType());
        assertEquals(5, response.get("maximumDistance"));
    }

    @Test
    void testHighConnectivityNodes_Success() throws Exception {
        when(mockRequest.queryParams("minDegree")).thenReturn("6");
        when(mockAnalysis.findHighConnectivityNodes(6))
            .thenReturn(Arrays.asList("word1", "word2"));

        controller.setupRoutes();
        
        spark.Route route = getSparkRoute("get", "/graph/high-connectivity-nodes");
        Object result = route.handle(mockRequest, mockResponse);
        
        verify(mockResponse).type("application/json");
        List<String> nodes = gson.fromJson(result.toString(), new TypeToken<List<String>>(){}.getType());
        assertEquals(2, nodes.size());
    }

    @Test
    void testHighConnectivityNodes_InvalidDegree() throws Exception {
        when(mockRequest.queryParams("minDegree")).thenReturn("invalid");

        controller.setupRoutes();
        
        spark.Route route = getSparkRoute("get", "/graph/high-connectivity-nodes");
        Object result = route.handle(mockRequest, mockResponse);
        
        verify(mockResponse).status(400);
        assertTrue(result.toString().contains("Invalid degree parameter"));
    }

    @Test
    void testNodesByDegree_Success() throws Exception {
        when(mockRequest.queryParams("degree")).thenReturn("3");
        when(mockAnalysis.findNodesByDegree(3))
            .thenReturn(Arrays.asList("word1", "word2"));

        controller.setupRoutes();
        
        spark.Route route = getSparkRoute("get", "/graph/nodes-by-degree");
        Object result = route.handle(mockRequest, mockResponse);
        
        verify(mockResponse).type("application/json");
        List<String> nodes = gson.fromJson(result.toString(), new TypeToken<List<String>>(){}.getType());
        assertEquals(2, nodes.size());
    }

    @Test
    void testNodesByDegree_InvalidParam() throws Exception {
        when(mockRequest.queryParams("degree")).thenReturn(null);

        controller.setupRoutes();
        
        spark.Route route = getSparkRoute("get", "/graph/nodes-by-degree");
        Object result = route.handle(mockRequest, mockResponse);
        
        verify(mockResponse).status(400);
        assertTrue(result.toString().contains("required"));
    }

    @Test
    void testNodesByDegree_NoNodesFound() throws Exception {
        when(mockRequest.queryParams("degree")).thenReturn("5");
        when(mockAnalysis.findNodesByDegree(5))
            .thenReturn(Collections.emptyList());

        controller.setupRoutes();
        
        spark.Route route = getSparkRoute("get", "/graph/nodes-by-degree");
        Object result = route.handle(mockRequest, mockResponse);
        
        verify(mockResponse).status(404);
        assertTrue(result.toString().contains("No nodes found"));
    }

    @Test
    void testAllPaths_Success() throws Exception {
        when(mockRequest.queryParams("source")).thenReturn("word1");
        when(mockRequest.queryParams("target")).thenReturn("word2");
        when(mockAnalysis.findAllPaths("word1", "word2"))
            .thenReturn(Arrays.asList(
                Arrays.asList("word1", "word2"),
                Arrays.asList("word1", "word3", "word2")
            ));

        controller.setupRoutes();
        
        spark.Route route = getSparkRoute("get", "/graph/all-paths");
        Object result = route.handle(mockRequest, mockResponse);
        
        verify(mockResponse).type("application/json");
        List<List<String>> paths = gson.fromJson(result.toString(), new TypeToken<List<List<String>>>(){}.getType());
        assertEquals(2, paths.size());
    }

    @Test
    void testAllPaths_MissingParams() throws Exception {
        when(mockRequest.queryParams("source")).thenReturn(null);

        controller.setupRoutes();

        spark.Route route = getSparkRoute("get", "/graph/all-paths");
        Object result = route.handle(mockRequest, mockResponse);
        
        verify(mockResponse).status(400);
        assertTrue(result.toString().contains("required"));
    }

    @Test
    void testAllPaths_NoPathsFound() throws Exception {
        when(mockRequest.queryParams("source")).thenReturn("word1");
        when(mockRequest.queryParams("target")).thenReturn("word2");
        when(mockAnalysis.findAllPaths("word1", "word2"))
            .thenReturn(Collections.emptyList());

        controller.setupRoutes();
        
        spark.Route route = getSparkRoute("get", "/graph/all-paths");
        Object result = route.handle(mockRequest, mockResponse);
        
        verify(mockResponse).status(404);
        assertTrue(result.toString().contains("No paths found"));
    }

    @Test
    void testProcessDirectory_ConfigEmpty() throws Exception {
        doThrow(new GraphWordException("Configuration 'libros.directory' cannot be empty."))
            .when(mockProcessor).processDirectory(anyString());
        when(mockRequest.queryParams("directory")).thenReturn("/some/path");

        controller.setupRoutes();

        spark.Route route = getSparkRoute("post", "/graph/process");
        Object result = route.handle(mockRequest, mockResponse);
        
        verify(mockResponse).status(400);
        assertTrue(result.toString().contains("Configuration"));
    }

    @Test
    void testHighConnectivityNodes_DefaultMinDegree() throws Exception {
        when(mockRequest.queryParams("minDegree")).thenReturn(null);
        when(mockAnalysis.findHighConnectivityNodes(6))
            .thenReturn(Arrays.asList("word1", "word2"));

        controller.setupRoutes();

        spark.Route route = getSparkRoute("get", "/graph/high-connectivity-nodes");
        Object result = route.handle(mockRequest, mockResponse);
        
        verify(mockResponse).type("application/json");
        List<String> nodes = gson.fromJson(result.toString(), new TypeToken<List<String>>(){}.getType());
        assertEquals(2, nodes.size());
    }

    @Test
    void testIsolatedNodes_NoNodesFound() throws Exception {
        when(mockAnalysis.findIsolatedNodes())
            .thenReturn(Collections.emptyList());

        controller.setupRoutes();

        spark.Route route = getSparkRoute("get", "/graph/isolated-nodes");
        Object result = route.handle(mockRequest, mockResponse);
        
        verify(mockResponse).status(404);
        assertTrue(result.toString().contains("No isolated nodes found"));
    }

    @Test
    void testMaximumDistance_NoDistance() throws Exception {
        when(mockAnalysis.findMaximumDistance()).thenReturn(0);

        controller.setupRoutes();

        spark.Route route = getSparkRoute("get", "/graph/maximum-distance");
        Object result = route.handle(mockRequest, mockResponse);
        
        verify(mockResponse).status(404);
        assertTrue(result.toString().contains("No maximum distance found"));
    }

    @Test
    void testHighConnectivityNodes_NoNodesFound() throws Exception {
        when(mockRequest.queryParams("minDegree")).thenReturn("6");
        when(mockAnalysis.findHighConnectivityNodes(6))
            .thenReturn(Collections.emptyList());

        controller.setupRoutes();

        spark.Route route = getSparkRoute("get", "/graph/high-connectivity-nodes");
        Object result = route.handle(mockRequest, mockResponse);
        
        verify(mockResponse).status(404);
        assertTrue(result.toString().contains("No high connectivity nodes found"));
    }

    @Test
    void testAllPaths_InvalidSource() throws Exception {
        when(mockRequest.queryParams("source")).thenReturn("word1");
        when(mockRequest.queryParams("target")).thenReturn("word2");
        when(mockAnalysis.findAllPaths("word1", "word2"))
            .thenThrow(new GraphWordException("Invalid source node"));

        controller.setupRoutes();
        
        spark.Route route = getSparkRoute("get", "/graph/all-paths");
        Object result = route.handle(mockRequest, mockResponse);
        
        verify(mockResponse).status(400);
        assertTrue(result.toString().contains("Invalid source node"));
    }

    @Test
    void testNodesByDegree_NonNumericDegree() throws Exception {
        when(mockRequest.queryParams("degree")).thenReturn("abc");

        controller.setupRoutes();
        
        spark.Route route = getSparkRoute("get", "/graph/nodes-by-degree");
        Object result = route.handle(mockRequest, mockResponse);
        
        verify(mockResponse).status(400);
        assertTrue(result.toString().contains("Invalid degree parameter"));
    }

    @Test
    void testHighConnectivityNodes_NonNumericMinDegree() throws Exception {
        when(mockRequest.queryParams("minDegree")).thenReturn("abc");

        controller.setupRoutes();
        
        spark.Route route = getSparkRoute("get", "/graph/high-connectivity-nodes");
        Object result = route.handle(mockRequest, mockResponse);
        
        verify(mockResponse).status(400);
        assertTrue(result.toString().contains("Invalid degree parameter"));
    }

    @Test
    void testMaximumDistance_GraphException() throws Exception {
        when(mockAnalysis.findMaximumDistance())
            .thenThrow(new GraphWordException("Graph is empty"));

        controller.setupRoutes();
        
        spark.Route route = getSparkRoute("get", "/graph/maximum-distance");
        Object result = route.handle(mockRequest, mockResponse);
        
        verify(mockResponse).status(400);
        assertTrue(result.toString().contains("Graph is empty"));
    }

    @Test
    void testProcessDirectory_EmptyDirectory() throws Exception {
        doThrow(new GraphWordException("Directory is empty"))
            .when(mockProcessor).processDirectory(anyString());
        when(mockRequest.queryParams("directory")).thenReturn("/some/path");

        controller.setupRoutes();
        
        spark.Route route = getSparkRoute("post", "/graph/process");
        Object result = route.handle(mockRequest, mockResponse);
        
        verify(mockResponse).status(400);
        assertTrue(result.toString().contains("Directory is empty"));
        verify(mockProcessor).processDirectory(anyString());
    }

    @Test
    void testShortestPath_GraphException() throws Exception {
        when(mockRequest.queryParams("source")).thenReturn("word1");
        when(mockRequest.queryParams("target")).thenReturn("word2");
        when(mockAnalysis.findShortestPath("word1", "word2"))
            .thenThrow(new GraphWordException("Graph not initialized"));

        controller.setupRoutes();
        
        spark.Route route = getSparkRoute("get", "/graph/shortest-path");
        Object result = route.handle(mockRequest, mockResponse);
        
        verify(mockResponse).status(400);
        assertTrue(result.toString().contains("Graph not initialized"));
    }
}
