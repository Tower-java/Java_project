package towergame.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
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
import towergame.model.entities.FireElementalBoss;
import towergame.model.entities.WaterElementalBoss;
import towergame.model.entities.Player;
import towergame.model.managers.BattleManager;
import towergame.model.managers.StageManager;
import towergame.model.managers.SuccessTracker;
import towergame.util.AudioManager;

public class BattleController {

    @FXML
    private ImageView playerSprite;

    @FXML
    private ImageView enemySprite;

    @FXML
    private ImageView arenaBackground;

    @FXML
    private StackPane battleArenaStack;

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

    @FXML
    private VBox bossBox;

    private Player player;
    private ABoss boss;
    private BattleManager battleManager;
    private StringBuilder narrativeHistory = new StringBuilder();
    private Stage primaryStage;
    private int currentPlayerSpriteThreshold = 100; // Tracking pour sprites Gandoulf
    private StageManager stageManager;

    @FXML
    public void initialize() {
        try {
            this.stageManager = new StageManager();
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
            playBattleMusic();

            // Initialiser l'affichage
            updateDisplay();
            setupActionButtons();

            // Initialiser le journal de narration avec séparation de tour
            addNarrativeMessage("--- TOUR DU JOUEUR ---");
            addNarrativeMessage("Le combat commence contre " + boss.getName() + " !");

            // Charger les images si disponibles
            loadSprites();

            // Initialiser la couleur du cadre du boss selon son type
            // setBossBoxColor();

            // Initialiser le seuil de sprite Gandoulf
            currentPlayerSpriteThreshold = player.getCurrentSpriteThreshold();

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
                double multiplier = action.getElement().getMultiplierAgainst(boss.getElement());
                if (multiplier == 0.5) {
                    addNarrativeMessage(boss.getName() + " resiste a " + action.getName()
                            + " ! L'attaque n'est pas tres efficace...");
                } else if (multiplier == 2.0) {
                    addNarrativeMessage(
                            boss.getName() + " craint " + action.getName() + " ! C'est super efficace !");
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

                    // Vérifier si le sprite du joueur doit changer (système Gandoulf)
                    int newThreshold = player.getCurrentSpriteThreshold();
                    if (currentPlayerSpriteThreshold != newThreshold) {
                        currentPlayerSpriteThreshold = newThreshold;
                        updatePlayerSpriteByHealth();
                    }

                    // VÉRIFIER SI LE BOSS EST MORT (one-shot)
                    if (!boss.isAlive()) {
                        showBossDeath();
                        return;
                    }

                    // Afficher le message de l'action du boss
                    // Afficher le message du tour du boss
                    addNarrativeMessage("--- TOUR DU BOSS ---");

                    AAction bossAction = battleManager.getLastBossAction();
                    if (bossAction != null) {
                        String bossMessage = generateBossActionMessage(bossAction);
                        addNarrativeMessage(bossMessage);
                    }

                    // Afficher l'effet d'action du boss avec animation
                    showBossActionEffect(bossAction);

                    // Attendre la fin de l'animation du feu/défense (~2s), puis afficher l'enrage
                    PauseTransition fireWaitPause = new PauseTransition(Duration.seconds(2.0));
                    fireWaitPause.setOnFinished(fireEnd -> {
                        // Vérifier si le boss vient d'entrer en rage et afficher immédiatement
                        // (mais seulement UNE FOIS - utiliser le flag spriteChangedForEnrage)
                        if (boss.isEnraged() && !boss.hasSpriteChangedForEnrage()) {
                            addNarrativeMessage(boss.getName()
                                    + " ENTRE EN RAGE ! Ses attaques deviennent plus puissantes !");
                            // Changer le sprite du boss avec transition en fonction du type de boss
                            String enragedSpritePath = "/sprites/fire_boss_enrage.png"; // Défaut
                            if (boss instanceof towergame.model.entities.WaterElementalBoss) {
                                enragedSpritePath = "/sprites/Boss_d'eau_enragé.png";
                            }
                            changeBossSprite(enragedSpritePath);
                            boss.markSpriteChangedForEnrage();
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

        // Déterminer le type de boss et charger le sprite approprié
        String bossSpritePath = "/sprites/fire_boss.png"; // Défaut
        String bossType = boss.getClass().getSimpleName();
        System.out.println("DEBUG: Boss type = " + bossType);

        if (boss instanceof towergame.model.entities.WaterElementalBoss) {
            bossSpritePath = "/sprites/Boss_d'eau.png";
            System.out.println("DEBUG: Loading water boss sprite: " + bossSpritePath);
        } else if (boss instanceof towergame.model.entities.FireElementalBoss) {
            bossSpritePath = "/sprites/fire_boss.png";
            System.out.println("DEBUG: Loading fire boss sprite: " + bossSpritePath);
        }

        InputStream eImg = getClass().getResourceAsStream(bossSpritePath);
        if (eImg != null) {
            Image bossSpriteImage = new Image(eImg);
            enemySprite.setImage(bossSpriteImage);
            // Sprite regarde vers la DROITE pour l'arène (pas de flip ou scaleX = 1)
            enemySprite.setScaleX(1);
            System.out.println("DEBUG: Boss sprite loaded and oriented correctly");
        } else {
            System.out.println("DEBUG: Boss sprite NOT FOUND: " + bossSpritePath);
        }

        InputStream fImg = getClass().getResourceAsStream("/sprites/fire_attack.png");
        if (fImg != null) {
            fireAttackImageView.setImage(new Image(fImg));
        }

        // Initialiser le sprite du joueur avec l'état de santé actuel
        currentPlayerSpriteThreshold = player.getCurrentSpriteThreshold();
        updatePlayerSpriteByHealth();

        // Charger le background de l'arène
        loadArenaBackground();
    }

    /**
     * Charge le background de l'arène en fonction du type de boss
     */
    private void loadArenaBackground() {
        String backgroundPath = null;

        if (boss instanceof towergame.model.entities.FireElementalBoss) {
            backgroundPath = "/sprites/background boss de feu.png";
        } else if (boss instanceof towergame.model.entities.WaterElementalBoss) {
            backgroundPath = "/sprites/background boss de glace.png";
        }

        if (backgroundPath != null) {
            InputStream bgImg = getClass().getResourceAsStream(backgroundPath);
            if (bgImg != null) {
                Image backgroundImage = new Image(bgImg);
                arenaBackground.setImage(backgroundImage);
                System.out.println("DEBUG: Arena background loaded: " + backgroundPath);
            } else {
                System.out.println("DEBUG: Arena background NOT FOUND: " + backgroundPath);
            }
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

                // Déterminer l'orientation correcte en fonction du type de boss
                // Le sprite du boss d'eau (normal et enragé) doit faire face à gauche
                if (boss instanceof towergame.model.entities.WaterElementalBoss) {
                    enemySprite.setScaleX(-1); // Face à gauche (vers le héros)
                } else {
                    enemySprite.setScaleX(1); // Face à droite
                }

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
     * Affiche l'effet d'action du boss selon le type d'action
     */
    private void showBossActionEffect(AAction action) {
        if (action instanceof towergame.model.actions.BossDefendAction) {
            // Vérifier le type de boss pour déterminer l'animation de défense
            String defendActionName = action.getName();
            if (boss instanceof towergame.model.entities.WaterElementalBoss
                    && "Carapace d'eau liquide".equals(defendActionName)) {
                showBossWaterShieldEffect();
            } else {
                showBossDefendEffect();
            }
        } else if (action instanceof towergame.model.actions.BossAttackAction) {
            // Vérifier le nom de l'action pour déterminer l'effet visuel
            String actionName = action.getName();

            // Actions du boss de feu
            if ("Morsure".equals(actionName)) {
                // Pour le boss de feu: affichage simple de la morsure
                if (!(boss instanceof towergame.model.entities.WaterElementalBoss)) {
                    showBossBiteEffect();
                } else {
                    // Pour le boss d'eau: afficher le sprite de morsure d'eau
                    showBossWaterBiteEffect();
                }
            } else if ("Explosion".equals(actionName)) {
                showBossExplosionEffect();
            } else if ("Jet de Flammes".equals(actionName)) {
                showBossFlameJetEffect();
            } else if ("Trempette".equals(actionName)) {
                showBossWaterSplashEffect();
            } else if ("Éclaboussure mortelle".equals(actionName)) {
                showBossDeadlySplashEffect();
            } else {
                showFireAttackEffect();
            }
        } else if (action instanceof towergame.model.actions.BossHealAction) {
            showFireAttackEffect(); // Réutiliser l'animation d'attaque par défaut
        } else {
            showFireAttackEffect(); // Animation par défaut pour les autres actions
        }
    }

    /**
     * Affiche l'effet de jet de flammes du boss
     */
    private void showBossFlameJetEffect() {
        // Charger l'image du jet de flammes (utilise fire_attack.png)
        InputStream flameImg = getClass().getResourceAsStream("/sprites/fire_attack.png");
        if (flameImg == null) {
            return;
        }

        Image flameImage = new Image(flameImg);
        fireAttackImageView.setImage(flameImage);

        // Fade in
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), fireAttackImageView);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        // Scale animation (agrandissement progressif)
        ScaleTransition scale = new ScaleTransition(Duration.millis(400), fireAttackImageView);
        scale.setFromX(0.6);
        scale.setFromY(0.6);
        scale.setToX(1.2);
        scale.setToY(1.2);

        // Rotation animation (rotation du jet)
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

    /**
     * Affiche l'effet d'explosion du boss
     */
    private void showBossExplosionEffect() {
        // Charger l'image de l'explosion (Boss_de_feu_explosion.png)
        InputStream explosionImg = getClass().getResourceAsStream("/sprites/Boss_de_feu_explosion.png");
        if (explosionImg == null) {
            showFireAttackEffect(); // Fallback si l'image n'existe pas
            return;
        }

        Image explosionImage = new Image(explosionImg);
        fireAttackImageView.setImage(explosionImage);

        // Fade in rapide
        FadeTransition fadeIn = new FadeTransition(Duration.millis(150), fireAttackImageView);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        // Scale animation très agressif (explosion)
        ScaleTransition scale = new ScaleTransition(Duration.millis(500), fireAttackImageView);
        scale.setFromX(0.2);
        scale.setFromY(0.2);
        scale.setToX(1.3);
        scale.setToY(1.3);
        scale.setCycleCount(2);
        scale.setAutoReverse(true);

        // Vibration pour simuler le blast
        TranslateTransition vibration = new TranslateTransition(Duration.millis(80), fireAttackImageView);
        vibration.setFromX(-8);
        vibration.setToX(8);
        vibration.setCycleCount(6);
        vibration.setAutoReverse(true);

        // Parallel transition pour les animations simultanees
        ParallelTransition parallel = new ParallelTransition(scale, vibration);

        // Sequential transition : fade in, puis animations paralleles
        SequentialTransition sequence = new SequentialTransition(fadeIn, parallel);

        // Fade out après les animations
        sequence.setOnFinished(e -> {
            PauseTransition delay = new PauseTransition(Duration.millis(600));
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

    /**
     * Affiche l'effet de morsure du boss avec animation
     */
    private void showBossBiteEffect() {
        // Charger l'image de la morsure
        InputStream biteImg = getClass().getResourceAsStream("/sprites/Morsure_boss_de_feu.png");
        if (biteImg == null) {
            showFireAttackEffect(); // Fallback si l'image n'existe pas
            return;
        }

        Image biteImage = new Image(biteImg);
        fireAttackImageView.setImage(biteImage);

        // Fade in
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), fireAttackImageView);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        // Scale animation (agrandissement rapide pour effet de morsure)
        ScaleTransition scale = new ScaleTransition(Duration.millis(300), fireAttackImageView);
        scale.setFromX(0.3);
        scale.setFromY(0.3);
        scale.setToX(1.1);
        scale.setToY(1.1);

        // Vibration animation (tremblements pour intensité)
        TranslateTransition vibration = new TranslateTransition(Duration.millis(100), fireAttackImageView);
        vibration.setFromX(-5);
        vibration.setToX(5);
        vibration.setCycleCount(4);
        vibration.setAutoReverse(true);

        // Parallel transition pour les animations simultanees
        ParallelTransition parallel = new ParallelTransition(scale, vibration);

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

    /**
     * Affiche l'effet de défense du boss avec animation du bouclier
     */
    private void showBossDefendEffect() {
        // Charger l'image du bouclier
        InputStream shieldImg = getClass().getResourceAsStream("/sprites/Bouclier_boss_de_feu.png");
        if (shieldImg == null) {
            showFireAttackEffect(); // Fallback si l'image n'existe pas
            return;
        }

        Image shieldImage = new Image(shieldImg);
        fireAttackImageView.setImage(shieldImage);

        // Fade in
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), fireAttackImageView);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        // Scale animation (agrandissement pour effet de bouclier)
        ScaleTransition scale = new ScaleTransition(Duration.millis(500), fireAttackImageView);
        scale.setFromX(0.5);
        scale.setFromY(0.5);
        scale.setToX(1.0);
        scale.setToY(1.0);

        // Rotation animation (légère rotation du bouclier)
        RotateTransition rotate = new RotateTransition(Duration.millis(600), fireAttackImageView);
        rotate.setFromAngle(-20);
        rotate.setToAngle(20);
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
        // Vérifier s'il y a un boss suivant
        ABoss nextBoss = stageManager.getNextBoss();

        if (nextBoss != null && player.isAlive()) {
            // Il y a un boss suivant, charger le combat suivant avec transition en fondu
            loadNextArena(nextBoss);
        } else {
            // Pas de boss suivant ou le joueur est mort, afficher l'écran de fin
            showEndScreen();
        }

        // Vérifier les succès
        SuccessTracker.checkAchievements(player, boss, battleManager.getTurnNumber(),
                battleManager.getActionsUsedHistory());
    }

    private void loadNextArena(ABoss nextBoss) {
        // Fade out de l'arène actuelle
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1.0), enemySprite);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        fadeOut.setOnFinished(e -> {
            try {
                // Réinitialiser la vie du joueur pour le nouveau combat
                player.heal(player.getMaxHp() - player.getHp());

                // Réinitialiser le combat avec le nouveau boss
                boss = nextBoss;
                battleManager = new BattleManager(player, boss);
                playBattleMusic();

                // Réinitialiser les contrôles
                currentPlayerSpriteThreshold = player.getCurrentSpriteThreshold();
                narrativeHistory = new StringBuilder();
                actionsBox.getChildren().clear();

                // Recharger les sprites et l'affichage
                updateDisplay();
                setupActionButtons();
                loadSprites();

                addNarrativeMessage("--- COMBAT SUIVANT ---");
                addNarrativeMessage("Nouvel adversaire : " + boss.getName() + " !");

                // Fade in de la nouvelle arène
                FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.0), enemySprite);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Erreur", "Erreur lors du chargement de la nouvelle arène : " + ex.getMessage());
            }
        });

        fadeOut.play();
    }

    private void showEndScreen() {
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

        // Afficher le sprite de l'attaque du joueur
        showPlayerActionSprite(actionName);

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
     * Affiche le sprite de l'action du joueur dans la zone centrale
     */
    private void showPlayerActionSprite(String actionName) {
        // Mapper le nom de l'action au fichier sprite
        String spritePath = getPlayerActionSpritePath(actionName);
        if (spritePath == null) {
            return; // Si pas de sprite, ne rien faire
        }

        InputStream actionImg = getClass().getResourceAsStream(spritePath);
        if (actionImg == null) {
            return; // Sprite non trouvé
        }

        Image actionImage = new Image(actionImg);
        fireAttackImageView.setImage(actionImage);

        // Fade in
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), fireAttackImageView);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        // Scale animation (agrandissement)
        ScaleTransition scale = new ScaleTransition(Duration.millis(400), fireAttackImageView);
        scale.setFromX(0.5);
        scale.setFromY(0.5);
        scale.setToX(1.0);
        scale.setToY(1.0);

        // Parallel transition pour les animations simultanees
        ParallelTransition parallel = new ParallelTransition(scale);

        // Sequential transition : fade in, puis animations paralleles
        SequentialTransition sequence = new SequentialTransition(fadeIn, parallel);

        // Fade out après les animations
        sequence.setOnFinished(e -> {
            PauseTransition delay = new PauseTransition(Duration.millis(600));
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

    /**
     * Retourne le chemin du sprite en fonction du nom de l'action du joueur
     */
    private String getPlayerActionSpritePath(String actionName) {
        switch (actionName) {
            case "Attaque":
                return "/sprites/Attaque.png";
            case "Coup de Feu":
                return "/sprites/Coup de Feu.png";
            case "Inferno":
                return "/sprites/Inferno.png";
            case "Blizzard":
                return "/sprites/Blizzard.png";
            case "Jet de glace":
                return "/sprites/Jet de glace.png";
            case "Lianes":
                return "/sprites/Lianes.png";
            case "Enracinement":
                return "/sprites/Enracinement.png";
            case "Soin Léger":
                return "/sprites/Soin Léger.png";
            case "Barrière":
                return "/sprites/Barrière.png";
            case "Fureur":
                return "/sprites/Fureur.png";
            default:
                return null; // Pas de sprite pour cette action
        }
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

    /**
     * Met à jour le sprite du joueur en fonction de sa santé
     * Sprites Gandoulf: 100% (Frais), 75% (Légers), 50% (Importants), 25%
     * (Critiques)
     */
    private void updatePlayerSpriteByHealth() {
        int threshold = player.getCurrentSpriteThreshold();
        String spritePath = "";

        switch (threshold) {
            case 100:
                spritePath = "/sprites/GandoulfFrais.png";
                break;
            case 75:
                spritePath = "/sprites/GandoulfDegatsLegers.png";
                break;
            case 50:
                spritePath = "/sprites/GandoulfDegatsImportants.png";
                break;
            case 25:
                spritePath = "/sprites/GandoulfDegatsCritiques.png";
                break;
        }

        changePlayerSprite(spritePath);
    }

    /**
     * Change le sprite du joueur avec une transition de fade
     */
    private void changePlayerSprite(String spritePath) {
        InputStream imgStream = getClass().getResourceAsStream(spritePath);
        if (imgStream != null) {
            Image newImage = new Image(imgStream);

            // Transition de fade out
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), playerSprite);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(e -> {
                // Changer l'image
                playerSprite.setImage(newImage);

                // Transition de fade in
                FadeTransition fadeIn = new FadeTransition(Duration.millis(300), playerSprite);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });

            fadeOut.play();
        }
    }

    /**
     * Affiche l'effet de Trempette du boss d'eau (attaque faible d'eau)
     */
    private void showBossWaterSplashEffect() {
        InputStream waterImg = getClass().getResourceAsStream("/sprites/Trempette_boss_d'eau.png");
        if (waterImg == null) {
            System.out.println("DEBUG: Trempette_boss_d'eau.png NOT FOUND");
            return;
        }

        Image waterImage = new Image(waterImg);
        fireAttackImageView.setImage(waterImage);
        fireAttackImageView.setTranslateX(0);
        fireAttackImageView.setTranslateY(0);

        // Fade in
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), fireAttackImageView);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        // Scale animation (légère agrandissement)
        ScaleTransition scale = new ScaleTransition(Duration.millis(400), fireAttackImageView);
        scale.setFromX(0.7);
        scale.setFromY(0.7);
        scale.setToX(1.1);
        scale.setToY(1.1);

        // Transition verticale (descente de gouttes)
        TranslateTransition translate = new TranslateTransition(Duration.millis(400), fireAttackImageView);
        translate.setFromY(-50);
        translate.setToY(50);

        ParallelTransition parallel = new ParallelTransition(scale, translate);
        SequentialTransition sequence = new SequentialTransition(fadeIn, parallel);

        // Fade out après les animations
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), fireAttackImageView);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setDelay(Duration.millis(600));

        ParallelTransition finalSeq = new ParallelTransition(sequence, fadeOut);
        finalSeq.setOnFinished(e -> {
            fireAttackImageView.setTranslateX(0);
            fireAttackImageView.setTranslateY(0);
        });
        finalSeq.play();
    }

    /**
     * Affiche l'effet de Morsure du boss d'eau
     */
    private void showBossWaterBiteEffect() {
        InputStream biteImg = getClass().getResourceAsStream("/sprites/Morsure_boss_d'eau.png");
        if (biteImg == null) {
            System.out.println("DEBUG: Morsure_boss_d'eau.png NOT FOUND");
            return;
        }

        Image biteImage = new Image(biteImg);
        fireAttackImageView.setImage(biteImage);
        fireAttackImageView.setTranslateX(0);
        fireAttackImageView.setTranslateY(0);

        // Fade in
        FadeTransition fadeIn = new FadeTransition(Duration.millis(150), fireAttackImageView);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        // Mouvement rapide d'attaque
        TranslateTransition moveAttack = new TranslateTransition(Duration.millis(200), fireAttackImageView);
        moveAttack.setFromX(-100);
        moveAttack.setToX(0);

        // Reculade
        TranslateTransition moveBack = new TranslateTransition(Duration.millis(150), fireAttackImageView);
        moveBack.setFromX(0);
        moveBack.setToX(100);

        SequentialTransition sequence = new SequentialTransition(fadeIn, moveAttack, moveBack);

        // Fade out
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), fireAttackImageView);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setDelay(Duration.millis(500));

        ParallelTransition finalSeq = new ParallelTransition(sequence, fadeOut);
        finalSeq.setOnFinished(e -> {
            fireAttackImageView.setTranslateX(0);
            fireAttackImageView.setTranslateY(0);
        });
        finalSeq.play();
    }

    /**
     * Affiche l'effet de Carapace d'eau liquide (défense du boss d'eau)
     */
    private void showBossWaterShieldEffect() {
        InputStream shieldImg = getClass().getResourceAsStream("/sprites/Bouclier_boss_d'eau.png");
        if (shieldImg == null) {
            System.out.println("DEBUG: Bouclier_boss_d'eau.png NOT FOUND");
            return;
        }

        Image shieldImage = new Image(shieldImg);
        fireAttackImageView.setImage(shieldImage);
        fireAttackImageView.setTranslateX(0);
        fireAttackImageView.setTranslateY(0);

        // Fade in
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), fireAttackImageView);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        // Rotation du bouclier
        RotateTransition rotate = new RotateTransition(Duration.millis(500), fireAttackImageView);
        rotate.setFromAngle(0);
        rotate.setToAngle(360);
        rotate.setCycleCount(1);

        // Scale légère
        ScaleTransition scale = new ScaleTransition(Duration.millis(500), fireAttackImageView);
        scale.setFromX(0.8);
        scale.setFromY(0.8);
        scale.setToX(1.2);
        scale.setToY(1.2);

        ParallelTransition parallel = new ParallelTransition(rotate, scale);
        SequentialTransition sequence = new SequentialTransition(fadeIn, parallel);

        // Fade out
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), fireAttackImageView);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setDelay(Duration.millis(700));

        ParallelTransition finalSeq = new ParallelTransition(sequence, fadeOut);
        finalSeq.setOnFinished(e -> {
            fireAttackImageView.setTranslateX(0);
            fireAttackImageView.setTranslateY(0);
        });
        finalSeq.play();
    }

    /**
     * Affiche l'effet d'Éclaboussure mortelle (grosse attaque du boss d'eau)
     */
    private void showBossDeadlySplashEffect() {
        InputStream deadlyImg = getClass().getResourceAsStream("/sprites/Eclaboussure_mortelle_boss_d'eau.png");
        if (deadlyImg == null) {
            System.out.println("DEBUG: Eclaboussure_mortelle_boss_d'eau.png NOT FOUND");
            return;
        }

        Image deadlyImage = new Image(deadlyImg);
        fireAttackImageView.setImage(deadlyImage);
        fireAttackImageView.setTranslateX(0);
        fireAttackImageView.setTranslateY(0);

        // Fade in avec délai (build-up)
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), fireAttackImageView);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.setDelay(Duration.millis(200));

        // Scale animation spectaculaire
        ScaleTransition scale = new ScaleTransition(Duration.millis(500), fireAttackImageView);
        scale.setFromX(0.5);
        scale.setFromY(0.5);
        scale.setToX(1.5);
        scale.setToY(1.5);
        scale.setDelay(Duration.millis(200));

        // Vibration d'impact
        TranslateTransition vibrate1 = new TranslateTransition(Duration.millis(50), fireAttackImageView);
        vibrate1.setByX(10);
        vibrate1.setDelay(Duration.millis(400));

        TranslateTransition vibrate2 = new TranslateTransition(Duration.millis(50), fireAttackImageView);
        vibrate2.setByX(-20);
        vibrate2.setDelay(Duration.millis(450));

        TranslateTransition vibrate3 = new TranslateTransition(Duration.millis(50), fireAttackImageView);
        vibrate3.setByX(10);
        vibrate3.setDelay(Duration.millis(500));

        ParallelTransition parallel = new ParallelTransition(fadeIn, scale, vibrate1, vibrate2, vibrate3);

        // Fade out final
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), fireAttackImageView);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setDelay(Duration.millis(700));

        ParallelTransition finalSeq = new ParallelTransition(parallel, fadeOut);
        finalSeq.setOnFinished(e -> {
            fireAttackImageView.setTranslateX(0);
            fireAttackImageView.setTranslateY(0);
        });
        finalSeq.play();
    }

    /**
     * Affiche la séquence de mort du boss avec message et pause
     */
    private void showBossDeath() {
        addNarrativeMessage(boss.getName() + " s'effondre... Le boss est vaincu !");

        // Effet visuel du boss qui s'estompe
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1.5), enemySprite);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.3);
        fadeOut.play();

        // Pause avant l'écran de victoire
        PauseTransition deathPause = new PauseTransition(Duration.seconds(2.0));
        deathPause.setOnFinished(e -> {
            endBattle();
        });
        deathPause.play();
    }

    /**
     * Définit la couleur du cadre du boss selon son type
     */
    private void setBossBoxColor() {
        if (bossBox == null)
            return;

        Color backgroundColor;
        if (boss instanceof towergame.model.entities.WaterElementalBoss) {
            // Couleur bleue/aquatique pour le boss d'eau
            backgroundColor = Color.web("4eb4dc", 0.1);
        } else {
            // Couleur rouge/brasier pour le boss de feu
            backgroundColor = Color.web("ff6b6b", 0.1);
        }

        BackgroundFill fill = new BackgroundFill(backgroundColor, new CornerRadii(10), Insets.EMPTY);
        bossBox.setBackground(new Background(fill));
        bossBox.setPadding(new Insets(10));
    }

    /**
     * Lance la musique appropriée selon le type de boss
     */
    private void playBattleMusic() {
        if (boss instanceof FireElementalBoss) {
            AudioManager.getInstance().playTrack(AudioManager.FIRE_BOSS_MUSIC);
        } else if (boss instanceof WaterElementalBoss) {
            AudioManager.getInstance().playTrack(AudioManager.WATER_BOSS_MUSIC);
        }
    }
}
