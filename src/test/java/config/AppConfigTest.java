package org.example.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AppConfigTest {

    @Test
    void testLoadConfiguration() {
        AppConfig config = new AppConfig();
        String value = config.getConfigValue("testKey");
        assertNotNull(value, "The configuration value must not be null.");
    }
}
