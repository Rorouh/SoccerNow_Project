package pt.ul.fc.css.soccernowfx.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/** Pantalla “Crear Partido”.  Envía directamente a /api/jogos  */
public class GameCreateController {

    /* ---------- nodos ---------- */
    @FXML private DatePicker  dateField;
    @FXML private TextField   timeField;
    @FXML private TextField   locationField;
    @FXML private TextField   team1Field;        // IDs numéricos
    @FXML private TextField   team2Field;
    @FXML private TextField   refereesField;     // IDs separados por coma
    @FXML private ComboBox<String> typeChoice;   //  ← ComboBox, NO ChoiceBox
    @FXML private TextField   championshipField;
    @FXML private Label       infoLabel;

    @FXML
    public void initialize() {
        typeChoice.getItems().setAll("Amistoso", "Campeonato");
        typeChoice.getSelectionModel().selectFirst();
    }

    /* ---------- volver ---------- */
    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/menu.fxml"));
            ((Stage) infoLabel.getScene().getWindow()).setScene(new Scene(root));
        } catch (Exception e) { infoLabel.setText("Error al volver: " + e.getMessage()); }
    }

    /* ---------- crear partido ---------- */
    @FXML
    private void handleCreate() {

        /* --- lectura y validación mínima --- */
        String fecha = dateField.getValue() == null ? "" : dateField.getValue().toString();
        String hora  = timeField.getText().trim();
        String lugar = locationField.getText().trim();
        String eq1   = team1Field.getText().trim();
        String eq2   = team2Field.getText().trim();
        String refs  = refereesField.getText().trim();
        String tipo  = typeChoice.getValue();
        String camp  = championshipField.getText().trim();

        if (fecha.isBlank() || hora.isBlank() || lugar.isBlank()
                || eq1.isBlank() || eq2.isBlank() || refs.isBlank()) {
            infoLabel.setText("Rellene todos los campos obligatorios."); return;
        }
        if (eq1.equals(eq2)) {
            infoLabel.setText("Los equipos deben ser distintos."); return;
        }

        boolean amigavel = "Amistoso".equalsIgnoreCase(tipo);

        /* --- JSON que espera /api/jogos --- */
        String json = String.format(
            "{\"dateTime\":\"%sT%s\",\"location\":\"%s\",\"amigavel\":%s,"
          + "\"teamIds\":[%s,%s],\"refereeIds\":[%s],\"primaryRefereeId\":%s%s}",
            fecha, hora, lugar, amigavel,
            eq1, eq2,
            refs,
            refs.split(",")[0],                                    // 1º árbitro = principal
            (amigavel || camp.isBlank()) ? "" : ",\"championshipName\":\""+camp+"\""
        );

        /* --- POST /api/jogos --- */
        HttpClient  client = HttpClient.newHttpClient();
        HttpRequest req    = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/jogos"))
                .header("Content-Type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
              .thenAccept(r -> Platform.runLater(() -> {
                  if (r.statusCode()==201 || r.statusCode()==200) {
                      infoLabel.setStyle("-fx-text-fill: green;");
                      infoLabel.setText("¡Partido creado con éxito!");
                      clearForm();
                  } else {
                      infoLabel.setStyle("-fx-text-fill: red;");
                      infoLabel.setText("Error ("+r.statusCode()+"): "+r.body());
                  }
              }))
              .exceptionally(ex -> { Platform.runLater(
                      () -> infoLabel.setText("Error: "+ex.getMessage())); return null; });
    }

    private void clearForm() {
        dateField.setValue(null);
        timeField.clear(); locationField.clear();
        team1Field.clear(); team2Field.clear();
        refereesField.clear();
        typeChoice.getSelectionModel().selectFirst();
        championshipField.clear();
    }
}
