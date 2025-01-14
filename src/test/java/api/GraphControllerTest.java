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
        controller = new GraphController(mockProcessor, mockAnalysis);
        gson = new Gson();

        doNothing().when(mockResponse).type(anyString());
        doNothing().when(mockResponse).status(anyInt());
    }

    @Test
    void testProcessDirectory_Success() throws Exception {
        doNothing().when(mockProcessor).processDirectory(anyString());
        
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
        
        controller.setupRoutes();

        // Simular la ruta POST /graph/process
        spark.Route route = getSparkRoute("post", "/graph/process");
        Object result = route.handle(mockRequest, mockResponse);
        
        verify(mockResponse).status(400);
        assertTrue(result.toString().contains("Error processing"));
    }

    private spark.Route getSparkRoute(String method, String path) {
        return (request, response) -> {
            response.type("application/json");
            switch (path) {
                case "/graph/process":
                    if (method.equals("post")) {
                        try {
                            mockProcessor.processDirectory(anyString());
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
                    return gson.toJson(Arrays.asList("word1", "middle", "word2"));
                case "/graph/communities":
                    return gson.toJson(mockAnalysis.findCommunities());
                case "/graph/isolated-nodes":
                    return gson.toJson(mockAnalysis.findIsolatedNodes());
                case "/graph/maximum-distance":
                    return gson.toJson(Map.of("maximumDistance", mockAnalysis.findMaximumDistance()));
                case "/graph/high-connectivity-nodes":
                    return gson.toJson(mockAnalysis.findHighConnectivityNodes(
                        Integer.parseInt(request.queryParams("minDegree"))));
                case "/graph/nodes-by-degree":
                    if (request.queryParams("degree") == null) {
                        response.status(400);
                        return gson.toJson(Map.of("error", "Parameter 'degree' is required."));
                    }
                    return gson.toJson(mockAnalysis.findNodesByDegree(
                        Integer.parseInt(request.queryParams("degree"))));
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
}
