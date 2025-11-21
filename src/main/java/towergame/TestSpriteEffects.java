package towergame;

import towergame.view.GameWindow;
import javax.swing.*;

/**
 * Test pour voir les effets visuels d'enrage et d'attaque
 */
public class TestSpriteEffects {

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            System.out.println("🔥 Test des effets de sprites et d'enrage...");

            // Créer une instance du jeu
            GameWindow game = new GameWindow();
            game.setVisible(true);

            // Attendre un peu puis forcer l'état d'enrage pour le test
            Timer testTimer = new Timer(3000, e -> {
                System.out.println("🧪 Test de l'enrage du boss...");

                // Essayer de forcer l'enrage du boss
                testEnrageState(game);

                // Instructions pour le test manuel
                System.out.println("ℹ️ Pour tester l'enrage:");
                System.out.println("  1. Attaquez le boss jusqu'à ce qu'il perde 40% de ses HP");
                System.out.println("  2. Observez le changement de sprite et les bordures rouges");
                System.out.println("  3. Regardez l'effet du poing de feu lors des attaques du boss");

                ((Timer) e.getSource()).stop();
            });
            testTimer.setRepeats(false);
            testTimer.start();
        });
    }

    /**
     * Méthode pour forcer l'état d'enrage du boss pour les tests
     */
    public static void testEnrageState(GameWindow gameWindow) {
        try {
            // Accéder au boss via réflexion
            java.lang.reflect.Field bossField = GameWindow.class.getDeclaredField("boss");
            bossField.setAccessible(true);
            Object boss = bossField.get(gameWindow);

            if (boss != null && boss.getClass().getSimpleName().contains("Fire")) {
                // Modifier les HP du boss pour déclencher l'enrage
                // Chercher le champ hp dans la hiérarchie des classes
                Class<?> currentClass = boss.getClass();
                java.lang.reflect.Field hpField = null;
                while (currentClass != null && hpField == null) {
                    try {
                        hpField = currentClass.getDeclaredField("hp");
                    } catch (NoSuchFieldException e) {
                        currentClass = currentClass.getSuperclass();
                    }
                }
                if (hpField == null) {
                    System.err.println("❌ Impossible de trouver le champ hp");
                    return;
                }
                hpField.setAccessible(true);

                // Obtenir les HP max
                java.lang.reflect.Method getMaxHpMethod = boss.getClass().getMethod("getMaxHp");
                int maxHp = (Integer) getMaxHpMethod.invoke(boss);

                // Réduire à 50% pour déclencher l'enrage (seuil à 60%)
                int newHp = (int) (maxHp * 0.5);
                hpField.set(boss, newHp);

                System.out.println("🔧 DEBUG: HP du boss réduits à " + newHp + "/" + maxHp + " pour tester l'enrage");

                // Forcer la mise à jour du sprite
                java.lang.reflect.Method updateMethod = GameWindow.class.getDeclaredMethod("updateBossSprite");
                updateMethod.setAccessible(true);
                updateMethod.invoke(gameWindow);

                System.out.println("✅ Test d'enrage appliqué - vérifiez si les cadres sont devenus rouges");

            } else {
                System.err.println("❌ Boss de feu non trouvé pour le test d'enrage");
            }

        } catch (Exception ex) {
            System.err.println("❌ Erreur lors du test d'enrage: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}