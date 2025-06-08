package pt.ul.fc.css.soccernowfx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

public class MenuController {
<<<<<<< HEAD

    @FXML
    private void handleCreateChampionship() {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/fxml/championship_create.fxml"));
            javafx.stage.Stage stage = (javafx.stage.Stage) infoLabel.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
        } catch (Exception e) {
            infoLabel.setText("Erro ao abrir criação de campeonato: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegisterUser() {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/fxml/register.fxml"));
            javafx.stage.Stage stage = (javafx.stage.Stage) infoLabel.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
        } catch (Exception e) {
            infoLabel.setText("Erro ao abrir cadastro: " + e.getMessage());
        }
    }

    @FXML
    private void handleCreateTeam() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/team_create.fxml"));
            Stage stage = (Stage) infoLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            infoLabel.setText("Erro ao abrir criação de equipa: " + e.getMessage());
        }
    }
=======
>>>>>>> 52a6f9c (Entrega fase1: implementação, testes, docker e documentação)
    @FXML private Label infoLabel;

    @FXML
    private void handleManageUsers() {
<<<<<<< HEAD
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/user_manage.fxml"));
            Stage stage = (Stage) infoLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            infoLabel.setText("Erro ao abrir gestão de utilizadores: " + e.getMessage());
        }
=======
        infoLabel.setText("Funcionalidade de gestão de utilizadores.");
        // TODO: Trocar para tela de gestão de utilizadores
>>>>>>> 52a6f9c (Entrega fase1: implementação, testes, docker e documentação)
    }

    @FXML
    private void handleManageTeams() {
<<<<<<< HEAD
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/team_manage.fxml"));
            Stage stage = (Stage) infoLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            infoLabel.setText("Erro ao abrir gestão de equipas: " + e.getMessage());
        }
=======
        infoLabel.setText("Funcionalidade de gestão de equipas.");
        // TODO: Trocar para tela de gestão de equipas
>>>>>>> 52a6f9c (Entrega fase1: implementação, testes, docker e documentação)
    }

    @FXML
    private void handleManageChampionships() {
<<<<<<< HEAD
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/championship_manage.fxml"));
            Stage stage = (Stage) infoLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            infoLabel.setText("Erro ao abrir gestão de campeonatos: " + e.getMessage());
        }
=======
        infoLabel.setText("Funcionalidade de gestão de campeonatos.");
        // TODO: Trocar para tela de gestão de campeonatos
>>>>>>> 52a6f9c (Entrega fase1: implementação, testes, docker e documentação)
    }

    @FXML
    private void handleManageGames() {
<<<<<<< HEAD
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/game_create.fxml"));
            Stage stage = (Stage) infoLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            infoLabel.setText("Erro ao abrir gestão de jogos: " + e.getMessage());
        }
=======
        infoLabel.setText("Funcionalidade de gestão de jogos.");
        // TODO: Trocar para tela de gestão de jogos
>>>>>>> 52a6f9c (Entrega fase1: implementação, testes, docker e documentação)
    }

    @FXML
    private void handleRegisterResult() {
<<<<<<< HEAD
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/game_result.fxml"));
            Stage stage = (Stage) infoLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            infoLabel.setText("Erro ao abrir registo de resultado: " + e.getMessage());
        }
=======
        infoLabel.setText("Funcionalidade de registo de resultado de jogo.");
        // TODO: Trocar para tela de registo de resultado
>>>>>>> 52a6f9c (Entrega fase1: implementação, testes, docker e documentação)
    }

    @FXML
    private void handleCancelGame() {
<<<<<<< HEAD
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/game_cancel.fxml"));
            Stage stage = (Stage) infoLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            infoLabel.setText("Erro ao abrir cancelamento de jogo: " + e.getMessage());
        }
=======
        infoLabel.setText("Funcionalidade de cancelamento de jogo de campeonato.");
        // TODO: Trocar para tela de cancelamento de jogo
>>>>>>> 52a6f9c (Entrega fase1: implementação, testes, docker e documentação)
    }

    @FXML
    private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Stage stage = (Stage) infoLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            infoLabel.setText("Erro ao fazer logout: " + e.getMessage());
        }
    }
}
