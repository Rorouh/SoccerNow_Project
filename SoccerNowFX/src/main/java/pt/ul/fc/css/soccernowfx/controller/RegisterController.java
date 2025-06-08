package pt.ul.fc.css.soccernowfx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegisterController {
<<<<<<< HEAD
    @FXML
    public void initialize() {
        typeChoice.getItems().addAll("JOGADOR", "ARBITRO");
        // Popular opções iniciais
        typeChoice.setOnAction(e -> updateExtraOptions());
        updateExtraOptions();
    }

    private void updateExtraOptions() {
        extraField.getItems().clear();
        String tipo = typeChoice.getValue();
        if ("JOGADOR".equalsIgnoreCase(tipo)) {
            // Só os valores aceitos pelo Enum do backend
            extraField.getItems().addAll(
                "PORTERO", "DEFENSA", "CENTROCAMPISTA", "DELANTERO"
            );
        } else if ("ARBITRO".equalsIgnoreCase(tipo)) {
            // Apenas opções válidas para certified=true
            extraField.getItems().addAll(
                "NACIONAL", "INTERNACIONAL"
            );
            // Se quiser permitir árbitros não certificados, adicione opção "REGIONAL", "ESTADUAL" como false
            extraField.getItems().addAll("REGIONAL", "ESTADUAL");
        }
        if (!extraField.getItems().isEmpty()) {
            extraField.setValue(extraField.getItems().get(0));
        }
    }
=======
>>>>>>> 52a6f9c (Entrega fase1: implementação, testes, docker e documentação)
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ChoiceBox<String> typeChoice;
<<<<<<< HEAD
    @FXML private ChoiceBox<String> extraField; // Agora é ChoiceBox

    @FXML private Label errorLabel;
    @FXML private Button backButton;

    @FXML
    private void handleRegister() {
        errorLabel.setText("");
        errorLabel.setStyle("");
=======
    @FXML private TextField extraField;
    @FXML private Label errorLabel;

    @FXML
    private void handleRegister() {
>>>>>>> 52a6f9c (Entrega fase1: implementação, testes, docker e documentação)
        String nome = nameField.getText();
        String email = emailField.getText();
        String senha = passwordField.getText();
        String tipo = typeChoice.getValue();
<<<<<<< HEAD
        String extra = extraField.getValue();
        // Validação de campos obrigatórios
=======
        String extra = extraField.getText();
>>>>>>> 52a6f9c (Entrega fase1: implementação, testes, docker e documentação)
        if (nome.isBlank() || email.isBlank() || senha.isBlank() || tipo == null) {
            errorLabel.setText("Preencha todos os campos obrigatórios.");
            return;
        }
<<<<<<< HEAD
        // Validação de formato de email
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            errorLabel.setText("Email inválido.");
            return;
        }
        // Não permitir jogador e árbitro ao mesmo tempo (tipo ChoiceBox só permite um, então não há conflito, mas validação extra)
        if (!tipo.equals("JOGADOR") && !tipo.equals("ARBITRO")) {
            errorLabel.setText("Tipo de utilizador inválido.");
            return;
        }
        // Montar JSON
        String roleBackend;
        if (tipo.equals("JOGADOR")) {
            roleBackend = "PLAYER";
        } else if (tipo.equals("ARBITRO")) {
            roleBackend = "REFEREE";
        } else {
            roleBackend = tipo;
        }
        String json;
        if (tipo.equals("JOGADOR")) {
            json = String.format("{\"name\":\"%s\",\"email\":\"%s\",\"password\":\"%s\",\"role\":\"%s\",\"preferredPosition\":\"%s\"}",
                nome, email, senha, roleBackend, extra);
        } else if (tipo.equals("ARBITRO")) {
            // Mapear opções para booleano: NACIONAL/INTERNACIONAL = true (certified), REGIONAL/ESTADUAL = false
            boolean certified = "NACIONAL".equalsIgnoreCase(extra) || "INTERNACIONAL".equalsIgnoreCase(extra);
            json = String.format("{\"name\":\"%s\",\"email\":\"%s\",\"password\":\"%s\",\"role\":\"%s\",\"certified\":%s}",
                nome, email, senha, roleBackend, certified);
        } else {
            json = String.format("{\"name\":\"%s\",\"email\":\"%s\",\"password\":\"%s\",\"role\":\"%s\"}",
                nome, email, senha, roleBackend);
        }
=======
        // Montar JSON
        String json = String.format("{\"nome\":\"%s\",\"email\":\"%s\",\"senha\":\"%s\",\"tipo\":\"%s\",\"extra\":\"%s\"}",
                nome, email, senha, tipo, extra);
>>>>>>> 52a6f9c (Entrega fase1: implementação, testes, docker e documentação)
        try {
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create("http://localhost:8080/api/users"))
                    .header("Content-Type", "application/json")
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(json))
                    .build();
            client.sendAsync(request, java.net.http.HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> javafx.application.Platform.runLater(() -> {
                    if (response.statusCode() == 201 || response.statusCode() == 200) {
<<<<<<< HEAD
                        errorLabel.setStyle("-fx-text-fill: green;");
                        errorLabel.setText("Utilizador registado com sucesso! Voltando ao menu...");
                        // Após 1s, volta ao menu principal
                        new Thread(() -> {
                            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
                            javafx.application.Platform.runLater(() -> handleBack());
                        }).start();
                    } else {
                        errorLabel.setStyle("-fx-text-fill: red;");
                        errorLabel.setText("Erro ao registar utilizador (status: " + response.statusCode() + "): " + response.body());
                        System.out.println("[DEBUG] Erro ao registar utilizador (status: " + response.statusCode() + "): " + response.body());
                    }
                }))
                .exceptionally(e -> { javafx.application.Platform.runLater(() -> { errorLabel.setStyle("-fx-text-fill: red;"); errorLabel.setText("Erro: " + e.getMessage()); }); return null; });
        } catch (Exception ex) {
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setText("Erro ao enviar requisição: " + ex.getMessage());
=======
                        errorLabel.setText("Cadastro realizado com sucesso!");
                        // Opcional: voltar para login automaticamente
                        // handleBack();
                    } else {
                        errorLabel.setText("Erro no cadastro: " + response.body());
                    }
                }))
                .exceptionally(e -> {
                    javafx.application.Platform.runLater(() -> errorLabel.setText("Falha de conexão: " + e.getMessage()));
                    return null;
                });
        } catch (Exception e) {
            errorLabel.setText("Erro: " + e.getMessage());
>>>>>>> 52a6f9c (Entrega fase1: implementação, testes, docker e documentação)
        }
    }

    @FXML
    private void handleBack() {
        try {
<<<<<<< HEAD
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/fxml/menu.fxml"));
            javafx.stage.Stage stage = (javafx.stage.Stage) backButton.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
        } catch (Exception e) {
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setText("Erro ao voltar ao menu: " + e.getMessage());
=======
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            javafx.stage.Stage stage = (javafx.stage.Stage) nameField.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
        } catch (Exception e) {
            errorLabel.setText("Erro ao voltar: " + e.getMessage());
>>>>>>> 52a6f9c (Entrega fase1: implementação, testes, docker e documentação)
        }
    }
}
