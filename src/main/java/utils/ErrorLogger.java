package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ErrorLogger {
    private static final String LOG_FILE = "error.log";

    public static void logError(String message, Throwable throwable) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        try (FileWriter writer = new FileWriter(LOG_FILE, true)) {
            writer.write(String.format("[%s] ERROR: %s%n", timestamp, message));
            if (throwable != null) {
                writer.write(String.format("Cause: %s%n", throwable.getMessage()));
            }
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }
}

