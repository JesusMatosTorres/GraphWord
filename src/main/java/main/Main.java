package main;

import api.GraphController;
import config.AppConfig;
import static spark.Spark.port;

public class Main {
    public static void main(String[] args) {
        try {
            // Inicializar controlador principal desde AppConfig
            GraphController graphController = AppConfig.initializeGraphController();

            // Configurar servidor Spark
            port(4567);
            graphController.setupRoutes();

            System.out.println("Server started at http://localhost:4567");
        } catch (Exception e) {
            System.err.println("Error during startup: " + e.getMessage());
        }
    }
}
