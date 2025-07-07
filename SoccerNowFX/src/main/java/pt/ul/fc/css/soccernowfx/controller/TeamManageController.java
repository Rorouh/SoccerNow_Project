package pt.ul.fc.css.soccernowfx.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TeamManageController {

    /* ---------- FXML ---------- */
    @FXML private TextField nameField;
    @FXML private TextField playersField;
    @FXML private Label     infoLabel;

    /* ---------- HTTP / JSON ---------- */
    private final HttpClient   client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    /* ---------- estado ---------- */
    private Long   currentId   = null;
    private Long[] currentPIds = new Long[0];

    /* ============================================================= */
    /*                        BUSCAR EQUIPO                           */
    /* ============================================================= */
    @FXML
    private void handleSearch() {

        String raw = nameField.getText().trim();
        if (raw.isEmpty()) { setError("Indica el nombre."); return; }

        String encoded = URLEncoder.encode(raw, StandardCharsets.UTF_8);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/teams?name=" + encoded))
                .GET()
                .build();

        client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
              .thenAccept(resp -> Platform.runLater(() -> {
                  try {
                      if (resp.statusCode() != 200) { setError("HTTP " + resp.statusCode()); return; }

                      TeamVM[] arr = mapper.readValue(resp.body(), TeamVM[].class);
                      if (arr.length == 0) { setInfo("Equipo no encontrado."); clear(); return; }

                      TeamVM t = arr[0];
                      currentId   = t.getId();
                      currentPIds = t.getPlayerIds() != null ? t.getPlayerIds() : new Long[0];

                      nameField.setText(t.getName());
                      playersField.setText(
                              t.getPlayerEmails() == null ? "" :
                              String.join(",", t.getPlayerEmails())
                      );

                      setOk("Equipo cargado.");
                  } catch (Exception e) { setError("JSON: " + e.getMessage()); }
              }))
              .exceptionally(ex -> { Platform.runLater(() -> setError(ex.getMessage())); return null; });
    }

    /* ============================================================= */
    /*                       ACTUALIZAR EQUIPO                       */
    /* ============================================================= */
    @FXML
    private void handleUpdate() {

        if (currentId == null) { setInfo("Busca un equipo primero."); return; }

        String newName = nameField.getText().trim();
        if (newName.isBlank()) { setError("El nombre no puede quedar vacío."); return; }

        String csv = playersField.getText().trim();
        Long[] ids;
        try {
            if (csv.isBlank()) {
                ids = currentPIds;                  // sin cambios
            } else {
                String[] emails = csv.split("\\s*,\\s*");
                List<Long> idList = new ArrayList<>();

                for (String email : emails) {
                    String enc = URLEncoder.encode(email, StandardCharsets.UTF_8);
                    HttpRequest q = HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8080/api/users/by-email/" + enc))
                            .GET()
                            .build();

                    HttpResponse<String> r = client.send(q, HttpResponse.BodyHandlers.ofString());
                    if (r.statusCode() != 200) throw new RuntimeException("No existe " + email);

                    UserVM u = mapper.readValue(r.body(), UserVM.class);
                    idList.add(u.getId());
                }
                ids = idList.toArray(new Long[0]);
            }
        } catch (Exception e) { setError("Conv. IDs: " + e.getMessage()); return; }

        try {
            String json = mapper.writeValueAsString(new TeamUpdPayload(newName, ids));
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/teams/" + currentId))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                  .thenAccept(r -> Platform.runLater(() ->
                      { if (r.statusCode() == 200) setOk("Actualizado.");
                        else setError(r.body().isBlank()
                               ? "No se pudo actualizar (código "+r.statusCode()+')'
                               : r.body()); }))
                  .exceptionally(ex -> { Platform.runLater(() -> setError(ex.getMessage())); return null; });

        } catch (Exception e) { setError("Serialización: " + e.getMessage()); }
    }

    /* ============================================================= */
    /*                       ELIMINAR EQUIPO                         */
    /* ============================================================= */
    @FXML
    private void handleRemove() {

        if (currentId == null) { setInfo("Busca un equipo primero."); return; }

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/teams/" + currentId))
                .DELETE()
                .build();

        client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
              .thenAccept(r -> Platform.runLater(() -> {
                  if (r.statusCode() == 204) { setOk("Eliminado."); clear(); }
                  else {
                      String msg = r.body().isBlank()
                                 ? "No se puede eliminar (código "+r.statusCode()+')'
                                 : r.body();
                      setError(msg);
                  }
              }))
              .exceptionally(ex -> { Platform.runLater(() -> setError(ex.getMessage())); return null; });
    }

    /* ============================================================= */
    /*                            VOLVER                             */
    /* ============================================================= */
    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/menu.fxml"));
            Stage st = (Stage) infoLabel.getScene().getWindow();
            st.setScene(new Scene(root));
        } catch (Exception e) { setError("Al volver: " + e.getMessage()); }
    }

    /* ---------- helpers ---------- */
    private void clear() {
        currentId   = null;
        currentPIds = new Long[0];
        nameField.clear();
        playersField.clear();
    }
    private void setOk   (String m){ infoLabel.setStyle("-fx-text-fill: green;"); infoLabel.setText(m); }
    private void setInfo (String m){ infoLabel.setStyle("-fx-text-fill: black;"); infoLabel.setText(m); }
    private void setError(String m){ infoLabel.setStyle("-fx-text-fill: red;");   infoLabel.setText("Error: " + m); }

    /* ---------- DTO auxiliares ---------- */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamVM {
        private Long     id;
        private String   name;
        private String[] playerEmails;
        private Long[]   playerIds;
        public Long     getId()                { return id; }
        public void     setId(Long id)         { this.id = id; }
        public String   getName()              { return name; }
        public void     setName(String n)      { this.name = n; }
        public String[] getPlayerEmails()      { return playerEmails; }
        public void     setPlayerEmails(String[] e){ this.playerEmails = e; }
        public Long[]   getPlayerIds()         { return playerIds; }
        public void     setPlayerIds(Long[] p) { this.playerIds = p; }
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class UserVM {
        private Long id;
        public Long getId()            { return id; }
        public void setId(Long id)     { this.id = id; }
    }
    private static class TeamUpdPayload {
        private final String name;
        private final Long[] playerIds;
        TeamUpdPayload(String n, Long[] ids){ this.name = n; this.playerIds = ids; }
        public String getName()      { return name; }
        public Long[] getPlayerIds() { return playerIds; }
    }
}
