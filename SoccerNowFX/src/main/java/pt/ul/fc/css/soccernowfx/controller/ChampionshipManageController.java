package pt.ul.fc.css.soccernowfx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.application.Platform;
import java.net.http.*;
import java.net.URI;

public class ChampionshipManageController {
    @FXML private TextField nameField;
    @FXML private TextField modalityField;
    @FXML private TextField teamsField;
    @FXML private TextField refereesField;
    @FXML private Label infoLabel;

    @FXML
    private void handleSearch() {
        String nome = nameField.getText();
        if (nome.isBlank()) {
            infoLabel.setText("Indica el nombre del campeonato para buscar.");
            return;
        }
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/championships/" + nome))
                .GET()
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenAccept(response -> Platform.runLater(() -> {
                if (response.statusCode() == 200) {
                    String body = response.body();
                    String modalidade = extractJson(body, "modalidade");
                    String equipas = extractArrayJson(body, "equipas");
                    String arbitros = extractArrayJson(body, "arbitrosCertificados");
                    modalityField.setText(modalidade);
                    teamsField.setText(equipas);
                    refereesField.setText(arbitros);
                    infoLabel.setText("Campeonato encontrado.");
                } else {
                    infoLabel.setText("Campeonato no encontrado.");
                }
            }))
            .exceptionally(e -> { Platform.runLater(() -> infoLabel.setText("Error: " + e.getMessage())); return null; });
    }

    @FXML
    private void handleUpdate() {
        String nome = nameField.getText();
        String modalidade = modalityField.getText();
        String equipas = teamsField.getText();
        String arbitros = refereesField.getText();
        if (nome.isBlank() || modalidade.isBlank() || equipas.isBlank() || arbitros.isBlank()) {
            infoLabel.setText("Complete todos los campos obligatorios.");
            return;
        }
        String[] equipasArr = equipas.split(",");
        String[] arbitrosArr = arbitros.split(",");
        StringBuilder sb = new StringBuilder();
        sb.append("{\"nome\":\"").append(nome).append("\",\"modalidade\":\"").append(modalidade).append("\",\"equipas\":[");
        for (int i = 0; i < equipasArr.length; i++) {
            sb.append("\"").append(equipasArr[i].trim()).append("\"");
            if (i < equipasArr.length - 1) sb.append(",");
        }
        sb.append("],\"arbitrosCertificados\":[");
        for (int i = 0; i < arbitrosArr.length; i++) {
            sb.append("\"").append(arbitrosArr[i].trim()).append("\"");
            if (i < arbitrosArr.length - 1) sb.append(",");
        }
        sb.append("]}");
        String json = sb.toString();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/championships/" + nome))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenAccept(response -> Platform.runLater(() -> {
                if (response.statusCode() == 200) {
                    infoLabel.setText("¡Campeonato actualizado con éxito!");
                } else {
                    infoLabel.setText("Error al actualizar: " + response.body());
                }
            }))
            .exceptionally(e -> { Platform.runLater(() -> infoLabel.setText("Error: " + e.getMessage())); return null; });
    }

    @FXML
    private void handleRemove() {
        String nome = nameField.getText();
        if (nome.isBlank()) {
            infoLabel.setText("Indica el nombre del campeonato para eliminar.");
            return;
        }
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/championships/" + nome))
                .DELETE()
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenAccept(response -> Platform.runLater(() -> {
                if (response.statusCode() == 200) {
                    infoLabel.setText("¡Campeonato eliminado con éxito!");
                    modalityField.setText("");
                    teamsField.setText("");
                    refereesField.setText("");
                } else {
                    infoLabel.setText("Error al eliminar: " + response.body());
                }
            }))
            .exceptionally(e -> { Platform.runLater(() -> infoLabel.setText("Error: " + e.getMessage())); return null; });
    }

    // Navegación al menú principal
    @FXML
    private void handleBack() {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/fxml/menu.fxml"));
            javafx.stage.Stage stage = (javafx.stage.Stage) infoLabel.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
        } catch (Exception e) {
            infoLabel.setStyle("-fx-text-fill: red;");
            infoLabel.setText("Error al volver al menú: " + e.getMessage());
        }
    }

    // Utilitario simple para extraer valor de campo JSON (sin dependencia externa)
    private String extractJson(String json, String field) {
        String search = "\"" + field + "\":";
        int idx = json.indexOf(search);
        if (idx == -1) return "";
        int start = json.indexOf('"', idx + search.length());
        int end = json.indexOf('"', start + 1);
        return (start != -1 && end != -1) ? json.substring(start + 1, end) : "";
    }
    // Extrae array JSON como cadena separada por comas
    private String extractArrayJson(String json, String field) {
        String search = "\"" + field + "\":[";
        int idx = json.indexOf(search);
        if (idx == -1) return "";
        int start = idx + search.length();
        int end = json.indexOf(']', start);
        if (end == -1) return "";
        String arr = json.substring(start, end);
        return arr.replaceAll("\"", "").replaceAll(",", ", ");
    }
}
