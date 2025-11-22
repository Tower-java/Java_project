package towergame;

import towergame.view.GameWindow;
import java.lang.reflect.Method;

/**
 * Test plus simple pour forcer l'appel direct de showAttackOverlayOnPlayer
 */
public class TestDirectOverlay {
    public static void main(String[] args) {
        try {
            System.out.println("🔥 === Test DIRECT de showAttackOverlayOnPlayer ===");

            // Créer une fenêtre de jeu
            GameWindow gameWindow = new GameWindow();
            gameWindow.setVisible(true);

            System.out.println("✅ Fenêtre de jeu créée");
            Thread.sleep(2000);

            // Appel DIRECT de la méthode showAttackOverlayOnPlayer
            System.out.println("🎯 Appel DIRECT de showAttackOverlayOnPlayer()...");
            Method showOverlayMethod = GameWindow.class.getDeclaredMethod("showAttackOverlayOnPlayer");
            showOverlayMethod.setAccessible(true);
            showOverlayMethod.invoke(gameWindow);

            System.out.println("⏰ Observez l'effet pendant 5 secondes...");
            Thread.sleep(5000);

        } catch (Exception e) {
            System.err.println("❌ Erreur durant le test direct: " + e.getMessage());
            e.printStackTrace();
        }
    }
}