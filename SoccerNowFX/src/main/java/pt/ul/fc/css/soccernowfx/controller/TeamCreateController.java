package pt.ul.fc.css.soccernowfx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.net.http.*;
import java.net.URI;

public class TeamCreateController {
    @FXML private TextField nameField;
    @FXML private TextField playersField;
    @FXML private Label infoLabel;
<<<<<<< HEAD
    @FXML private Button backButton;
    @FXML private ListView<String> emailListView;

    @FXML
    private void handleCreate() {
        // Limpa validação visual
        infoLabel.setText("");
        playersField.setStyle("");
        if (emailListView != null) emailListView.getItems().clear();
=======

    @FXML
    private void handleCreate() {
>>>>>>> 52a6f9c (Entrega fase1: implementação, testes, docker e documentação)
        String nome = nameField.getText();
        String jogadores = playersField.getText();
        if (nome.isBlank()) {
            infoLabel.setText("Nome da equipa obrigatório.");
            return;
        }
<<<<<<< HEAD
        // Validação de jogadores
        String[] jogadoresArr = jogadores.isBlank() ? new String[0] : jogadores.split(",");
        if (jogadoresArr.length == 0) {
            infoLabel.setText("Informe pelo menos um jogador.");
            return;
        }
        // Validação visual de emails
        boolean algumInvalido = false;
        StringBuilder invalidos = new StringBuilder();
        if (emailListView != null) emailListView.getItems().clear();
        for (String email : jogadoresArr) {
            email = email.trim();
            boolean valido = false;
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/users/by-email/" + email))
                        .GET()
                        .build();
                HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
                String body = resp.body().toLowerCase();
                if (resp.statusCode() == 200 &&
                    (body.contains("\"tipo\":\"jogador\"") || body.contains("\"role\":\"jogador\"") || body.contains("\"role\":\"player\""))) {
                    valido = true;
                }
            } catch (Exception ex) {
                // erro de conexão ou servidor
            }
            if (emailListView != null) {
                emailListView.getItems().add(email + (valido ? " ✓" : " ✗"));
            }
            if (!valido) {
                algumInvalido = true;
                invalidos.append(email).append(", ");
            }
        }
        if (algumInvalido) {
            playersField.setStyle("-fx-border-color: red;");
            infoLabel.setStyle("-fx-text-fill: red;");
            infoLabel.setText("Os seguintes emails não são válidos: " + invalidos.toString());
            return;
        }
        playersField.setStyle("");
        infoLabel.setStyle("-fx-text-fill: green;");
        // Monta JSON: {"name":"...","playerEmails":["email1","email2",...]}
        StringBuilder sb = new StringBuilder();
        sb.append("{\"name\":\"").append(nome).append("\",\"playerEmails\":[");
=======
        // Monta JSON: {"nome":"...","jogadores":["email1","email2",...]}
        String[] jogadoresArr = jogadores.isBlank() ? new String[0] : jogadores.split(",");
        StringBuilder sb = new StringBuilder();
        sb.append("{\"nome\":\"").append(nome).append("\",\"jogadores\":[");
>>>>>>> 52a6f9c (Entrega fase1: implementação, testes, docker e documentação)
        for (int i = 0; i < jogadoresArr.length; i++) {
            sb.append("\"").append(jogadoresArr[i].trim()).append("\"");
            if (i < jogadoresArr.length - 1) sb.append(",");
        }
        sb.append("]}");
        String json = sb.toString();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/teams"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenAccept(response -> Platform.runLater(() -> {
                if (response.statusCode() == 201 || response.statusCode() == 200) {
<<<<<<< HEAD
                    infoLabel.setStyle("-fx-text-fill: green;");
                    infoLabel.setText("Equipa criada com sucesso!");
                } else {
                    infoLabel.setStyle("-fx-text-fill: red;");
                    infoLabel.setText("Erro ao criar equipa: " + response.body());
                }
            }))
            .exceptionally(e -> { Platform.runLater(() -> { infoLabel.setStyle("-fx-text-fill: red;"); infoLabel.setText("Erro: " + e.getMessage()); }); return null; });
=======
                    infoLabel.setText("Equipa criada com sucesso!");
                } else {
                    infoLabel.setText("Erro ao criar equipa: " + response.body());
                }
            }))
            .exceptionally(e -> { Platform.runLater(() -> infoLabel.setText("Erro: " + e.getMessage())); return null; });
>>>>>>> 52a6f9c (Entrega fase1: implementação, testes, docker e documentação)
    }

    @FXML
    private void handleBack() {
        try {
<<<<<<< HEAD
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/menu.fxml"));
            Stage stage = (Stage) infoLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            infoLabel.setText("Erro ao voltar ao menu: " + e.getMessage());
=======
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Stage stage = (Stage) infoLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            infoLabel.setText("Erro ao voltar: " + e.getMessage());
>>>>>>> 52a6f9c (Entrega fase1: implementação, testes, docker e documentação)
        }
    }
}
