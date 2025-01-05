package config;

import api.GraphController;
import graph.GraphProcessor;
import storage.GraphManipulation;
import storage.GraphAnalysis;
import storage.GraphManipulator;
import storage.GraphAnalyzer;
import storage.LocalFileReader;

public class AppConfig {

    public static GraphController initializeGraphController() {
        // Leer configuración desde el ConfigLoader
        String uri = ConfigLoader.get("neo4j.uri");
        String user = ConfigLoader.get("neo4j.user");
        String password = ConfigLoader.get("neo4j.password");

        // Crear dependencias
        GraphManipulation graphManipulation = new GraphManipulator(uri, user, password);
        GraphAnalysis graphAnalysis = new GraphAnalyzer(uri, user, password);
        GraphProcessor graphProcessor = new GraphProcessor(new LocalFileReader(), graphManipulation);

        // Crear y devolver el GraphController
        return new GraphController(graphProcessor, graphAnalysis);
    }
}

