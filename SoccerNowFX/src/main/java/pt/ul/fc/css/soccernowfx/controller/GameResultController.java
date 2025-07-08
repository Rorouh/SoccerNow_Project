// src/main/java/pt/ul/fc/css/soccernowfx/controller/GameResultController.java
package pt.ul.fc.css.soccernowfx.controller;

import com.fasterxml.jackson.core.type.TypeReference;
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
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class GameResultController {

    @FXML private TextField gameIdField;
    @FXML private TextField scoreField;
    @FXML private Label      infoLabel;

    private final HttpClient   client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    /* ─────────── volver ─────────── */
    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/menu.fxml"));
            ((Stage) infoLabel.getScene().getWindow()).setScene(new Scene(root));
        } catch (Exception e) {
            infoLabel.setText("Error al volver: " + e.getMessage());
        }
    }

    /* ─────────── registrar ─────────── */
    @FXML
    private void handleRegister() {

        String idTxt = gameIdField.getText().trim();
        String score = scoreField.getText().trim();

        if (idTxt.isBlank() || score.isBlank()) {
            infoLabel.setStyle("-fx-text-fill: red;");
            infoLabel.setText("ID y marcador son obligatorios.");
            return;
        }

        long gameId;
        try { gameId = Long.parseLong(idTxt); }
        catch (NumberFormatException ex) {
            infoLabel.setStyle("-fx-text-fill: red;");
            infoLabel.setText("El ID debe ser numérico.");
            return;
        }

        int homeScore, awayScore;
        try {
            String[] p = score.split("-");
            homeScore = Integer.parseInt(p[0].trim());
            awayScore = Integer.parseInt(p[1].trim());
        } catch (Exception ex) {
            infoLabel.setStyle("-fx-text-fill: red;");
            infoLabel.setText("Marcador inválido. Use formato 2-1.");
            return;
        }

        HttpRequest getGame = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/jogos/" + gameId))
                .GET().build();

        client.sendAsync(getGame, HttpResponse.BodyHandlers.ofString())
              .thenCompose(gResp -> {
                  if (gResp.statusCode() != 200) {
                      Platform.runLater(() -> {
                          infoLabel.setStyle("-fx-text-fill: red;");
                          infoLabel.setText("Partido no encontrado.");
                      });
                      return CompletableFuture.failedFuture(new IllegalStateException());
                  }

                  try {
                      Map<String,Object> g = mapper.readValue(
                              gResp.body(),
                              new TypeReference<>() {});
                      long homeId = ((Number) g.get("homeTeamId")).longValue();
                      long awayId = ((Number) g.get("awayTeamId")).longValue();

                      long winnerId = -1;
                      if      (homeScore > awayScore) winnerId = homeId;
                      else if (awayScore > homeScore) winnerId = awayId;

                      Map<String,Object> body = new LinkedHashMap<>();
                      body.put("homeScore", homeScore);
                      body.put("awayScore", awayScore);
                      if (winnerId != -1) body.put("winnerId", winnerId);

                      String json = mapper.writeValueAsString(body);

                      HttpRequest post = HttpRequest.newBuilder()
                              .uri(URI.create("http://localhost:8080/api/jogos/" + gameId + "/resultado"))
                              .header("Content-Type", "application/json")
                              .POST(HttpRequest.BodyPublishers.ofString(json))
                              .build();

                      return client.sendAsync(post, HttpResponse.BodyHandlers.discarding());

                  } catch (Exception e) {
                      Platform.runLater(() -> {
                          infoLabel.setStyle("-fx-text-fill: red;");
                          infoLabel.setText("Error JSON: " + e.getMessage());
                      });
                      return CompletableFuture.failedFuture(e);
                  }
              })
              .thenAccept(r -> Platform.runLater(() -> {
                  if (r == null) return;           // ya gestionado
                  switch (r.statusCode()) {
                      case 200 -> {
                          infoLabel.setStyle("-fx-text-fill: green;");
                          infoLabel.setText("Resultado registrado");
                      }
                      case 400 -> {
                          infoLabel.setStyle("-fx-text-fill: red;");
                          infoLabel.setText("Datos inválidos (400).");
                      }
                      case 404 -> {
                          infoLabel.setStyle("-fx-text-fill: red;");
                          infoLabel.setText("Partido no encontrado.");
                      }
                      default -> {
                          infoLabel.setStyle("-fx-text-fill: red;");
                          infoLabel.setText("Error (" + r.statusCode() + ")");
                      }
                  }
              }))
              .exceptionally(ex -> null);   // mensaje ya mostrado
    }
}
