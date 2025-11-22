package towergame.model.status;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import towergame.model.actions.Element;
import towergame.model.entities.AEntity;

/**
 * Classe de test pour WeakenStatus.
 * "Weaken" (Affaiblir) signifie que la cible subit PLUS de dégâts.
 * Un multiplicateur > 1.0 est attendu.
 */
class WeakenStatusTest {

    // Classe "stub" pour pouvoir instancier AEntity
    private static class TestEntity extends AEntity {
        public TestEntity() {
            super("Test Dummy", 100, Element.NEUTRAL);
        }
    }

    @Test
    void modifyDamageTaken_shouldIncreaseIncomingDamage() {
        // Multiplicateur de 1.5, donc 50% de dégâts en plus.
        IStatusEffect weaken = new WeakenStatus(2, 1.5);
        int initialDamage = 50;

        int modifiedDamage = weaken.modifyDamageTaken(initialDamage);

        assertEquals(75, modifiedDamage, "Les dégâts subis devraient être augmentés à 150% de leur valeur initiale.");
    }

    @Test
    void modifyDamageTaken_shouldHandleZeroDamage() {
        IStatusEffect weaken = new WeakenStatus(2, 0.8);
        int initialDamage = 0;

        int modifiedDamage = weaken.modifyDamageTaken(initialDamage);

        assertEquals(0, modifiedDamage, "0 dégât modifié doit rester 0.");
    }

    @Test
    void modifyDamageDealt_shouldBeUnchanged() {
        IStatusEffect weaken = new WeakenStatus(2, 1.5);
        int initialDamage = 50;

        int modifiedDamage = weaken.modifyDamageDealt(initialDamage);

        assertEquals(initialDamage, modifiedDamage, "WeakenStatus ne doit pas affecter les dégâts infligés.");
    }

    @Test
    void duration_shouldDecreaseAndEffectShouldEnd() {
        WeakenStatus weaken = new WeakenStatus(2, 1.5);

        assertFalse(weaken.isDone());
        assertEquals(2, weaken.getDuration());

        weaken.updateDuration(); // Fin du tour 1
        assertFalse(weaken.isDone());
        assertEquals(1, weaken.getDuration());

        weaken.updateDuration(); // Fin du tour 2
        assertTrue(weaken.isDone());
        assertEquals(0, weaken.getDuration());
    }

    @Test
    void getName_shouldReturnCorrectName() {
        WeakenStatus weaken = new WeakenStatus(1, 1.5);
        assertEquals("Weaken", weaken.getName());
    }

    @Test
    void onTurnStart_shouldHaveNoEffect() {
        WeakenStatus weaken = new WeakenStatus(1, 1.5);
        TestEntity entity = new TestEntity();
        int initialHp = entity.getHp();
        weaken.onTurnStart(entity);
        assertEquals(initialHp, entity.getHp());
    }

    @Test
    void onTurnEnd_shouldHaveNoEffect() {
        WeakenStatus weaken = new WeakenStatus(1, 1.5);
        TestEntity entity = new TestEntity();
        int initialHp = entity.getHp();
        weaken.onTurnEnd(entity);
        assertEquals(initialHp, entity.getHp());
    }
}