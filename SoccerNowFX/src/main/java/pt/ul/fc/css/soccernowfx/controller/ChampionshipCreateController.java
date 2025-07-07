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

public class ChampionshipCreateController {

    @FXML private TextField nameField;
    @FXML private TextField modalityField;
    @FXML private TextField formatField;
    @FXML private TextField teamsField;
    @FXML private TextField refereesField;
    @FXML private Label     infoLabel;

    /* --- Vista mínima de TeamDTO --- */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamVM {
        public Long id;
        public String name;
    }

    public static final class CampePayload {
        public String   nome;
        public String   modalidade;
        public String   formato;
        public Set<Long> participanteIds = new HashSet<>();
        CampePayload(String n, String m, String f, Collection<Long> ids) {
            nome=n; modalidade=m; formato=f; participanteIds.addAll(ids);
        }
    }

    /* ---------- acciones ---------- */
    @FXML
    private void handleCreate() {
        infoLabel.setStyle("-fx-text-fill: red;");
        infoLabel.setText("");

        String nome       = nameField.getText().trim();
        String modalidade = modalityField.getText().trim();
        String formato    = formatField.getText().trim();
        List<String> teamNames = parseComma(teamsField.getText());

        if (nome.isBlank() || modalidade.isBlank() || formato.isBlank() || teamNames.isEmpty()) {
            infoLabel.setText("Todos los campos (menos árbitros) son obligatorios.");
            return;
        }

        Set<Long> ids = new HashSet<>();
        for (String tName : teamNames) {
            try {
                String enc = URLEncoder.encode(tName, StandardCharsets.UTF_8);
                TeamVM[] list = RestClient.get("/api/teams?name=" + enc, TeamVM[].class);
                Optional<TeamVM> match = Arrays.stream(list)
                                               .filter(t -> t.name.equalsIgnoreCase(tName))
                                               .findFirst();
                if (match.isEmpty()) {
                    infoLabel.setText("Equipo no encontrado: " + tName);
                    return;
                }
                ids.add(match.get().id);
            } catch (Exception ex) {
                infoLabel.setText("Error buscando equipo " + tName + ": " + ex.getMessage());
                return;
            }
        }

        try {
            RestClient.post("/api/campeonatos", new CampePayload(nome, modalidade, formato, ids), Map.class);
            infoLabel.setStyle("-fx-text-fill: green;");
            infoLabel.setText("¡Campeonato creado con éxito!");
            clearForm();
        } catch (Exception ex) {
            infoLabel.setText("Error al crear campeonato: " + ex.getMessage());
        }
    }

    @FXML
    private void handleBack() { SceneNavigator.switchScene(infoLabel, "/fxml/menu.fxml"); }

    /* ---------- util ---------- */
    private List<String> parseComma(String raw) {
        if (raw == null) return List.of();
        return Arrays.stream(raw.split(","))
                     .map(String::trim)
                     .filter(s -> !s.isBlank())
                     .collect(Collectors.toList());
    }
    private void clearForm() {
        Platform.runLater(() -> {
            nameField.clear(); modalityField.clear(); formatField.clear();
            teamsField.clear(); refereesField.clear();
        });
    }
}
