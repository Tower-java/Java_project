package towergame;

import towergame.view.GameWindow;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import javax.swing.JPanel;

/**
 * Test de debug pour vérifier exactement où l'overlay fire_attack s'affiche
 */
public class TestOverlayDebug {
    public static void main(String[] args) {
        try {
            System.out.println("🔍 === DEBUG: Où s'affiche l'overlay fire_attack ? ===");

            // Créer une fenêtre de jeu
            GameWindow gameWindow = new GameWindow();
            gameWindow.setVisible(true);

            System.out.println("✅ Fenêtre de jeu créée");
            Thread.sleep(2000);

            // Accès aux panels via réflection pour debug
            Field playerPanelField = GameWindow.class.getDeclaredField("playerPanel");
            playerPanelField.setAccessible(true);
            JPanel playerPanel = (JPanel) playerPanelField.get(gameWindow);

            Field bossPanelField = GameWindow.class.getDeclaredField("bossPanel");
            bossPanelField.setAccessible(true);
            JPanel bossPanel = (JPanel) bossPanelField.get(gameWindow);

            System.out.println("📊 État initial:");
            System.out.println("   PlayerPanel components: " + playerPanel.getComponentCount());
            System.out.println("   BossPanel components: " + bossPanel.getComponentCount());

            // Déclencher l'attaque du boss
            System.out.println("🎯 Déclenchement de l'attaque du boss...");
            Method playAttackMethod = GameWindow.class.getDeclaredMethod("playBossAttackAnimation");
            playAttackMethod.setAccessible(true);
            playAttackMethod.invoke(gameWindow);

            // Attendre que l'overlay apparaisse
            Thread.sleep(2000);

            System.out.println("📊 État après attaque:");
            System.out.println("   PlayerPanel components: " + playerPanel.getComponentCount());
            System.out.println("   BossPanel components: " + bossPanel.getComponentCount());

            if (playerPanel.getComponentCount() > bossPanel.getComponentCount()) {
                System.out.println("✅ SUCCESS: L'overlay est sur le PLAYER PANEL (héros) !");
            } else if (bossPanel.getComponentCount() > playerPanel.getComponentCount()) {
                System.out.println("❌ PROBLÈME: L'overlay est sur le BOSS PANEL !");
            } else {
                System.out.println("⚠️  État incertain - vérifiez visuellement");
            }

            Thread.sleep(8000);

        } catch (Exception e) {
            System.err.println("❌ Erreur durant le debug: " + e.getMessage());
            e.printStackTrace();
        }
    }
}