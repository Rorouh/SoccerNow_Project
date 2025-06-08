package pt.ul.fc.css.soccernowfx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.application.Platform;
import java.net.http.*;
import java.net.URI;

public class GameCancelController {
    @FXML private TextField gameIdField;
    @FXML private Label infoLabel;

    @FXML
    private void handleCancel() {
        String gameId = gameIdField.getText();
        if (gameId.isBlank()) {
            infoLabel.setText("Informe o ID do jogo.");
            return;
        }
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/games/" + gameId + "/cancel"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenAccept(response -> Platform.runLater(() -> {
                if (response.statusCode() == 200) {
                    infoLabel.setText("Jogo cancelado com sucesso!");
                } else {
                    infoLabel.setText("Erro ao cancelar jogo: " + response.body());
                }
            }))
            .exceptionally(e -> { Platform.runLater(() -> infoLabel.setText("Erro: " + e.getMessage())); return null; });
    }
}
