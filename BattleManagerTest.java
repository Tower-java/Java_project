import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BattleManagerTest {

    private Player player;
    private FireElementalBoss boss;
    private BattleManager battleManager;

    // Action factice pour les tests
    private static class TestAction extends AAction {
        private int damage;
        public TestAction(Element element, int damage) {
            super("Test Action", element, 0);
            this.damage = damage;
        }
        public void execute(AEntity u, AEntity t) { t.takeDamage(damage); }
    }

    @BeforeEach
    void setUp() {
        player = new Player("Test Player");
        boss = new FireElementalBoss(); // Commence invulnérable
        battleManager = new BattleManager(player, boss);
    }

    @Test
    void testExecuteTurn_PlayerAttacks_BossTakesNoDamageWhenInvulnerable() {
        int bossHpBefore = boss.getHp();
        AAction playerAction = new TestAction(Element.NEUTRAL, 20);

        battleManager.executeTurn(playerAction);

        assertEquals(bossHpBefore, boss.getHp(), "Le boss invulnérable ne doit pas perdre de PV.");
        assertEquals(2, battleManager.getTurnNumber(), "Le numéro du tour doit passer à 2.");
    }

    @Test
    void testExecuteTurn_PlayerBreaksGimmick_BossTakesDamageNextTurn() {
        // Tour 1: Le joueur utilise une attaque FEU au mauvais tour
        battleManager.executeTurn(new TestAction(Element.FEU, 10));
        assertTrue(boss.isInvulnerable(), "Le boss doit rester invulnérable au tour 1.");

        // Tour 2: Le joueur attaque normalement
        battleManager.executeTurn(new TestAction(Element.NEUTRAL, 10));
        assertTrue(boss.isInvulnerable(), "Le boss doit rester invulnérable au tour 2.");

        // Tour 3: Le joueur utilise une attaque FEU au bon tour
        battleManager.executeTurn(new TestAction(Element.FEU, 10));
        assertFalse(boss.isInvulnerable(), "Le boss aurait dû perdre son invulnérabilité au tour 3.");
    }

    @Test
    void testBattleIsOver_WhenBossDies() {
        // Arrange
        boss.isInvulnerable = false; // On rend le boss vulnérable
        AAction lethalAction = new TestAction(Element.NEUTRAL, boss.getMaxHp());

        // Act
        battleManager.executeTurn(lethalAction);

        // Assert
        assertFalse(boss.isAlive(), "Le boss ne doit plus être en vie.");
        assertTrue(battleManager.isBattleOver(), "La bataille doit être terminée quand le boss est mort.");
    }

    @Test
    void testActionCooldown_IsUpdated() {
        // Arrange
        AAction actionWithCooldown = new PlayerAttackAction("Action Lente", Element.NEUTRAL, 2, 10);
        player.setEquippedActions(List.of(actionWithCooldown));

        // Act
        // Tour 1: Le joueur utilise l'action
        battleManager.executeTurn(actionWithCooldown);

        // Assert
        assertEquals(2, actionWithCooldown.currentCooldown, "Le cooldown doit être de 2 juste après l'utilisation.");
        assertFalse(actionWithCooldown.isReady(), "L'action ne doit pas être prête.");

        // Tour 2: Le joueur passe son tour (on utilise une autre action pour faire avancer le tour)
        battleManager.executeTurn(new TestAction(Element.NEUTRAL, 0));
        assertEquals(1, actionWithCooldown.currentCooldown, "Le cooldown doit être réduit à 1 après un tour.");
    }
}