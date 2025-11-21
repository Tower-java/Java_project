package towergame;

import towergame.view.GameWindow;
import javax.swing.*;

/**
 * Test spécifique pour vérifier le changement de sprite lors de l'enrage
 */
public class TestEnrageSprite {

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            System.out.println("🔥 Test du changement de sprite lors de l'enrage...");

            // Créer une instance du jeu
            GameWindow game = new GameWindow();
            game.setVisible(true);

            // Attendre que le jeu se charge puis forcer l'enrage
            Timer testTimer = new Timer(2000, e -> {
                System.out.println("🧪 Forçage de l'enrage du boss...");

                try {
                    // Accéder au boss
                    java.lang.reflect.Field bossField = GameWindow.class.getDeclaredField("boss");
                    bossField.setAccessible(true);
                    Object boss = bossField.get(game);

                    if (boss != null) {
                        // Chercher le champ hp dans la hiérarchie des classes
                        Class<?> currentClass = boss.getClass();
                        java.lang.reflect.Field hpField = null;
                        while (currentClass != null && hpField == null) {
                            try {
                                hpField = currentClass.getDeclaredField("hp");
                            } catch (NoSuchFieldException ex) {
                                currentClass = currentClass.getSuperclass();
                            }
                        }

                        if (hpField != null) {
                            hpField.setAccessible(true);

                            // Obtenir les HP max
                            java.lang.reflect.Method getMaxHpMethod = boss.getClass().getMethod("getMaxHp");
                            int maxHp = (Integer) getMaxHpMethod.invoke(boss);

                            // Réduire à 50% pour déclencher l'enrage (seuil à 60%)
                            int newHp = (int) (maxHp * 0.5);
                            hpField.set(boss, newHp);

                            System.out.println("🔧 HP du boss réduits à " + newHp + "/" + maxHp);

                            // Forcer la mise à jour du sprite
                            java.lang.reflect.Method updateMethod = GameWindow.class
                                    .getDeclaredMethod("updateBossSprite");
                            updateMethod.setAccessible(true);
                            updateMethod.invoke(game);

                            System.out.println("✅ Test d'enrage appliqué");
                            System.out.println("👀 Vérifiez que :");
                            System.out.println("   - Le sprite du boss a changé pour la version enragée");
                            System.out.println("   - Les bordures sont devenues rouges");
                        }
                    }

                } catch (Exception ex) {
                    System.err.println("❌ Erreur : " + ex.getMessage());
                    ex.printStackTrace();
                }

                ((Timer) e.getSource()).stop();
            });

            testTimer.setRepeats(false);
            testTimer.start();
        });
    }
}