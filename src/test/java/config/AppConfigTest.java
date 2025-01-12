package config;

import api.GraphController;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AppConfigTest {

    @Test
    void testInitializeGraphController() {
        GraphController controller = AppConfig.initializeGraphController();

        assertNotNull(controller, "The GraphController must not be null.");
    }
}
