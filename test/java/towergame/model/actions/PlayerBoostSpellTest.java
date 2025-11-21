package towergame.model.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import towergame.model.entities.FireElementalBoss;
import towergame.model.entities.Player;

import static org.junit.jupiter.api.Assertions.*;

class PlayerBoostSpellTest {

    private Player player;
    private PlayerBoostSpell boostSpell;
    private FireElementalBoss boss;

    @BeforeEach
    void setUp() {
        player = new Player("Héros");
        boostSpell = new PlayerBoostSpell("Boost", Element.NEUTRAL, 3, 5); // Nom, Élément, Durée du boost, Cooldown
        boss = new FireElementalBoss();
        // On s'assure que le boss est vulnérable pour les tests
        boss.setInvulnerable(false);
    }

    @Test
    void constructor_shouldInitializePropertiesCorrectly() {
        assertEquals("Boost", boostSpell.getName());
        assertEquals(Element.NEUTRAL, boostSpell.getElement());
        assertTrue(boostSpell.isReady(), "Le sort devrait être prêt à la création.");
    }

    @Test
    void execute_shouldIncreasePlayerDamageOnNextAttack() {
        // ARRANGE
        PlayerAttackAction attack = new PlayerAttackAction("Basic Attack", Element.NEUTRAL, 0, 10);

        // ACT 1: Attaque sans boost
        int hpBeforeFirstAttack = boss.getHp();
        attack.execute(player, boss);
        int damageWithoutBoost = hpBeforeFirstAttack - boss.getHp();

        // ARRANGE 2: Réinitialisation du boss et application du boost
        // On réinitialise la vie du boss pour mesurer les dégâts proprement
        boss = new FireElementalBoss();
        boss.setInvulnerable(false);
        boostSpell.execute(player, null); // Le sort s'applique à l'utilisateur

        // ACT 2: Attaque avec boost
        int hpBeforeSecondAttack = boss.getHp();
        attack.execute(player, boss);
        int damageWithBoost = hpBeforeSecondAttack - boss.getHp();

        // ASSERT
        // Cette assertion échouera tant que la logique de boost n'est pas correctement
        // prise en compte par l'action d'attaque.
        assertTrue(damageWithBoost > damageWithoutBoost,
                "Les dégâts avec boost (" + damageWithBoost + ") devraient être supérieurs aux dégâts sans boost (" + damageWithoutBoost + ")");
    }

    @Test
    void execute_shouldStartCooldown() {
        assertTrue(boostSpell.isReady());
        boostSpell.execute(player, null);
        assertFalse(boostSpell.isReady(), "Le sort ne devrait plus être prêt après exécution.");
    }

    @Test
    void execute_shouldDoNothing_whenNotReady() {
        // ARRANGE
        // On exécute une première fois pour mettre le sort en cooldown
        boostSpell.execute(player, null);
        assertFalse(boostSpell.isReady());

        // On applique un boost différent pour vérifier qu'il ne sera pas écrasé
        player.applyDamageBoost(5.0, 1);

        // ACT
        // On tente d'exécuter le sort à nouveau alors qu'il est en cooldown
        boostSpell.execute(player, null);

        // ASSERT
        // Le multiplicateur de dégâts ne doit pas avoir changé
        assertEquals(5.0, player.getDamageMultiplier(), "Le sort ne devrait rien faire s'il est en cooldown.");
    }

    @Test
    void execute_shouldDoNothing_whenUserIsNotPlayer() {
        // ACT
        // On tente d'exécuter le sort avec une entité qui n'est pas un joueur (le boss)
        boostSpell.execute(boss, null);

        // ASSERT
        assertTrue(boostSpell.isReady(), "Le cooldown du sort ne doit pas démarrer si l'utilisateur n'est pas un joueur.");
    }
}