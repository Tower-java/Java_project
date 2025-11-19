package towergame.model.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import towergame.model.entities.AEntity;
import towergame.model.entities.FireElementalBoss;

import static org.junit.jupiter.api.Assertions.*;

class BossHealActionTest {

    // Une classe "stub" pour une entité qui n'est pas un Boss.
    // Utile pour tester le cas où un non-boss essaie d'utiliser l'action.
    private static class NonBossEntity extends AEntity {
        public NonBossEntity(String name, int maxHp) {
            super(name, maxHp, Element.NEUTRAL);
        }
    }

    private FireElementalBoss boss;

    @BeforeEach
    void setUp() {
        // Initialisation d'un vrai boss pour chaque test
        boss = new FireElementalBoss();
    }

    @Test
    void execute_shouldHealBoss_whenUserIsBoss() {
        // Arrange: Action avec un multiplicateur de 1.5
        boss.setInvulnerable(false); // On désactive l'invulnérabilité pour le test
        boss.takeDamage(50); // On s'assure que le boss n'a pas tous ses PV (HP = 50)
        int initialHp = boss.getHp();
        int baseHeal = boss.getHealPoints();
        BossHealAction healAction = new BossHealAction("Soin Puissant", Element.NEUTRAL, 3, 1.5);
        int expectedHeal = (int) (baseHeal * 1.5);

        // Act: Le boss utilise l'action sur lui-même
        healAction.execute(boss, boss);

        // Assert: Les PV du boss ont augmenté correctement
        assertEquals(initialHp + expectedHeal, boss.getHp(), "Le boss devrait récupérer le bon montant de PV.");
    }

    @Test
    void execute_shouldStartCooldownAfterUse() {
        // Arrange: Action avec un cooldown de 3 tours
        // On s'assure que le boss n'est pas full vie pour que le soin ait un effet visible
        // même si ce test ne vérifie que le cooldown.
        boss.setInvulnerable(false);
        boss.takeDamage(50); 
        BossHealAction healAction = new BossHealAction("Soin", Element.NEUTRAL, 3, 1.0);

        // Act: Le boss utilise l'action
        healAction.execute(boss, boss);

        // Assert: Le cooldown est bien démarré
        assertEquals(3, healAction.getCurrentCooldown(), "Le cooldown devrait être de 3 après utilisation.");
        assertFalse(healAction.isReady(), "L'action ne devrait plus être prête.");
    }

    @Test
    void execute_shouldNotHeal_whenUserIsNotABoss() {
        // Arrange: Une entité non-boss et une action de soin
        NonBossEntity nonBoss = new NonBossEntity("Héros", 100);
        nonBoss.takeDamage(20); // PV actuels: 80
        BossHealAction healAction = new BossHealAction("Soin", Element.NEUTRAL, 3, 1.0);

        // Act: L'entité non-boss tente d'utiliser l'action
        healAction.execute(nonBoss, nonBoss);

        // Assert: Les PV n'ont pas changé et le cooldown n'a pas démarré
        assertEquals(80, nonBoss.getHp(), "Une entité non-boss ne doit pas pouvoir se soigner avec cette action.");
        assertEquals(0, healAction.getCurrentCooldown(), "Le cooldown ne doit pas démarrer si l'action échoue.");
    }

    @Test
    void execute_shouldNotHealBeyondMaxHp() {
        // Arrange: Le boss a presque tous ses PV.
        // On lui enlève 10 PV pour s'assurer qu'il n'est pas au max.
        boss.setInvulnerable(false);
        boss.takeDamage(10); // HP = 90
        int maxHp = boss.getMaxHp();
        BossHealAction healAction = new BossHealAction("Gros Soin", Element.NEUTRAL, 4, 1.0); // Devrait tenter de soigner plus que les 10 PV manquants

        // Act: Le boss utilise le soin
        healAction.execute(boss, boss);

        // Assert: Les PV sont plafonnés au maximum (100)
        assertEquals(maxHp, boss.getHp(), "La guérison ne doit pas dépasser les PV maximum.");
    }

    @Test
    void execute_withZeroMultiplier_shouldHealNothing() {
        // Arrange: Action avec un multiplicateur de 0
        boss.setInvulnerable(false);
        boss.takeDamage(50); // On s'assure que le boss peut être soigné (HP = 50)
        BossHealAction healAction = new BossHealAction("Soin Nul", Element.NEUTRAL, 1, 0.0);
        int initialHp = boss.getHp();

        // Act: Le boss utilise l'action
        healAction.execute(boss, boss);

        // Assert: Les PV n'ont pas changé
        assertEquals(initialHp, boss.getHp(), "Avec un multiplicateur de 0, aucun soin ne doit être appliqué.");
    }

    @Test
    void updateCooldown_shouldDecrementCooldown_andStopAtZero() {
        // Arrange: Une action avec un cooldown de 3 tours
        BossHealAction healAction = new BossHealAction("Soin", Element.NEUTRAL, 3, 1.0);
        healAction.startCooldown(); // Cooldown démarre à 3

        // Assert initial state
        assertEquals(3, healAction.getCurrentCooldown(), "Le cooldown initial doit être de 3.");

        // Act & Assert: Simuler le passage des tours
        healAction.updateCooldown(); // Tour 1
        assertEquals(2, healAction.getCurrentCooldown(), "Le cooldown doit être de 2 après 1 tour.");

        healAction.updateCooldown(); // Tour 2
        assertEquals(1, healAction.getCurrentCooldown(), "Le cooldown doit être de 1 après 2 tours.");

        healAction.updateCooldown(); // Tour 3
        assertEquals(0, healAction.getCurrentCooldown(), "Le cooldown doit être de 0 après 3 tours.");
        assertTrue(healAction.isReady(), "L'action doit être prête quand le cooldown est à 0.");

        healAction.updateCooldown(); // Tour 4 (extra)
        assertEquals(0, healAction.getCurrentCooldown(), "Le cooldown ne doit pas devenir négatif.");
    }
}
