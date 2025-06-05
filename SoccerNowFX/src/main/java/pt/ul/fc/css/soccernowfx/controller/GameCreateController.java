package pt.ul.fc.css.soccernowfx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.application.Platform;
import java.net.http.*;
import java.net.URI;

public class GameCreateController {


    @FXML
    public void initialize() {
        typeChoice.getItems().addAll("Amigável", "Campeonato");
        typeChoice.setValue("Amigável"); // valor padrão
    }

    @FXML
    private void handleBack() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/menu.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) dateField.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            infoLabel.setText("Erro ao voltar ao menu: " + e.getMessage());
        }
    }

    @FXML private DatePicker dateField;
    @FXML private TextField timeField;
    @FXML private TextField locationField;
    @FXML private TextField team1Field;
    @FXML private TextField team2Field;
    @FXML private TextField refereesField;
    @FXML private ChoiceBox<String> typeChoice;
    @FXML private TextField championshipField;

    @FXML private TextField goalkeeper1Field;
    @FXML private TextField goalkeeper2Field;
    @FXML private TextField mainRefereeField;

    @FXML private Label infoLabel;

    @FXML
    private void handleCreate() {
        String data = dateField.getValue() != null ? dateField.getValue().toString() : "";
        String hora = timeField.getText();
        String local = locationField.getText();
        String equipa1 = team1Field.getText();
        String equipa2 = team2Field.getText();
        String arbitros = refereesField.getText();
        String tipo = typeChoice.getValue();
        String campeonato = championshipField.getText();

        String goalkeeper1 = goalkeeper1Field.getText();
        String goalkeeper2 = goalkeeper2Field.getText();
        String mainReferee = mainRefereeField.getText();
        if (data.isBlank() || hora.isBlank() || local.isBlank() || equipa1.isBlank() || equipa2.isBlank() || arbitros.isBlank() || tipo == null || goalkeeper1.isBlank() || goalkeeper2.isBlank() || mainReferee.isBlank()) {
            infoLabel.setText("Preencha todos os campos obrigatórios.");
            return;
        }
        // Validação: 5 jogadores por equipa
        String[] jogadores1 = equipa1.split(",");
        String[] jogadores2 = equipa2.split(",");
        if (jogadores1.length != 5) {
            infoLabel.setText("A Equipa 1 deve ter exatamente 5 jogadores.");
            return;
        }
        if (jogadores2.length != 5) {
            infoLabel.setText("A Equipa 2 deve ter exatamente 5 jogadores.");
            return;
        }
        // Validação: guarda-redes está entre os jogadores
        boolean gk1ok = false;
        for (String email : jogadores1) {
            if (email.trim().equalsIgnoreCase(goalkeeper1.trim())) gk1ok = true;
        }
        if (!gk1ok) {
            infoLabel.setText("O guarda-redes da Equipa 1 deve estar entre os jogadores informados.");
            return;
        }
        boolean gk2ok = false;
        for (String email : jogadores2) {
            if (email.trim().equalsIgnoreCase(goalkeeper2.trim())) gk2ok = true;
        }
        if (!gk2ok) {
            infoLabel.setText("O guarda-redes da Equipa 2 deve estar entre os jogadores informados.");
            return;
        }
        // Validação: árbitro principal está entre os árbitros
        String[] arbitrosArr = arbitros.split(",");
        boolean mainRefOk = false;
        for (String email : arbitrosArr) {
            if (email.trim().equalsIgnoreCase(mainReferee.trim())) mainRefOk = true;
        }
        if (!mainRefOk) {
            infoLabel.setText("O árbitro principal deve estar entre os árbitros informados.");
            return;
        }
        // Se for jogo de campeonato, árbitro principal deve ser certificado
        if (tipo.equalsIgnoreCase("Campeonato")) {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/users/by-email/" + mainReferee.trim()))
                        .GET()
                        .build();
                HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
                if (resp.statusCode() != 200 || !resp.body().contains("\"role\":\"REFEREE\"") || !resp.body().contains("\"certified\":true")) {
                    infoLabel.setText("O árbitro principal deve ser certificado para jogos de campeonato.");
                    return;
                }
            } catch (Exception ex) {
                infoLabel.setText("Erro ao validar árbitro principal: " + mainReferee);
                return;
            }
        }
        // Monta JSON incluindo guarda-redes e árbitro principal

        if (data.isBlank() || hora.isBlank() || local.isBlank() || equipa1.isBlank() || equipa2.isBlank() || arbitros.isBlank() || tipo == null) {
            infoLabel.setText("Preencha todos os campos obrigatórios.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("{\"data\":\"").append(data).append("\",")
          .append("\"hora\":\"").append(hora).append("\",")
          .append("\"local\":\"").append(local).append("\",")
          .append("\"equipa1\":\"").append(equipa1).append("\",")
          .append("\"equipa2\":\"").append(equipa2).append("\",")
          .append("\"arbitros\":[");
        for (int i = 0; i < arbitrosArr.length; i++) {
            sb.append("\"").append(arbitrosArr[i].trim()).append("\"");
            if (i < arbitrosArr.length - 1) sb.append(",");
        }
        sb.append("]");
        if ("Campeonato".equals(tipo) && !campeonato.isBlank()) {
            sb.append(",\"campeonato\":\"").append(campeonato).append("\"");
        }
        sb.append("}");
        String json = sb.toString();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/games"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenAccept(response -> Platform.runLater(() -> {
                if (response.statusCode() == 201 || response.statusCode() == 200) {
                    infoLabel.setText("Jogo criado com sucesso!");
                } else {
                    infoLabel.setText("Erro ao criar jogo: " + response.body());
                }
            }))
            .exceptionally(e -> { Platform.runLater(() -> infoLabel.setText("Erro: " + e.getMessage())); return null; });
    }
}
