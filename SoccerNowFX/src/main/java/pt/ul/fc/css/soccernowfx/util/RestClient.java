package pt.ul.fc.css.soccernowfx.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Helper muy sencillo para invocar tu backend REST.
 *  – GET / POST / PUT / DELETE
 *  – Serializa / des-serializa con Jackson.
 */
public final class RestClient {

    private static final String BASE_URL = "http://localhost:8080";

    private static final HttpClient HTTP = HttpClient.newHttpClient();

    private static final ObjectMapper JSON = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            /*  ⟵  IGNORAR propiedades desconocidas              */
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private RestClient() {}

    /* ---------- GET ---------- */
    public static <T> T get(String path, Class<T> type) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .GET()
                .build();
        HttpResponse<String> resp = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
            return JSON.readValue(resp.body(), type);
        }
        throw new RuntimeException("GET " + path + " -> " + resp.statusCode() + ": " + resp.body());
    }

    /* ---------- POST ---------- */
    public static <T> T post(String path, Object body, Class<T> type) throws Exception {
        String json = JSON.writeValueAsString(body);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> resp = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
            return JSON.readValue(resp.body(), type);
        }
        throw new RuntimeException("POST " + path + " -> " + resp.statusCode() + ": " + resp.body());
    }

    /* ---------- PUT ---------- */
    public static void put(String path, Object body) throws Exception {
        String json = JSON.writeValueAsString(body);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> resp = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
            throw new RuntimeException("PUT " + path + " -> " + resp.statusCode() + ": " + resp.body());
        }
    }

    /* ---------- DELETE ---------- */
    public static void delete(String path) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .DELETE()
                .build();
        HttpResponse<String> resp = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
            throw new RuntimeException("DELETE " + path + " -> " + resp.statusCode() + ": " + resp.body());
        }
    }
}
