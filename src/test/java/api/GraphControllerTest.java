package api;

import graph.GraphProcessor;
import org.junit.jupiter.api.Test;
import storage.GraphAnalysis;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GraphControllerTest {

    @Test
    void testSetupRoutes() {
        GraphProcessor mockProcessor = mock(GraphProcessor.class);
        GraphAnalysis mockAnalysis = mock(GraphAnalysis.class);

        GraphController controller = new GraphController(mockProcessor, mockAnalysis);

        controller.setupRoutes();

        assertNotNull(controller, "GraphController should not be null.");
    }
}
