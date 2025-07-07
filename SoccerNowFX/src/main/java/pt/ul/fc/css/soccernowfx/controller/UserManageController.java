package pt.ul.fc.css.soccernowfx.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import pt.ul.fc.css.soccernowfx.util.RestClient;
import pt.ul.fc.css.soccernowfx.util.SceneNavigator;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Pantalla “Gestión de Usuario”.
 * – Java 8 compatible (sin records, sin switch → expresiones).<br>
 * – Maneja correctamente 200/204, 404, 409 y 5xx.
 */
public class UserManageController {

    // ───────────── FXML ─────────────
    @FXML private TextField emailField;
    @FXML private TextField nameField;
    @FXML private ChoiceBox<String> typeChoice;
    @FXML private ChoiceBox<String> extraField;
    @FXML private Label infoLabel;

    // ───────────── DTO para JSON ─────────────
    public static class UserVM {
        private Long id;
        private String name;
        private String email;
        private String role;
        private String preferredPosition;
        private Boolean certified;
        // getters (Jackson necesita default ctor + getters)
        public Long getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getRole() { return role; }
        public String getPreferredPosition() { return preferredPosition; }
        public Boolean getCertified() { return certified; }
    }

    // ───────────── INIT ─────────────
    @FXML
    public void initialize() {
        typeChoice.getItems().addAll("PLAYER", "REFEREE");
        typeChoice.setOnAction(e -> updateExtraOptions());
        updateExtraOptions();
    }

    private void updateExtraOptions() {
        extraField.getItems().clear();
        String role = typeChoice.getValue();
        if ("PLAYER".equalsIgnoreCase(role)) {
            extraField.getItems().addAll("PORTERO", "DEFENSA", "CENTROCAMPISTA", "DELANTERO");
        } else if ("REFEREE".equalsIgnoreCase(role)) {
            extraField.getItems().addAll("true", "false"); // certified?
        }
        if (!extraField.getItems().isEmpty()) {
            extraField.setValue(extraField.getItems().get(0));
        }
    }

    // ───────────── BUSCAR ─────────────
    @FXML
    private void handleSearch() {
        final String email = emailField.getText().trim();
        if (email.isEmpty()) { infoLabel.setText("Indica el correo para buscar."); return; }

        final String encoded = URLEncoder.encode(email, StandardCharsets.UTF_8);
        final String path = "/api/users/by-email/" + encoded;

        CompletableFuture.<UserVM>supplyAsync(() -> {
            try { return RestClient.get(path, UserVM.class); }
            catch (Exception ex) { throw new RuntimeException(ex); }
        }).thenAccept(user -> Platform.runLater(() -> {
            nameField.setText(user.getName());
            typeChoice.setValue(user.getRole());
            updateExtraOptions();
            if ("PLAYER".equalsIgnoreCase(user.getRole())) {
                extraField.setValue(user.getPreferredPosition());
            } else {
                extraField.setValue(String.valueOf(Boolean.TRUE.equals(user.getCertified())));
            }
            infoLabel.setText("Usuario encontrado.");
        })).exceptionally(ex -> {
            Platform.runLater(() -> infoLabel.setText("No encontrado o error: "
                                                     + ex.getCause().getMessage()));
            return null;
        });
    }

    // ───────────── ACTUALIZAR ─────────────
    @FXML
    private void handleUpdate() {
        final String email = emailField.getText().trim();
        final String name  = nameField.getText().trim();
        final String role  = typeChoice.getValue();
        final String extra = extraField.getValue();

        if (email.isEmpty() || name.isEmpty() || role == null || role.isEmpty()) {
            infoLabel.setText("Completa los campos obligatorios."); return;
        }

        Map<String,Object> body = new HashMap<>();
        body.put("name", name);
        body.put("email", email);
        body.put("role", role);  // ← indispensable si backend valida @NotNull

        if ("PLAYER".equalsIgnoreCase(role))  body.put("preferredPosition", extra);
        else                                   body.put("certified", Boolean.valueOf(extra));

        final String encoded = URLEncoder.encode(email, StandardCharsets.UTF_8);
        final String path = "/api/users/by-email/" + encoded;

        CompletableFuture.runAsync(() -> {
            try { RestClient.put(path, body); }
            catch (Exception ex) { throw new RuntimeException(ex); }
        }).thenRun(() -> Platform.runLater(() ->
            infoLabel.setText("¡Usuario actualizado!")
        )).exceptionally(ex -> {
            Platform.runLater(() ->
                infoLabel.setText("Error al actualizar: " + ex.getCause().getMessage()));
            return null;
        });
    }

    // ───────────── ELIMINAR ─────────────
    @FXML
    private void handleRemove() {
        final String email = emailField.getText().trim();
        if (email.isEmpty()) { infoLabel.setText("Indica el correo para eliminar."); return; }

        final String encoded = URLEncoder.encode(email, StandardCharsets.UTF_8);
        final String url = "http://localhost:8080/api/users/by-email/" + encoded;

        CompletableFuture.runAsync(() -> {
            try {
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .DELETE()
                        .build();
                HttpResponse<String> resp = HttpClient.newHttpClient()
                        .send(req, HttpResponse.BodyHandlers.ofString());

                int code = resp.statusCode();
                if (code == 200 || code == 204) {        // eliminado
                    Platform.runLater(() -> {
                        infoLabel.setText("¡Usuario eliminado!");
                        nameField.clear();
                        typeChoice.setValue(null);
                        extraField.getItems().clear();
                    });
                } else if (code == 404) {                // no existe
                    Platform.runLater(() -> infoLabel.setText("Usuario no encontrado."));
                } else if (code == 409 || code == 400) { // integridad referencial
                    Platform.runLater(() -> infoLabel.setText(
                        "No se puede eliminar: el usuario está referenciado."));
                } else {                                 // 5xx u otros
                    Platform.runLater(() -> infoLabel.setText(
                        "Error interno (" + code + "): " + abrevia(resp.body())));
                }
            } catch (Exception ex) {
                Platform.runLater(() -> infoLabel.setText("Error al eliminar: " + ex.getMessage()));
            }
        });
    }

    // ───────────── VOLVER ─────────────
    @FXML
    private void handleBack() {
        SceneNavigator.switchScene(infoLabel, "/fxml/menu.fxml");
    }

    // Util ─ recorta HTML grande en caso de 500
    private String abrevia(String s) {
        if (s == null) return "";
        int idx = s.indexOf("<p>");
        if (idx != -1) {
            int end = s.indexOf("</p>", idx);
            if (end != -1) return s.substring(idx + 3, Math.min(end, idx + 200));
        }
        return s.length() > 200 ? s.substring(0, 200) + "..." : s;
    }
}
