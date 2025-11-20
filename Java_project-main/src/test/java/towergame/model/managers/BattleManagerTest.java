package towergame.model.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import towergame.model.actions.AAction;
import towergame.model.actions.Element;
import towergame.model.actions.PlayerAttackAction;
import towergame.model.entities.ABoss;
import towergame.model.entities.Player;
import towergame.model.entities.FireElementalBoss;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BattleManagerTest {

    private Player player;
    private ABoss boss;
    private BattleManager battleManager;

    @BeforeEach
    void setUp() {
        player = new Player("Test Player");
        // Using FireElementalBoss for testing, but a mock would be better
        boss = new FireElementalBoss();
        // Désactiver l'invulnérabilité du boss pour les tests
        boss.setInvulnerable(false);
        battleManager = new BattleManager(player, boss);
    }

    @Test
    void testExecuteTurn_PlayerAttacks_BossTakesDamage() {
        // Given
        int initialBossHp = boss.getHp();
        AAction playerAttack = new PlayerAttackAction("Test Attack", Element.NEUTRAL, 0, 10);
        player.setEquippedActions(List.of(playerAttack));

        // When
        battleManager.executeTurn(playerAttack);

        // Then
        assertTrue(boss.getHp() < initialBossHp, "Boss HP should decrease after a player attack.");
        assertEquals(initialBossHp - 10, boss.getHp(), "Boss HP should be reduced by the attack damage.");
    }

    @Test
    void testExecuteTurn_BossIsDefeated_BattleIsOver() {
        // Given
        AAction playerAttack = new PlayerAttackAction("Finishing Blow", Element.NEUTRAL, 0, boss.getMaxHp());
        player.setEquippedActions(List.of(playerAttack));

        // When
        battleManager.executeTurn(playerAttack);

        // Then
        assertFalse(boss.isAlive(), "Boss should not be alive after taking lethal damage.");
        assertTrue(battleManager.isBattleOver(), "Battle should be over when the boss is defeated.");
    }

    @Test
    void testExecuteTurn_TurnNumberIncrements() {
        // Given
        int initialTurnNumber = battleManager.getTurnNumber();
        AAction playerAttack = new PlayerAttackAction("Test Attack", Element.NEUTRAL, 0, 1);
        player.setEquippedActions(List.of(playerAttack));

        // When
        battleManager.executeTurn(playerAttack);

        // Then
        assertEquals(initialTurnNumber + 1, battleManager.getTurnNumber(), "Turn number should increment after a turn.");
    }

    @Test
    void testExecuteTurn_ActionGoesOnCooldown() {
        // Given
        AAction playerAttack = new PlayerAttackAction("Cooldown Attack", Element.NEUTRAL, 2, 10);
        player.setEquippedActions(List.of(playerAttack));
        
        // When
        battleManager.executeTurn(playerAttack);

        // Then
        assertFalse(playerAttack.isReady(), "Action should not be ready after being used.");
        assertEquals(1, playerAttack.getCurrentCooldown(), "Action cooldown should be decremented after turn end effects.");
    }
}
