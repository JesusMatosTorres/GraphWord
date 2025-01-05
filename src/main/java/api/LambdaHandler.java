package api;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import graph.GraphProcessor;
import storage.LocalFileReader;
import utils.ValidationUtils;

import java.util.HashMap;
import java.util.Map;

public class LambdaHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        context.getLogger().log("Input: " + input);

        Map<String, Object> response = new HashMap<>();

        try {
            // Validar y obtener parámetros de entrada
            String operation = (String) input.get("operation");
            String pathOrKey = (String) input.get("pathOrKey");

            ValidationUtils.validateNotEmpty(operation, "Operation parameter is required.");
            ValidationUtils.validateNotEmpty(pathOrKey, "PathOrKey parameter is required.");

            // Configuración del entorno Lambda
            String neo4jUri = System.getenv("NEO4J_URI");
            String neo4jUser = System.getenv("NEO4J_USER");
            String neo4jPassword = System.getenv("NEO4J_PASSWORD");
            String wordDatabasePath = "/tmp/wordDatabase.json"; // Almacenamiento temporal en Lambda

            ValidationUtils.validateNotEmpty(neo4jUri, "Environment variable NEO4J_URI is missing.");
            ValidationUtils.validateNotEmpty(neo4jUser, "Environment variable NEO4J_USER is missing.");
            ValidationUtils.validateNotEmpty(neo4jPassword, "Environment variable NEO4J_PASSWORD is missing.");

            // Inicializar instancias necesarias


            // Ejecutar operación solicitada


        } catch (Exception e) {
            // Captura errores y los incluye en la respuesta
            context.getLogger().log("Error: " + e.getMessage());
            response.put("error", "Failed to process graph: " + e.getMessage());
        }

        return response;
    }
}