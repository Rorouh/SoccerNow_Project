package pt.ul.fc.css.soccernowfx.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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

/**
 * Controlador de la pantalla de Login.
 * – Valida campos                  (no vacíos)
 * – Envía POST  /api/login         (e-mail + password)
 * – Gestiona códigos de respuesta:
 *     200 ✔  → abre menú principal
 *     401 ✘  → contraseña incorrecta
 *     404 ✘  → e-mail no existe
 *     otro   → mensaje genérico
 */
public class LoginController {

    /* ╔═══════════════ FXML ═══════════════╗ */
    @FXML private TextField      emailField;
    @FXML private PasswordField  passwordField;
    @FXML private Label          errorLabel;
    @FXML private Hyperlink      registerLink;
    /* ╚═══════════════════════════════════╝ */

    private final HttpClient   client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    /* ---------- LOGIN ---------- */
    @FXML
    private void handleLogin() {

        String email    = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isBlank() || password.isBlank()) {
            errorLabel.setText("Correo y contraseña son obligatorios.");
            return;
        }

        try {
            String json = mapper.writeValueAsString(new LoginDTO(email, password));

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            client.sendAsync(req, HttpResponse.BodyHandlers.discarding())
                  .thenAccept(r -> Platform.runLater(() -> handleResponse(r.statusCode())))
                  .exceptionally(ex -> { 
                      Platform.runLater(() ->
                          errorLabel.setText("Error de red: " + ex.getMessage()));
                      return null;
                  });

        } catch (Exception e) {
            errorLabel.setText("Error interno: " + e.getMessage());
        }
    }

    /* decide qué hacer con cada status code */
    private void handleResponse(int code) {
        switch (code) {
            case 200 -> openMenu();
            case 401 -> errorLabel.setText("Contraseña incorrecta.");
            case 404 -> errorLabel.setText("No existe un usuario con ese correo.");
            default -> errorLabel.setText("Error al iniciar sesión. Código " + code);
        }
    }

    /* ---------- REGISTRO (enlace) ---------- */
    @FXML
    private void handleShowRegister() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/register.fxml"));
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            errorLabel.setText("Error al abrir el registro: " + e.getMessage());
        }
    }

    /* ---------- Utils ---------- */
    private void openMenu() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/menu.fxml"));
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            errorLabel.setText("Error al abrir el menú: " + e.getMessage());
        }
    }

    /* DTO local para serializar ⇄ JSON */
    private record LoginDTO(String email, String password) {}
}
