package towergame.model.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import towergame.model.entities.FireElementalBoss;
import towergame.model.entities.Player;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerAttackActionTest {

    private Player player;
    private FireElementalBoss boss;

    @BeforeEach
    void setUp() {
        // On utilise des instances réelles pour des tests d'intégration
        player = new Player("Héros");
        boss = new FireElementalBoss();
        // On désactive l'invulnérabilité par défaut du boss pour les tests
        boss.setInvulnerable(false);
    }

    @Test
    void constructor_shouldInitializePropertiesCorrectly() {
        PlayerAttackAction action = new PlayerAttackAction("Test Attack", Element.FIRE, 10, 25);

        assertEquals("Test Attack", action.getName());
        assertEquals(Element.FIRE, action.getElement());
        assertTrue(action.isReady(), "Action should be ready upon creation");
    }

    @Test
    void execute_whenReadyAndTargetNotNull_shouldDamageTargetAndStartCooldown() {
        int attackDamage = 15;
        PlayerAttackAction action = new PlayerAttackAction("Attack", Element.NEUTRAL, 5, attackDamage);

        // Vérification que l'action est prête avant l'exécution
        assertTrue(action.isReady());
        int initialBossHp = boss.getHp();

        action.execute(player, boss);

        // Vérification que le boss a subi des dégâts
        assertEquals(initialBossHp - attackDamage, boss.getHp());

        // Vérification que l'action est maintenant en cooldown
        assertFalse(action.isReady());
    }

    @Test
    void execute_whenNotReady_shouldDoNothing() {
        int attackDamage = 15;
        PlayerAttackAction action = new PlayerAttackAction("Attack", Element.NEUTRAL, 5, attackDamage);

        // On exécute une première fois pour démarrer le cooldown
        action.execute(player, boss);
        assertFalse(action.isReady());
        int hpAfterFirstAttack = boss.getHp();

        // On tente d'exécuter à nouveau alors que l'action n'est pas prête
        action.execute(player, boss);

        // On vérifie que les PV n'ont pas changé
        assertEquals(hpAfterFirstAttack, boss.getHp());
    }

    @Test
    void execute_whenTargetIsNull_shouldDoNothing() {
        PlayerAttackAction action = new PlayerAttackAction("Attack", Element.NEUTRAL, 5, 15);

        int initialHealth = boss.getHp();
        assertTrue(action.isReady());

        // On exécute avec une cible null
        action.execute(player, null);

        // On vérifie qu'aucune interaction n'a eu lieu avec notre boss
        assertEquals(initialHealth, boss.getHp());

        // L'action ne devrait pas avoir démarré son cooldown car rien ne s'est passé
        assertTrue(action.isReady());
    }

    @Test
    void execute_shouldDoNothing_whenUserIsNotPlayer() {
        PlayerAttackAction action = new PlayerAttackAction("Attack", Element.NEUTRAL, 5, 15);
        int initialBossHp = boss.getHp();

        // On crée une autre cible pour être sûr de ne pas la toucher
        FireElementalBoss otherTarget = new FireElementalBoss();
        otherTarget.setInvulnerable(false);
        int initialOtherTargetHp = otherTarget.getHp();

        // On exécute avec le boss comme utilisateur
        action.execute(boss, otherTarget);

        // On vérifie que la cible n'a pas subi de dégâts et que le cooldown n'a pas démarré
        assertEquals(initialOtherTargetHp, otherTarget.getHp(), "La cible ne devrait pas subir de dégâts si l'utilisateur n'est pas un joueur.");
        assertTrue(action.isReady(), "Le cooldown ne devrait pas démarrer si l'utilisateur n'est pas un joueur.");
    }
}