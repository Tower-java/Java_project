package towergame.model.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import towergame.model.entities.AEntity;
import towergame.model.entities.FireElementalBoss;

import static org.junit.jupiter.api.Assertions.*;

class BossAttackActionTest {

    // Classe "stub" pour une cible
    private static class TestTarget extends AEntity {
        public TestTarget(String name, int maxHp, Element element) {
            super(name, maxHp, element);
        }
    }

    // Classe "stub" pour une entité qui n'est pas un boss
    private static class NonBossEntity extends AEntity {
        public NonBossEntity(String name, int maxHp) {
            super(name, maxHp, Element.NEUTRAL);
        }
    }

    private FireElementalBoss fireBoss;
    private TestTarget plantTarget;
    private TestTarget waterTarget;

    @BeforeEach
    void setUp() {
        fireBoss = new FireElementalBoss(); // Utilise le vrai boss
        plantTarget = new TestTarget("Plant Target", 200, Element.PLANT);
        waterTarget = new TestTarget("Water Target", 200, Element.WATER);
    }

    @Test
    void execute_shouldDamageTarget_whenUserIsBoss() {
        // Arrange
        BossAttackAction attack = new BossAttackAction("Griffe", Element.NEUTRAL, 0, 1.0);
        int initialHp = plantTarget.getHp();
        int expectedDamage = (int) (fireBoss.getAttackPoints() * 1.0);

        // Act
        attack.execute(fireBoss, plantTarget);

        // Assert
        assertEquals(initialHp - expectedDamage, plantTarget.getHp(), "La cible devrait subir les dégâts de base de l'attaque.");
    }

    @Test
    void execute_shouldApplyMultiplierToDamage() {
        // Arrange
        BossAttackAction strongAttack = new BossAttackAction("Grosse Griffe", Element.NEUTRAL, 0, 2.5);
        int initialHp = plantTarget.getHp();
        int expectedDamage = (int) (fireBoss.getAttackPoints() * 2.5);

        // Act
        strongAttack.execute(fireBoss, plantTarget);

        // Assert
        assertEquals(initialHp - expectedDamage, plantTarget.getHp(), "Le multiplicateur de l'action doit être appliqué aux dégâts.");
    }

    @Test
    void execute_shouldApplyElementalAdvantage() {
        // Arrange (Feu vs Plante -> x2)
        BossAttackAction fireAttack = new BossAttackAction("Souffle de Feu", Element.FIRE, 0, 1.0);
        int initialHp = plantTarget.getHp();
        int expectedDamage = (int) (fireBoss.getAttackPoints() * 1.0 * 2.0);

        // Act
        fireAttack.execute(fireBoss, plantTarget);

        // Assert
        assertEquals(initialHp - expectedDamage, plantTarget.getHp(), "Les dégâts devraient être doublés à cause de l'avantage élémentaire.");
    }

    @Test
    void execute_shouldApplyElementalDisadvantage() {
        // Arrange (Feu vs Eau -> x0.5)
        BossAttackAction fireAttack = new BossAttackAction("Souffle de Feu", Element.FIRE, 0, 1.0);
        int initialHp = waterTarget.getHp();
        int expectedDamage = (int) (fireBoss.getAttackPoints() * 1.0 * 0.5);

        // Act
        fireAttack.execute(fireBoss, waterTarget);

        // Assert
        assertEquals(initialHp - expectedDamage, waterTarget.getHp(), "Les dégâts devraient être réduits de moitié à cause du désavantage élémentaire.");
    }

    @Test
    void execute_shouldDoNothing_whenUserIsNotABoss() {
        // Arrange
        NonBossEntity nonBoss = new NonBossEntity("Héros", 100);
        BossAttackAction attack = new BossAttackAction("Attaque illégale", 0, 1.0);
        int initialTargetHp = plantTarget.getHp();

        // Act
        attack.execute(nonBoss, plantTarget);

        // Assert
        assertEquals(initialTargetHp, plantTarget.getHp(), "Les PV de la cible ne doivent pas changer si l'utilisateur n'est pas un boss.");
        assertTrue(attack.isReady(), "Le cooldown ne doit pas démarrer si l'action échoue.");
    }
}