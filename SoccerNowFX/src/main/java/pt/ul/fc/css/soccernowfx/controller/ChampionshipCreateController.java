package pt.ul.fc.css.soccernowfx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.application.Platform;
import java.net.http.*;
import java.net.URI;

public class ChampionshipCreateController {
    @FXML private TextField nameField;
    @FXML private TextField modalityField;
<<<<<<< HEAD
    @FXML private TextField formatField;
=======
>>>>>>> 52a6f9c (Entrega fase1: implementação, testes, docker e documentação)
    @FXML private TextField teamsField;
    @FXML private TextField refereesField;
    @FXML private Label infoLabel;

    @FXML
<<<<<<< HEAD
    private void handleBack() {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/fxml/menu.fxml"));
            javafx.stage.Stage stage = (javafx.stage.Stage) infoLabel.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
        } catch (Exception e) {
            infoLabel.setText("Erro ao voltar ao menu: " + e.getMessage());
        }
    }

    @FXML
    private void handleCreate() {
        String nome = nameField.getText();
        String modalidade = modalityField.getText();
        String formato = formatField.getText();
        String equipas = teamsField.getText();
        String arbitros = refereesField.getText();
        if (nome.isBlank() || modalidade.isBlank() || formato.isBlank() || equipas.isBlank() || arbitros.isBlank()) {
            infoLabel.setText("Preencha todos os campos obrigatórios.");
            return;
        }
        // Validação: pelo menos 8 equipas
        String[] equipasArr = equipas.split(",");
        if (equipasArr.length < 8) {
            infoLabel.setText("O campeonato deve ter pelo menos 8 equipas.");
            return;
        }
        // Buscar IDs dos times participantes
        java.util.List<Long> participanteIds = new java.util.ArrayList<>();
        for (String nomeTime : equipasArr) {
            nomeTime = nomeTime.trim();
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/teams?name=" + nomeTime))
                        .GET()
                        .build();
                HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
                if (resp.statusCode() == 200 && resp.body().contains("id")) {
                    // Extrair o primeiro id do array retornado
                    String body = resp.body();
                    int idx = body.indexOf("\"id\":");
                    if (idx != -1) {
                        int start = idx + 5;
                        int end = body.indexOf(',', start);
                        if (end == -1) end = body.indexOf('}', start);
                        if (end != -1) {
                            String idStr = body.substring(start, end).replaceAll("[^0-9]", "");
                            if (!idStr.isEmpty()) participanteIds.add(Long.parseLong(idStr));
                        }
                    }
                } else {
                    infoLabel.setText("Equipe não encontrada: " + nomeTime);
                    return;
                }
            } catch (Exception ex) {
                infoLabel.setText("Erro ao buscar equipe: " + nomeTime);
                return;
            }
        }
        if (participanteIds.size() < 8) {
            infoLabel.setText("Não foi possível encontrar todos os times informados.");
            return;
        }
        // Validação: pelo menos 1 árbitro certificado
        String[] arbitrosArr = arbitros.split(",");
        boolean temCertificado = false;
        for (String email : arbitrosArr) {
            email = email.trim();
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/users/by-email/" + email))
                        .GET()
                        .build();
                HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
                // Considera certificado se campo 'certified' é true
                if (resp.statusCode() == 200 && resp.body().contains("\"role\":\"REFEREE\"") && resp.body().contains("\"certified\":true")) {
                    temCertificado = true;
                }
            } catch (Exception ex) {
                infoLabel.setText("Erro ao validar árbitro: " + email);
                return;
            }
        }
        if (!temCertificado) {
            infoLabel.setText("É necessário pelo menos um árbitro certificado.");
            return;
        }
        // Monta JSON conforme DTO do backend
        StringBuilder sb = new StringBuilder();
        sb.append("{\"nome\":\"").append(nome).append("\",\"modalidade\":\"").append(modalidade).append("\",\"formato\":\"").append(formato).append("\",\"participanteIds\":[");
        for (int i = 0; i < participanteIds.size(); i++) {
            sb.append(participanteIds.get(i));
            if (i < participanteIds.size() - 1) sb.append(",");
=======
    private void handleCreate() {
        String nome = nameField.getText();
        String modalidade = modalityField.getText();
        String equipas = teamsField.getText();
        String arbitros = refereesField.getText();
        if (nome.isBlank() || modalidade.isBlank() || equipas.isBlank() || arbitros.isBlank()) {
            infoLabel.setText("Preencha todos os campos obrigatórios.");
            return;
        }
        String[] equipasArr = equipas.split(",");
        String[] arbitrosArr = arbitros.split(",");
        StringBuilder sb = new StringBuilder();
        sb.append("{\"nome\":\"").append(nome).append("\",\"modalidade\":\"").append(modalidade).append("\",\"equipas\":[");
        for (int i = 0; i < equipasArr.length; i++) {
            sb.append("\"").append(equipasArr[i].trim()).append("\"");
            if (i < equipasArr.length - 1) sb.append(",");
>>>>>>> 52a6f9c (Entrega fase1: implementação, testes, docker e documentação)
        }
        sb.append("],\"arbitrosCertificados\":[");
        for (int i = 0; i < arbitrosArr.length; i++) {
            sb.append("\"").append(arbitrosArr[i].trim()).append("\"");
            if (i < arbitrosArr.length - 1) sb.append(",");
        }
        sb.append("]}");
        String json = sb.toString();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
<<<<<<< HEAD
                .uri(URI.create("http://localhost:8080/api/campeonatos"))
=======
                .uri(URI.create("http://localhost:8080/api/championships"))
>>>>>>> 52a6f9c (Entrega fase1: implementação, testes, docker e documentação)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenAccept(response -> Platform.runLater(() -> {
                if (response.statusCode() == 201 || response.statusCode() == 200) {
                    infoLabel.setText("Campeonato criado com sucesso!");
                } else {
                    infoLabel.setText("Erro ao criar campeonato: " + response.body());
                }
            }))
            .exceptionally(e -> { Platform.runLater(() -> infoLabel.setText("Erro: " + e.getMessage())); return null; });
    }
}
