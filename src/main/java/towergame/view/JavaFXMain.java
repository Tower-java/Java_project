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
import towergame.util.AudioManager;

public class JavaFXMain extends Application {

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

        // Toujours afficher le menu principal
        showMainMenu();
    }

    private void showMainMenu() {
        // Lancer la musique du menu
        AudioManager.getInstance().playTrack(AudioManager.MENU_MUSIC);

        javafx.scene.layout.VBox root = new javafx.scene.layout.VBox();
        root.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #0a0e27, #1a1a3e); -fx-alignment: CENTER; -fx-spacing: 40;");

        javafx.scene.control.Label title = new javafx.scene.control.Label("⚔ TOWER BATTLE ⚔");
        title.setStyle("-fx-font-size: 50; -fx-text-fill: #ff6b00; -fx-font-weight: bold;");

        javafx.scene.control.Button playBtn = new javafx.scene.control.Button("▶ JOUER");
        playBtn.setStyle(
                "-fx-font-size: 24; -fx-padding: 20; -fx-background-color: #0066cc; -fx-text-fill: white; -fx-min-width: 400; -fx-cursor: hand;");
        playBtn.setOnAction(e -> showWelcomeScreen());

        javafx.scene.control.Button testBtn = new javafx.scene.control.Button("🧪 TESTER LES ÉCRANS DE FIN");
        testBtn.setStyle(
                "-fx-font-size: 24; -fx-padding: 20; -fx-background-color: #ff6b00; -fx-text-fill: white; -fx-min-width: 400; -fx-cursor: hand;");
        testBtn.setOnAction(e -> showTestMenu());

        javafx.scene.control.Button exitBtn = new javafx.scene.control.Button("❌ QUITTER");
        exitBtn.setStyle(
                "-fx-font-size: 24; -fx-padding: 20; -fx-background-color: #cc0000; -fx-text-fill: white; -fx-min-width: 400; -fx-cursor: hand;");
        exitBtn.setOnAction(e -> System.exit(0));

        root.getChildren().addAll(title, playBtn, testBtn, exitBtn);

        Scene scene = new Scene(root, 1600, 900);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Tower Battle - Menu Principal");
        primaryStage.show();
    }

    private void showWelcomeScreen() {
        WelcomeScreenFX welcomeScreen = new WelcomeScreenFX(primaryStage, this::startBattle);
        welcomeScreen.show();
    }

    private void startBattle() {
        try {
            System.out.println("🎮 Chargement de la scène de combat...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/battle.fxml"));

            if (loader.getLocation() == null) {
                System.err.println("❌ Fichier FXML introuvable: /fxml/battle.fxml");
                return;
            }

            System.out.println("✅ FXML trouvé: " + loader.getLocation());

            Parent root = loader.load();
            System.out.println("✅ FXML chargé avec succès");

            Scene scene = new Scene(root);

            try {
                scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
                System.out.println("✅ CSS chargée avec succès");
            } catch (Exception e) {
                System.out.println("⚠️ Avertissement: CSS non trouvée, continuant sans style");
            }

            // Passer le stage au contrôleur
            towergame.controller.BattleController battleController = loader.getController();
            if (battleController == null) {
                System.err.println("❌ Le contrôleur BattleController est null");
                return;
            }

            System.out.println("✅ Contrôleur obtenu avec succès");
            battleController.setPrimaryStage(primaryStage);
            System.out.println("✅ PrimaryStage défini");

            primaryStage.setTitle("Turn-based Battle");
            primaryStage.setScene(scene);
            primaryStage.show();
            System.out.println("✅ Scène de combat affichée avec succès");

        } catch (javafx.fxml.LoadException loadException) {
            System.err.println("❌ Erreur de chargement FXML:");
            System.err.println("Message: " + loadException.getMessage());
            System.err.println("Cause: " + loadException.getCause());
            loadException.printStackTrace();
        } catch (java.io.IOException ioException) {
            System.err.println("❌ Erreur IO lors du chargement du combat:");
            ioException.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Erreur critique lors du lancement du combat:");
            System.err.println("Type: " + e.getClass().getName());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
        }
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

        javafx.scene.control.Button backBtn = new javafx.scene.control.Button("◀ Retour au menu");
        backBtn.setStyle(
                "-fx-font-size: 18; -fx-padding: 15; -fx-background-color: #0066cc; -fx-text-fill: white; -fx-min-width: 300;");
        backBtn.setOnAction(e -> showMainMenu());

        javafx.scene.control.Button exitBtn = new javafx.scene.control.Button("Quitter");
        exitBtn.setStyle(
                "-fx-font-size: 18; -fx-padding: 15; -fx-background-color: #444444; -fx-text-fill: white; -fx-min-width: 300;");
        exitBtn.setOnAction(e -> System.exit(0));

        root.getChildren().addAll(title, victoryBtn, defeatBtn, backBtn, exitBtn);

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
