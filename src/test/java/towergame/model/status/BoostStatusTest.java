package towergame.model.status;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import towergame.model.status.BoostStatus;

// Supposition : 'StatusEffect' est une classe parente ou interface pour BoostStatus. 

/**
 * Classe de test pour BoostStatus.
 * Vérifie la modification des dégâts et la gestion de la durée.
 */
class BoostStatusTest {

    @Test
    void modifyDamageDealt_shouldReturnBoostedValue_forPositiveBoost() {
        System.out.println("\n--- Début du test : modifyDamageDealt_shouldReturnBoostedValue_forPositiveBoost ---");
        // Statut qui augmente les dégâts de 50%
        BoostStatus boost = new BoostStatus(3, 1.5);
        int baseDamage = 100;
        
        System.out.println("Dégâts de base : " + baseDamage);
        System.out.println("Multiplicateur du boost : 1.5");

        int modifiedDamage = boost.modifyDamageDealt(baseDamage);

        System.out.println("Dégâts modifiés (valeur réelle) : " + modifiedDamage);
        System.out.println("Dégâts modifiés (valeur attendue) : 150");

        assertEquals(150, modifiedDamage, "Les dégâts doivent être augmentés de 50% (100 -> 150).");
        System.out.println("--- Fin du test ---");
    }

    @Test
    void modifyDamageDealt_shouldReturnReducedValue_forNegativeBoost() {
        // Statut qui réduit les dégâts de 25% (multiplicateur de 0.75)
        BoostStatus debuff = new BoostStatus(3, 0.75);
        int baseDamage = 100;

        int modifiedDamage = debuff.modifyDamageDealt(baseDamage);

        assertEquals(75, modifiedDamage, "Les dégâts doivent être réduits de 25% (100 -> 75).");
    }
    
    @Test
    void modifyDamageDealt_shouldHandleZeroDamage() {
        BoostStatus boost = new BoostStatus(3, 1.5);
        int baseDamage = 0;

        int modifiedDamage = boost.modifyDamageDealt(baseDamage);

        assertEquals(0, modifiedDamage, "0 dégât multiplié par n'importe quoi doit rester 0.");
    }

    @Test
    void modifyDamageTaken_shouldReturnUnalteredDamage() {
        BoostStatus boost = new BoostStatus(3, 1.5);
        int incomingDamage = 100;

        int modifiedDamage = boost.modifyDamageTaken(incomingDamage);

        assertEquals(100, modifiedDamage, "BoostStatus ne doit pas modifier les dégâts subis.");
    }

    @Test
    void duration_shouldDecrease_andEffectShouldEnd() {
        BoostStatus boost = new BoostStatus(2, 1.5); // Durée de 2 tours

        assertFalse(boost.isDone(), "L'effet ne doit pas être terminé au début.");
        assertEquals(2, boost.getDuration(), "La durée initiale doit être de 2.");

        boost.updateDuration(); // Fin du tour 1
        assertFalse(boost.isDone(), "L'effet ne doit pas être terminé après 1 tour.");
        assertEquals(1, boost.getDuration(), "La durée doit être de 1 après 1 tour.");

        boost.updateDuration(); // Fin du tour 2
        assertTrue(boost.isDone(), "L'effet DOIT être terminé après 2 tours.");
        assertEquals(0, boost.getDuration(), "La durée doit être de 0 à la fin.");
        
        boost.updateDuration(); // Tour supplémentaire
        assertTrue(boost.isDone(), "L'effet doit rester terminé.");
        assertEquals(-1, boost.getDuration(), "La durée peut devenir négative, mais l'effet reste terminé.");
    }
}