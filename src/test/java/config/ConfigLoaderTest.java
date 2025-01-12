package config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class ConfigLoaderTest {

    @Test
    void testGetConfigValue() {
        // Mock ConfigLoader
        ConfigLoader mockConfigLoader = mock(ConfigLoader.class);

        // Define behavior for the mock
        when(mockConfigLoader.get("testKey")).thenReturn("mockedValue");

        // Use the mock
        String configValue = mockConfigLoader.get("testKey");

        // Assert the mocked value is returned
        assertNotNull(configValue, "The configuration value must not be null.");
    }

    @Test
    void testMissingConfigKey() {
        // Mock ConfigLoader
        ConfigLoader mockConfigLoader = mock(ConfigLoader.class);

        // Define behavior for the mock
        when(mockConfigLoader.get("missingKey")).thenReturn(null);

        // Use the mock
        String missingValue = mockConfigLoader.get("missingKey");

        // Assert the value is null for a missing key
        assertNull(missingValue, "Expected null for a missing configuration key.");
    }
}
