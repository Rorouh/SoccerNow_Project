package pt.ul.fc.css.soccernowfx.controller;

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

/**
 * Controlador FX para “Cancelar Partido de Campeonato”.
 *
 * Backend:
 *   PUT /api/jogos/{id}/cancelar   – 204 NO CONTENT si se cancela.
 */
public class GameCancelController {

    /* ────── nodos FXML ────── */
    @FXML private TextField gameIdField;
    @FXML private Label     infoLabel;

    private final HttpClient client = HttpClient.newHttpClient();

    /* ═══════════ cancelar ═══════════ */
    @FXML
    private void handleCancel() {

        String idTxt = gameIdField.getText().trim();
        if (idTxt.isBlank()) {
            infoLabel.setText("Indica el ID del partido.");
            return;
        }

        /* construye la petición: PUT /api/jogos/{id}/cancelar */
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/jogos/" + idTxt + "/cancelar"))
                .PUT(HttpRequest.BodyPublishers.noBody())   // ← PUT + cuerpo vacío
                .build();

        client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
              .thenAccept(resp -> Platform.runLater(() -> {
                  int code = resp.statusCode();
                  if (code == 204) {                          // éxito
                      infoLabel.setStyle("-fx-text-fill: green;");
                      infoLabel.setText("¡Partido cancelado con éxito!");
                  } else if (code == 404) {                   // no existe
                      infoLabel.setStyle("-fx-text-fill: red;");
                      infoLabel.setText("Partido no encontrado.");
                  } else {
                      infoLabel.setStyle("-fx-text-fill: red;");
                      infoLabel.setText("Error al cancelar: HTTP " + code);
                  }
              }))
              .exceptionally(ex -> {
                  Platform.runLater(() -> {
                      infoLabel.setStyle("-fx-text-fill: red;");
                      infoLabel.setText("Error: " + ex.getMessage());
                  });
                  return null;
              });
    }

    /* ═══════════ volver al menú ═══════════ */
    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/menu.fxml"));
            Stage st    = (Stage) infoLabel.getScene().getWindow();
            st.setScene(new Scene(root));
        } catch (Exception e) {
            infoLabel.setStyle("-fx-text-fill: red;");
            infoLabel.setText("Error al volver: " + e.getMessage());
        }
    }
}
