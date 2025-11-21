package towergame.view;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;

/**
 * Classe de test pour GameEndScreen.
 * Ce test est conçu pour être exécuté dans un environnement automatisé (headless).
 * Son but principal est d'assurer la couverture de code par JaCoCo.
 */
class GameEndScreenTest {

    @BeforeAll
    static void setup() {
        // Permet aux tests Swing de s'exécuter dans un environnement sans écran (comme un serveur de build)
        System.setProperty("java.awt.headless", "true");
    }

    @AfterEach
    void tearDown() {
        // Nettoie les fenêtres créées après chaque test pour éviter les interférences.
        for (Window window : Window.getWindows()) {
            window.dispose();
        }
    }

    @Test
    @DisplayName("Le code de l'écran de victoire doit être exécuté")
    void testShowGameEnd_Victory() {
        // Act: On appelle la méthode pour l'écran de victoire.
        // Le test ne vérifie pas l'apparence, il s'assure juste que le code s'exécute sans erreur.
        GameEndScreen.showGameEnd(true, "Héros", "Boss", 10, () -> {}, () -> {});
    }

    @Test
    @DisplayName("Le code de l'écran de défaite doit être exécuté")
    void testShowGameEnd_Defeat() {
        // Act: On appelle la méthode pour l'écran de défaite.
        GameEndScreen.showGameEnd(false, "Héros", "Boss", 20, () -> {}, () -> {});
    }
}