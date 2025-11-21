package towergame.model.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import towergame.model.actions.AAction;
import towergame.model.actions.Element;
import towergame.model.actions.PlayerAttackAction;

import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player("Héros");
    }

    @Test
    void constructor_shouldInitializePlayerWithCorrectDefaults() {
        assertEquals("Héros", player.getName());
        assertEquals(100, player.getMaxHp());
        assertEquals(100, player.getHp());
        assertEquals(Element.NEUTRAL, player.getElement());
        assertNotNull(player.getEquippedActions());
        assertTrue(player.getEquippedActions().isEmpty());
        assertEquals(1.0, player.getDamageMultiplier(), "Le multiplicateur de dégâts initial doit être de 1.0.");
    }

    @Test
    void setAndGetEquippedActions_shouldWorkCorrectly() {
        List<AAction> actions = new ArrayList<>();
        actions.add(new PlayerAttackAction("Coup", Element.NEUTRAL, 0, 10));
        
        player.setEquippedActions(actions);

        assertEquals(1, player.getEquippedActions().size());
        assertEquals("Coup", player.getEquippedActions().get(0).getName());
    }

    @Test
    void applyDamageBoost_shouldSetMultiplierAndDuration() {
        player.applyDamageBoost(1.5, 3);
        assertEquals(1.5, player.getDamageMultiplier(), "Le multiplicateur de dégâts doit être mis à jour.");
    }

    @Test
    void updateStatusEffects_shouldDecrementBoostDuration() {
        // Arrange
        player.applyDamageBoost(1.5, 3);
        assertEquals(1.5, player.getDamageMultiplier());

        // Act
        player.updateStatusEffects();

        // Assert
        assertEquals(1.5, player.getDamageMultiplier(), "Le multiplicateur ne doit pas changer après 1 tour.");
    }

    @Test
    void updateStatusEffects_shouldResetMultiplier_whenBoostExpires() {
        // Arrange
        player.applyDamageBoost(1.5, 1); // Boost pour 1 tour
        assertEquals(1.5, player.getDamageMultiplier());

        // Act
        player.updateStatusEffects(); // Le boost expire ici

        // Assert
        assertEquals(1.0, player.getDamageMultiplier(), "Le multiplicateur doit être réinitialisé à 1.0 lorsque le boost expire.");
    }

    @Test
    void updateStatusEffects_shouldDoNothing_whenNoBoostIsActive() {
        assertEquals(1.0, player.getDamageMultiplier());
        player.updateStatusEffects();
        assertEquals(1.0, player.getDamageMultiplier(), "Le multiplicateur ne doit pas changer s'il n'y a pas de boost actif.");
    }
}