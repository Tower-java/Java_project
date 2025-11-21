package towergame.model.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import towergame.model.actions.AAction;
import towergame.model.actions.Element;
import towergame.model.actions.FireSpell;
import towergame.model.actions.PlayerBoostSpell;
import towergame.model.actions.PlayerDefendSpell;
import towergame.model.actions.PlayerHealSpell;
import towergame.model.entities.AEntity;
import towergame.model.entities.Player;

/**
 * Classe de test pour valider le comportement des sorts du joueur (PlayerSpell).
 * Ces tests vérifient que les sorts appliquent les bons effets aux bonnes cibles.
 */
class PlayerSpellsTest {

    private Player player;
    private AEntity target; // Une entité simple suffit comme cible

    // Entité de test simple
    private static class TestTarget extends AEntity {
        public TestTarget() { super("Dummy Target", 200, Element.NEUTRAL); }
    }

    @BeforeEach
    void setUp() {
        player = new Player("Héros");
        target = new TestTarget();
    }

    @Test
    void fireSpell_shouldApplyPoisonStatusToTarget() {
        // Arrange
        int initialTargetHp = target.getHp();
        AAction fireSpell = new FireSpell("Boule de Feu", Element.FIRE, 10, 2, 0);

        // Act
        fireSpell.execute(player, target);

        // Assert
        assertTrue(target.hasEffect("Poison"), "La cible doit avoir le statut 'Poison' après l'attaque de feu.");
        assertEquals(initialTargetHp - 10, target.getHp(), "La cible doit subir les dégâts de base du sort.");
        assertFalse(player.hasEffect("Poison"), "Le joueur ne doit pas recevoir le statut à la place de la cible.");
    }

    @Test
    void playerBoostSpell_shouldIncreaseDamageMultiplierOnUser() {
        // Arrange
        // Le sort de boost dans le code utilise un multiplicateur fixe de 1.5
        PlayerBoostSpell boostSpell = new PlayerBoostSpell("Inspiration", Element.NEUTRAL, 3, 0);
        double initialMultiplier = player.getDamageMultiplier();

        // Act
        // La cible n'a pas d'importance, l'effet doit s'appliquer au lanceur (user)
        boostSpell.execute(player, target);

        // Assert
        assertEquals(1.5, player.getDamageMultiplier(), "Le multiplicateur de dégâts du joueur doit passer à 1.5.");
        assertNotEquals(initialMultiplier, player.getDamageMultiplier(), "Le multiplicateur doit avoir changé.");
    }

    @Test
    void playerDefendSpell_shouldApplyDefendStatusToUser() {
        // Arrange
        AAction defendSpell = new PlayerDefendSpell("Barrière", Element.NEUTRAL, 2, 0);

        // Act
        defendSpell.execute(player, target);

        // Assert
        assertTrue(player.hasEffect("Defend"), "Le joueur (user) doit recevoir le statut 'Defend'.");
        assertFalse(target.hasEffect("Defend"), "La cible (target) ne doit pas recevoir le statut.");
    }

    @Test
    void playerHealSpell_shouldHealTheTarget() {
        // Arrange
        player.takeDamage(40); // HP du joueur = 60
        int initialPlayerHp = player.getHp();
        AAction healSpell = new PlayerHealSpell("Soin", Element.NEUTRAL, 25, 0);

        // Act
        // Un sort de soin peut cibler n'importe qui, ici on se cible soi-même.
        healSpell.execute(player, player);

        // Assert
        assertEquals(initialPlayerHp + 25, player.getHp(), "Le joueur doit être soigné du montant du sort.");
    }
}