package towergame.model.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import towergame.model.actions.AAction;
import towergame.model.actions.Element;
import towergame.model.actions.PlayerAttackAction;

import static org.junit.jupiter.api.Assertions.*;

class WaterElementalBossTest {

    private WaterElementalBoss boss;
    private Player player;

    @BeforeEach
    void setUp() {
        boss = new WaterElementalBoss();
        player = new Player("Test Player");
    }

    @Test
    void constructor_shouldInitializeCorrectly() {
        assertEquals("Élémentaire d'eau", boss.getName());
        assertEquals(100, boss.getMaxHp());
        assertEquals(Element.WATER, boss.getElement());
        assertTrue(boss.isInvulnerable(), "Le boss doit commencer invulnérable.");
        assertFalse(boss.getActionScript().isEmpty(), "Le script d'actions ne doit pas être vide.");
        assertEquals(12, boss.getActionScript().size(), "Le script doit contenir 12 actions.");
    }

    @Test
    void checkGimmick_shouldLoseInvulnerability_whenHitByFireOnTurn1() {
        assertTrue(boss.isInvulnerable(), "Le boss est invulnérable au départ.");
        AAction fireAction = new PlayerAttackAction("Fire Spell", Element.FIRE, 0, 10);

        WaterElementalBoss.GimmickEvent event = boss.checkGimmick(player, fireAction, 1);

        assertFalse(boss.isInvulnerable(), "Le boss doit perdre son invulnérabilité après avoir été touché par une attaque FEU au tour 1.");
        assertEquals(WaterElementalBoss.GimmickEvent.SHIELD_BROKEN, event, "L'événement SHIELD_BROKEN doit être retourné.");
    }

    @Test
    void checkGimmick_shouldLoseInvulnerability_whenHitByFireOnTurn2() {
        assertTrue(boss.isInvulnerable(), "Le boss est invulnérable au départ.");
        AAction fireAction = new PlayerAttackAction("Fire Spell", Element.FIRE, 0, 10);

        WaterElementalBoss.GimmickEvent event = boss.checkGimmick(player, fireAction, 2);

        assertFalse(boss.isInvulnerable(), "Le boss doit perdre son invulnérabilité après avoir été touché par une attaque FEU au tour 2.");
        assertEquals(WaterElementalBoss.GimmickEvent.SHIELD_BROKEN, event, "L'événement SHIELD_BROKEN doit être retourné.");
    }

    @Test
    void checkGimmick_shouldNotLoseInvulnerability_withWrongElement() {
        AAction waterAction = new PlayerAttackAction("Water Spell", Element.WATER, 0, 10);

        WaterElementalBoss.GimmickEvent event = boss.checkGimmick(player, waterAction, 1);

        assertTrue(boss.isInvulnerable(), "Le boss ne doit pas perdre son invulnérabilité avec une attaque d'un autre élément que FEU.");
        assertEquals(WaterElementalBoss.GimmickEvent.NONE, event, "Aucun événement ne doit se produire.");
    }

    @Test
    void checkGimmick_shouldNotLoseInvulnerability_withWrongTurn() {
        AAction fireAction = new PlayerAttackAction("Fire Spell", Element.FIRE, 0, 10);

        WaterElementalBoss.GimmickEvent event = boss.checkGimmick(player, fireAction, 3);

        assertTrue(boss.isInvulnerable(), "Le boss ne doit pas perdre son invulnérabilité en dehors des tours 1 ou 2.");
        assertEquals(WaterElementalBoss.GimmickEvent.NONE, event, "Rien ne doit se passer si l'attaque FEU est au mauvais tour.");
    }

    @Test
    void checkGimmick_shouldBecomeEnraged_whenHitByFireAfterTurn4() {
        assertFalse(boss.isEnraged(), "Le boss ne doit pas être enragé au départ.");
        AAction fireAction = new PlayerAttackAction("Fire Spell", Element.FIRE, 0, 10);

        WaterElementalBoss.GimmickEvent event = boss.checkGimmick(player, fireAction, 5);

        assertTrue(boss.isEnraged(), "Le boss doit devenir enragé s'il est touché par une attaque FEU après le tour 4.");
        assertEquals(WaterElementalBoss.GimmickEvent.ENRAGED, event, "L'événement ENRAGED doit être retourné.");
    }

    @Test
    void checkGimmick_shouldBecomeEnraged_whenHpIsLow() {
        assertFalse(boss.isEnraged(), "Le boss ne doit pas être enragé au départ.");
        AAction neutralAction = new PlayerAttackAction("Attack", Element.NEUTRAL, 0, 10);
        boss.setInvulnerable(false); // On le rend vulnérable pour pouvoir baisser ses PV

        boss.takeDamage(61); // HP = 39, ce qui est < 40% de 100
        
        WaterElementalBoss.GimmickEvent event = boss.checkGimmick(player, neutralAction, 6);

        assertTrue(boss.isEnraged(), "Le boss doit devenir enragé quand ses PV sont en dessous de 40%.");
        assertEquals(WaterElementalBoss.GimmickEvent.ENRAGED, event, "L'événement ENRAGED doit être retourné.");
    }

    @Test
    void checkGimmick_shouldDoNothing_ifAlreadyEnraged() {
        boss.setEnraged(true);
        AAction fireAction = new PlayerAttackAction("Fire Spell", Element.FIRE, 0, 10);

        WaterElementalBoss.GimmickEvent event = boss.checkGimmick(player, fireAction, 5);

        assertEquals(WaterElementalBoss.GimmickEvent.NONE, event, "Aucun nouvel événement ne doit être déclenché si le boss est déjà enragé.");
    }

    @Test
    void checkGimmick_shouldDoNothing_ifAlreadyNotInvulnerable() {
        boss.setInvulnerable(false);
        AAction fireAction = new PlayerAttackAction("Fire Spell", Element.FIRE, 0, 10);

        WaterElementalBoss.GimmickEvent event = boss.checkGimmick(player, fireAction, 1);

        assertEquals(WaterElementalBoss.GimmickEvent.NONE, event, "Aucun événement SHIELD_BROKEN ne doit être déclenché si le boss n'est plus invulnérable.");
    }
}