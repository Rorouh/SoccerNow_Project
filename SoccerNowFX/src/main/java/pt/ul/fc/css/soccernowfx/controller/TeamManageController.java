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

public class TeamManageController {
<<<<<<< HEAD
    @FXML
    private void handleBack() {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/fxml/menu.fxml"));
            javafx.stage.Stage stage = (javafx.stage.Stage) infoLabel.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
        } catch (Exception e) {
            infoLabel.setText("Erro ao voltar ao menu: " + e.getMessage());
        }
    }
=======
>>>>>>> 52a6f9c (Entrega fase1: implementação, testes, docker e documentação)
    @FXML private TextField nameField;
    @FXML private TextField playersField;
    @FXML private Label infoLabel;

    @FXML
    private void handleSearch() {
        String nome = nameField.getText();
        if (nome.isBlank()) {
            infoLabel.setText("Informe o nome da equipa para buscar.");
            return;
        }
        HttpClient client = HttpClient.newHttpClient();
<<<<<<< HEAD
        // Buscar equipa pelo nome usando o endpoint de filtro
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/teams?name=" + nome))
=======
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/teams/" + nome))
>>>>>>> 52a6f9c (Entrega fase1: implementação, testes, docker e documentação)
                .GET()
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenAccept(response -> Platform.runLater(() -> {
<<<<<<< HEAD
                if (response.statusCode() == 200 && response.body().contains("\"id\":")) {
=======
                if (response.statusCode() == 200) {
                    // Espera JSON: {"nome":"...","jogadores":["email1",...]}
>>>>>>> 52a6f9c (Entrega fase1: implementação, testes, docker e documentação)
                    String body = response.body();
                    String jogadores = extractPlayersJson(body);
                    playersField.setText(jogadores);
                    infoLabel.setText("Equipa encontrada.");
                } else {
                    infoLabel.setText("Equipa não encontrada.");
                }
            }))
            .exceptionally(e -> { Platform.runLater(() -> infoLabel.setText("Erro: " + e.getMessage())); return null; });
    }

    @FXML
    private void handleUpdate() {
        String nome = nameField.getText();
        String jogadores = playersField.getText();
        if (nome.isBlank()) {
            infoLabel.setText("Preencha o nome da equipa.");
            return;
        }
        String[] jogadoresArr = jogadores.isBlank() ? new String[0] : jogadores.split(",");
        StringBuilder sb = new StringBuilder();
<<<<<<< HEAD
        sb.append("{\"name\":\"").append(nome).append("\",\"playerEmails\":[");
=======
        sb.append("{\"nome\":\"").append(nome).append("\",\"jogadores\":[");
>>>>>>> 52a6f9c (Entrega fase1: implementação, testes, docker e documentação)
        for (int i = 0; i < jogadoresArr.length; i++) {
            sb.append("\"").append(jogadoresArr[i].trim()).append("\"");
            if (i < jogadoresArr.length - 1) sb.append(",");
        }
        sb.append("]}");
        String json = sb.toString();
        HttpClient client = HttpClient.newHttpClient();
<<<<<<< HEAD
        // Buscar o ID da equipa pelo nome
        HttpRequest getIdReq = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/api/teams?name=" + nome))
            .GET()
            .build();
        client.sendAsync(getIdReq, HttpResponse.BodyHandlers.ofString())
            .thenAccept(getIdResp -> {
                if (getIdResp.statusCode() == 200 && getIdResp.body().contains("\"id\":")) {
                    Long id = extractTeamIdJson(getIdResp.body());
                    if (id == null) {
                        Platform.runLater(() -> infoLabel.setText("Equipa não encontrada para atualizar."));
                        return;
                    }
                    HttpRequest putReq = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/teams/" + id))
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(json))
                        .build();
                    client.sendAsync(putReq, HttpResponse.BodyHandlers.ofString())
                        .thenAccept(response -> Platform.runLater(() -> {
                            if (response.statusCode() == 200) {
                                infoLabel.setText("Equipa atualizada com sucesso!");
                            } else {
                                infoLabel.setText("Erro ao atualizar: " + response.body());
                            }
                        }))
                        .exceptionally(e -> { Platform.runLater(() -> infoLabel.setText("Erro: " + e.getMessage())); return null; });
                } else {
                    Platform.runLater(() -> infoLabel.setText("Equipa não encontrada para atualizar."));
                }
            })
            .exceptionally(e -> { Platform.runLater(() -> infoLabel.setText("Erro ao buscar equipa: " + e.getMessage())); return null; });
    }

    // Extrai o primeiro id encontrado no JSON da resposta de /api/teams?name=...
    private Long extractTeamIdJson(String json) {
        int idx = json.indexOf("\"id\":");
        if (idx == -1) return null;
        int start = idx + 6;
        int end = json.indexOf(',', start);
        if (end == -1) end = json.indexOf('}', start);
        if (end == -1) return null;
        String idStr = json.substring(start, end).replaceAll("[^0-9]", "");
        try {
            return Long.parseLong(idStr);
        } catch (Exception e) {
            return null;
        }
=======
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/teams/" + nome))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenAccept(response -> Platform.runLater(() -> {
                if (response.statusCode() == 200) {
                    infoLabel.setText("Equipa atualizada com sucesso!");
                } else {
                    infoLabel.setText("Erro ao atualizar: " + response.body());
                }
            }))
            .exceptionally(e -> { Platform.runLater(() -> infoLabel.setText("Erro: " + e.getMessage())); return null; });
>>>>>>> 52a6f9c (Entrega fase1: implementação, testes, docker e documentação)
    }

    @FXML
    private void handleRemove() {
        String nome = nameField.getText();
        if (nome.isBlank()) {
            infoLabel.setText("Informe o nome da equipa para remover.");
            return;
        }
        HttpClient client = HttpClient.newHttpClient();
<<<<<<< HEAD
        // Buscar o ID da equipa pelo nome
        HttpRequest getIdReq = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/api/teams?name=" + nome))
            .GET()
            .build();
        client.sendAsync(getIdReq, HttpResponse.BodyHandlers.ofString())
            .thenAccept(getIdResp -> {
                if (getIdResp.statusCode() == 200 && getIdResp.body().contains("\"id\":")) {
                    Long id = extractTeamIdJson(getIdResp.body());
                    if (id == null) {
                        Platform.runLater(() -> infoLabel.setText("Equipa não encontrada para remover."));
                        return;
                    }
                    HttpRequest delReq = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/teams/" + id))
                        .DELETE()
                        .build();
                    client.sendAsync(delReq, HttpResponse.BodyHandlers.ofString())
                        .thenAccept(response -> Platform.runLater(() -> {
                            if (response.statusCode() == 200) {
                                infoLabel.setText("Equipa removida com sucesso!");
                                playersField.setText("");
                            } else {
                                infoLabel.setText("Erro ao remover: " + response.body());
                            }
                        }))
                        .exceptionally(e -> { Platform.runLater(() -> infoLabel.setText("Erro: " + e.getMessage())); return null; });
                } else {
                    Platform.runLater(() -> infoLabel.setText("Equipa não encontrada para remover."));
                }
            })
            .exceptionally(e -> { Platform.runLater(() -> infoLabel.setText("Erro ao buscar equipa: " + e.getMessage())); return null; });
    }



    // Extrai lista de emails do campo playerEmails do JSON do endpoint /api/teams?name=...
    private String extractPlayersJson(String json) {
        // Busca o campo playerEmails no primeiro objeto do array
        String search = "\"playerEmails\":[";
=======
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/teams/" + nome))
                .DELETE()
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenAccept(response -> Platform.runLater(() -> {
                if (response.statusCode() == 200) {
                    infoLabel.setText("Equipa removida com sucesso!");
                    playersField.setText("");
                } else {
                    infoLabel.setText("Erro ao remover: " + response.body());
                }
            }))
            .exceptionally(e -> { Platform.runLater(() -> infoLabel.setText("Erro: " + e.getMessage())); return null; });
    }

    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Stage stage = (Stage) infoLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            infoLabel.setText("Erro ao voltar: " + e.getMessage());
        }
    }

    // Utilitário simples para extrair lista de jogadores do JSON (sem dependência externa)
    private String extractPlayersJson(String json) {
        String search = "\"jogadores\":[";
>>>>>>> 52a6f9c (Entrega fase1: implementação, testes, docker e documentação)
        int idx = json.indexOf(search);
        if (idx == -1) return "";
        int start = idx + search.length();
        int end = json.indexOf(']', start);
        if (end == -1) return "";
        String arr = json.substring(start, end);
        return arr.replaceAll("\"", "").replaceAll(",", ", ");
    }
}
