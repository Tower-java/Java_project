package towergame;

import towergame.view.GameWindow;
import towergame.model.entities.FireElementalBoss;
import towergame.model.entities.Player;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

/**
 * Test pour vérifier l'affichage des effets PNG d'attaque
 */
public class TestAttackEffect {
    public static void main(String[] args) {
        try {
            System.out.println("🔥 === Test des effets PNG d'attaque ===");

            // Créer une fenêtre de jeu
            GameWindow gameWindow = new GameWindow();
            gameWindow.setVisible(true);

            System.out.println("✅ Fenêtre de jeu créée et affichée");

            // Attendre que l'interface soit prête
            Thread.sleep(2000);

            // Utiliser la réflection pour accéder à la méthode privée
            Method playAttackMethod = GameWindow.class.getDeclaredMethod("playBossAttackAnimation");
            playAttackMethod.setAccessible(true);

            // Exécuter l'animation d'attaque du boss
            System.out.println("🎯 Lancement de l'animation d'attaque du boss...");
            playAttackMethod.invoke(gameWindow);

            System.out.println("🔥 L'effet PNG d'attaque devrait maintenant s'afficher sur le cadre du héros !");
            System.out.println("⏰ L'effet durera environ 1 seconde puis disparaîtra automatiquement");

            // Garder la fenêtre ouverte pour observer les effets
            Thread.sleep(5000);

            System.out.println("✅ Test terminé - l'effet PNG s'affiche-t-il bien sur le héros ?");

        } catch (Exception e) {
            System.err.println("❌ Erreur durant le test: " + e.getMessage());
            e.printStackTrace();
        }
    }
}