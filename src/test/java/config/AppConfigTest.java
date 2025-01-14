package config;

import api.GraphController;
import graph.GraphProcessor;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import storage.GraphAnalyzer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AppConfigTest {

    @Test
    void testInitializeGraphController_Success() {
        // Mock ConfigLoader
        try (MockedStatic<ConfigLoader> mockedConfig = mockStatic(ConfigLoader.class)) {
            // Mock configuration values
            when(ConfigLoader.get("neo4j.uri")).thenReturn("bolt://localhost:7687");
            when(ConfigLoader.get("neo4j.user")).thenReturn("neo4j");
            when(ConfigLoader.get("neo4j.password")).thenReturn("password");

            // Initialize controller
            GraphController controller = AppConfig.initializeGraphController();

            // Verify controller and its dependencies are properly initialized
            assertNotNull(controller, "GraphController should not be null");
            assertTrue(controller.getGraphProcessor() instanceof GraphProcessor);
            assertTrue(controller.getGraphAnalysis() instanceof GraphAnalyzer);
        }
    }

    @Test
    void testInitializeGraphController_MissingConfig() {
        try (MockedStatic<ConfigLoader> mockedConfig = mockStatic(ConfigLoader.class)) {
            // Mock missing configuration
            when(ConfigLoader.get("neo4j.uri")).thenReturn(null);
            when(ConfigLoader.get("neo4j.user")).thenReturn(null);
            when(ConfigLoader.get("neo4j.password")).thenReturn(null);

            // Verify initialization fails
            assertThrows(IllegalStateException.class, 
                () -> AppConfig.initializeGraphController(),
                "Should throw exception when configuration is missing");
        }
    }

    @Test
    void testInitializeGraphController_EmptyConfig() {
        try (MockedStatic<ConfigLoader> mockedConfig = mockStatic(ConfigLoader.class)) {
            // Mock empty configuration
            when(ConfigLoader.get("neo4j.uri")).thenReturn("");
            when(ConfigLoader.get("neo4j.user")).thenReturn("");
            when(ConfigLoader.get("neo4j.password")).thenReturn("");

            // Verify initialization fails
            assertThrows(IllegalStateException.class, 
                () -> AppConfig.initializeGraphController(),
                "Should throw exception when configuration is empty");
        }
    }

    @Test
    void testInitializeGraphController_ValidateComponents() {
        try (MockedStatic<ConfigLoader> mockedConfig = mockStatic(ConfigLoader.class)) {
            when(ConfigLoader.get("neo4j.uri")).thenReturn("bolt://localhost:7687");
            when(ConfigLoader.get("neo4j.user")).thenReturn("neo4j");
            when(ConfigLoader.get("neo4j.password")).thenReturn("password");

            GraphController controller = AppConfig.initializeGraphController();

            assertNotNull(controller.getGraphProcessor());
            assertNotNull(controller.getGraphAnalysis());
            assertTrue(controller.getGraphAnalysis() instanceof GraphAnalyzer);
        }
    }
}
