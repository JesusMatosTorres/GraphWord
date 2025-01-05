package storage;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import storage.WordFileReader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class S3FileReader implements WordFileReader {

    private final S3Client s3Client;

    public S3FileReader(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public Set<String> extractWords(String s3Path) {
        Set<String> words = new HashSet<>();
        String bucketName = s3Path.split("/")[0];
        String key = s3Path.substring(s3Path.indexOf("/") + 1);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        s3Client.getObject(GetObjectRequest.builder()
                                .bucket(bucketName)
                                .key(key)
                                .build())))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\\s+");
                for (String token : tokens) {
                    if (token.matches("[a-zA-Z]+") && token.length() >= 3) { // Filtra palabras menores a 3 caracteres
                        words.add(token.toLowerCase());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return words;
    }
}