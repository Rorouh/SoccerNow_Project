package pt.ul.fc.css.soccernowfx.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class TeamManageController {

    /* FXML */
    @FXML private TextField nameField;
    @FXML private TextField playersField;
    @FXML private ListView<String> emailListView;
    @FXML private Label infoLabel;

    /* infra */
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    /* estado de la vista */
    private Long   currentId   = null;

    /* ------------- BUSCAR ------------- */
    @FXML
    private void handleSearch() {
        String query = nameField.getText().trim();
        if (query.isEmpty()) { setError("Indica el nombre."); return; }

        String url = "http://localhost:8080/api/teams?name=" +
                     URLEncoder.encode(query, StandardCharsets.UTF_8);

        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

        client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
              .thenAcceptAsync(resp -> {
                  if (resp.statusCode()!=200)       { setError("HTTP " + resp.statusCode()); return; }
                  try {
                      TeamVM[] arr = mapper.readValue(resp.body(), TeamVM[].class);
                      if (arr.length==0)            { setInfo("Equipo no encontrado."); clear(); return; }

                      TeamVM t = arr[0];
                      currentId = t.id;
                      nameField.setText(t.name);

                      /* muestra e-mails */
                      String csv = String.join(", ", t.playerEmails);
                      playersField.setText(csv);
                      emailListView.getItems().setAll(Arrays.asList(t.playerEmails));

                      setOk("Equipo cargado.");
                  } catch (Exception e) { setError("JSON: "+e.getMessage()); }
              }, Platform::runLater)
              .exceptionally(ex -> { Platform.runLater(() -> setError(ex.getMessage())); return null; });
    }

    /* ------------- ACTUALIZAR ------------- */
    @FXML
    private void handleUpdate() {
        if (currentId==null) { setInfo("Busca antes un equipo."); return; }

        String nuevoNombre = nameField.getText().trim();
        if (nuevoNombre.isBlank()) { setError("El nombre no puede ser vacío."); return; }

        /* convertimos la caja de e-mails -> array de IDs */
        String csv = playersField.getText().trim();
        Long[] ids;
        try {
            if (csv.isBlank()) { ids = new Long[0]; }
            else {
                String[] mails = csv.split("\\s*,\\s*");
                ids = new Long[mails.length];
                for (int i=0;i<mails.length;i++) {
                    String url = "http://localhost:8080/api/users/by-email/" +
                                 URLEncoder.encode(mails[i], StandardCharsets.UTF_8);
                    HttpRequest q = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
                    HttpResponse<String> r = client.send(q, HttpResponse.BodyHandlers.ofString());
                    if (r.statusCode()!=200) throw new IllegalStateException("No existe "+mails[i]);
                    ids[i] = mapper.readValue(r.body(), UserVM.class).id;
                }
            }
        } catch(Exception e){ setError(e.getMessage()); return; }

        try {
            String body = mapper.writeValueAsString(new TeamUpdPayload(nuevoNombre, ids));
            String url  = "http://localhost:8080/api/teams/" + currentId;
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type","application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                  .thenAcceptAsync(r -> {
                      if (r.statusCode()==200) setOk("Actualizado.");
                      else                      setError(r.body());
                  }, Platform::runLater);
        } catch (Exception e){ setError(e.getMessage()); }
    }

    /* ------------- ELIMINAR ------------- */
    @FXML
    private void handleRemove() {
        if (currentId==null) { setInfo("Busca antes un equipo."); return; }

        String url = "http://localhost:8080/api/teams/" + currentId;
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).DELETE().build();

        client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
              .thenAcceptAsync(r -> {
                  if (r.statusCode()==204) { clear(); setOk("Eliminado."); }
                  else if (r.statusCode()==400) setError("No se puede eliminar: "+r.body());
                  else setError("HTTP "+r.statusCode());
              }, Platform::runLater);
    }

    /* ------------- VOLVER ------------- */
    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/menu.fxml"));
            Stage st = (Stage) infoLabel.getScene().getWindow();
            st.setScene(new Scene(root));
        } catch(Exception e){ setError(e.getMessage()); }
    }

    /* -------------------------------------------------------- */
    private void clear(){
        currentId=null; nameField.clear(); playersField.clear(); emailListView.getItems().clear();
    }
    private void setOk(String m){   infoLabel.setStyle("-fx-text-fill: green;"); infoLabel.setText(m); }
    private void setInfo(String m){ infoLabel.setStyle("-fx-text-fill: black;"); infoLabel.setText(m); }
    private void setError(String m){infoLabel.setStyle("-fx-text-fill: red;");   infoLabel.setText("Error: "+m); }

    /* DTOs auxiliares ------- */
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class UserVM { public Long id; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class TeamVM {
        public Long     id;
        public String   name;
        public String[] playerEmails = new String[0];
    }

    private record TeamUpdPayload(String name, Long[] playerIds){}
}
