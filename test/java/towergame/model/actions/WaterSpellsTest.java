package towergame.model.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import towergame.model.entities.FireElementalBoss;
import towergame.model.entities.Player;

import static org.junit.jupiter.api.Assertions.*;

class WaterSpellsTest {

    private Player player;
    private FireElementalBoss bossTarget;

    @BeforeEach
    void setUp() {
        // On utilise de vraies instances pour tester les interactions concrètes.
        player = new Player("Héros");
        bossTarget = new FireElementalBoss();
    }

    @Test
    void waterSpell_shouldApplyEntraveStatus_whenReady() {
        // Arrange
        int baseDamage = 5;
        WaterSpell waterSpell = new WaterSpell("Jet de Glace", Element.WATER, baseDamage, 2, 2);
        int initialBossHp = bossTarget.getHp();

        // Act
        waterSpell.execute(player, bossTarget);

        // Assert
        // 1. Le boss est invulnérable, les dégâts directs ne s'appliquent pas.
        assertEquals(initialBossHp, bossTarget.getHp(), "Le boss invulnérable ne devrait pas subir de dégâts directs.");

        // 2. Le statut 'Entrave' doit être appliqué.
        assertTrue(bossTarget.hasEffect("Entrave"), "Le boss devrait avoir le statut 'Entrave'.");

        // 3. Le sort doit être en cooldown.
        assertFalse(waterSpell.isReady());
    }

    @Test
    void waterSpell_shouldDoNothing_whenOnCooldown() {
        // Arrange
        WaterSpell waterSpell = new WaterSpell("Jet de Glace", Element.WATER, 5, 2, 2);
        waterSpell.execute(player, bossTarget); // Premier appel
        int hpAfterFirstAttack = bossTarget.getHp();

        // Act
        waterSpell.execute(player, bossTarget); // Deuxième appel

        // Assert
        assertEquals(hpAfterFirstAttack, bossTarget.getHp(), "Les PV du boss ne devraient pas changer si le sort est en cooldown.");
    }

    @Test
    void waterSpell_shouldDealDoubleDamage_whenBossIsVulnerable() {
        // Arrange
        int baseDamage = 5;
        WaterSpell waterSpell = new WaterSpell("Jet de Glace", Element.WATER, baseDamage, 2, 2);
        bossTarget.setInvulnerable(false); // On rend le boss vulnérable
        int initialBossHp = bossTarget.getHp();
        // Eau vs Feu -> Avantage élémentaire (x2)
        int expectedDamage = (int) (baseDamage * 2.0);

        // Act
        waterSpell.execute(player, bossTarget);

        // Assert
        assertEquals(initialBossHp - expectedDamage, bossTarget.getHp(), "Le boss vulnérable devrait subir des dégâts doublés par l'avantage élémentaire.");
    }

    @Test
    void waterHardSpell_shouldApplyEntraveStatus_whenReady() {
        // Arrange
        int baseDamage = 10;
        WaterHardSpell waterHardSpell = new WaterHardSpell("Tsunami", Element.WATER, baseDamage, 3, 5);
        int initialBossHp = bossTarget.getHp();

        // Act
        waterHardSpell.execute(player, bossTarget);

        // Assert
        // 1. Le boss est invulnérable, les dégâts directs ne s'appliquent pas.
        assertEquals(initialBossHp, bossTarget.getHp(), "Le boss invulnérable ne devrait pas subir de dégâts directs.");

        // 2. Le statut 'Entrave' doit être appliqué.
        assertTrue(bossTarget.hasEffect("Entrave"), "Le boss devrait avoir le statut 'Entrave'.");

        // 3. Le sort doit être en cooldown.
        assertFalse(waterHardSpell.isReady());
    }

    @Test
    void waterHardSpell_shouldDoNothing_whenOnCooldown() {
        // Arrange
        WaterHardSpell waterHardSpell = new WaterHardSpell("Tsunami", Element.WATER, 10, 3, 5);
        waterHardSpell.execute(player, bossTarget); // Premier appel
        int hpAfterFirstAttack = bossTarget.getHp();

        // Act
        waterHardSpell.execute(player, bossTarget); // Deuxième appel

        // Assert
        assertEquals(hpAfterFirstAttack, bossTarget.getHp(), "Les PV du boss ne devraient pas changer si le sort est en cooldown.");
    }

    @Test
    void waterHardSpell_shouldDealDoubleDamage_whenBossIsVulnerable() {
        // Arrange
        int baseDamage = 10;
        WaterHardSpell waterHardSpell = new WaterHardSpell("Tsunami", Element.WATER, baseDamage, 3, 5);
        bossTarget.setInvulnerable(false); // On rend le boss vulnérable
        int initialBossHp = bossTarget.getHp();
        // Eau vs Feu a un avantage (x2)
        int expectedDamage = (int) (baseDamage * 2.0);

        // Act
        waterHardSpell.execute(player, bossTarget);

        // Assert
        assertEquals(initialBossHp - expectedDamage, bossTarget.getHp(), "Le boss vulnérable devrait subir des dégâts doublés par l'avantage élémentaire.");
    }
}