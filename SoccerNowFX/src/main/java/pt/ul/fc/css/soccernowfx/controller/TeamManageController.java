package pt.ul.fc.css.soccernowfx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.application.Platform;


import java.net.http.*;
import java.net.URI;

public class TeamManageController {

    @FXML
    private void handleBack() {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/fxml/menu.fxml"));
            javafx.stage.Stage stage = (javafx.stage.Stage) infoLabel.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
        } catch (Exception e) {
            infoLabel.setText("Erro ao voltar ao menu: " + e.getMessage());
        }
    }

    @FXML private TextField nameField;
    @FXML private TextField playersField;
    @FXML private Label infoLabel;

    @FXML
    private void handleSearch() {
        String nome = nameField.getText();
        if (nome.isBlank()) {
            infoLabel.setText("Informe o nome da equipa para buscar.");
            return;
        }
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/teams?name=" + nome))
                .GET()
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenAccept(response -> Platform.runLater(() -> {

                if (response.statusCode() == 200 && response.body().contains("\"id\":")) {
                    String body = response.body();
                    String jogadores = extractPlayersJson(body);
                    playersField.setText(jogadores);
                    infoLabel.setText("Equipa encontrada.");
                } else {
                    infoLabel.setText("Equipa não encontrada.");
                }
            }))
            .exceptionally(e -> { Platform.runLater(() -> infoLabel.setText("Erro: " + e.getMessage())); return null; });
    }

    @FXML
    private void handleUpdate() {
        String nome = nameField.getText();
        String jogadores = playersField.getText();
        if (nome.isBlank()) {
            infoLabel.setText("Preencha o nome da equipa.");
            return;
        }
        String[] jogadoresArr = jogadores.isBlank() ? new String[0] : jogadores.split(",");
        StringBuilder sb = new StringBuilder();

        sb.append("{\"nome\":\"").append(nome).append("\",\"jogadores\":[");
        for (int i = 0; i < jogadoresArr.length; i++) {
            sb.append("\"").append(jogadoresArr[i].trim()).append("\"");
            if (i < jogadoresArr.length - 1) sb.append(",");
        }
        sb.append("]}");
        String json = sb.toString();
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/teams/" + nome))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenAccept(response -> Platform.runLater(() -> {
                if (response.statusCode() == 200) {
                    infoLabel.setText("Equipa atualizada com sucesso!");
                } else {
                    infoLabel.setText("Erro ao atualizar: " + response.body());
                }
            }))
            .exceptionally(e -> { Platform.runLater(() -> infoLabel.setText("Erro: " + e.getMessage())); return null; });
    }

    @FXML
    private void handleRemove() {
        String nome = nameField.getText();
        if (nome.isBlank()) {
            infoLabel.setText("Informe o nome da equipa para remover.");
            return;
        }
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/teams/" + nome))
                .DELETE()
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenAccept(response -> Platform.runLater(() -> {
                if (response.statusCode() == 200) {
                    infoLabel.setText("Equipa removida com sucesso!");
                    playersField.setText("");
                } else {
                    infoLabel.setText("Erro ao remover: " + response.body());
                }
            }))
            .exceptionally(e -> { Platform.runLater(() -> infoLabel.setText("Erro: " + e.getMessage())); return null; });
    }

    // Utilitário simples para extrair lista de jogadores do JSON (sem dependência externa)
    private String extractPlayersJson(String json) {
        String search = "\"jogadores\":[";
        int idx = json.indexOf(search);
        if (idx == -1) return "";
        int start = idx + search.length();
        int end = json.indexOf(']', start);
        if (end == -1) return "";
        String arr = json.substring(start, end);
        return arr.replaceAll("\"", "").replaceAll(",", ", ");
    }
}
