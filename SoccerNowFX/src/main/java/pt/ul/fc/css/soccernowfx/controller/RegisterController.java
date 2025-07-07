package pt.ul.fc.css.soccernowfx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegisterController {

    @FXML
    public void initialize() {
        typeChoice.getItems().addAll("JUGADOR", "ÁRBITRO");
        // Población de opciones iniciales
        typeChoice.setOnAction(e -> updateExtraOptions());
        updateExtraOptions();
    }

    private void updateExtraOptions() {
        extraField.getItems().clear();
        String tipo = typeChoice.getValue();
        if ("JUGADOR".equalsIgnoreCase(tipo)) {
            // Sólo los valores aceptados por el Enum del backend
            extraField.getItems().addAll(
                "PORTERO", "DEFENSA", "CENTROCAMPISTA", "DELANTERO"
            );
        } else if ("ÁRBITRO".equalsIgnoreCase(tipo)) {
            // Sólo opciones válidas para certified=true
            extraField.getItems().addAll(
                "NACIONAL", "INTERNACIONAL"
            );
            // Si se quieren admitir árbitros no certificados, añadir "REGIONAL", "ESTATAL"
            extraField.getItems().addAll("REGIONAL", "ESTATAL");
        }
        if (!extraField.getItems().isEmpty()) {
            extraField.setValue(extraField.getItems().get(0));
        }
    }

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ChoiceBox<String> typeChoice;
    @FXML private ChoiceBox<String> extraField; // Ahora es ChoiceBox
    @FXML private Label errorLabel;
    @FXML private Button backButton;

    @FXML
    private void handleRegister() {
        errorLabel.setText("");
        errorLabel.setStyle("");
        String nome = nameField.getText();
        String email = emailField.getText();
        String senha = passwordField.getText();
        String tipo = typeChoice.getValue();
        String extra = extraField.getValue();

        // Validación de campos obligatorios
        if (nome.isBlank() || email.isBlank() || senha.isBlank() || tipo == null) {
            errorLabel.setText("Complete todos los campos obligatorios.");
            return;
        }

        // Validación de formato de email
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            errorLabel.setText("Email inválido.");
            return;
        }
        // Validar tipo
        if (!tipo.equals("JUGADOR") && !tipo.equals("ÁRBITRO")) {
            errorLabel.setText("Tipo de usuario inválido.");
            return;
        }

        // Montar JSON
        String roleBackend;
        if (tipo.equals("JUGADOR")) {
            roleBackend = "PLAYER";
        } else {
            roleBackend = "REFEREE";
        }
        String json;
        if (tipo.equals("JUGADOR")) {
            json = String.format(
                "{\"name\":\"%s\",\"email\":\"%s\",\"password\":\"%s\",\"role\":\"%s\",\"preferredPosition\":\"%s\"}",
                nome, email, senha, roleBackend, extra
            );
        } else {
            boolean certified = "NACIONAL".equalsIgnoreCase(extra) || "INTERNACIONAL".equalsIgnoreCase(extra);
            json = String.format(
                "{\"name\":\"%s\",\"email\":\"%s\",\"password\":\"%s\",\"role\":\"%s\",\"certified\":%s}",
                nome, email, senha, roleBackend, certified
            );
        }

        try {
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://localhost:8080/api/users"))
                .header("Content-Type", "application/json")
                .POST(java.net.http.HttpRequest.BodyPublishers.ofString(json))
                .build();
            client.sendAsync(request, java.net.http.HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> javafx.application.Platform.runLater(() -> {
                    if (response.statusCode() == 201 || response.statusCode() == 200) {
                        errorLabel.setStyle("-fx-text-fill: green;");
                        errorLabel.setText("¡Usuario registrado con éxito! Volviendo al menú...");
                        // Tras 1s, vuelve al menú principal
                        new Thread(() -> {
                            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
                            javafx.application.Platform.runLater(this::handleBack);
                        }).start();
                    } else {
                        errorLabel.setStyle("-fx-text-fill: red;");
                        errorLabel.setText("Error al registrar usuario (estado: " + response.statusCode() + "): " + response.body());
                        System.out.println("[DEBUG] Error al registrar usuario (estado: " + response.statusCode() + "): " + response.body());
                    }
                }))
                .exceptionally(e -> {
                    javafx.application.Platform.runLater(() -> {
                        errorLabel.setStyle("-fx-text-fill: red;");
                        errorLabel.setText("Error: " + e.getMessage());
                    });
                    return null;
                });
        } catch (Exception ex) {
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setText("Error al enviar la solicitud: " + ex.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/fxml/menu.fxml"));
            javafx.stage.Stage stage = (javafx.stage.Stage) backButton.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
        } catch (Exception e) {
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setText("Error al volver al menú: " + e.getMessage());
        }
    }
}
