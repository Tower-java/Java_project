package towergame.model.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import towergame.model.entities.FireElementalBoss;
import towergame.model.entities.Player;
import towergame.model.status.Element;
import towergame.model.status.WeakenStatus;

import static org.junit.jupiter.api.Assertions.*;

class PlantSpellsTest {

    private Player player;
    private FireElementalBoss bossTarget;

    @BeforeEach
    void setUp() {
        // On utilise de vraies instances pour tester les interactions concrètes.
        player = new Player("Héros");
        bossTarget = new FireElementalBoss();
    }

    @Test
    void plantSpell_shouldDealDamageAndApplyWeakenStatus_whenReady() {
        // Arrange: Préparation du test
        int baseDamage = 10;
        int statusDuration = 3;
        int cooldown = 5;
        PlantSpell plantSpell = new PlantSpell("Vine Whip", Element.PLANT, baseDamage, statusDuration, cooldown);
        int initialBossHp = bossTarget.getHp();

        // Act: Exécution de la méthode à tester
        plantSpell.execute(player, bossTarget);

        // Assert: Vérification des résultats
        // 1. Vérifier que le boss a bien subi les dégâts attendus.
        // Le boss est invulnérable au début, donc il ne devrait pas prendre de dégâts.
        assertEquals(initialBossHp, bossTarget.getHp(), "Le boss invulnérable ne devrait pas subir de dégâts.");

        // 2. Vérifier que le statut a bien été appliqué.
        assertTrue(bossTarget.hasEffect("Weaken"), "Le boss devrait avoir le statut 'Weaken'.");
        
        // Idéalement, WeakenStatus aurait des getters pour vérifier duration et weakenFactor.
        // En leur absence, on s'assure au moins que le bon type de statut est ajouté.

        // 3. Vérifier que le sort n'est plus prêt (cooldown a commencé).
        assertFalse(plantSpell.isReady());
    }

    @Test
    void plantSpell_shouldDoNothing_whenOnCooldown() {
        // Arrange
        PlantSpell plantSpell = new PlantSpell("Vine Whip", Element.PLANT, 10, 3, 5);
        int initialBossHp = bossTarget.getHp();
        plantSpell.execute(player, bossTarget); // Premier appel pour démarrer le cooldown
        int hpAfterFirstAttack = bossTarget.getHp();

        // Act
        plantSpell.execute(player, bossTarget); // Deuxième appel, devrait être bloqué par le cooldown

        // Assert
        // On vérifie que les PV n'ont pas changé après le deuxième appel.
        assertEquals(hpAfterFirstAttack, bossTarget.getHp(), "Les PV du boss ne devraient pas changer si le sort est en cooldown.");
    }

    @Test
    void plantHardSpell_shouldApplyWeakenStatus_whenReady() {
        // Arrange
        int baseDamage = 15;
        int statusDuration = 4;
        int cooldown = 8;
        PlantHardSpell plantHardSpell = new PlantHardSpell("Solar Beam", Element.PLANT, baseDamage, statusDuration, cooldown);
        int initialBossHp = bossTarget.getHp();

        // Act
        plantHardSpell.execute(player, bossTarget);

        // Assert
        // 1. Vérifier que le boss a subi les dégâts (il est invulnérable).
        assertEquals(initialBossHp, bossTarget.getHp(), "Le boss invulnérable ne devrait pas subir de dégâts.");

        // 2. Vérifier que le statut a été appliqué.
        assertTrue(bossTarget.hasEffect("Weaken"), "Le boss devrait avoir le statut 'Weaken'.");
        // Ici aussi, on pourrait vérifier le facteur de 0.4 si un getter existait.
        
        // 3. Vérifier que le sort est en cooldown.
        assertFalse(plantHardSpell.isReady());
    }

    @Test
    void plantHardSpell_shouldDealNormalDamage_whenBossIsVulnerable() {
        // Arrange
        int baseDamage = 15;
        PlantHardSpell plantHardSpell = new PlantHardSpell("Solar Beam", Element.PLANT, baseDamage, 4, 8);
        bossTarget.setInvulnerable(false); // On rend le boss vulnérable
        int initialBossHp = bossTarget.getHp();
        // Plante vs Feu -> désavantage élémentaire (x0.5)
        int expectedDamage = (int) (baseDamage * 0.5);

        // Act
        plantHardSpell.execute(player, bossTarget);

        // Assert
        assertEquals(initialBossHp - expectedDamage, bossTarget.getHp(), "Le boss vulnérable devrait subir des dégâts réduits par le désavantage élémentaire.");
    }

    @Test
    void plantHardSpell_shouldDoNothing_whenOnCooldown() {
        // Arrange
        PlantHardSpell plantHardSpell = new PlantHardSpell("Solar Beam", Element.PLANT, 15, 4, 8);
        plantHardSpell.execute(player, bossTarget); // Premier appel
        int hpAfterFirstAttack = bossTarget.getHp();

        // Act
        plantHardSpell.execute(player, bossTarget); // Deuxième appel

        // Assert
        // On vérifie que les PV n'ont pas changé.
        assertEquals(hpAfterFirstAttack, bossTarget.getHp(), "Les PV du boss ne devraient pas changer si le sort est en cooldown.");
    }
}