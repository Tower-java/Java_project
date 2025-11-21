package towergame.model.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import towergame.model.actions.Element;
import towergame.model.entities.FireElementalBoss;
import towergame.model.entities.Player;import towergame.model.status.DefendStatus;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerDefendSpellTest {

    private PlayerDefendSpell defendSpell;
    private Player player;
    private FireElementalBoss boss;

    private static final int DURATION = 5;
    private static final int COOLDOWN = 10;
    private static final String SPELL_NAME = "Shield";

    @BeforeEach
    void setUp() {
        defendSpell = new PlayerDefendSpell(SPELL_NAME, Element.NEUTRAL, DURATION, COOLDOWN);
        // On utilise des instances réelles pour des tests d'intégration
        player = new Player("Héros");
        boss = new FireElementalBoss();
    }

    @Test
    void constructorAndGetters_ShouldInitializeCorrectly() {
        assertEquals(SPELL_NAME, defendSpell.getName());
        assertEquals(Element.NEUTRAL, defendSpell.getElement());
        assertEquals(DURATION, defendSpell.getDuration());
        assertEquals(COOLDOWN, defendSpell.getCooldownDuration());
        assertTrue(defendSpell.isReady(), "Le sort devrait être prêt à l'initialisation.");
    }

    @Test
    void execute_WhenReady_ShouldAddDefendStatusToUserAndStartCooldown() {
        // Pré-condition : le sort est prêt
        assertTrue(defendSpell.isReady());

        // Action
        defendSpell.execute(player, boss);

        // Vérifications
        // 1. Vérifier que le joueur a bien un statut et que c'est un DefendStatus
        assertEquals(1, player.getActiveStatus().size(), "Le joueur devrait avoir un statut actif.");
        assertTrue(player.getActiveStatus().get(0) instanceof DefendStatus, "Le statut devrait être un DefendStatus.");

        // 2. Vérifier que le statut ajouté a les bonnes propriétés
        DefendStatus status = (DefendStatus) player.getActiveStatus().get(0);
        assertEquals(10, status.getDamageBlockAmount(), "La puissance de la défense devrait être de 10.");
        assertEquals(DURATION, status.getDuration(), "La durée du statut devrait correspondre à celle du sort.");

        // 3. Vérifier que le sort est maintenant en cooldown
        assertFalse(defendSpell.isReady(), "Le sort devrait être en cooldown après utilisation.");
    }

    @Test
    void execute_WhenOnCooldown_ShouldDoNothing() {
        // Mettre le sort en cooldown en l'exécutant une première fois
        defendSpell.execute(player, boss);
        assertFalse(defendSpell.isReady());
        assertEquals(1, player.getActiveStatus().size());

        // Action : tenter de l'exécuter à nouveau
        defendSpell.execute(player, boss);

        // Vérification : le nombre de statuts sur le joueur n'a pas changé
        assertEquals(1, player.getActiveStatus().size(), "Aucun nouveau statut ne devrait être ajouté si le sort est en cooldown.");
    }
}