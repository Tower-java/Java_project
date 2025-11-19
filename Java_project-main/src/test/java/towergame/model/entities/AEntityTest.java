package towergame.model.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import towergame.model.actions.Element;
import towergame.model.entities.AEntity;
import towergame.model.status.BoostStatus;
import towergame.model.status.DefendStatus;
import towergame.model.status.IStatusEffect;
import towergame.model.status.PoisonStatus;

/**
 * Classe de test pour les mécaniques de base de la classe abstraite AEntity.
 */
class AEntityTest {

    // Classe concrète simple pour pouvoir instancier et tester AEntity
    private static class ConcreteEntity extends AEntity {
        public ConcreteEntity(String name, int maxHp) {
            super(name, maxHp, Element.NEUTRAL);
        }
    }

    private ConcreteEntity entity;

    @BeforeEach
    void setUp() {
        entity = new ConcreteEntity("Test Entity", 100);
    }

    @Test
    void heal_shouldNotExceedMaxHp() {
        entity.takeDamage(10); // HP = 90
        assertEquals(90, entity.getHp());

        entity.heal(20); // Tente de soigner de 20, devrait plafonner à 100

        assertEquals(100, entity.getHp(), "Le soin ne doit pas dépasser les PV maximum.");
    }

    @Test
    void takeDamage_shouldReduceHp() {
        entity.takeDamage(30);
        assertEquals(70, entity.getHp(), "L'entité doit perdre 30 PV.");
    }

    @Test
    void takeDamage_shouldNotGoBelowZero() {
        entity.takeDamage(120); // Plus de dégâts que de PV
        assertEquals(0, entity.getHp(), "Les PV ne doivent pas tomber en dessous de 0.");
    }

    @Test
    void updateStatusEffects_shouldRemoveExpiredEffects() {
        // Arrange
        IStatusEffect shortStatus = new PoisonStatus(1, 5); // Durée de 1 tour
        entity.addStatus(shortStatus);
        assertFalse(entity.getActiveStatus().isEmpty(), "Le statut doit être actif avant la mise à jour.");

        // Act
        entity.updateStatusEffects();

        // Assert
        assertTrue(entity.getActiveStatus().isEmpty(), "Le statut avec une durée de 1 doit être supprimé après une mise à jour.");
    }

    @Test
    void updateStatusEffects_shouldDecreaseDurationOfActiveEffects() {
        IStatusEffect longStatus = new PoisonStatus(3, 5);
        entity.addStatus(longStatus);

        entity.updateStatusEffects();

        assertEquals(2, entity.getActiveStatus().get(0).getDuration(), "La durée du statut doit diminuer de 1.");
        assertFalse(entity.getActiveStatus().isEmpty(), "Le statut ne doit pas être supprimé.");
    }

    @Test
    void takeDamage_isModifiedByDefendStatus() {
        IStatusEffect defense = new DefendStatus(2, 10); // Bloque 10 de dégâts
        entity.addStatus(defense);

        entity.takeDamage(25); // Devrait subir 15 dégâts (25 - 10)

        assertEquals(85, entity.getHp(), "Les dégâts subis doivent être réduits par le DefendStatus.");
    }
    
    @Test
    void hasEffect_shouldReturnTrueForActiveEffect() {
        entity.addStatus(new BoostStatus(2, 1.5));
        assertTrue(entity.hasEffect("Boost"), "hasEffect doit retourner true pour un statut actif.");
    }

    @Test
    void hasEffect_shouldReturnFalseForInactiveEffect() {
        assertFalse(entity.hasEffect("Poison"), "hasEffect doit retourner false pour un statut non actif.");
    }
}