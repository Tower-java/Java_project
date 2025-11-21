package towergame.model.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import towergame.model.actions.AAction;
import towergame.model.actions.Element;
import towergame.model.actions.PlayerAttackAction;
import towergame.model.entities.ABoss;
import towergame.model.entities.Player;
import towergame.model.status.PoisonStatus;
import towergame.model.entities.FireElementalBoss;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BattleManagerTest {

    private Player player;
    private ABoss boss;
    private FireElementalBoss fireBoss; // Gardons une instance spécifique pour les tests de gimmick
    private BattleManager battleManager;

    /**
     * Un boss simple et contrôlable pour les tests unitaires.
     */
    private static class ControllableBoss extends ABoss {
        public ControllableBoss(List<AAction> script) {
            super("Controllable Boss", 100, Element.NEUTRAL, 10, 10);
            setActionScript(script);
        }
    }

    @BeforeEach
    void setUp() {
        player = new Player("Test Player");
        // Using FireElementalBoss for testing, but a mock would be better
        boss = new FireElementalBoss();
        // IMPORTANT: On rend le boss vulnérable pour que les tests de dégâts fonctionnent.
        boss.setInvulnerable(false);
        fireBoss = (FireElementalBoss) boss;
        battleManager = new BattleManager(player, fireBoss);
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
        assertEquals(2, playerAttack.getCurrentCooldown(), "Action should be on cooldown.");
    }

    @Test
    void testExecuteTurn_PlayerTakesDamageFromBoss() {
        // Given
        int initialPlayerHp = player.getHp();
        AAction playerAttack = new PlayerAttackAction("Test Attack", Element.NEUTRAL, 0, 1);
        player.setEquippedActions(List.of(playerAttack));

        // When
        battleManager.executeTurn(playerAttack);

        // Then
        // On vérifie que le joueur a bien subi des dégâts en retour.
        assertTrue(player.getHp() < initialPlayerHp, "Player HP should decrease after the boss's counter-attack.");
    }

    @Test
    void testExecuteTurn_PlayerIsDefeated_BattleIsOver() {
        // Given
        // On crée un boss qui peut tuer le joueur en un coup
        AAction powerfulAttack = new towergame.model.actions.BossAttackAction("Obliterate", Element.NEUTRAL, 0, 10.0); // 10 * 10 = 100 damage
        ABoss oneShotBoss = new ControllableBoss(List.of(powerfulAttack));
        battleManager = new BattleManager(player, oneShotBoss);

        AAction playerAttack = new PlayerAttackAction("Weak Hit", Element.NEUTRAL, 0, 1);
        player.setEquippedActions(List.of(playerAttack));

        // When
        battleManager.executeTurn(playerAttack);

        // Then
        assertFalse(player.isAlive(), "Player should not be alive after taking lethal damage.");
        assertTrue(battleManager.isBattleOver(), "Battle should be over when the player is defeated.");
    }

    @Test
    void testExecuteTurn_UpdatesStatusEffectsOnBothEntities() {
        // Given
        player.addStatus(new PoisonStatus(2, 5));
        boss.addStatus(new PoisonStatus(3, 5));
        AAction playerAttack = new PlayerAttackAction("Test Attack", Element.NEUTRAL, 0, 1);
        player.setEquippedActions(List.of(playerAttack));

        // When
        battleManager.executeTurn(playerAttack);

        // Then
        assertEquals(1, player.getActiveStatus().get(0).getDuration(), "Player's status duration should decrease.");
        assertEquals(2, boss.getActiveStatus().get(0).getDuration(), "Boss's status duration should decrease.");
    }

    @Test
    void testExecuteTurn_BossActionGoesOnCooldown() {
        // Given
        // L'action du tour 3 du FireElementalBoss est une défense avec un cooldown de 2 tours.
        AAction playerAttack = new PlayerAttackAction("Test Attack", Element.NEUTRAL, 0, 1);
        player.setEquippedActions(List.of(playerAttack));
        battleManager.executeTurn(playerAttack); // Tour 1
        battleManager.executeTurn(playerAttack); // Tour 2

        AAction bossAction = fireBoss.getActionScript().get(2); // L'action qui sera utilisée au tour 3
        assertTrue(bossAction.isReady(), "Boss action should be ready before its turn.");

        // When
        battleManager.executeTurn(playerAttack); // Tour 3, le boss utilise sa défense

        // Then
        assertFalse(bossAction.isReady(), "Boss action should be on cooldown after being used.");
    }

    @Test
    void testExecuteTurn_AddsBothActionsToHistory() {
        AAction playerAttack = new PlayerAttackAction("Test Attack", Element.NEUTRAL, 0, 1);
        player.setEquippedActions(List.of(playerAttack));
        battleManager.executeTurn(playerAttack);
        assertEquals(2, battleManager.getActionsUsedHistory().size(), "History should contain both player's and boss's actions.");
    }

    @Test
    void testGetters_shouldReturnCorrectInstances() {
        // This test covers the simple getters of the class.
        assertEquals(player, battleManager.getPlayer(), "getPlayer() should return the correct player instance.");
        assertEquals(fireBoss, battleManager.getBoss(), "getBoss() should return the correct boss instance.");
    }

    @Test
    void testExecuteTurn_BossSkipsTurn_ifActionNotReady() {
        // Given
        // On crée un boss avec une seule action qui a un cooldown
        AAction bossAttack = new towergame.model.actions.BossAttackAction("Heavy Slam", Element.NEUTRAL, 1, 10.0);
        ABoss cooldownBoss = new ControllableBoss(List.of(bossAttack));
        battleManager = new BattleManager(player, cooldownBoss);

        AAction playerAttack = new PlayerAttackAction("Poke", Element.NEUTRAL, 0, 1);
        player.setEquippedActions(List.of(playerAttack));

        battleManager.executeTurn(playerAttack); // Tour 1: le boss attaque, son action entre en cooldown.
        int playerHpAfterFirstTurn = player.getHp();

        // When: Tour 2, l'action du boss est en cooldown
        battleManager.executeTurn(playerAttack);

        // Then: Le joueur ne doit pas avoir subi de dégâts supplémentaires
        assertEquals(playerHpAfterFirstTurn, player.getHp(), "Player HP should not change if the boss's action is on cooldown.");
    }

    @Test
    void testExecuteTurn_BothDieSimultaneously_BattleIsOverAndPlayerIsDead() {
        // Given
        // Le joueur a 5 PV et est empoisonné pour 5 dégâts.
        player.takeDamage(95);
        player.addStatus(new PoisonStatus(1, 5));

        // L'action du joueur va tuer le boss.
        AAction finishingBlow = new PlayerAttackAction("Last Gasp", Element.NEUTRAL, 0, boss.getHp());
        player.setEquippedActions(List.of(finishingBlow));

        // When
        battleManager.executeTurn(finishingBlow);

        // Then
        assertTrue(battleManager.isBattleOver(), "La bataille doit être terminée.");
        assertFalse(player.isAlive(), "Le joueur doit être mort à cause du poison à la fin du tour.");
    }
}
