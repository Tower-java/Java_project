package towergame.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.geometry.Insets;
import javafx.application.Platform;

import java.io.InputStream;
import java.util.List;

/**
 * Contrôleur JavaFX pour la vue de combat.
 * Gère l'affichage et les interactions du combat graphique.
 */
import towergame.model.actions.AAction;
import towergame.model.entities.ABoss;
import towergame.model.entities.Player;
import towergame.model.managers.BattleManager;
import towergame.model.managers.StageManager;
import towergame.model.managers.SuccessTracker;

public class BattleController {

    @FXML
    Label playerName;

    @FXML
    Label playerHp;

    @FXML
    Label enemyName;

    @FXML
    Label enemyHp;

    @FXML
    ImageView playerSprite;

    @FXML
    ImageView enemySprite;

    @FXML
    VBox actionsBox;

    @FXML
    Label turnLabel;

    @FXML
    Label messageLabel;

    private Player player;
    private ABoss boss;
    private BattleManager battleManager;

    @FXML
    public void initialize() {
        try {
            StageManager stageManager = new StageManager();
            player = new Player("Héros");

            List<AAction> availableActions = stageManager.getUnlockedActions();
            player.setEquippedActions(availableActions.subList(0, Math.min(4, availableActions.size())));

            boss = stageManager.getNextBoss();

            if (boss == null) {
                showAlert("Pas de boss", "Aucun boss disponible !");
                return;
            }

            battleManager = new BattleManager(player, boss);

            // Initialiser l'affichage
            updateDisplay();
            setupActionButtons();

            // Charger les images si disponibles
            loadSprites();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'initialisation : " + e.getMessage());
        }
    }

    private void updateDisplay() {
        // Mettre à jour les informations du joueur
        playerName.setText(player.getName());
        playerHp.setText("HP: " + player.getHp() + "/" + player.getMaxHp());

        // Mettre à jour les informations du boss
        enemyName.setText(boss.getName());
        enemyHp.setText("HP: " + boss.getHp() + "/" + boss.getMaxHp());

        // Mettre à jour le tour si le label existe
        if (turnLabel != null) {
            turnLabel.setText("Tour " + battleManager.getTurnNumber());
        }

        // Message de statut
        if (messageLabel != null) {
            String status = boss.isInvulnerable() ? " (Invulnérable)" : "";
            status += boss.isEnraged() ? " (Enragé)" : "";
            messageLabel.setText(status);
        }
    }

    private void setupActionButtons() {
        actionsBox.getChildren().clear();

        for (AAction action : player.getEquippedActions()) {
            Button button = new Button(action.getName());
            button.setPrefWidth(150);

            button.setOnAction(evt -> {
                if (battleManager.isBattleOver()) {
                    showAlert("Combat terminé", "Le combat est déjà terminé !");
                    return;
                }

                if (!action.isReady()) {
                    showAlert("Action indisponible",
                            action.getName() + " est en recharge (" + action.getCurrentCooldown() + " tours)");
                    return;
                }

                // Vérifier les résistances/faiblesses avant l'attaque
                if (boss instanceof towergame.model.entities.FireElementalBoss) {
                    towergame.model.entities.FireElementalBoss fireBoss = (towergame.model.entities.FireElementalBoss) boss;

                    if (fireBoss.isResistant(action.getElement())) {
                        showResistanceEffect(action.getName());
                    } else if (fireBoss.isWeak(action.getElement())) {
                        showWeaknessEffect(action.getName());
                    }
                }

                // Animer l'attaque du joueur
                animatePlayerAction(action.getName());

                // Exécuter le tour après un délai
                PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
                delay.setOnFinished(e -> {
                    battleManager.executeTurn(action);

                    // Vérifier si le boss vient d'entrer en rage
                    if (boss.isEnraged()) {
                        showBossEnrageEffect();
                    }

                    updateDisplay();

                    // Vérifier fin du combat
                    if (battleManager.isBattleOver()) {
                        endBattle();
                    } else {
                        // Mettre à jour les boutons (cooldowns)
                        setupActionButtons();
                    }
                });
                delay.play();
            });

            // Désactiver si en cooldown
            if (!action.isReady()) {
                button.setDisable(true);
                button.setText(action.getName() + " (" + action.getCurrentCooldown() + ")");
            }

            actionsBox.getChildren().add(button);
        }
    }

    private void loadSprites() {
        InputStream pImg = getClass().getResourceAsStream("/images/player.png");
        if (pImg != null) {
            playerSprite.setImage(new Image(pImg));
        }

        InputStream eImg = getClass().getResourceAsStream("/images/enemy.png");
        if (eImg != null) {
            enemySprite.setImage(new Image(eImg));
        }
    }

    void endBattle() {
        String message = player.isAlive()
                ? "Victoire ! " + boss.getName() + " a été vaincu !"
                : "Défaite... " + player.getName() + " est tombé au combat.";

        showAlert("Combat terminé", message + "\nCombat terminé en " + battleManager.getTurnNumber() + " tours.");

        // Vérifier les succès
        SuccessTracker.checkAchievements(player, boss, battleManager.getTurnNumber(),
                battleManager.getActionsUsedHistory());
    }

    void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Affiche l'effet de résistance style Pokémon/FF7
     */
    void showResistanceEffect(String attackName) {
        Text effectText = new Text("🛡️ " + boss.getName() + " résiste à " + attackName + " !");
        effectText.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        effectText.setFill(Color.LIGHTBLUE);

        Text subText = new Text("L'attaque n'est pas très efficace...");
        subText.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        subText.setFill(Color.GRAY);

        showEffectAnimation(effectText, subText, Color.LIGHTBLUE);
    }

    /**
     * Affiche l'effet de faiblesse style Pokémon/FF7
     */
    void showWeaknessEffect(String attackName) {
        Text effectText = new Text("⚡ " + boss.getName() + " craint " + attackName + " !");
        effectText.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        effectText.setFill(Color.YELLOW);

        Text subText = new Text("C'est super efficace !");
        subText.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        subText.setFill(Color.ORANGE);

        showEffectAnimation(effectText, subText, Color.YELLOW);
    }

    /**
     * Affiche l'effet d'enrage du boss style Pokémon/FF7
     */
    void showBossEnrageEffect() {
        Text effectText = new Text("🔥 " + boss.getName() + " ENTRE EN RAGE ! 🔥");
        effectText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        effectText.setFill(Color.RED);

        Text subText = new Text("Ses attaques deviennent plus puissantes !");
        subText.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        subText.setFill(Color.ORANGE);

        showEffectAnimation(effectText, subText, Color.RED);

        // Effet de clignotement sur le sprite du boss
        FadeTransition blink = new FadeTransition(Duration.millis(200), enemySprite);
        blink.setFromValue(1.0);
        blink.setToValue(0.3);
        blink.setCycleCount(6);
        blink.setAutoReverse(true);
        blink.play();
    }

    /**
     * Anime une action du joueur
     */
    void animatePlayerAction(String actionName) {
        messageLabel.setText("🎯 " + player.getName() + " utilise " + actionName + " !");
        messageLabel.setTextFill(Color.LIGHTGREEN);

        // Animation de scale sur le sprite du joueur
        ScaleTransition scale = new ScaleTransition(Duration.millis(300), playerSprite);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(1.2);
        scale.setToY(1.2);
        scale.setCycleCount(2);
        scale.setAutoReverse(true);
        scale.play();
    }

    /**
     * Méthode générique pour afficher des effets d'animation
     */
    void showEffectAnimation(Text mainText, Text subText, Color borderColor) {
        // Créer une VBox pour contenir l'effet
        VBox effectBox = new VBox(10);
        effectBox.setAlignment(Pos.CENTER);
        effectBox.setPadding(new Insets(20));
        effectBox.setBackground(new Background(new BackgroundFill(
                Color.BLACK.deriveColor(0, 1, 1, 0.8),
                new CornerRadii(10),
                Insets.EMPTY)));
        effectBox.setStyle("-fx-border-color: " + toHexString(borderColor) +
                "; -fx-border-width: 3; -fx-border-radius: 10;");

        effectBox.getChildren().addAll(mainText, subText);

        // Ajouter temporairement à la scène
        if (actionsBox.getParent() instanceof VBox) {
            VBox parent = (VBox) actionsBox.getParent();
            parent.getChildren().add(0, effectBox);

            // Animation d'apparition
            effectBox.setOpacity(0);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), effectBox);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();

            // Animation de clignotement du texte principal
            FadeTransition blink = new FadeTransition(Duration.millis(400), mainText);
            blink.setFromValue(1.0);
            blink.setToValue(0.3);
            blink.setCycleCount(4);
            blink.setAutoReverse(true);

            // Supprimer après animation
            PauseTransition delay = new PauseTransition(Duration.seconds(2.5));
            delay.setOnFinished(e -> {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(300), effectBox);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(f -> parent.getChildren().remove(effectBox));
                fadeOut.play();
            });

            // Démarrer les animations en séquence
            SequentialTransition sequence = new SequentialTransition(blink, delay);
            sequence.play();
        }
    }

    /**
     * Convertit une couleur JavaFX en chaîne hexadécimale
     */
    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
}
