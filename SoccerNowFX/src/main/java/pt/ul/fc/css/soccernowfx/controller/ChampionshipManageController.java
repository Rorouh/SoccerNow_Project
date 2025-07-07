package pt.ul.fc.css.soccernowfx.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import pt.ul.fc.css.soccernowfx.util.RestClient;
import pt.ul.fc.css.soccernowfx.util.SceneNavigator;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ChampionshipManageController {

    /* ---------- vista ---------- */
    @FXML private TextField nameField;
    @FXML private TextField modalityField;
    @FXML private TextField formatField;
    @FXML private TextField teamsField;
    @FXML private TextField refereesField;
    @FXML private Label     infoLabel;

    /* ---------- VM ---------- */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChampVM {
        public Long   id;
        public String nome;
        public String modalidade;
        public String formato;
        public Set<Long> participanteIds;
    }

    /* ---------- estado ---------- */
    private Long currentId = null;

    /* ---------- acciones ---------- */
    @FXML
    private void handleSearch() {
        String raw = nameField.getText().trim();
        if (raw.isBlank()) { showError("Indica un nombre de campeonato."); return; }

        try {
            String enc   = URLEncoder.encode(raw, StandardCharsets.UTF_8);
            ChampVM[] vs = RestClient.get("/api/campeonatos?nome=" + enc, ChampVM[].class);

            ChampVM c = Arrays.stream(vs)
                              .filter(v -> v.nome.equalsIgnoreCase(raw))
                              .findFirst()
                              .orElse(null);

            if (c == null) { clearForm(); showError("Campeonato no encontrado."); return; }

            currentId = c.id;
            modalityField.setText(c.modalidade);
            formatField.setText(c.formato);
            teamsField.setText(c.participanteIds.stream()
                          .map(Object::toString).collect(Collectors.joining(", ")));
            // (opcional) refereesField.setText(...)
            showOk("Campeonato cargado.");
        } catch (Exception ex) {
            showError("Error buscando campeonato: " + ex.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (currentId == null) { showError("Busca un campeonato primero."); return; }

        ChampVM dto = new ChampVM();
        dto.nome       = nameField.getText().trim();
        dto.modalidade = modalityField.getText().trim();
        dto.formato    = formatField.getText().trim();
        dto.participanteIds = parseIds(teamsField.getText());

        try {
            RestClient.put("/api/campeonatos/" + currentId, dto);
            showOk("Campeonato actualizado.");
        } catch (Exception ex) {
            showError("Error al actualizar: " + ex.getMessage());
        }
    }

    @FXML
    private void handleRemove() {
        if (currentId == null) { showError("Busca un campeonato primero."); return; }
        try {
            RestClient.delete("/api/campeonatos/" + currentId);
            clearForm(); currentId = null;
            showOk("Campeonato eliminado.");
        } catch (Exception ex) {
            showError("Error al eliminar: " + ex.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        SceneNavigator.switchScene(infoLabel, "/fxml/menu.fxml");
    }

    /* ---------- util ---------- */
    private Set<Long> parseIds(String raw) {
        return Arrays.stream(raw.split(","))
                     .map(String::trim)
                     .filter(s -> !s.isBlank())
                     .map(Long::valueOf)
                     .collect(Collectors.toSet());
    }

    private void clearForm() {
        Platform.runLater(() -> {
            modalityField.clear(); formatField.clear();
            teamsField.clear(); refereesField.clear();
        });
    }

    private void showError(String m) { infoLabel.setStyle("-fx-text-fill:red;");   infoLabel.setText(m); }
    private void showOk   (String m) { infoLabel.setStyle("-fx-text-fill:green;"); infoLabel.setText(m); }
}
