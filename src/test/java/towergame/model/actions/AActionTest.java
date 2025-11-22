package towergame.model.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import towergame.model.entities.AEntity;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de test pour la classe abstraite AAction.
 * Elle utilise une implémentation concrète minimale (StubAction) pour tester la logique partagée.
 */
class AActionTest {

    /**
     * Une implémentation "stub" de AAction juste pour les besoins des tests.
     * L'implémentation de execute() est vide car nous ne testons pas cette méthode ici.
     */
    private static class StubAction extends AAction {
        public StubAction(String name, Element element, int cooldownDuration) {
            super(name, element, cooldownDuration);
        }

        @Override
        public void execute(AEntity user, AEntity target) {
            // Ne fait rien, utilisé uniquement pour instancier AAction
        }
    }

    private AAction action;

    @BeforeEach
    void setUp() {
        action = new StubAction("Test Action", Element.NEUTRAL, 5);
    }

    @Test
    void constructor_shouldInitializePropertiesCorrectly() {
        // Assert
        assertEquals("Test Action", action.getName(), "Le nom de l'action doit être correctement initialisé.");
        assertEquals(Element.NEUTRAL, action.getElement(), "L'élément de l'action doit être correctement initialisé.");
        assertEquals(0, action.getCurrentCooldown(), "Le cooldown initial doit être à 0.");
        assertTrue(action.isReady(), "Une nouvelle action doit être prête à l'utilisation.");
    }

    @Test
    void startCooldown_shouldSetCurrentCooldownToDuration() {
        // Act
        action.startCooldown();

        // Assert
        assertEquals(5, action.getCurrentCooldown(), "Le cooldown actuel doit être égal à la durée définie.");
        assertFalse(action.isReady(), "L'action ne doit pas être prête après le démarrage du cooldown.");
    }
}