package towergame.model.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import towergame.model.entities.AEntity;
import towergame.model.managers.World;

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
        // Initialise le monde pour que les statuts puissent y être ajoutés
        new World();
        target = new TestEntity("Unused Target", 100);
    }

    @Test
    void execute_shouldAddDefendStatusToUserWithCorrectParameters() {
        // Arrange
        int duration = 3;
        int blockAmount = 50;
        BossDefendAction defendAction = new BossDefendAction("Barrière Magique", 5, duration, blockAmount);

        // Act
        defendAction.execute(user, target);

        // Assert
        // On vérifie que le statut a été ajouté en testant son effet.
        // Un statut de défense devrait réduire les dégâts subis.
        int initialHp = user.getHp();
        int damageToTake = 100;
        
        // Simule la prise de dégâts alors que le statut est actif
        user.takeDamage(damageToTake);
        
        assertEquals(initialHp - (damageToTake - blockAmount), user.getHp(), "Les dégâts subis devraient être réduits par le montant de blocage du statut de défense.");
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
    }
}