package config;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import utils.GraphWordException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ConfigLoaderTest {

    @Test
    void testGetConfigValueWithMock() {
        try (MockedStatic<ConfigLoader> mockedStatic = mockStatic(ConfigLoader.class)) {
            mockedStatic.when(() -> ConfigLoader.get("neo4j.uri"))
                        .thenReturn("bolt://localhost:7687");

            String uri = ConfigLoader.get("neo4j.uri");

            assertEquals("bolt://localhost:7687", uri, "The URI should match the mocked configuration.");
        }
    }

    @Test
    void testMissingConfigKeyWithMock() {
        try (MockedStatic<ConfigLoader> mockedStatic = mockStatic(ConfigLoader.class)) {
            mockedStatic.when(() -> ConfigLoader.get("nonexistent.key"))
                        .thenReturn(null);

            String missingValue = ConfigLoader.get("nonexistent.key");

            assertNull(missingValue, "Expected null for a missing configuration key.");
        }
    }

    @Test
    void testConfigFileNotFoundWithMock() {
        try (MockedStatic<ConfigLoader> mockedStatic = mockStatic(ConfigLoader.class)) {
            mockedStatic.when(() -> ConfigLoader.get("missingKey"))
                        .thenThrow(new GraphWordException("Config file not found"));

            assertThrows(GraphWordException.class, () -> ConfigLoader.get("missingKey"));
        }
    }
}
