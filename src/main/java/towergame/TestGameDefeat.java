package towergame;

import towergame.view.GameEndScreen;
import towergame.view.WelcomeScreen;
import towergame.view.GameWindow;

import javax.swing.*;

/**
 * Test pour l'écran de défaite
 */
public class TestGameDefeat {

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            System.out.println("💀 Test de l'écran de défaite...");

            // Test écran de défaite
            GameEndScreen.showGameEnd(
                    false, // défaite
                    "Héros Vaillant",
                    "Dragon Noir Ultime",
                    27, // 27 tours
                    () -> {
                        // Callback restart
                        System.out.println("🔄 Retour à l'accueil pour une revanche...");
                        new WelcomeScreen(() -> {
                            System.out.println("⚔️ Nouvelle tentative !");
                            new GameWindow().setVisible(true);
                        }).setVisible(true);
                    },
                    () -> {
                        // Callback exit
                        System.out.println("💔 Abandon définitif...");
                        System.exit(0);
                    });
        });
    }
}