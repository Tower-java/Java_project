package towergame.controller;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

// NOTE: On pourrait utiliser une extension comme TestFX pour simplifier,
// mais pour éviter d'ajouter des dépendances, on initialise le toolkit manuellement.
public class BattleControllerTest {

    private BattleController controller;

    /**
     * Initialise le toolkit JavaFX une seule fois pour tous les tests de cette classe.
     * Sans cela, les tests utilisant des composants JavaFX échouent avec une IllegalStateException.
     */
    @BeforeAll
    public static void initToolkit() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(() -> {
            new JFXPanel(); // Initialise le toolkit
            latch.countDown();
        });
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new InterruptedException("Timeout waiting for JavaFX toolkit to start.");
        }
    }

    @BeforeEach
    public void setUp() {
        controller = new BattleController();

        // Injection manuelle des dépendances FXML
        // C'est nécessaire car nous n'utilisons pas FXMLLoader dans ce test unitaire.
        controller.playerName = new Label();
        controller.playerHp = new Label();
        controller.enemyName = new Label();
        controller.enemyHp = new Label();
        controller.playerSprite = new ImageView();
        controller.enemySprite = new ImageView();
        controller.actionsBox = new VBox();
        controller.turnLabel = new Label();
        controller.messageLabel = new Label();
    }

    @Test
    public void testInitialize_UpdatesUIDisplay() throws InterruptedException {
        // La méthode initialize crée ses propres objets (StageManager, Player, Boss)
        // ce qui rend le mocking difficile. Ce test est donc plus un test d'intégration.
        // On vérifie que l'UI est bien mise à jour après l'initialisation.

        final CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            try {
                // Act
                controller.initialize();

                // Assert
                // Le joueur est créé avec le nom "Héros" dans la méthode
                assertEquals("Héros", controller.playerName.getText());
                
                // On vérifie que les HP ne sont pas nuls ou vides, car les valeurs exactes
                // dépendent de l'implémentation de Player et ABoss.
                assertNotNull(controller.playerHp.getText());
                assertFalse(controller.playerHp.getText().isEmpty());

                // Le boss est chargé via StageManager, on vérifie juste que le nom n'est pas vide
                assertNotNull(controller.enemyName.getText());
                assertFalse(controller.enemyName.getText().isEmpty());
                assertNotNull(controller.enemyHp.getText());
                assertFalse(controller.enemyHp.getText().isEmpty());

                // Vérifie que le label de tour est initialisé
                assertTrue(controller.turnLabel.getText().contains("Tour"));

                // Vérifie que des boutons d'action ont été créés
                assertFalse(controller.actionsBox.getChildren().isEmpty(), "La VBox des actions ne devrait pas être vide après initialisation");
                assertTrue(controller.actionsBox.getChildren().get(0) instanceof Button, "Le premier enfant devrait être un bouton");
            } finally {
                latch.countDown();
            }
        });
        
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new InterruptedException("Timeout: test did not complete within 5 seconds");
        }
    }

    @Test
    public void testActionButtonClick_UpdatesMessageLabelImmediately() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            try {
                // Arrange
                controller.initialize();
                // On récupère le premier bouton d'action créé
                Button actionButton = (Button) controller.actionsBox.getChildren().get(0);

                // Act
                // Simule un clic sur le bouton
                actionButton.fire();

                // Assert
                // On vérifie que le message affiché est bien celui de l'utilisation de l'action,
                // qui est la première chose qui se passe avant la PauseTransition.
                String expectedMessage = "🎯 Héros utilise " + actionButton.getText() + " !";
                assertEquals(expectedMessage, controller.messageLabel.getText());
            } finally {
                latch.countDown();
            }
        });
        
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new InterruptedException("Timeout: test did not complete within 5 seconds");
        }
    }
}
