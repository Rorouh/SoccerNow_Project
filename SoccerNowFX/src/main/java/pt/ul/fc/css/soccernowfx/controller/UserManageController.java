package pt.ul.fc.css.soccernowfx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.net.http.*;
import java.net.URI;

public class UserManageController {
    @FXML private TextField emailField;
    @FXML private TextField nameField;

    @FXML private ChoiceBox<String> typeChoice;
    @FXML private ChoiceBox<String> extraField;
    @FXML private Label infoLabel;

    @FXML
    public void initialize() {
        typeChoice.getItems().addAll("JOGADOR", "ARBITRO");
        typeChoice.setOnAction(e -> updateExtraOptions());
        updateExtraOptions();
    }

    private void updateExtraOptions() {
        extraField.getItems().clear();
        String tipo = typeChoice.getValue();
        if ("JOGADOR".equalsIgnoreCase(tipo)) {
            extraField.getItems().addAll(
                "PORTERO", "DEFENSA", "CENTROCAMPISTA", "DELANTERO"
            );
        } else if ("ARBITRO".equalsIgnoreCase(tipo)) {
            extraField.getItems().addAll(
                "NACIONAL", "INTERNACIONAL", "REGIONAL", "ESTADUAL"
            );
        }
        if (!extraField.getItems().isEmpty()) {
            extraField.setValue(extraField.getItems().get(0));
        }
    }

    @FXML
    private void handleSearch() {
        String email = emailField.getText();
        if (email.isBlank()) {
            infoLabel.setText("Informe o email para buscar.");
            return;
        }
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/users/by-email/" + email))
                .GET()
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenAccept(response -> Platform.runLater(() -> {
                if (response.statusCode() == 200) {
                    // Espera JSON: {"nome":"...","email":"...","tipo":"...","extra":"..."}
                    String body = response.body();
                    String nome = extractJson(body, "nome");
                    String tipo = extractJson(body, "tipo");
                    String extra = extractJson(body, "extra");
                    nameField.setText(nome);
                    typeChoice.setValue(tipo);
                    updateExtraOptions();
                    extraField.setValue(extra);
                    infoLabel.setText("Usuário encontrado.");
                } else {
                    infoLabel.setText("Usuário não encontrado.");
                }
            }))
            .exceptionally(e -> { Platform.runLater(() -> infoLabel.setText("Erro: " + e.getMessage())); return null; });
    }

    @FXML
    private void handleUpdate() {
        String email = emailField.getText();
        String nome = nameField.getText();
        String tipo = typeChoice.getValue();
        String extra = extraField.getValue();
        if (email.isBlank() || nome.isBlank() || tipo == null || tipo.isBlank()) {
            infoLabel.setText("Preencha todos os campos obrigatórios.");
            return;
        }
        String json;
        if ("JOGADOR".equalsIgnoreCase(tipo)) {
            json = String.format("{\"nome\":\"%s\",\"email\":\"%s\",\"tipo\":\"%s\",\"preferredPosition\":\"%s\"}", nome, email, tipo, extra);
        } else if ("ARBITRO".equalsIgnoreCase(tipo)) {
            json = String.format("{\"nome\":\"%s\",\"email\":\"%s\",\"tipo\":\"%s\",\"certification\":\"%s\"}", nome, email, tipo, extra);
        } else {
            json = String.format("{\"nome\":\"%s\",\"email\":\"%s\",\"tipo\":\"%s\"}", nome, email, tipo);
        }
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/users/by-email/" + email))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenAccept(response -> Platform.runLater(() -> {
                if (response.statusCode() == 200) {
                    infoLabel.setText("Usuário atualizado com sucesso!");
                } else {
                    infoLabel.setText("Erro ao atualizar: " + response.body());
                }
            }))
            .exceptionally(e -> { Platform.runLater(() -> infoLabel.setText("Erro: " + e.getMessage())); return null; });
    }

    @FXML
    private void handleRemove() {
        String email = emailField.getText();
        if (email.isBlank()) {
            infoLabel.setText("Informe o email para remover.");
            return;
        }
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/users/by-email/" + email))
                .DELETE()
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenAccept(response -> Platform.runLater(() -> {
                if (response.statusCode() == 200 || response.statusCode() == 204) {
                    infoLabel.setText("Usuário removido com sucesso!");
                    nameField.setText("");
                    typeChoice.setValue(null);
                    extraField.getItems().clear();
                } else if (response.statusCode() == 404) {
                    infoLabel.setText("Usuário não encontrado.");
                } else {
                    infoLabel.setText("Erro ao remover: " + response.body());
                }
            }))
            .exceptionally(e -> { Platform.runLater(() -> infoLabel.setText("Erro: " + e.getMessage())); return null; });
    }

    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/menu.fxml"));
            Stage stage = (Stage) infoLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            infoLabel.setText("Erro ao voltar ao menu: " + e.getMessage());
        }
    }

    // Utilitário simples para extrair valor de campo JSON (sem dependência externa)
    private String extractJson(String json, String field) {
        String search = "\"" + field + "\":";
        int idx = json.indexOf(search);
        if (idx == -1) return "";
        int start = json.indexOf('"', idx + search.length());
        int end = json.indexOf('"', start + 1);
        return (start != -1 && end != -1) ? json.substring(start + 1, end) : "";
    }
}
