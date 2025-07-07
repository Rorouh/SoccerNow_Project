package pt.ul.fc.css.soccernowfx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.application.Platform;
import java.net.http.*;
import java.net.URI;

public class ChampionshipCreateController {
    @FXML private TextField nameField;
    @FXML private TextField modalityField;
    @FXML private TextField formatField;
    @FXML private TextField teamsField;
    @FXML private TextField refereesField;
    @FXML private Label infoLabel;

    @FXML
    private void handleBack() {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/fxml/menu.fxml"));
            javafx.stage.Stage stage = (javafx.stage.Stage) infoLabel.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
        } catch (Exception e) {
            infoLabel.setText("Error al volver al menú: " + e.getMessage());
        }
    }

    @FXML
    private void handleCreate() {
        String nome = nameField.getText();
        String modalidade = modalityField.getText();
        String formato = formatField.getText();
        String equipas = teamsField.getText();
        String arbitros = refereesField.getText();
        if (nome.isBlank() || modalidade.isBlank() || formato.isBlank() || equipas.isBlank() || arbitros.isBlank()) {
            infoLabel.setText("Complete todos los campos obligatorios.");
            return;
        }
        String[] equipasArr = equipas.split(",");
        String[] arbitrosArr = arbitros.split(",");
        StringBuilder sb = new StringBuilder();
        sb.append("{\"nome\":\"").append(nome).append("\",\"modalidade\":\"").append(modalidade).append("\",\"formato\":\"").append(formato).append("\",\"equipas\":[");
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
                .uri(URI.create("http://localhost:8080/api/championships"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenAccept(response -> Platform.runLater(() -> {
                if (response.statusCode() == 201 || response.statusCode() == 200) {
                    infoLabel.setText("¡Campeonato creado con éxito!");
                } else {
                    infoLabel.setText("Error al crear el campeonato: " + response.body());
                }
            }))
            .exceptionally(e -> { Platform.runLater(() -> infoLabel.setText("Error: " + e.getMessage())); return null; });
    }

}
