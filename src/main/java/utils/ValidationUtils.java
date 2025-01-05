package utils;

public class ValidationUtils {
    public static void validateNonNull(Object obj, String errorMessage) {
        if (obj == null) {
            throw new GraphWordException(errorMessage);
        }
    }

    public static void validateNotEmpty(String str, String errorMessage) {
        if (str == null || str.trim().isEmpty()) {
            throw new GraphWordException(errorMessage);
        }
    }

    public static boolean isAlphaWord(String word) {
        return word.matches("[a-zA-Z]+");
    }

    public static boolean isValidWordLength(String word, int minLength) {
        return word.length() >= minLength;
    }
}

