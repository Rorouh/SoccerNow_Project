// src/main/java/pt/ul/fc/css/soccernowfx/controller/GameResultController.java
package pt.ul.fc.css.soccernowfx.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameResultController {

    /* ---------- nodos ---------- */
    @FXML private TextField gameIdField;
    @FXML private TextField scoreField;
    @FXML private TextField winnerField;
    @FXML private TextField cardsField;
    @FXML private Label     infoLabel;

    private final HttpClient   client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    /* ---------- volver ---------- */
    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/menu.fxml"));
            Stage st    = (Stage) infoLabel.getScene().getWindow();
            st.setScene(new Scene(root));
        } catch (Exception e) { infoLabel.setText("Error al volver: " + e.getMessage()); }
    }

    /* ---------- registrar resultado ---------- */
    @FXML
    private void handleRegister() {

        String idTxt   = gameIdField.getText().trim();
        String score   = scoreField.getText().trim();
        String winner  = winnerField.getText().trim();
        String cards   = cardsField.getText().trim();

        if (idTxt.isBlank() || score.isBlank() || winner.isBlank()) {
            infoLabel.setText("Complete los campos obligatorios.");
            return;
        }

        /* --- tarjetas: email:tipo -> List<Map<String,String>> --- */
        List<Map<String,String>> cardsList = new ArrayList<>();
        if (!cards.isBlank()) {
            String[] arr = cards.split("\\s*,\\s*");
            for (String c : arr) {
                String[] p = c.split(":");
                if (p.length == 2) {
                    cardsList.add(Map.of("email", p[0].trim(),
                                         "type" , p[1].trim()));
                }
            }
        }

        try {
            String body = mapper.writeValueAsString(
                    Map.of("score" , score,
                           "winner", winner,
                           "cards" , cardsList));

            String encId = URLEncoder.encode(idTxt, StandardCharsets.UTF_8);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/games/" + encId + "/result"))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(body))     // ← PUT en vez de POST
                    .build();

            client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                  .thenAccept(r -> Platform.runLater(() -> {
                      if (r.statusCode() == 200) {
                          infoLabel.setText("¡Resultado registrado!");
                      } else if (r.statusCode() == 404) {
                          infoLabel.setText("Partido no encontrado.");
                      } else {
                          infoLabel.setText("Error (" + r.statusCode() + "): " + r.body());
                      }
                  }))
                  .exceptionally(ex -> { Platform.runLater(() -> infoLabel.setText("Error: " + ex.getMessage())); return null; });

        } catch (Exception e) { infoLabel.setText("Error: " + e.getMessage()); }
    }
}
