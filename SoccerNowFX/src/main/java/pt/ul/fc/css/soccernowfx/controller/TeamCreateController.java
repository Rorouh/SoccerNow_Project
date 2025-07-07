package pt.ul.fc.css.soccernowfx.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import pt.ul.fc.css.soccernowfx.util.RestClient;
import pt.ul.fc.css.soccernowfx.util.SceneNavigator;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class TeamCreateController {

    @FXML private TextField nameField;
    @FXML private TextField playersField;
    @FXML private ListView<String> emailListView;
    @FXML private Label infoLabel;

    /* ---------- DTOs internos ---------- */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserVM {
        public Long   id;
        public String role;
    }

    public static final class TeamPayload {
        public String   name;
        public Set<Long> playerIds = new HashSet<>();
        TeamPayload(String n, Collection<Long> ids) { name = n; playerIds.addAll(ids); }
    }

    /* ---------- init ---------- */
    @FXML
    private void initialize() {
        playersField.textProperty().addListener((obs, o, n) ->
                emailListView.getItems().setAll(parseEmails(n)));
    }

    /* ---------- acciones ---------- */
    @FXML
    private void handleCreate() {
        infoLabel.setStyle("-fx-text-fill: red;");
        infoLabel.setText("");

        String teamName = nameField.getText().trim();
        List<String> emails = parseEmails(playersField.getText());

        if (teamName.isBlank() || emails.isEmpty()) {
            infoLabel.setText("Nombre y correos son obligatorios.");
            return;
        }

        List<Long> ids = new ArrayList<>();
        for (String mail : emails) {
            try {
                String enc = URLEncoder.encode(mail, StandardCharsets.UTF_8);
                UserVM u = RestClient.get("/api/users/by-email/" + enc, UserVM.class);
                if (!"PLAYER".equalsIgnoreCase(u.role)) {
                    infoLabel.setText(mail + " no pertenece a un PLAYER.");
                    return;
                }
                ids.add(u.id);
            } catch (Exception ex) {
                infoLabel.setText("No existe usuario con correo: " + mail);
                return;
            }
        }

        try {
            RestClient.post("/api/teams", new TeamPayload(teamName, ids), Map.class);
            infoLabel.setStyle("-fx-text-fill: green;");
            infoLabel.setText("¡Equipo creado con éxito!");
            clearForm();
        } catch (Exception ex) {
            infoLabel.setText("Error al crear equipo: " + ex.getMessage());
        }
    }

    @FXML
    private void handleBack() { SceneNavigator.switchScene(infoLabel, "/fxml/menu.fxml"); }

    /* ---------- util ---------- */
    private List<String> parseEmails(String raw) {
        if (raw == null) return List.of();
        return Arrays.stream(raw.split(","))
                     .map(String::trim)
                     .filter(s -> !s.isBlank())
                     .collect(Collectors.toList());
    }
    private void clearForm() {
        Platform.runLater(() -> {
            nameField.clear(); playersField.clear(); emailListView.getItems().clear();
        });
    }
}
