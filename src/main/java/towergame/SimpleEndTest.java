package towergame;

import towergame.view.GameEndScreen;
import javax.swing.*;

/**
 * Test simple des écrans de fin
 */
public class SimpleEndTest {

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            System.out.println("🏆 Test écran de VICTOIRE sans statistiques...");

            // Test écran de victoire simplifié
            GameEndScreen.showGameEnd(
                    true, // victoire
                    "Héros",
                    "Boss",
                    15, // tours (ne sera plus affiché)
                    () -> System.out.println("🔄 Redémarrer"),
                    () -> System.exit(0));

            // Après 3 secondes, montrer l'écran de défaite
            Timer timer = new Timer(3000, e -> {
                System.out.println("💀 Test écran de DÉFAITE sans statistiques...");
                GameEndScreen.showGameEnd(
                        false, // défaite
                        "Héros",
                        "Boss",
                        27, // tours (ne sera plus affiché)
                        () -> System.out.println("🔄 Redémarrer"),
                        () -> System.exit(0));
            });
            timer.setRepeats(false);
            timer.start();
        });
    }
}