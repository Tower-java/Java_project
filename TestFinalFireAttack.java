package towergame;

import towergame.view.GameWindow;
import java.lang.reflect.Method;

/**
 * Test final pour valider que fire_attack PNG apparaît UNIQUEMENT sur le héros
 * et UNIQUEMENT quand le boss attaque
 */
public class TestFinalFireAttack {
    public static void main(String[] args) {
        try {
            System.out.println("🔥 === Test FINAL fire_attack PNG ===");
            System.out.println("📋 Comportement CORRECT attendu:");
            System.out.println("   ✅ Boss attaque héros → PNG fire_attack sur cadre HÉROS");
            System.out.println("   ❌ Héros attaque boss → AUCUN PNG fire_attack");
            System.out.println("   🎯 fire_attack est exclusif aux attaques du boss !");
            System.out.println();

            // Créer une fenêtre de jeu
            GameWindow gameWindow = new GameWindow();
            gameWindow.setVisible(true);

            System.out.println("✅ Fenêtre de jeu créée et affichée");
            Thread.sleep(2000);

            // Test 1: Déclencher une attaque du boss (DOIT montrer PNG sur héros)
            System.out.println("🎯 Test 1: Attaque du boss → PNG fire_attack DOIT apparaître sur HÉROS");
            Method playAttackMethod = GameWindow.class.getDeclaredMethod("playBossAttackAnimation");
            playAttackMethod.setAccessible(true);
            playAttackMethod.invoke(gameWindow);

            Thread.sleep(3000);

            System.out.println("🎮 Test 2: Maintenant cliquez sur une attaque du héros");
            System.out.println("           → AUCUN PNG fire_attack ne doit apparaître");
            System.out.println("           → Seul le boss peut utiliser fire_attack !");
            System.out.println("⏰ Observez la différence entre les deux types d'attaques");

            // Garder la fenêtre ouverte pour les tests manuels
            Thread.sleep(15000);

        } catch (Exception e) {
            System.err.println("❌ Erreur durant le test: " + e.getMessage());
            e.printStackTrace();
        }
    }
}