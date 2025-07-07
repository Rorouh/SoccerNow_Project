package pt.ul.fc.css.soccernowfx.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/** Controller para “Creación de Partido” (versión simplificada). */
public class GameCreateController {

    /* ---------- nodos ---------- */
    @FXML private DatePicker        dateField;
    @FXML private TextField         timeField;
    @FXML private TextField         locationField;
    @FXML private TextField         team1Field;
    @FXML private TextField         team2Field;
    @FXML private TextField         refereesField;
    @FXML private ChoiceBox<String> typeChoice;
    @FXML private TextField         championshipField;
    @FXML private Label             infoLabel;

    @FXML
    public void initialize() {
        typeChoice.getItems().addAll("Amistoso", "Campeonato");
        typeChoice.setValue("Amistoso");
    }

    /* ---------- volver al menú ---------- */
    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/fxml/menu.fxml"));
            var stage = (javafx.stage.Stage) dateField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            infoLabel.setText("Error al volver al menú: " + e.getMessage());
        }
    }

    /* ---------- crear partido ---------- */
    @FXML
    private void handleCreate() {
        String fecha   = dateField.getValue() != null ? dateField.getValue().toString() : "";
        String hora    = timeField.getText().trim();
        String lugar   = locationField.getText().trim();
        String eq1     = team1Field.getText().trim();
        String eq2     = team2Field.getText().trim();
        String refsCsv = refereesField.getText().trim();
        String tipo    = typeChoice.getValue();
        String camp    = championshipField.getText().trim();

        if (fecha.isBlank() || hora.isBlank() || lugar.isBlank() ||
            eq1.isBlank() || eq2.isBlank() || refsCsv.isBlank()) {
            infoLabel.setText("Rellene todos los campos obligatorios.");
            return;
        }
        if (eq1.equalsIgnoreCase(eq2)) {
            infoLabel.setText("Los equipos deben ser distintos.");
            return;
        }

        /* ---- JSON ---- */
        StringBuilder sb = new StringBuilder("{")
            .append("\"date\":\"").append(fecha).append("\",")
            .append("\"time\":\"").append(hora).append("\",")
            .append("\"location\":\"").append(lugar).append("\",")
            .append("\"team1\":\"").append(eq1).append("\",")
            .append("\"team2\":\"").append(eq2).append("\",")
            .append("\"referees\":[");
        String[] refArr = refsCsv.split("\\s*,\\s*");
        for (int i = 0; i < refArr.length; i++) {
            sb.append("\"").append(refArr[i]).append("\"");
            if (i < refArr.length - 1) sb.append(",");
        }
        sb.append("],\"type\":\"").append(tipo).append("\"");
        if ("Campeonato".equalsIgnoreCase(tipo) && !camp.isBlank()) {
            sb.append(",\"championship\":\"").append(camp).append("\"");
        }
        sb.append("}");

        /* ---- petición ---- */
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req   = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/games"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(sb.toString()))
                .build();

        client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
              .thenAccept(r -> Platform.runLater(() -> {
                  if (r.statusCode() == 200 || r.statusCode() == 201) {
                      infoLabel.setStyle("-fx-text-fill: green;");
                      infoLabel.setText("¡Partido creado con éxito!");
                      clearForm();
                  } else {
                      infoLabel.setStyle("-fx-text-fill: red;");
                      infoLabel.setText("Error (" + r.statusCode() + "): " + r.body());
                  }
              }))
              .exceptionally(ex -> { Platform.runLater(
                      () -> infoLabel.setText("Error: " + ex.getMessage())); return null; });
    }

    private void clearForm() {
        dateField.setValue(null);
        timeField.clear();
        locationField.clear();
        team1Field.clear();
        team2Field.clear();
        refereesField.clear();
        typeChoice.setValue("Amistoso");
        championshipField.clear();
    }
}
