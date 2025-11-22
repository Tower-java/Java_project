package towergame.model.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import towergame.model.entities.AEntity;
import towergame.model.entities.FireElementalBoss;
import towergame.model.status.DefendStatus;

import static org.junit.jupiter.api.Assertions.*;

class BossDefendActionTest {

    /**
     * Classe "stub" pour une entité de test.
     * Elle nous permet d'inspecter le dernier statut qui lui a été appliqué.
     */
    private static class TestEntity extends AEntity {
        public TestEntity(String name, int maxHp) {
            super(name, maxHp, Element.NEUTRAL);
        }
    }

    private TestEntity user;
    private TestEntity target; // Requis par la signature de la méthode execute, même si non utilisé

    @BeforeEach
    void setUp() {
        user = new TestEntity("Test Boss", 500);
        target = new TestEntity("Unused Target", 100);
    }

    @Test
    void execute_whenReady_shouldAddDefendStatusToUser() {
        // Arrange
        int duration = 3;
        int blockAmount = 50;
        BossDefendAction defendAction = new BossDefendAction("Barrière Magique", 5, duration, blockAmount);
        assertTrue(user.getActiveStatus().isEmpty(), "L'utilisateur ne doit avoir aucun statut au départ.");

        // Act
        defendAction.execute(user, target);

        // Assert
        // On vérifie directement que le statut a été ajouté à l'utilisateur.
        assertEquals(1, user.getActiveStatus().size(), "Un statut doit avoir été ajouté à l'utilisateur.");
        assertTrue(user.getActiveStatus().get(0) instanceof DefendStatus, "Le statut ajouté doit être un DefendStatus.");

        // On vérifie que les paramètres du statut sont corrects.
        DefendStatus status = (DefendStatus) user.getActiveStatus().get(0);
        assertEquals(duration, status.getDuration(), "La durée du statut doit correspondre à celle de l'action.");
        assertEquals(blockAmount, status.getDamageBlockAmount(), "Le montant de blocage du statut doit correspondre à celui de l'action.");
    }

    @Test
    void execute_shouldStartCooldown() {
        // Arrange
        int cooldownDuration = 4;
        BossDefendAction defendAction = new BossDefendAction("Garde", cooldownDuration, 2, 30);
        assertTrue(defendAction.isReady(), "L'action devrait être prête avant son exécution.");

        // Act
        defendAction.execute(user, target);

        // Assert
        assertFalse(defendAction.isReady(), "L'action ne devrait pas être prête juste après son exécution.");
        
        // On simule le passage des tours pour vérifier que le cooldown diminue
        defendAction.updateCooldown(); // Cooldown restant: 3
        defendAction.updateCooldown(); // Cooldown restant: 2
        assertFalse(defendAction.isReady(), "L'action ne devrait pas être prête après 2 tours.");
        assertEquals(2, defendAction.getCurrentCooldown());
    }

    @Test
    void execute_whenNotReady_shouldDoNothing() {
        // Arrange
        BossDefendAction defendAction = new BossDefendAction("Garde", 4, 2, 30);
        defendAction.execute(user, target); // Première exécution pour démarrer le cooldown
        assertFalse(defendAction.isReady(), "L'action doit être en cooldown.");
        assertEquals(1, user.getActiveStatus().size(), "L'utilisateur doit avoir 1 statut après la première exécution.");

        // Act
        defendAction.execute(user, target); // On tente d'exécuter à nouveau

        // Assert
        assertEquals(1, user.getActiveStatus().size(), "Aucun nouveau statut ne doit être ajouté si l'action est en cooldown.");
    }

    @Test
    void getBlockAmount_shouldReturnCorrectValue() {
        // Arrange
        int blockAmount = 75;
        BossDefendAction defendAction = new BossDefendAction("Mur de pierre", 5, 2, blockAmount);

        // Act & Assert
        assertEquals(blockAmount, defendAction.getBlockAmount(), "Le getter getBlockAmount doit retourner la valeur définie dans le constructeur.");
    }

    @Test
    void execute_shouldWorkWithRealBossInstance() {
        // Arrange
        FireElementalBoss realBoss = new FireElementalBoss();
        BossDefendAction defendAction = new BossDefendAction("Carapace", 4, 2, 30);

        // Act
        defendAction.execute(realBoss, target);

        // Assert
        assertEquals(1, realBoss.getActiveStatus().size(), "Le vrai boss doit recevoir le statut de défense.");
        assertTrue(realBoss.hasEffect("Defend"), "Le statut 'Defend' doit être actif sur le boss.");
    }
}