package config;

import utils.GraphWordException;

import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private final Properties properties = new Properties();

    public ConfigLoader() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new GraphWordException("Configuration file not found!");
            }
            properties.load(input);
        } catch (Exception e) {
            throw new GraphWordException("Failed to load configuration: " + e.getMessage());
        }
    }

    public String get(String key) {
        return properties.getProperty(key);
    }
}



