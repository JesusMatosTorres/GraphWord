package config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConfigLoaderTest {

    @Test
    void testGetConfigValue() {
        String configValue = ConfigLoader.get("testKey");
        assertNotNull(configValue, "The configuration value must not be null.");
    }

    @Test
    void testMissingConfigKey() {
        String missingValue = ConfigLoader.get("missingKey");
        assertNotNull(missingValue, "Expected null for a missing configuration key.");
    }
}
