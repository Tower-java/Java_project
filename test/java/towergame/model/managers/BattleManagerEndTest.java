package towergame.model.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import towergame.model.entities.ABoss;
import towergame.model.entities.FireElementalBoss;
import towergame.model.entities.Player;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de test dédiée à la vérification des conditions de fin de combat
 * gérées par le BattleManager.
 */
class BattleManagerEndTest {

    private Player player;
    private ABoss boss;
    private BattleManager battleManager;

    @BeforeEach
    void setUp() {
        player = new Player("Héros Test");
        boss = new FireElementalBoss();
        // IMPORTANT : On désactive l'invulnérabilité pour les tests de fin de combat.
        boss.setInvulnerable(false);
        battleManager = new BattleManager(player, boss);
    }

    @Test
    void testBattleDoesNotEnd_WhenBothAreAlive() {
        // Arrange: Les deux entités sont vivantes au début.
        assertTrue(player.isAlive());
        assertTrue(boss.isAlive());

        // Act & Assert: La bataille ne doit pas être terminée.
        assertFalse(battleManager.isBattleOver(), "La bataille ne devrait pas être terminée si les deux combattants sont en vie.");
    }

    @Test
    void testBattleEnds_WhenBossIsDefeated() {
        // Arrange: On inflige des dégâts mortels au boss.
        boss.takeDamage(boss.getMaxHp() + 10); // Overkill pour être sûr

        // Act & Assert: La bataille doit être terminée.
        assertTrue(battleManager.isBattleOver(), "La bataille devrait être terminée car le boss est vaincu.");
        assertFalse(boss.isAlive(), "Le boss ne devrait plus être en vie.");
        assertTrue(player.isAlive(), "Le joueur devrait toujours être en vie.");
    }

    @Test
    void testBattleEnds_WhenPlayerIsDefeated() {
        // Arrange: On inflige des dégâts mortels au joueur.
        player.takeDamage(player.getMaxHp() + 10);

        // Act & Assert: La bataille doit être terminée.
        assertTrue(battleManager.isBattleOver(), "La bataille devrait être terminée car le joueur est vaincu.");
        assertFalse(player.isAlive(), "Le joueur ne devrait plus être en vie.");
        assertTrue(boss.isAlive(), "Le boss devrait toujours être en vie.");
    }

    @Test
    void testBattleEnds_WhenBothAreDefeated_ShouldBePlayerLoss() {
        // Arrange: On simule un cas où les deux sont vaincus en même temps.
        // Dans la plupart des jeux, une égalité est une défaite pour le joueur.
        player.takeDamage(player.getMaxHp());
        boss.takeDamage(boss.getMaxHp());

        // Act & Assert: La bataille est terminée.
        assertTrue(battleManager.isBattleOver(), "La bataille devrait être terminée car au moins un combattant est vaincu.");
        
        // Vérification de l'état final
        assertFalse(player.isAlive(), "Le joueur ne devrait plus être en vie.");
        assertFalse(boss.isAlive(), "Le boss ne devrait plus être en vie.");
    }
}