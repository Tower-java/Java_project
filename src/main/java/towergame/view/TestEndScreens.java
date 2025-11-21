package towergame.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import towergame.controller.VictoryController;
import towergame.controller.DefeatController;

/**
 * Application de test pour les écrans de fin de combat
 * Lance un menu pour choisir entre tester l'écran de victoire ou de défaite
 */
public class TestEndScreens extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        primaryStage.setWidth(1600);
        primaryStage.setHeight(900);
        primaryStage.setResizable(false);

        // Centrer la fenêtre
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX((screenBounds.getWidth() - 1600) / 2);
        primaryStage.setY((screenBounds.getHeight() - 900) / 2);

        showTestMenu();
    }

    private void showTestMenu() {
        javafx.scene.layout.VBox root = new javafx.scene.layout.VBox();
        root.setStyle("-fx-background-color: #1a1a2e; -fx-alignment: CENTER; -fx-spacing: 30;");

        javafx.scene.control.Label title = new javafx.scene.control.Label("Test des écrans de fin");
        title.setStyle("-fx-font-size: 40; -fx-text-fill: #00ff00; -fx-font-weight: bold;");

        javafx.scene.control.Button victoryBtn = new javafx.scene.control.Button("Tester Écran de Victoire");
        victoryBtn.setStyle(
                "-fx-font-size: 18; -fx-padding: 15; -fx-background-color: #00aa00; -fx-text-fill: white; -fx-min-width: 300;");
        victoryBtn.setOnAction(e -> showVictoryScreen());

        javafx.scene.control.Button defeatBtn = new javafx.scene.control.Button("Tester Écran de Défaite");
        defeatBtn.setStyle(
                "-fx-font-size: 18; -fx-padding: 15; -fx-background-color: #cc0000; -fx-text-fill: white; -fx-min-width: 300;");
        defeatBtn.setOnAction(e -> showDefeatScreen());

        javafx.scene.control.Button exitBtn = new javafx.scene.control.Button("Quitter");
        exitBtn.setStyle(
                "-fx-font-size: 18; -fx-padding: 15; -fx-background-color: #444444; -fx-text-fill: white; -fx-min-width: 300;");
        exitBtn.setOnAction(e -> System.exit(0));

        root.getChildren().addAll(title, victoryBtn, defeatBtn, exitBtn);

        Scene scene = new Scene(root, 1600, 900);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Test - Écrans de fin");
        primaryStage.show();
    }

    private void showVictoryScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/victory.fxml"));
            Parent root = loader.load();
            VictoryController controller = loader.getController();

            // Données de test
            controller.setGameData(5, "Élémentaire de Feu", 42, 100);

            // Callbacks pour les boutons
            controller.setOnRestart(this::showTestMenu);
            controller.setOnQuit(() -> System.exit(0));

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Test - Écran de Victoire");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDefeatScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/defeat.fxml"));
            Parent root = loader.load();
            DefeatController controller = loader.getController();

            // Données de test
            controller.setGameData(8, "Élémentaire de Feu", 5, 75);

            // Callbacks pour les boutons
            controller.setOnRestart(this::showTestMenu);
            controller.setOnQuit(() -> System.exit(0));

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Test - Écran de Défaite");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
