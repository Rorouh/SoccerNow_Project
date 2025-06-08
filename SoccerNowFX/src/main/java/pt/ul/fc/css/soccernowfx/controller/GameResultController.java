package pt.ul.fc.css.soccernowfx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.application.Platform;
import java.net.http.*;
import java.net.URI;

public class GameResultController {
<<<<<<< HEAD

    @FXML
    private void handleBack() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/menu.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) gameIdField.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            infoLabel.setText("Erro ao voltar ao menu: " + e.getMessage());
        }
    }
=======
>>>>>>> 52a6f9c (Entrega fase1: implementação, testes, docker e documentação)
    @FXML private TextField gameIdField;
    @FXML private TextField scoreField;
    @FXML private TextField winnerField;
    @FXML private TextField cardsField;
    @FXML private Label infoLabel;

    @FXML
    private void handleRegister() {
        String gameId = gameIdField.getText();
        String placar = scoreField.getText();
        String vencedora = winnerField.getText();
        String cartoes = cardsField.getText();
        if (gameId.isBlank() || placar.isBlank() || vencedora.isBlank()) {
            infoLabel.setText("Preencha os campos obrigatórios.");
            return;
        }
        // Cartões: email:tipo, separados por vírgula
        String[] cartoesArr = cartoes.isBlank() ? new String[0] : cartoes.split(",");
        StringBuilder sb = new StringBuilder();
<<<<<<< HEAD
        sb.append("{\"gameId\":\"").append(gameId).append("\",\"placar\":\"").append(placar).append("\",\"vencedora\":\"").append(vencedora).append("\",\"cartoes\":[");
        for (int i = 0; i < cartoesArr.length; i++) {
            sb.append("\"").append(cartoesArr[i].trim()).append("\"");
            if (i < cartoesArr.length - 1) sb.append(",");
=======
        sb.append("{\"placar\":\"").append(placar).append("\",\"vencedora\":\"").append(vencedora).append("\",\"cartoes\":[");
        for (int i = 0; i < cartoesArr.length; i++) {
            String[] partes = cartoesArr[i].trim().split(":");
            if (partes.length == 2) {
                sb.append("{\"email\":\"").append(partes[0].trim()).append("\",\"tipo\":\"").append(partes[1].trim()).append("\"}");
                if (i < cartoesArr.length - 1) sb.append(",");
            }
>>>>>>> 52a6f9c (Entrega fase1: implementação, testes, docker e documentação)
        }
        sb.append("]}");
        String json = sb.toString();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
<<<<<<< HEAD
                .uri(URI.create("http://localhost:8080/api/games/result"))
=======
                .uri(URI.create("http://localhost:8080/api/games/" + gameId + "/result"))
>>>>>>> 52a6f9c (Entrega fase1: implementação, testes, docker e documentação)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenAccept(response -> Platform.runLater(() -> {
                if (response.statusCode() == 200) {
                    infoLabel.setText("Resultado registado com sucesso!");
<<<<<<< HEAD
                    // Atualizar histórico de conquistas se for jogo de campeonato
                    atualizarPodioSeCampeonato(gameId, vencedora);
=======
>>>>>>> 52a6f9c (Entrega fase1: implementação, testes, docker e documentação)
                } else {
                    infoLabel.setText("Erro ao registar resultado: " + response.body());
                }
            }))
            .exceptionally(e -> { Platform.runLater(() -> infoLabel.setText("Erro: " + e.getMessage())); return null; });
    }
<<<<<<< HEAD

    private void atualizarPodioSeCampeonato(String gameId, String vencedora) {
        // Busca info do jogo para saber se é campeonato e quem ficou em 2º/3º, etc.
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/games/" + gameId))
                    .GET()
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() == 200 && resp.body().contains("\"tipo\":\"Campeonato\"")) {
                // Atualiza pódio da equipa vencedora (1º lugar)
                atualizarPodioEquipe(vencedora, "primeiro");
                // Opcional: buscar equipa perdedora e atualizar como segundo lugar
                String equipa2 = extrairCampo(resp.body(), "equipa1").equals(vencedora) ? extrairCampo(resp.body(), "equipa2") : extrairCampo(resp.body(), "equipa1");
                atualizarPodioEquipe(equipa2, "segundo");
                // Opcional: se houver playoff para 3º, adicione aqui
            }
        } catch (Exception e) {
            Platform.runLater(() -> infoLabel.setText("Resultado registado, mas falha ao atualizar conquistas: " + e.getMessage()));
        }
    }

    private void atualizarPodioEquipe(String equipa, String posicao) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String json = String.format("{\"posicao\":\"%s\"}", posicao);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/teams/" + equipa + "/podium"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() == 200) {
                Platform.runLater(() -> infoLabel.setText("Conquistas atualizadas para a equipa " + equipa + "."));
            } else {
                Platform.runLater(() -> infoLabel.setText("Falha ao atualizar conquistas para " + equipa + ": " + resp.body()));
            }
        } catch (Exception e) {
            Platform.runLater(() -> infoLabel.setText("Erro ao atualizar conquistas para " + equipa + ": " + e.getMessage()));
        }
    }

    // Utilitário simples para extrair valor de campo JSON
    private String extrairCampo(String json, String campo) {
        String search = "\"" + campo + "\":";
        int idx = json.indexOf(search);
        if (idx == -1) return "";
        int start = json.indexOf('"', idx + search.length());
        int end = json.indexOf('"', start + 1);
        return (start != -1 && end != -1) ? json.substring(start + 1, end) : "";
    }
=======
>>>>>>> 52a6f9c (Entrega fase1: implementação, testes, docker e documentação)
}
