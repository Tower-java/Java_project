package towergame.model.status;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import towergame.model.actions.Element;
import towergame.model.entities.AEntity;

import towergame.model.status.DefendStatus;
import towergame.model.status.IStatusEffect;

/**
 * Classe de test pour DefendStatus.
 */
class DefendStatusTest {

    // Classe "stub" pour pouvoir instancier AEntity pour les tests onTurnStart/End
    private static class TestEntity extends AEntity {
        public TestEntity() {
            super("Test Dummy", 100, Element.NEUTRAL);
        }
    }

    @Test
    void modifyDamageTaken_shouldReduceDamageByFlatAmount() {
        // Bloque 10 points de dégâts
        IStatusEffect defend = new DefendStatus(1, 10);
        int initialDamage = 25;

        int modifiedDamage = defend.modifyDamageTaken(initialDamage);

        assertEquals(15, modifiedDamage, "Les dégâts doivent être réduits d'un montant fixe.");
    }

    @Test
    void modifyDamageTaken_shouldNotResultInNegativeDamage() {
        // Bloque 30 points de dégâts
        IStatusEffect defend = new DefendStatus(1, 30);
        int initialDamage = 20; // Moins que le montant bloqué

        int modifiedDamage = defend.modifyDamageTaken(initialDamage);

        assertEquals(0, modifiedDamage, "Les dégâts finaux ne doivent pas être négatifs, mais 0.");
    }

    @Test
    void modifyDamageTaken_shouldBlockAllDamage_ifDamageEqualsBlock() {
        IStatusEffect defend = new DefendStatus(1, 20);
        int initialDamage = 20;

        int modifiedDamage = defend.modifyDamageTaken(initialDamage);

        assertEquals(0, modifiedDamage, "Les dégâts doivent être entièrement bloqués.");
    }

    @Test
    void modifyDamageDealt_shouldBeUnchanged() {
        IStatusEffect defend = new DefendStatus(1, 10);
        int initialDamage = 20;

        int modifiedDamage = defend.modifyDamageDealt(initialDamage);

        assertEquals(initialDamage, modifiedDamage, "DefendStatus ne doit pas affecter les dégâts infligés.");
    }

    @Test
    void duration_shouldDecreaseAndEffectShouldEnd() {
        DefendStatus defend = new DefendStatus(2, 10);

        assertFalse(defend.isDone(), "L'effet ne doit pas être terminé au début.");
        assertEquals(2, defend.getDuration(), "La durée initiale doit être de 2.");

        defend.updateDuration(); // Fin du tour 1
        assertFalse(defend.isDone(), "L'effet ne doit pas être terminé après 1 tour.");
        assertEquals(1, defend.getDuration(), "La durée doit être de 1 après 1 tour.");

        defend.updateDuration(); // Fin du tour 2
        assertTrue(defend.isDone(), "L'effet doit être terminé après 2 tours.");
        assertEquals(0, defend.getDuration(), "La durée doit être de 0 à la fin.");
    }

    @Test
    void getName_shouldReturnCorrectName() {
        DefendStatus defend = new DefendStatus(1, 10);
        assertEquals("Defend", defend.getName(), "Le nom du statut doit être 'Defend'.");
    }

    @Test
    void getDamageBlockAmount_shouldReturnCorrectValue() {
        DefendStatus defend = new DefendStatus(1, 25);
        assertEquals(25, defend.getDamageBlockAmount(), "Le getter doit retourner la valeur de blocage correcte.");
    }

    @Test
    void onTurnStart_shouldHaveNoEffect() {
        DefendStatus defend = new DefendStatus(1, 10);
        TestEntity entity = new TestEntity();
        int initialHp = entity.getHp();

        defend.onTurnStart(entity);

        assertEquals(initialHp, entity.getHp(), "onTurnStart ne doit pas modifier les PV de l'entité.");
    }

    @Test
    void onTurnEnd_shouldHaveNoEffect() {
        DefendStatus defend = new DefendStatus(1, 10);
        TestEntity entity = new TestEntity();
        int initialHp = entity.getHp();

        defend.onTurnEnd(entity);

        assertEquals(initialHp, entity.getHp(), "onTurnEnd ne doit pas modifier les PV de l'entité.");
    }
}