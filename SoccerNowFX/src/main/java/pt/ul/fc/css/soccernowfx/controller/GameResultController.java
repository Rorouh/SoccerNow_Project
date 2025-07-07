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
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * Pantalla FX “Registrar Resultado”.
 *
 *  POST /api/jogos/{id}/resultado
 *  Body → { "homeScore":2, "awayScore":1, "winnerId": 3 }
 */
public class GameResultController {

    /* ─── Nodos FXML ──────────────────────────────────────────────── */
    @FXML private TextField gameIdField;
    @FXML private TextField scoreField;           // 2-1
    @FXML private TextField winnerField;          // nombre equipo (opcional si empate)
    @FXML private Label      infoLabel;

    /* ─── HTTP helpers ────────────────────────────────────────────── */
    private final HttpClient   client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    /* ═══════════ Volver al menú ═══════════ */
    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/menu.fxml"));
            ((Stage) infoLabel.getScene().getWindow()).setScene(new Scene(root));
        } catch (Exception e) {
            infoLabel.setText("Error al volver: " + e.getMessage());
        }
    }

    /* ═══════════ Registrar resultado ═══════════ */
    @FXML
    private void handleRegister() {

        /* ---------- validación básica ---------- */
        String idTxt  = gameIdField.getText().trim();
        String score  = scoreField.getText().trim();
        String winner = winnerField.getText().trim();

        if (idTxt.isBlank() || score.isBlank()) {
            infoLabel.setText("ID y marcador son obligatorios.");
            return;
        }

        /* ---------- parsear marcador “X-Y” ---------- */
        int homeScore, awayScore;
        try {
            String[] p = score.split("-");
            homeScore = Integer.parseInt(p[0].trim());
            awayScore = Integer.parseInt(p[1].trim());
        } catch (Exception ex) {
            infoLabel.setText("Marcador inválido. Use formato 2-1.");
            return;
        }

        /* ---------- obtener detalles del partido ---------- */
        long gameId;
        try { gameId = Long.parseLong(idTxt); }
        catch (NumberFormatException ex) {
            infoLabel.setText("El ID debe ser numérico.");
            return;
        }

        final String gameUrl = "http://localhost:8080/api/jogos/" + gameId;
        HttpRequest getGame = HttpRequest.newBuilder().uri(URI.create(gameUrl)).GET().build();

        client.sendAsync(getGame, HttpResponse.BodyHandlers.ofString())
              .thenCompose(gameResp -> {
                  if (gameResp.statusCode() != 200) {
                      Platform.runLater(() -> infoLabel.setText("Partido no encontrado."));
                      return null;
                  }
                  try {
                      Map<String,Object> g =
                              mapper.readValue(gameResp.body(),
                                               new TypeReference<Map<String,Object>>() {});
                      long homeId = ((Number)((Map<?,?>)g.get("homeTeam")).get("id")).longValue();
                      long awayId = ((Number)((Map<?,?>)g.get("awayTeam")).get("id")).longValue();

                      /* ---------- resolver winnerId ---------- */
                      Long winnerId = null;
                      if (!winner.isBlank()) {
                          String enc = URLEncoder.encode(winner, StandardCharsets.UTF_8);
                          HttpRequest qTeams = HttpRequest.newBuilder()
                                  .uri(URI.create("http://localhost:8080/api/teams?name=" + enc))
                                  .GET().build();

                          // devolvemos un future encadenado
                          return client.sendAsync(qTeams, HttpResponse.BodyHandlers.ofString())
                                       .thenApply(teamResp -> {
                                           if (teamResp.statusCode() != 200) return null;
                                           try {
                                               List<Map<String,Object>> arr =
                                                   mapper.readValue(teamResp.body(),
                                                           new TypeReference<>() {});
                                               return arr.isEmpty()
                                                       ? null
                                                       : ((Number) arr.get(0).get("id")).longValue();
                                           } catch (Exception e) { return null; }
                                       }).thenApply(id -> new long[]{homeId, awayId, id == null ? -1 : id});
                      }
                      // sin ganador (empate)
                      return java.util.concurrent.CompletableFuture.completedFuture(
                              new long[]{homeId, awayId, -1});
                  } catch (Exception e) {
                      Platform.runLater(() -> infoLabel.setText("Error parseando partido."));
                      return null;
                  }
              })
              .thenCompose(arr -> {
                  if (arr == null) return null;   // error ya mostrado
                  long homeId = arr[0], awayId = arr[1], winId = arr[2];

                  /* ---------- coherencia Score ↔ Winner ---------- */
                  if (homeScore == awayScore && winId != -1) {
                      Platform.runLater(() -> infoLabel
                          .setText("Marcador es empate, deja vacío equipo ganador."));
                      return null;
                  }
                  if (homeScore > awayScore && winId != -1 && winId != homeId ||
                      awayScore > homeScore && winId != -1 && winId != awayId) {
                      Platform.runLater(() -> infoLabel
                          .setText("Marcador no coincide con el equipo ganador."));
                      return null;
                  }

                  /* ---------- JSON payload ---------- */
                  Map<String,Object> payload = new LinkedHashMap<>();
                  payload.put("homeScore", homeScore);
                  payload.put("awayScore", awayScore);
                  if (winId != -1) payload.put("winnerId", winId);

                  String json;
                  try { json = mapper.writeValueAsString(payload); }
                  catch (Exception e) {
                      Platform.runLater(() -> infoLabel.setText("Error JSON: " + e.getMessage()));
                      return null;
                  }

                  /* ---------- POST resultado ---------- */
                  HttpRequest post = HttpRequest.newBuilder()
                          .uri(URI.create("http://localhost:8080/api/jogos/" + gameId + "/resultado"))
                          .header("Content-Type", "application/json")
                          .POST(HttpRequest.BodyPublishers.ofString(json))
                          .build();
                  return client.sendAsync(post, HttpResponse.BodyHandlers.ofString());
              })
              .thenAccept(res -> {
                  if (res == null) return;              // ya gestionado
                  Platform.runLater(() -> {
                      switch (res.statusCode()) {
                          case 200  -> infoLabel.setText("¡Resultado registrado!");
                          case 404  -> infoLabel.setText("Partido no encontrado.");
                          case 400  -> infoLabel.setText("Datos inválidos (400).");
                          default   -> infoLabel.setText("Error (" + res.statusCode() + "): "
                                                         + res.body());
                      }
                  });
              })
              .exceptionally(ex -> {
                  Platform.runLater(() -> infoLabel.setText("Error: " + ex.getMessage()));
                  return null;
              });
    }
}
