package towergame.model.status;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import towergame.model.status.DefendStatus;
import towergame.model.status.IStatusEffect;

/**
 * Classe de test pour DefendStatus.
 */
class DefendStatusTest {

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
}