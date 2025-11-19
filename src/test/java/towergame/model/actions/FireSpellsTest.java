package towergame.model.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import towergame.model.entities.FireElementalBoss;
import towergame.model.entities.Player;

import static org.junit.jupiter.api.Assertions.*;

class FireSpellsTest {

    private Player player;
    private FireElementalBoss bossTarget;

    @BeforeEach
    void setUp() {
        // On utilise de vraies instances pour tester les interactions concrètes.
        player = new Player("Héros");
        bossTarget = new FireElementalBoss();
    }

    @Test
    void fireSpell_shouldApplyPoisonStatus_whenReady() {
        // Arrange: Préparation du test
        int baseDamage = 15;
        int statusDuration = 3;
        int cooldown = 4;
        FireSpell fireSpell = new FireSpell("Boule de Feu", Element.FIRE, baseDamage, statusDuration, cooldown);
        int initialBossHp = bossTarget.getHp();

        // Act: Exécution de la méthode à tester
        fireSpell.execute(player, bossTarget);

        // Assert: Vérification des résultats
        // 1. Vérifier que le boss a bien subi les dégâts attendus.
        // Le boss est invulnérable au début, donc il ne devrait pas prendre de dégâts directs.
        assertEquals(initialBossHp, bossTarget.getHp(), "Le boss invulnérable ne devrait pas subir de dégâts directs.");

        // 2. Vérifier que le statut 'Poison' a bien été appliqué.
        assertTrue(bossTarget.hasEffect("Poison"), "Le boss devrait avoir le statut 'Poison'.");

        // 3. Vérifier que le sort n'est plus prêt (cooldown a commencé).
        assertFalse(fireSpell.isReady());
    }

    @Test
    void fireSpell_shouldDoNothing_whenOnCooldown() {
        // Arrange
        FireSpell fireSpell = new FireSpell("Boule de Feu", Element.FIRE, 15, 3, 4);
        fireSpell.execute(player, bossTarget); // Premier appel pour démarrer le cooldown
        int hpAfterFirstAttack = bossTarget.getHp();

        // Act
        fireSpell.execute(player, bossTarget); // Deuxième appel, devrait être bloqué par le cooldown

        // Assert
        // On vérifie que les PV n'ont pas changé après le deuxième appel.
        assertEquals(hpAfterFirstAttack, bossTarget.getHp(), "Les PV du boss ne devraient pas changer si le sort est en cooldown.");
    }

    @Test
    void fireSpell_shouldDealDamage_whenBossIsVulnerable() {
        // Arrange
        int baseDamage = 15;
        FireSpell fireSpell = new FireSpell("Boule de Feu", Element.FIRE, baseDamage, 3, 4);
        bossTarget.setInvulnerable(false); // On rend le boss vulnérable pour ce test
        int initialBossHp = bossTarget.getHp();
        // Feu vs Feu -> multiplicateur neutre (x1.0)
        int expectedDamage = (int) (baseDamage * 1.0);

        // Act
        fireSpell.execute(player, bossTarget);

        // Assert
        assertEquals(initialBossHp - expectedDamage, bossTarget.getHp(), "Le boss vulnérable devrait subir les dégâts du sort.");
    }

    @Test
    void fireHardSpell_shouldApplyPoisonStatus_whenReady() {
        // Arrange
        int baseDamage = 20;
        int statusDuration = 4;
        int cooldown = 7;
        // Je suppose l'existence de FireHardSpell sur le modèle de PlantHardSpell
        FireHardSpell fireHardSpell = new FireHardSpell("Explosion", Element.FIRE, baseDamage, statusDuration, cooldown);
        int initialBossHp = bossTarget.getHp();

        // Act
        fireHardSpell.execute(player, bossTarget);

        // Assert
        // 1. Le boss est invulnérable, les dégâts directs ne s'appliquent pas.
        assertEquals(initialBossHp, bossTarget.getHp(), "Le boss invulnérable ne devrait pas subir de dégâts directs.");

        // 2. Le statut Poison doit être appliqué.
        assertTrue(bossTarget.hasEffect("Poison"), "Le boss devrait avoir le statut 'Poison'.");

        // 3. Le sort doit être en cooldown.
        assertFalse(fireHardSpell.isReady());
    }

    @Test
    void fireHardSpell_shouldDoNothing_whenOnCooldown() {
        // Arrange
        FireHardSpell fireHardSpell = new FireHardSpell("Explosion", Element.FIRE, 20, 4, 7);
        fireHardSpell.execute(player, bossTarget); // Premier appel
        int hpAfterFirstAttack = bossTarget.getHp();

        // Act
        fireHardSpell.execute(player, bossTarget); // Deuxième appel

        // Assert
        assertEquals(hpAfterFirstAttack, bossTarget.getHp(), "Les PV du boss ne devraient pas changer si le sort est en cooldown.");
    }

    @Test
    void fireHardSpell_shouldDealNormalDamage_whenBossIsVulnerable() {
        // Arrange
        int baseDamage = 20;
        FireHardSpell fireHardSpell = new FireHardSpell("Explosion", Element.FIRE, baseDamage, 4, 7);
        bossTarget.setInvulnerable(false); // On rend le boss vulnérable
        int initialBossHp = bossTarget.getHp();
        // Feu vs Feu -> multiplicateur neutre (x1.0)
        int expectedDamage = (int) (baseDamage * 1.0);

        // Act
        fireHardSpell.execute(player, bossTarget);

        // Assert
        assertEquals(initialBossHp - expectedDamage, bossTarget.getHp(), "Le boss vulnérable devrait subir les dégâts normaux du sort.");
    }
}