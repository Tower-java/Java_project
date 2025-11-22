package towergame;

import towergame.view.GameWindow;
import java.lang.reflect.Method;

/**
 * Test pour vérifier que l'effet PNG fire_attack apparaît toujours sur le cadre
 * du héros
 */
public class TestHeroAttackFrame {
    public static void main(String[] args) {
        try {
            System.out.println("🔥 === Test PNG fire_attack sur cadre héros ===");
            System.out.println("📋 Comportement attendu:");
            System.out.println("   ✅ Héros attaque → PNG fire_attack sur cadre HÉROS");
            System.out.println("   ✅ Boss attaque → PNG fire_attack sur cadre HÉROS");
            System.out.println("   🎯 Dans TOUS les cas, l'effet doit être sur le héros !");
            System.out.println();

            // Créer une fenêtre de jeu
            GameWindow gameWindow = new GameWindow();
            gameWindow.setVisible(true);

            System.out.println("✅ Fenêtre de jeu créée et affichée");
            Thread.sleep(2000);

            // Test 1: Déclencher une attaque du boss (doit montrer PNG sur héros)
            System.out.println("🎯 Test 1: Attaque du boss → PNG doit apparaître sur HÉROS");
            Method playAttackMethod = GameWindow.class.getDeclaredMethod("playBossAttackAnimation");
            playAttackMethod.setAccessible(true);
            playAttackMethod.invoke(gameWindow);

            Thread.sleep(3000);

            System.out.println("🎮 Test 2: Maintenant cliquez sur une attaque du héros");
            System.out.println("           → PNG doit AUSSI apparaître sur HÉROS");
            System.out.println("⏰ Observez que dans les deux cas, l'effet est sur le héros !");

            // Garder la fenêtre ouverte pour les tests manuels
            Thread.sleep(15000);

        } catch (Exception e) {
            System.err.println("❌ Erreur durant le test: " + e.getMessage());
            e.printStackTrace();
        }
    }
}