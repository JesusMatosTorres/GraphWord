package config;

import org.junit.jupiter.api.Test;
import utils.GraphWordException;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigLoaderTest {

    @Test
    void testGetConfigValue() {
        String uri = ConfigLoader.get("neo4j.uri");
        assertEquals("bolt://localhost:7687", uri, "The URI should match the test configuration.");
    }

    @Test
    void testMissingConfigKey() {
        String missingValue = ConfigLoader.get("nonexistent.key");
        assertNull(missingValue, "Expected null for a missing configuration key.");
    }

    @Test
    void testConfigFileNotFound() {
        assertThrows(GraphWordException.class, () -> {
            ConfigLoader.getClass().getClassLoader().getResourceAsStream("missing.properties");
        });
    }
}
