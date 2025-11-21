package towergame.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane;
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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;

/**
 * Contrôleur JavaFX pour la vue de combat.
 * Gère l'affichage et les interactions du combat graphique.
 */
import towergame.model.actions.AAction;
import towergame.model.actions.BossAttackAction;
import towergame.model.actions.BossHealAction;
import towergame.model.actions.BossDefendAction;
import towergame.model.entities.ABoss;
import towergame.model.entities.Player;
import towergame.model.managers.BattleManager;
import towergame.model.managers.StageManager;
import towergame.model.managers.SuccessTracker;

public class BattleController {

    @FXML
    private ImageView playerSprite;

    @FXML
    private ImageView enemySprite;

    @FXML
    private FlowPane actionsBox;

    @FXML
    private Label turnLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private Label narrativeLabel;

    @FXML
    private Label enemyStatusLabel;

    @FXML
    private ImageView fireAttackImageView;

    @FXML
    private Label playerHpText;

    @FXML
    private Label bossHpText;

    private Player player;
    private ABoss boss;
    private BattleManager battleManager;
    private StringBuilder narrativeHistory = new StringBuilder();
    private Stage primaryStage;

    @FXML
    public void initialize() {
        try {
            StageManager stageManager = new StageManager();
            player = new Player("Héros");

            // Charger TOUS les 10 sorts disponibles
            List<AAction> availableActions = stageManager.getUnlockedActions();
            player.setEquippedActions(new ArrayList<>(availableActions));

            boss = stageManager.getNextBoss();

            if (boss == null) {
                showAlert("Pas de boss", "Aucun boss disponible !");
                return;
            }

            battleManager = new BattleManager(player, boss);

            // Initialiser l'affichage
            updateDisplay();
            setupActionButtons();

            // Initialiser le journal de narration avec séparation de tour
            addNarrativeMessage("--- TOUR DU JOUEUR ---");
            addNarrativeMessage("Le combat commence contre " + boss.getName() + " !");

            // Charger les images si disponibles
            loadSprites();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'initialisation : " + e.getMessage());
        }
    }

    private void updateDisplay() {
        // Mettre à jour le texte de HP du joueur
        playerHpText.setText(player.getHp() + "/" + player.getMaxHp());

        // Mettre à jour le texte de HP du boss
        bossHpText.setText(boss.getHp() + "/" + boss.getMaxHp());

        // Mettre à jour les statuts du boss
        StringBuilder statusText = new StringBuilder();
        if (boss.isInvulnerable()) {
            statusText.append("Invulnerable");
        }
        if (boss.isEnraged()) {
            if (statusText.length() > 0) {
                statusText.append(" | ");
            }
            statusText.append("Enrage");
        }
        enemyStatusLabel.setText(statusText.toString());

        // Mettre à jour le tour si le label existe
        if (turnLabel != null) {
            turnLabel.setText("Tour " + battleManager.getTurnNumber());
        }
    }

    private void setupActionButtons() {
        actionsBox.getChildren().clear();

        for (AAction action : player.getEquippedActions()) {
            Button button = new Button(action.getName());
            button.setPrefWidth(150);

            button.setOnAction(evt -> {
                // Désactiver IMMÉDIATEMENT tous les boutons pour éviter les double-clicks
                for (javafx.scene.Node node : actionsBox.getChildren()) {
                    if (node instanceof Button) {
                        ((Button) node).setDisable(true);
                    }
                }

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
                        addNarrativeMessage(boss.getName() + " resiste a " + action.getName()
                                + " ! L'attaque n'est pas tres efficace...");
                    } else if (fireBoss.isWeak(action.getElement())) {
                        addNarrativeMessage(
                                boss.getName() + " craint " + action.getName() + " ! C'est super efficace !");
                    }
                }

                // Animer l'attaque du joueur
                animatePlayerAction(action.getName());

                // Exécuter le tour après l'animation du joueur + message
                // Attendre juste assez pour voir l'anim et lire le message
                PauseTransition playerPause = new PauseTransition(Duration.seconds(2.0));
                playerPause.setOnFinished(e -> {
                    // Exécuter le tour du boss (génère les messages)
                    battleManager.executeTurn(action);

                    updateDisplay();

                    // Afficher le message de l'action du boss
                    // Afficher le message du tour du boss
                    addNarrativeMessage("--- TOUR DU BOSS ---");

                    AAction bossAction = battleManager.getLastBossAction();
                    if (bossAction != null) {
                        String bossMessage = generateBossActionMessage(bossAction);
                        addNarrativeMessage(bossMessage);
                    }

                    // Afficher l'attaque du feu du boss avec animation
                    showFireAttackEffect();

                    // Attendre la fin de l'animation du feu (~2s), puis afficher l'enrage
                    PauseTransition fireWaitPause = new PauseTransition(Duration.seconds(2.0));
                    fireWaitPause.setOnFinished(fireEnd -> {
                        // Vérifier si le boss vient d'entrer en rage et afficher immédiatement
                        if (boss.isEnraged()) {
                            addNarrativeMessage(boss.getName()
                                    + " ENTRE EN RAGE ! Ses attaques deviennent plus puissantes !");
                            // Changer le sprite du boss avec transition
                            changeBossSprite("/sprites/fire_boss_enrage.png");
                        }

                        // Attendre que le joueur ait le temps de lire avant de réactiver les boutons
                        PauseTransition bossPauseTrans = new PauseTransition(Duration.seconds(2.0));
                        bossPauseTrans.setOnFinished(f -> {
                            // Afficher le séparateur du tour suivant
                            addNarrativeMessage("--- TOUR DU JOUEUR ---");

                            // Vérifier fin du combat
                            if (battleManager.isBattleOver()) {
                                endBattle();
                            } else {
                                // Réactiver les boutons pour le prochain tour
                                setupActionButtons();
                            }
                        });
                        bossPauseTrans.play();
                    });
                    fireWaitPause.play();
                });
                playerPause.play();
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

        InputStream eImg = getClass().getResourceAsStream("/sprites/fire_boss.png");
        if (eImg != null) {
            enemySprite.setImage(new Image(eImg));
        }

        InputStream fImg = getClass().getResourceAsStream("/sprites/fire_attack.png");
        if (fImg != null) {
            fireAttackImageView.setImage(new Image(fImg));
        }
    }

    /**
     * Change le sprite du boss avec une transition de fade
     */
    private void changeBossSprite(String spritePath) {
        InputStream imgStream = getClass().getResourceAsStream(spritePath);
        if (imgStream != null) {
            Image newImage = new Image(imgStream);

            // Transition de fade out
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), enemySprite);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(e -> {
                // Changer l'image
                enemySprite.setImage(newImage);

                // Transition de fade in
                FadeTransition fadeIn = new FadeTransition(Duration.millis(300), enemySprite);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });

            fadeOut.play();
        }
    }

    /**
     * Affiche l'effet d'attaque du feu avec animation
     */
    private void showFireAttackEffect() {
        // Fade in
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), fireAttackImageView);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        // Scale animation (pulsation)
        ScaleTransition scale = new ScaleTransition(Duration.millis(400), fireAttackImageView);
        scale.setFromX(0.8);
        scale.setFromY(0.8);
        scale.setToX(1.2);
        scale.setToY(1.2);
        scale.setCycleCount(2);
        scale.setAutoReverse(true);

        // Rotation animation
        RotateTransition rotate = new RotateTransition(Duration.millis(400), fireAttackImageView);
        rotate.setFromAngle(-15);
        rotate.setToAngle(15);
        rotate.setCycleCount(2);
        rotate.setAutoReverse(true);

        // Parallel transition pour les animations simultanees
        ParallelTransition parallel = new ParallelTransition(scale, rotate);

        // Sequential transition : fade in, puis animations paralleles
        SequentialTransition sequence = new SequentialTransition(fadeIn, parallel);

        // Fade out après les animations
        sequence.setOnFinished(e -> {
            PauseTransition delay = new PauseTransition(Duration.millis(800));
            delay.setOnFinished(f -> {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(300), fireAttackImageView);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.play();
            });
            delay.play();
        });

        sequence.play();
    }

    private void endBattle() {
        String message = player.isAlive()
                ? "Victoire ! " + boss.getName() + " a été vaincu !"
                : "Défaite... " + player.getName() + " est tombé au combat.";

        if (primaryStage != null) {
            try {
                if (player.isAlive()) {
                    // Charger l'écran de victoire
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/victory.fxml"));
                    Parent root = loader.load();
                    VictoryController controller = loader.getController();
                    controller.setGameData(battleManager.getTurnNumber(), boss.getName(), player.getHp(),
                            boss.getMaxHp());

                    // Configurer les callbacks pour les boutons
                    controller.setOnRestart(this::restartGame);
                    controller.setOnQuit(() -> {
                        System.exit(0);
                    });

                    Scene scene = new Scene(root);
                    primaryStage.setScene(scene);
                    primaryStage.setTitle("Victoire !");
                    primaryStage.show();
                } else {
                    // Charger l'écran de défaite
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/defeat.fxml"));
                    Parent root = loader.load();
                    DefeatController controller = loader.getController();
                    controller.setGameData(battleManager.getTurnNumber(), boss.getName(), player.getHp(), boss.getHp());

                    // Configurer les callbacks pour les boutons
                    controller.setOnRestart(this::restartGame);
                    controller.setOnQuit(() -> {
                        System.exit(0);
                    });

                    Scene scene = new Scene(root);
                    primaryStage.setScene(scene);
                    primaryStage.setTitle("Défaite");
                    primaryStage.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors du chargement de l'écran de fin : " + e.getMessage());
            }
        } else {
            // Fallback vers l'Alert si stage n'est pas disponible
            showAlert("Combat terminé", message + "\nCombat terminé en " + battleManager.getTurnNumber() + " tours.");
        }

        // Vérifier les succès
        SuccessTracker.checkAchievements(player, boss, battleManager.getTurnNumber(),
                battleManager.getActionsUsedHistory());
    }

    private void restartGame() {
        try {
            // Recharger la scène de combat
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/battle.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            // Passer le stage au nouveau contrôleur
            BattleController battleController = loader.getController();
            battleController.setPrimaryStage(primaryStage);

            primaryStage.setTitle("Turn-based Battle");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du redémarrage du jeu : " + e.getMessage());
        }
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
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
    private void animatePlayerAction(String actionName) {
        // Ajouter le message de narration au journal
        addNarrativeMessage(player.getName() + " utilise " + actionName + " !");

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
     * Génère un message détaillé basé sur l'action du boss
     */
    private String generateBossActionMessage(AAction action) {
        String actionName = action.getName();

        // Messages spécifiques par action du boss
        switch (actionName) {
            case "Jet de Flammes":
                return "L'Élémentaire déchaîne un Jet de Flammes dévastateur !";
            case "Morsure":
                return "L'Élémentaire t'attaque avec une Morsure féroce !";
            case "Explosion":
                return "L'Élémentaire libère une Explosion ravageuse !";
            case "Carapace de Magma":
                return "L'Élémentaire active Carapace de Magma ! Ses défenses augmentent considérablement...";
            default:
                // Pour les autres actions, utiliser le nom d'action
                if (action instanceof towergame.model.actions.BossAttackAction) {
                    return boss.getName() + " t'attaque de toutes ses forces !";
                } else if (action instanceof towergame.model.actions.BossHealAction) {
                    return boss.getName() + " se concentre et utilise " + actionName
                            + " ! Une aura de guérison le traverse !";
                } else if (action instanceof towergame.model.actions.BossDefendAction) {
                    return boss.getName() + " active " + actionName + " ! Ses défenses augmentent considérablement...";
                } else {
                    return boss.getName() + " utilise " + actionName + " !";
                }
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

    /**
     * Ajoute un message au journal de narration - AFFICHAGE IMMÉDIAT
     */
    public void addNarrativeMessage(String message) {
        // Ajouter à l'historique
        if (narrativeHistory.length() > 0) {
            narrativeHistory.append("\n");
        }
        narrativeHistory.append("→ ").append(message);

        // Afficher le message courant + un aperçu de l'historique (3-4 derniers)
        updateNarrativeDisplay();
    }

    /**
     * Met à jour l'affichage du journal avec animation de fade
     */
    private void updateNarrativeDisplay() {
        String displayText = narrativeHistory.toString();

        // Garder seulement les 4 dernières lignes pour l'affichage
        String[] lines = displayText.split("\n");
        StringBuilder displayContent = new StringBuilder();
        int startIndex = Math.max(0, lines.length - 4);
        for (int i = startIndex; i < lines.length; i++) {
            if (i > startIndex)
                displayContent.append("\n");
            displayContent.append(lines[i]);
        }

        String finalText = displayContent.toString();

        // Animation de fade pour le nouveau message
        narrativeLabel.setOpacity(0.5);
        narrativeLabel.setText(finalText);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), narrativeLabel);
        fadeIn.setFromValue(0.5);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    /**
     * Vide le journal de narration
     */
    public void clearNarrativeHistory() {
        narrativeHistory = new StringBuilder();
        narrativeLabel.setText("En attente d'action...");
    }
}
