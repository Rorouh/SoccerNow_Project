package pt.ul.fc.css.soccernowfx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

public class MenuController {

    @FXML private Label infoLabel;

    @FXML
    private void handleCreateChampionship() {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/fxml/championship_create.fxml"));
            javafx.stage.Stage stage = (javafx.stage.Stage) infoLabel.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
        } catch (Exception e) {
            infoLabel.setText("Error al abrir creación de campeonato: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegisterUser() {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/fxml/register.fxml"));
            javafx.stage.Stage stage = (javafx.stage.Stage) infoLabel.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
        } catch (Exception e) {
            infoLabel.setText("Error al abrir el registro: " + e.getMessage());
        }
    }

    @FXML
    private void handleCreateTeam() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/team_create.fxml"));
            Stage stage = (Stage) infoLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            infoLabel.setText("Error al abrir creación de equipo: " + e.getMessage());
        }
    }

    @FXML
    private void handleManageUsers() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/user_manage.fxml"));
            Stage stage = (Stage) infoLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            infoLabel.setText("Error al abrir gestión de usuarios: " + e.getMessage());
        }
    }

    @FXML
    private void handleManageTeams() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/team_manage.fxml"));
            Stage stage = (Stage) infoLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            infoLabel.setText("Error al abrir gestión de equipos: " + e.getMessage());
        }
    }

    @FXML
    private void handleManageChampionships() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/championship_manage.fxml"));
            Stage stage = (Stage) infoLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            infoLabel.setText("Error al abrir gestión de campeonatos: " + e.getMessage());
        }
    }

    @FXML
    private void handleManageGames() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/game_create.fxml"));
            Stage stage = (Stage) infoLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            infoLabel.setText("Error al abrir gestión de partidos: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegisterResult() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/game_result.fxml"));
            Stage stage = (Stage) infoLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            infoLabel.setText("Error al abrir registro de resultado: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelGame() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/game_cancel.fxml"));
            Stage stage = (Stage) infoLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            infoLabel.setText("Error al abrir cancelación de partido: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Stage stage = (Stage) infoLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            infoLabel.setText("Error al cerrar sesión: " + e.getMessage());
        }
    }
}
