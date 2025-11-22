package towergame;

import towergame.view.GameWindow;
import towergame.model.actions.AAction;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

/**
 * Test pour vérifier les effets PNG d'attaque bidirectionnels
 * - Héros attaque → PNG sur boss
 * - Boss attaque → PNG sur héros
 */
public class TestBidirectionalAttack {
    public static void main(String[] args) {
        try {
            System.out.println("🔥 === Test des effets PNG d'attaque bidirectionnels ===");

            // Créer une fenêtre de jeu
            GameWindow gameWindow = new GameWindow();
            gameWindow.setVisible(true);

            System.out.println("✅ Fenêtre de jeu créée et affichée");
            System.out.println("📋 Instructions de test:");
            System.out.println("   1️⃣ Cliquez sur une action d'attaque du héros → PNG doit apparaître sur le BOSS");
            System.out.println("   2️⃣ Attendez le tour du boss → PNG doit apparaître sur le HÉROS");
            System.out.println("   3️⃣ Vérifiez que les effets s'affichent aux bons endroits");

            // Attendre pour permettre l'observation
            Thread.sleep(2000);

            // Utiliser la réflection pour déclencher une attaque du boss en test
            System.out.println("🎯 Test automatique d'attaque du boss...");
            Method playAttackMethod = GameWindow.class.getDeclaredMethod("playBossAttackAnimation");
            playAttackMethod.setAccessible(true);
            playAttackMethod.invoke(gameWindow);

            System.out.println("🔥 Effet PNG du boss → héros affiché !");
            System.out.println("⏰ Maintenant testez manuellement une attaque du héros vers le boss");

            // Garder la fenêtre ouverte pour les tests manuels
            Thread.sleep(10000);

        } catch (Exception e) {
            System.err.println("❌ Erreur durant le test: " + e.getMessage());
            e.printStackTrace();
        }
    }
}