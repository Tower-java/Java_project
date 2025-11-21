package towergame.model.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import towergame.model.actions.AAction;
import towergame.model.actions.Element;
import towergame.model.actions.PlayerAttackAction;

import static org.junit.jupiter.api.Assertions.*;

class FireElementalBossTest {

    private FireElementalBoss boss;
    private Player player;

    @BeforeEach
    void setUp() {
        boss = new FireElementalBoss();
        player = new Player("Test Player");
    }

    @Test
    void constructor_shouldInitializeCorrectly() {
        assertEquals("Élémentaire de feu", boss.getName());
        assertEquals(100, boss.getMaxHp());
        assertEquals(Element.FIRE, boss.getElement());
        assertTrue(boss.isInvulnerable(), "Le boss doit commencer invulnérable.");
        assertFalse(boss.getActionScript().isEmpty(), "Le script d'actions ne doit pas être vide.");
        assertEquals(12, boss.getActionScript().size(), "Le script doit contenir 12 actions.");
    }

    @Test
    void checkGimmick_shouldLoseInvulnerability_whenHitByFireOnTurn3() {
        assertTrue(boss.isInvulnerable(), "Le boss est invulnérable au départ.");
        AAction fireAction = new PlayerAttackAction("Fire Spell", Element.FIRE, 0, 10);

        FireElementalBoss.GimmickEvent event = boss.checkGimmick(player, fireAction, 3);

        assertFalse(boss.isInvulnerable(), "Le boss doit perdre son invulnérabilité après avoir été touché par une attaque FEU au tour 3.");
        assertEquals(FireElementalBoss.GimmickEvent.SHIELD_BROKEN, event, "L'événement SHIELD_BROKEN doit être retourné.");
    }

    @Test
    void checkGimmick_shouldLoseInvulnerability_whenHitByFireOnTurn5() {
        assertTrue(boss.isInvulnerable(), "Le boss est invulnérable au départ.");
        AAction fireAction = new PlayerAttackAction("Fire Spell", Element.FIRE, 0, 10);

        FireElementalBoss.GimmickEvent event = boss.checkGimmick(player, fireAction, 5);

        assertFalse(boss.isInvulnerable(), "Le boss doit perdre son invulnérabilité après avoir été touché par une attaque FEU au tour 5.");
        assertEquals(FireElementalBoss.GimmickEvent.SHIELD_BROKEN, event, "L'événement SHIELD_BROKEN doit être retourné.");
    }

    @Test
    void checkGimmick_shouldNotLoseInvulnerability_withWrongElement() {
        // Arrange: Utiliser une attaque EAU au tour 3 (un tour correct pour briser le bouclier, mais avec le mauvais élément)
        AAction waterAction = new PlayerAttackAction("Water Spell", Element.WATER, 0, 10);

        // Act
        FireElementalBoss.GimmickEvent event = boss.checkGimmick(player, waterAction, 3);

        // Assert
        assertTrue(boss.isInvulnerable(), "Le boss ne doit pas perdre son invulnérabilité avec une attaque d'un autre élément que FEU.");
        assertEquals(FireElementalBoss.GimmickEvent.ENRAGED, event, "Une attaque EAU avant le tour 5 doit déclencher l'événement ENRAGED.");
    }

    @Test
    void checkGimmick_shouldNotLoseInvulnerability_withWrongTurn() {
        // Arrange: Utiliser une attaque FEU au tour 4 (mauvais tour)
        AAction fireAction = new PlayerAttackAction("Fire Spell", Element.FIRE, 0, 10);

        // Act
        FireElementalBoss.GimmickEvent event = boss.checkGimmick(player, fireAction, 4);

        // Assert
        assertTrue(boss.isInvulnerable(), "Le boss ne doit pas perdre son invulnérabilité en dehors des tours 3 ou 5.");
        assertEquals(FireElementalBoss.GimmickEvent.NONE, event, "Rien ne doit se passer si l'attaque FEU est au mauvais tour.");
    }

    @Test
    void checkGimmick_shouldBecomeEnraged_whenHitByWaterBeforeTurn5() {
        assertFalse(boss.isEnraged(), "Le boss ne doit pas être enragé au départ.");
        AAction waterAction = new PlayerAttackAction("Water Spell", Element.WATER, 0, 10);

        FireElementalBoss.GimmickEvent event = boss.checkGimmick(player, waterAction, 4);

        assertTrue(boss.isEnraged(), "Le boss doit devenir enragé s'il est touché par une attaque EAU avant le tour 5.");
        assertEquals(FireElementalBoss.GimmickEvent.ENRAGED, event, "L'événement ENRAGED doit être retourné.");
    }

    @Test
    void checkGimmick_shouldBecomeEnraged_whenHpIsLow() {
        assertFalse(boss.isEnraged(), "Le boss ne doit pas être enragé au départ.");
        AAction neutralAction = new PlayerAttackAction("Attack", Element.NEUTRAL, 0, 10);
        boss.setInvulnerable(false); // On le rend vulnérable pour pouvoir baisser ses PV

        boss.takeDamage(81); // HP = 19, ce qui est < 20% de 100
        
        // La vérification de la gimmick a lieu après l'attaque du joueur
        FireElementalBoss.GimmickEvent event = boss.checkGimmick(player, neutralAction, 6);

        assertTrue(boss.isEnraged(), "Le boss doit devenir enragé quand ses PV sont en dessous de 20%.");
        assertEquals(FireElementalBoss.GimmickEvent.ENRAGED, event, "L'événement ENRAGED doit être retourné.");
    }

    @Test
    void checkGimmick_shouldDoNothing_ifAlreadyEnraged() {
        // Arrange: On met le boss en état "enragé" manuellement
        boss.setEnraged(true);
        AAction waterAction = new PlayerAttackAction("Water Spell", Element.WATER, 0, 10);

        // Act: On appelle la gimmick avec une condition qui devrait normalement déclencher l'enrage
        FireElementalBoss.GimmickEvent event = boss.checkGimmick(player, waterAction, 2);

        // Assert: On vérifie que rien n'a changé et que l'événement est NONE
        assertTrue(boss.isEnraged(), "Le boss doit rester enragé.");
        assertEquals(FireElementalBoss.GimmickEvent.NONE, event, "Aucun nouvel événement ne doit être déclenché si le boss est déjà enragé.");
    }

    @Test
    void checkGimmick_shouldDoNothing_ifAlreadyNotInvulnerable() {
        // Arrange: On rend le boss vulnérable manuellement
        boss.setInvulnerable(false);
        AAction fireAction = new PlayerAttackAction("Fire Spell", Element.FIRE, 0, 10);

        // Act: On appelle la gimmick avec une condition qui devrait normalement briser le bouclier
        FireElementalBoss.GimmickEvent event = boss.checkGimmick(player, fireAction, 3);

        // Assert: On vérifie que l'événement est NONE
        assertEquals(FireElementalBoss.GimmickEvent.NONE, event, "Aucun événement SHIELD_BROKEN ne doit être déclenché si le boss n'est plus invulnérable.");
    }
}