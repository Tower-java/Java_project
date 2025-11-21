package towergame.model.status;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import towergame.model.actions.Element;
import towergame.model.entities.AEntity;

/**
 * Classe de test pour EntraveStatus.
 * "Entrave" signifie que la cible inflige MOINS de dégâts.
 * Un multiplicateur < 1.0 est attendu.
 */
class EntraveStatusTest {

    // Classe "stub" pour pouvoir instancier AEntity
    private static class TestEntity extends AEntity {
        public TestEntity() {
            super("Test Dummy", 100, Element.NEUTRAL);
        }
    }

    @Test
    void modifyDamageDealt_shouldReduceOutgoingDamage() {
        // Multiplicateur de 0.7, donc 30% de réduction de dégâts.
        IStatusEffect entrave = new EntraveStatus(2, 0.7);
        int initialDamage = 100;

        int modifiedDamage = entrave.modifyDamageDealt(initialDamage);

        assertEquals(70, modifiedDamage, "Les dégâts infligés devraient être réduits à 70% de leur valeur initiale.");
    }

    @Test
    void modifyDamageTaken_shouldBeUnchanged() {
        IStatusEffect entrave = new EntraveStatus(2, 0.7);
        int initialDamage = 100;

        int modifiedDamage = entrave.modifyDamageTaken(initialDamage);

        assertEquals(initialDamage, modifiedDamage, "EntraveStatus ne doit pas affecter les dégâts subis.");
    }

    @Test
    void duration_shouldDecreaseAndEffectShouldEnd() {
        EntraveStatus entrave = new EntraveStatus(2, 0.7);

        assertFalse(entrave.isDone());
        assertEquals(2, entrave.getDuration());

        entrave.updateDuration(); // Fin du tour 1
        assertFalse(entrave.isDone());
        assertEquals(1, entrave.getDuration());

        entrave.updateDuration(); // Fin du tour 2
        assertTrue(entrave.isDone());
        assertEquals(0, entrave.getDuration());
    }

    @Test
    void getName_shouldReturnCorrectName() {
        EntraveStatus entrave = new EntraveStatus(1, 0.7);
        assertEquals("Entrave", entrave.getName());
    }

    @Test
    void onTurnStart_shouldHaveNoEffect() {
        EntraveStatus entrave = new EntraveStatus(1, 0.7);
        TestEntity entity = new TestEntity();
        entrave.onTurnStart(entity);
        // Aucune assertion nécessaire, on vérifie juste que ça ne crashe pas.
    }

    @Test
    void onTurnEnd_shouldHaveNoEffect() {
        EntraveStatus entrave = new EntraveStatus(1, 0.7);
        TestEntity entity = new TestEntity();
        entrave.onTurnEnd(entity);
        // Aucune assertion nécessaire, on vérifie juste que ça ne crashe pas.
    }
}