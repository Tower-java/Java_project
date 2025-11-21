package towergame.view;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * JavaFX Welcome Screen with "Press any key to start" functionality
 */
public class WelcomeScreenFX {

    private Stage stage;
    private Runnable onStartCallback;
    private Label pressKeyLabel;
    private FadeTransition fadeTransition;

    public WelcomeScreenFX(Stage stage, Runnable onStartCallback) {
        this.stage = stage;
        this.onStartCallback = onStartCallback;
    }

    public Scene createWelcomeScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background: linear-gradient(to bottom, #1a1a2e, #16213e, #0f3460);");

        // Main content VBox
        VBox content = new VBox(30);
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-padding: 50;");

        // Title
        Label titleLabel = new Label("TOWER BATTLE");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 56));
        titleLabel.setTextFill(Color.web("#ffd700"));
        titleLabel.setStyle("-fx-effect: dropshadow(gaussian, rgba(255,215,0,0.8), 10, 0, 0, 2);");

        // Subtitle
        Label subtitleLabel = new Label("Epic Turn-Based RPG");
        subtitleLabel.setFont(Font.font("Arial", 28));
        subtitleLabel.setStyle("-fx-font-style: italic;");
        subtitleLabel.setTextFill(Color.web("#4ecdc4"));

        // Description
        Label descLabel = new Label("Affrontez des boss légendaires\ndans des combats au tour par tour");
        descLabel.setFont(Font.font("Arial", 18));
        descLabel.setTextFill(Color.web("#c8c8c8"));
        descLabel.setAlignment(Pos.CENTER);
        descLabel.setStyle("-fx-text-alignment: center;");

        // Spacer
        VBox spacer = new VBox();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Press key label with blinking animation
        pressKeyLabel = new Label("APPUYEZ SUR N'IMPORTE QUELLE TOUCHE POUR COMMENCER");
        pressKeyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        pressKeyLabel.setTextFill(Color.web("#ff6b6b"));
        setupBlinkingAnimation();

        // Add components
        content.getChildren().addAll(
                titleLabel,
                subtitleLabel,
                descLabel,
                spacer,
                pressKeyLabel);

        root.setCenter(content);

        // Create scene
        Scene scene = new Scene(root, 1600, 900);

        // Key press handler
        scene.setOnKeyPressed(this::handleKeyPress);

        return scene;
    }

    private void setupBlinkingAnimation() {
        fadeTransition = new FadeTransition(Duration.millis(600), pressKeyLabel);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.3);
        fadeTransition.setCycleCount(FadeTransition.INDEFINITE);
        fadeTransition.setAutoReverse(true);
        fadeTransition.play();
    }

    private void handleKeyPress(KeyEvent e) {
        if (e.getCode() != KeyCode.UNDEFINED) {
            startGame();
        }
    }

    private void startGame() {
        // Stop blinking animation
        if (fadeTransition != null) {
            fadeTransition.stop();
        }

        // Execute callback to start the game
        if (onStartCallback != null) {
            try {
                Platform.runLater(onStartCallback);
            } catch (Exception e) {
                System.err.println("❌ Erreur lors du lancement du jeu: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void show() {
        stage.setScene(createWelcomeScene());
        stage.setTitle("Tower Battle - Welcome");
        stage.show();

        // Forcer le focus avec un délai pour s'assurer que la scène est bien rendue
        Platform.runLater(() -> {
            stage.getScene().getRoot().requestFocus();
        });
    }
}
