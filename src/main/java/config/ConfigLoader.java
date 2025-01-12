package config;

import utils.GraphWordException;

import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new GraphWordException("Configuration file not found!");
            }
            properties.load(input);
        } catch (Exception e) {
            throw new GraphWordException("Failed to load configuration: " + e.getMessage());
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}
