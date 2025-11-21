package towergame;

import towergame.view.WelcomeScreen;
import towergame.view.GameWindow;

import javax.swing.*;

/**
 * Classe principale pour lancer Tower Battle
 * Commence par l'écran d'accueil puis lance le jeu
 */
public class TowerBattleMain {

    public static void main(String[] args) {
        // Configuration pour un meilleur rendu Swing
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // Utiliser le Look and Feel du système si possible
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Utiliser le Look and Feel par défaut si échec
            System.err.println("Impossible de définir le Look and Feel: " + e.getMessage());
        }

        // Lancer l'écran d'accueil
        SwingUtilities.invokeLater(() -> {
            WelcomeScreen welcomeScreen = new WelcomeScreen(() -> {
                // Callback exécuté quand l'utilisateur appuie sur une touche
                System.out.println("🎮 Lancement de Tower Battle...");

                // Créer et afficher la fenêtre de jeu
                GameWindow gameWindow = new GameWindow();
                gameWindow.setVisible(true);
            });

            // Afficher l'écran d'accueil
            welcomeScreen.setVisible(true);
        });
    }
}