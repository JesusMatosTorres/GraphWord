package org.example.api;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GraphControllerTest {

    @Test
    void testHandleRequest() {
        GraphController controller = new GraphController();
        String response = controller.handleRequest("testRequest");
        assertNotNull(response, "La respuesta no debe ser nula.");
    }
}
