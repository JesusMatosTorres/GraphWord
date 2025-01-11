package org.example.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConfigLoaderTest {

    @Test
    void testLoadConfigFile() {
        ConfigLoader loader = new ConfigLoader();
        String config = loader.loadConfigFile("testConfigPath");
        assertNotNull(config, "The configuration file must not be null.");
    }
}
