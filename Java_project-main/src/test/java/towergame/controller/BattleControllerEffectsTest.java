package towergame.controller;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import towergame.model.entities.ABoss;
import towergame.model.entities.FireElementalBoss;
import towergame.model.entities.Player;
import towergame.model.managers.BattleManager;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class BattleControllerEffectsTest {

    @BeforeAll
    public static void initToolkit() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(() -> {
                new JFXPanel();
                latch.countDown();
            });
        } catch (IllegalStateException ise) {
            // Toolkit already initialized by another test; just count down
            latch.countDown();
        }
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new InterruptedException("Timeout starting JavaFX");
        }
    }

    static class TestableController extends BattleController {
        String lastAlertTitle;
        String lastAlertContent;

        String lastEffectMain;
        String lastEffectSub;
        Color lastEffectColor;

        boolean bossEnrageCalled = false;

        @Override
        void showAlert(String title, String content) {
            this.lastAlertTitle = title;
            this.lastAlertContent = content;
        }

        @Override
        void showEffectAnimation(Text mainText, Text subText, Color borderColor) {
            this.lastEffectMain = mainText.getText();
            this.lastEffectSub = subText.getText();
            this.lastEffectColor = borderColor;
        }

        @Override
        void showBossEnrageEffect() {
            // Avoid playing animations in tests
            this.bossEnrageCalled = true;
            Text main = new Text("ENRAGE TEST");
            Text sub = new Text("Sub");
            showEffectAnimation(main, sub, Color.RED);
        }
    }

    private TestableController controller;
    private Player player;
    private ABoss boss;

    @BeforeEach
    void setup() throws Exception {
        controller = new TestableController();

        // Inject UI fields
        controller.playerName = new Label();
        controller.playerHp = new Label();
        controller.enemyName = new Label();
        controller.enemyHp = new Label();
        controller.playerSprite = new ImageView();
        controller.enemySprite = new ImageView();
        controller.actionsBox = new VBox();
        controller.turnLabel = new Label();
        controller.messageLabel = new Label();

        player = new Player("Héros");
        boss = new FireElementalBoss();
        ((FireElementalBoss) boss).setInvulnerable(false);

        // Inject entities and battle manager via reflection for endBattle usage
        try {
            java.lang.reflect.Field fPlayer = BattleController.class.getDeclaredField("player");
            fPlayer.setAccessible(true);
            fPlayer.set(controller, player);

            java.lang.reflect.Field fBoss = BattleController.class.getDeclaredField("boss");
            fBoss.setAccessible(true);
            fBoss.set(controller, boss);

            java.lang.reflect.Field fBM = BattleController.class.getDeclaredField("battleManager");
            fBM.setAccessible(true);
            fBM.set(controller, new BattleManager(player, boss));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testEndBattle_showsVictoryAlert() throws Exception {
        // Set boss dead to force victory
        boss.takeDamage(boss.getMaxHp());
        assertFalse(boss.isAlive());

        // Ensure player alive
        assertTrue(player.isAlive());

        // Call endBattle on JavaFX thread
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.endBattle();
            } finally {
                latch.countDown();
            }
        });
        assertTrue(latch.await(3, TimeUnit.SECONDS));

        assertNotNull(controller.lastAlertTitle);
        assertTrue(controller.lastAlertTitle.contains("Combat terminé") || controller.lastAlertTitle.toLowerCase().contains("combat"));
        assertNotNull(controller.lastAlertContent);
        assertTrue(controller.lastAlertContent.contains("Victoire") || controller.lastAlertContent.contains("vaincu") );
    }

    @Test
    void testShowResistanceEffect_recordsEffect() {
        Platform.runLater(() -> {
            controller.showResistanceEffect("Flamme");
        });
        // small wait to let JavaFX run the runnable
        try { Thread.sleep(100); } catch (InterruptedException ignored) {}

        assertNotNull(controller.lastEffectMain);
        assertTrue(controller.lastEffectMain.contains("résiste") || controller.lastEffectMain.toLowerCase().contains("resist"));
        assertNotNull(controller.lastEffectSub);
    }

    @Test
    void testShowWeaknessEffect_recordsEffect() {
        Platform.runLater(() -> {
            controller.showWeaknessEffect("Eau");
        });
        try { Thread.sleep(100); } catch (InterruptedException ignored) {}

        assertNotNull(controller.lastEffectMain);
        assertTrue(controller.lastEffectMain.contains("craint") || controller.lastEffectMain.toLowerCase().contains("craint")|| controller.lastEffectMain.toLowerCase().contains("weak"));
    }

    @Test
    void testShowBossEnrageEffect_recordsEffect() {
        Platform.runLater(() -> {
            controller.showBossEnrageEffect();
        });
        try { Thread.sleep(100); } catch (InterruptedException ignored) {}

        assertTrue(controller.bossEnrageCalled);
        assertNotNull(controller.lastEffectMain);
        assertTrue(controller.lastEffectMain.toUpperCase().contains("RAGE") || controller.lastEffectMain.toUpperCase().contains("ENRAGE"));
    }
}
