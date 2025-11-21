package towergame.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.application.Platform;

/**
 * Contrôleur pour l'écran de défaite
 */
public class DefeatController {

    @FXML
    private Label defeatMessage;

    @FXML
    private Label statsLabel;

    @FXML
    private Button restartButton;

    @FXML
    private Button quitButton;

    private int turnsCount = 0;
    private String bossName = "Boss";
    private Runnable onRestart;
    private Runnable onQuit;

    @FXML
    public void initialize() {
        restartButton.setOnAction(event -> restartGame());
        quitButton.setOnAction(event -> quitGame());
    }

    public void setGameData(int turns, String boss, int playerHP, int bossHP) {
        this.turnsCount = turns;
        this.bossName = boss;

        defeatMessage.setText("Vous avez échoué face au " + bossName + "...");
    }

    public void setOnRestart(Runnable onRestart) {
        this.onRestart = onRestart;
    }

    public void setOnQuit(Runnable onQuit) {
        this.onQuit = onQuit;
    }

    private void restartGame() {
        if (onRestart != null) {
            onRestart.run();
        }
    }

    private void quitGame() {
        if (onQuit != null) {
            onQuit.run();
        } else {
            Platform.exit();
        }
    }
}
