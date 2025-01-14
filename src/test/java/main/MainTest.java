package main;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.MockedStatic;

import spark.Spark;
import api.GraphController;
import config.AppConfig;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class MainTest {
    private GraphController mockController;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private MockedStatic<AppConfig> mockedAppConfig;

    @BeforeEach
    void setUp() {
        mockController = mock(GraphController.class);
        outContent.reset();
        errContent.reset();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        
        mockedAppConfig = mockStatic(AppConfig.class);
        mockedAppConfig.when(AppConfig::initializeGraphController).thenReturn(mockController);
    }

    @Test
    void testMainInitialization() {
        assertDoesNotThrow(() -> {
            Main.main(new String[]{});
            
            Thread.sleep(1000);
            
            String output = outContent.toString();
            assertTrue(output.contains("Server started at http://localhost:4567"), 
                "Expected output to contain startup message, but was: '" + output + "'");
        });
    }

    @Test
    void testMainConfiguresSparkCorrectly() {
        assertDoesNotThrow(() -> {
            Main.main(new String[]{});
            
            Thread.sleep(1000);
            
            verify(mockController).setupRoutes();
        });
    }

    @Test
    void testMainHandlesInitializationError() {
        mockedAppConfig.when(AppConfig::initializeGraphController)
                .thenThrow(new RuntimeException("Initialization error"));

        Main.main(new String[]{});
        assertTrue(errContent.toString().contains("Error during startup"));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        mockedAppConfig.close();
        Spark.stop();
        Spark.awaitStop();
    }
} 
