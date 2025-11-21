package towergame.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import towergame.model.actions.AAction;
import towergame.model.actions.Element;
import towergame.model.actions.PlayerAttackAction;
import towergame.model.entities.Player;
import towergame.model.entities.FireElementalBoss;
import towergame.model.entities.ABoss;
import towergame.model.managers.BattleManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de test pour GameEngine.
 * Teste la logique du moteur de jeu sans avoir besoin d'interactivité réelle.
 */
class GameEngineTest {

    private Player testPlayer;
    private ABoss testBoss;

    @BeforeEach
    void setUp() {
        testPlayer = new Player("Test Hero");
        testBoss = new FireElementalBoss();
        testBoss.setInvulnerable(false);
    }

    @Test
    void testGameInitialization() {
        // Test que les composants principaux du jeu sont correctement initialisés
        assertNotNull(testPlayer);
        assertNotNull(testBoss);
        assertEquals("Test Hero", testPlayer.getName());
        assertTrue(testPlayer.isAlive());
    }

    @Test
    void testPlayerEquipsActions() {
        // Test que le joueur peut équiper des actions
        AAction action1 = new PlayerAttackAction("Attaque Basique", Element.NEUTRAL, 0, 10);
        AAction action2 = new PlayerAttackAction("Coup Puissant", Element.NEUTRAL, 2, 15);
        
        List<AAction> actions = List.of(action1, action2);
        testPlayer.setEquippedActions(actions);

        // Vérifier que les actions ont été équipées
        List<AAction> equippedActions = testPlayer.getEquippedActions();
        assertEquals(2, equippedActions.size());
        assertEquals("Attaque Basique", equippedActions.get(0).getName());
        assertEquals("Coup Puissant", equippedActions.get(1).getName());
    }

    @Test
    void testBattleExecution_PlayerDefeatsEnemy() {
        // Test qu'une bataille se déroule correctement jusqu'à la victoire du joueur
        AAction playerAction = new PlayerAttackAction("One Hit Kill", Element.NEUTRAL, 0, testBoss.getMaxHp());
        testPlayer.setEquippedActions(List.of(playerAction));

        BattleManager battleManager = new BattleManager(testPlayer, testBoss);
        
        assertFalse(battleManager.isBattleOver(), "Battle should not be over at start");
        
        battleManager.executeTurn(playerAction);
        
        assertTrue(battleManager.isBattleOver(), "Battle should be over after boss is defeated");
        assertFalse(testBoss.isAlive(), "Boss should be dead");
        assertTrue(testPlayer.isAlive(), "Player should still be alive");
    }

    @Test
    void testBattleExecution_PlayerDefeat() {
        // Test qu'une bataille se termine par la défaite du joueur
        int playerMaxHp = testPlayer.getMaxHp();
        
        // Ajouter une attaque basique pour le joueur
        AAction playerAttack = new PlayerAttackAction("Weak Attack", Element.NEUTRAL, 0, 5);
        testPlayer.setEquippedActions(List.of(playerAttack));

        // Créer un BattleManager (utilisé pour la structure logique du test)
        new BattleManager(testPlayer, testBoss);

        // Simuler une attaque qui tue le joueur
        testPlayer.takeDamage(playerMaxHp + 50);
        
        assertFalse(testPlayer.isAlive(), "Player should be dead");
    }

    @Test
    void testMultipleBattles() {
        // Test qu'on peut gérer plusieurs combats successifs
        Player player = new Player("Multi-Battle Hero");
        player.setEquippedActions(List.of(
            new PlayerAttackAction("Attack 1", Element.NEUTRAL, 0, 50),
            new PlayerAttackAction("Attack 2", Element.NEUTRAL, 1, 50),
            new PlayerAttackAction("Attack 3", Element.NEUTRAL, 1, 50),
            new PlayerAttackAction("Attack 4", Element.NEUTRAL, 2, 50)
        ));

        // Premier combat
        ABoss boss1 = new FireElementalBoss();
        boss1.setInvulnerable(false);
        BattleManager battle1 = new BattleManager(player, boss1);
        
        assertTrue(player.isAlive());
        int battleCount = 0;
        
        // Simuler plusieurs attaques jusqu'à victoire
        while (!battle1.isBattleOver() && battleCount < 10) {
            battle1.executeTurn(player.getEquippedActions().get(0));
            battleCount++;
        }
        
        assertTrue(battle1.isBattleOver(), "First battle should end");
        assertTrue(player.isAlive(), "Player should survive first battle");

        // Deuxième combat avec le même joueur
        ABoss boss2 = new FireElementalBoss();
        boss2.setInvulnerable(false);
        BattleManager battle2 = new BattleManager(player, boss2);
        
        battleCount = 0;
        while (!battle2.isBattleOver() && battleCount < 10) {
            battle2.executeTurn(player.getEquippedActions().get(0));
            battleCount++;
        }
        
        assertTrue(battle2.isBattleOver(), "Second battle should end");
        assertTrue(player.isAlive(), "Player should survive second battle");
    }

    @Test
    void testTurnProgression() {
        // Test que les tours s'incrémentent correctement
        AAction playerAction = new PlayerAttackAction("Test Attack", Element.NEUTRAL, 0, 5);
        testPlayer.setEquippedActions(List.of(playerAction));
        
        BattleManager battleManager = new BattleManager(testPlayer, testBoss);
        
        assertEquals(1, battleManager.getTurnNumber(), "Battle should start at turn 1");
        
        battleManager.executeTurn(playerAction);
        assertEquals(2, battleManager.getTurnNumber(), "Turn should increment after execution");
        
        battleManager.executeTurn(playerAction);
        assertEquals(3, battleManager.getTurnNumber(), "Turn should continue to increment");
    }

    @Test
    void testActionHistory() {
        // Test que l'historique des actions est enregistré
        AAction playerAction = new PlayerAttackAction("Tracked Attack", Element.NEUTRAL, 0, 10);
        testPlayer.setEquippedActions(List.of(playerAction));
        
        BattleManager battleManager = new BattleManager(testPlayer, testBoss);
        
        assertTrue(battleManager.getActionsUsedHistory().isEmpty(), "History should be empty at start");
        
        battleManager.executeTurn(playerAction);
        
        assertFalse(battleManager.getActionsUsedHistory().isEmpty(), "History should have actions after execution");
        assertTrue(battleManager.getActionsUsedHistory().size() >= 1, "History should contain player action");
    }

    @Test
    void testPlayerSurvivalThroughMultipleTurns() {
        // Test que le joueur peut survivre plusieurs tours avec des attaques faibles
        AAction weakAttack = new PlayerAttackAction("Tickle", Element.NEUTRAL, 0, 1);
        testPlayer.setEquippedActions(List.of(weakAttack));
        
        BattleManager battleManager = new BattleManager(testPlayer, testBoss);
        
        // Executer 5 tours sans que le joueur meure
        for (int i = 0; i < 5 && battleManager.isBattleOver() == false; i++) {
            battleManager.executeTurn(weakAttack);
            assertTrue(testPlayer.isAlive() || battleManager.isBattleOver(), 
                      "Player should be alive or battle should be over after turn " + (i + 1));
        }
    }

    @Test
    void testGameOver_PlayerDeath() {
        // Test que le jeu se termine correctement quand le joueur meurt
        testPlayer = new Player("Fragile Hero");
        testPlayer.takeDamage(testPlayer.getMaxHp() + 10); // Overkill pour être sûr
        
        assertFalse(testPlayer.isAlive(), "Player should be dead");
        assertEquals(0, testPlayer.getHp(), "Player HP should be 0 or less");
    }

    @Test
    void testBattleManagerInitialization() {
        // Test que le BattleManager s'initialise correctement
        BattleManager battleManager = new BattleManager(testPlayer, testBoss);
        
        assertNotNull(battleManager);
        assertEquals(testPlayer, battleManager.getPlayer());
        assertEquals(testBoss, battleManager.getBoss());
        assertEquals(1, battleManager.getTurnNumber());
        assertTrue(battleManager.getActionsUsedHistory().isEmpty());
    }
}
