package config;

import org.junit.jupiter.api.Test;
import utils.GraphWordException;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigLoaderTest {

    @Test
    void testGetConfigValue() {
        String value = ConfigLoader.get("testKey");
        assertNotNull(value, "The configuration value must not be null.");
        assertEquals("testValue", value, "The configuration value should match 'testValue'.");
    }

    @Test
    void testMissingConfigKey() {
        String value = ConfigLoader.get("missingKey");
        assertNull(value, "Expected null for a missing configuration key.");
    }

    @Test
    void testConfigFileNotFound() {
        Exception exception = assertThrows(GraphWordException.class, () -> {
            ConfigLoader.get("nonExistentKey");
        });

        assertTrue(exception.getMessage().contains("Configuration file not found!"));
    }
}
