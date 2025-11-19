package towergame.model.status;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import towergame.model.status.IStatusEffect;
import towergame.model.status.WeakenStatus;

/**
 * Classe de test pour WeakenStatus.
 * Note: Le nom de la classe peut prêter à confusion. "Weaken" (Affaiblir) 
 * suggère que la cible subit PLUS de dégâts, mais l'implémentation actuelle
 * avec un multiplicateur < 1.0 fait qu'elle en subit MOINS. 
 * Ces tests valident le comportement tel qu'il est implémenté.
 */
class WeakenStatusTest {

    @Test
    void modifyDamageTaken_shouldReduceIncomingDamage() {
        // Multiplicateur de 0.8, donc 20% de réduction de dégâts.
        IStatusEffect weaken = new WeakenStatus(2, 0.8);
        int initialDamage = 50;

        int modifiedDamage = weaken.modifyDamageTaken(initialDamage);

        assertEquals(40, modifiedDamage, "Les dégâts subis devraient être réduits à 80% de leur valeur initiale.");
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
        IStatusEffect weaken = new WeakenStatus(2, 0.8);
        int initialDamage = 50;

        int modifiedDamage = weaken.modifyDamageDealt(initialDamage);

        assertEquals(initialDamage, modifiedDamage, "WeakenStatus ne doit pas affecter les dégâts infligés.");
    }
}