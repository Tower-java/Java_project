package towergame.model.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import towergame.model.entities.AEntity;
import towergame.model.actions.Element;
import towergame.model.status.IStatusEffect;
import towergame.model.status.PoisonStatus;

/**
 * Classe de test pour PoisonStatus.
 */
class PoisonStatusTest {

    // Entité de test simple
    private static class TestEntity extends AEntity {
        public TestEntity() { super("Test Dummy", 100, Element.NEUTRAL); }
    }

    private TestEntity entity;

    @BeforeEach
    void setUp() {
        entity = new TestEntity();
    }

    @Test
    void onTurnEnd_shouldApplyPoisonDamage() {
        int initialHp = entity.getHp();
        int poisonDamage = 5;
        IStatusEffect poison = new PoisonStatus(2, poisonDamage);

        // onTurnEnd est appelé par updateStatusEffects
        poison.onTurnEnd(entity);

        assertEquals(initialHp - poisonDamage, entity.getHp(), "L'entité doit subir des dégâts de poison à la fin du tour.");
    }

    @Test
    void updateStatusEffects_shouldApplyDamageAndDecreaseDuration() {
        // Arrange
        IStatusEffect poison = new PoisonStatus(3, 5);
        entity.addStatus(poison);
        int initialHp = entity.getHp();

        // Act : on simule la fin d'un tour pour l'entité
        entity.updateStatusEffects();

        // Assert
        assertEquals(initialHp - 5, entity.getHp(), "L'entité doit subir les dégâts de poison via updateStatusEffects.");
        assertEquals(2, poison.getDuration(), "La durée du poison doit diminuer après la mise à jour des statuts.");
    }

    @Test
    void modifyDamageDealt_shouldBeUnchanged() {
        IStatusEffect poison = new PoisonStatus(2, 5);
        int initialDamage = 20;
        
        int modifiedDamage = poison.modifyDamageDealt(initialDamage);

        assertEquals(initialDamage, modifiedDamage, "PoisonStatus ne doit pas modifier les dégâts infligés.");
    }

    @Test
    void modifyDamageTaken_shouldBeUnchanged() {
        IStatusEffect poison = new PoisonStatus(2, 5);
        int initialDamage = 20;

        int modifiedDamage = poison.modifyDamageTaken(initialDamage);

        assertEquals(initialDamage, modifiedDamage, "PoisonStatus ne doit pas modifier les dégâts subis directement.");
    }

    @Test
    void getName_shouldReturnCorrectName() {
        IStatusEffect poison = new PoisonStatus(1, 5);
        assertEquals("Poison", poison.getName(), "Le nom du statut doit être 'Poison'.");
    }

    @Test
    void onTurnStart_shouldHaveNoEffect() {
        IStatusEffect poison = new PoisonStatus(1, 5);
        int initialHp = entity.getHp();

        poison.onTurnStart(entity);

        assertEquals(initialHp, entity.getHp(), "onTurnStart ne doit pas modifier les PV de l'entité.");
    }

    @Test
    void status_shouldBeRemoved_whenDurationIsOver() {
        // Arrange
        IStatusEffect poison = new PoisonStatus(1, 5);
        entity.addStatus(poison);
        assertEquals(1, entity.getActiveStatus().size());

        // Act
        entity.updateStatusEffects(); // Le poison fait effet, la durée passe à 0

        // Assert
        assertEquals(0, poison.getDuration(), "La durée doit être de 0.");
        assertTrue(poison.isDone(), "Le statut doit être considéré comme terminé.");
        // La suppression effective est gérée par AEntity, on vérifie que le statut est bien marqué comme terminé.
        assertTrue(entity.getActiveStatus().isEmpty(), "Le statut expiré doit être retiré de la liste de l'entité.");
    }
}