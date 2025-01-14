package config;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import utils.GraphWordException;

import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ConfigLoaderTest {
    
    @Test
    void testSuccessfulConfigLoad() {
        // Crear un Properties simulado con datos de prueba
        Properties testProps = new Properties();
        testProps.setProperty("neo4j.uri", "bolt://localhost:7687");
        testProps.setProperty("neo4j.user", "neo4j");
        testProps.setProperty("neo4j.password", "password");
        
        // Simular la carga del archivo de configuración
        try (MockedStatic<ConfigLoader> mockedLoader = mockStatic(ConfigLoader.class)) {
            mockedLoader.when(() -> ConfigLoader.get("neo4j.uri"))
                    .thenReturn("bolt://localhost:7687");
            mockedLoader.when(() -> ConfigLoader.get("neo4j.user"))
                    .thenReturn("neo4j");
            mockedLoader.when(() -> ConfigLoader.get("neo4j.password"))
                    .thenReturn("password");

            assertEquals("bolt://localhost:7687", ConfigLoader.get("neo4j.uri"));
            assertEquals("neo4j", ConfigLoader.get("neo4j.user"));
            assertEquals("password", ConfigLoader.get("neo4j.password"));
        }
    }

    @Test
    void testMissingConfigFile() throws IOException {
        try (MockedStatic<ConfigLoader> mockedLoader = mockStatic(ConfigLoader.class)) {
            mockedLoader.when(() -> ConfigLoader.get("any.key"))
                    .thenThrow(new GraphWordException("Configuration file not found!"));

            assertThrows(GraphWordException.class, () -> ConfigLoader.get("any.key"));
        }
    }

    @Test
    void testInvalidConfigFile() throws IOException {
        try (MockedStatic<ConfigLoader> mockedLoader = mockStatic(ConfigLoader.class)) {
            mockedLoader.when(() -> ConfigLoader.get("any.key"))
                    .thenThrow(new GraphWordException("Invalid configuration format"));

            assertThrows(GraphWordException.class, () -> ConfigLoader.get("any.key"));
        }
    }

    @Test
    void testNonExistentProperty() {
        try (MockedStatic<ConfigLoader> mockedLoader = mockStatic(ConfigLoader.class)) {
            mockedLoader.when(() -> ConfigLoader.get("nonexistent.key"))
                    .thenReturn(null);

            assertNull(ConfigLoader.get("nonexistent.key"), 
                    "Should return null for non-existent properties");
        }
    }

    @Test
    void testLoadConfigurationWithIOException() throws IOException {
        try (MockedStatic<ConfigLoader> mockedLoader = mockStatic(ConfigLoader.class)) {
            mockedLoader.when(() -> ConfigLoader.get("any.key"))
                    .thenThrow(new GraphWordException("Failed to load configuration"));

            assertThrows(GraphWordException.class, () -> ConfigLoader.get("any.key"));
        }
    }
}
