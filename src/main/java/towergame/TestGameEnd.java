package towergame;

import towergame.view.GameEndScreen;
import towergame.view.WelcomeScreen;
import towergame.view.GameWindow;

import javax.swing.*;

/**
 * Test pour l'écran de fin
 */
public class TestGameEnd {

    public static void main(String[] args) {
        // Configuration pour un meilleur rendu Swing
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            System.out.println("🎮 Test de l'écran de fin...");

            // Test écran de victoire
            GameEndScreen.showGameEnd(
                    true, // victoire
                    "Héros Légendaire",
                    "Dragon des Abysses",
                    15, // 15 tours
                    () -> {
                        // Callback restart
                        System.out.println("🔄 Retour à l'accueil...");
                        new WelcomeScreen(() -> {
                            System.out.println("🎯 Nouveau jeu lancé !");
                            new GameWindow().setVisible(true);
                        }).setVisible(true);
                    },
                    () -> {
                        // Callback exit
                        System.out.println("❌ Fermeture du jeu");
                        System.exit(0);
                    });
        });
    }
}