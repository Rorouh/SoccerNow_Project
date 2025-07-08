package pt.ul.fc.css.soccernowfx.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GameCancelController {

    @FXML private TextField gameIdField;
    @FXML private Label     infoLabel;

    @FXML
    private void handleCancel() {

        String id = gameIdField.getText().trim();
        if (id.isBlank()) {
            infoLabel.setText("Indica el ID del partido.");
            return;
        }

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req   = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/jogos/" + id + "/cancelar"))
                .PUT(HttpRequest.BodyPublishers.noBody())           //  ◄── PUT (+ noBody)
                .build();

        client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
              .thenAccept(r -> Platform.runLater(() -> {
                  if (r.statusCode() == 204) {
                      infoLabel.setStyle("-fx-text-fill: green;");
                      infoLabel.setText("¡Partido cancelado con éxito!");
                  } else if (r.statusCode() == 404) {
                      infoLabel.setText("Partido no encontrado.");
                  } else {
                      infoLabel.setText("Error (" + r.statusCode() + "): " + r.body());
                  }
              }))
              .exceptionally(e -> { Platform.runLater(
                      () -> infoLabel.setText("Error: " + e.getMessage())); return null; });
    }
}
