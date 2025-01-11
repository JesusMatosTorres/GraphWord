package org.example.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConfigLoaderTest {

    @Test
    void testLoadConfigFile() {
        ConfigLoader loader = new ConfigLoader();
        String config = loader.loadConfigFile("testConfigPath");
        assertNotNull(config, "El archivo de configuración no debe ser nulo.");
    }
}
