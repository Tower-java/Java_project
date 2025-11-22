package towergame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Lanceur principal de l'application JavaFX Tower Battle
 * Style Pokémon/Final Fantasy 7
 */
public class JavaFXLauncher extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/battle.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            primaryStage.setTitle("⚔️ Tower Battle - Style FF7/Pokémon ⚔️");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();

            // Ajouter une icône si disponible
            try {
                Image icon = new Image(getClass().getResourceAsStream("/images/icon.png"));
                primaryStage.getIcons().add(icon);
            } catch (Exception e) {
                // Pas d'icône disponible, continuer sans
            }

            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors du lancement de l'application JavaFX : " + e.getMessage());
            throw e;
        }
    }

    public static void main(String[] args) {
        System.out.println("🎮 Lancement de Tower Battle - Version graphique 🎮");
        System.out.println("Style : Pokémon/Final Fantasy 7");
        System.out.println("===============================================");
        launch(args);
    }
}