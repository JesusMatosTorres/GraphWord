package config;

import api.GraphController;
import graph.GraphProcessor;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import storage.GraphAnalysis;
import storage.GraphManipulation;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

public class AppConfigTest {

    @Test
    void testInitializeGraphController() {
        GraphManipulation mockGraphManipulation = mock(GraphManipulation.class);
        GraphAnalysis mockGraphAnalysis = mock(GraphAnalysis.class);
        GraphProcessor mockGraphProcessor = mock(GraphProcessor.class);
        GraphController mockGraphController = new GraphController(mockGraphProcessor, mockGraphAnalysis);

        try (MockedStatic<AppConfig> mockedAppConfig = mockStatic(AppConfig.class)) {
            mockedAppConfig.when(AppConfig::initializeGraphController).thenReturn(mockGraphController);

            GraphController controller = AppConfig.initializeGraphController();
            assertNotNull(controller, "The GraphController must not be null.");
        }
    }
}
