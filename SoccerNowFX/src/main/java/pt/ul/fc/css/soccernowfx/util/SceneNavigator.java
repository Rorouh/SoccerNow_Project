package pt.ul.fc.css.soccernowfx.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Facilita cambiar entre pantallas.
 * Para usarlo desde cualquier Controller:
 *   SceneNavigator.switchScene(anyControl, "/fxml/otra.fxml");
 */
public final class SceneNavigator {

    private SceneNavigator() {}

    public static void switchScene(javafx.scene.Node nodeInActualScene, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(SceneNavigator.class.getResource(fxmlPath));
            Stage stage = (Stage) nodeInActualScene.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception ex) {
            throw new RuntimeException("Não foi possível abrir " + fxmlPath, ex);
        }
    }
}
