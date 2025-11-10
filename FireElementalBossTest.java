import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de test unitaire pour FireElementalBoss.
 * Chaque méthode annotée avec @Test vérifie un comportement spécifique.
 */
class FireElementalBossTest {

    private FireElementalBoss boss;
    private Player player;
    // On utilise une classe d'action factice pour les tests, comme dans GameEngine
    private static class TestAction extends AAction {
        public TestAction(Element element) { super("Test Action", element, 0); }
        public void execute(AEntity u, AEntity t) { /* Pas besoin de logique ici */ }
    }

    @BeforeEach
    void setUp() {
        // Cette méthode est appelée avant CHAQUE test.
        // On a donc un boss et un joueur "propres" pour chaque scénario.
        boss = new FireElementalBoss();
        player = new Player("Test Player");
    }

    @Test
    void testInitialState_IsInvulnerableAndNotEnraged() {
        assertTrue(boss.isInvulnerable(), "Le boss doit commencer invulnérable.");
        assertFalse(boss.isEnraged(), "Le boss ne doit pas commencer enragé.");
    }

    @Test
    void testGimmick_InvulnerabilityRemoved_OnTurn3WithFire() {
        // Arrange
        AAction fireAction = new TestAction(Element.FEU);

        // Act
        boss.checkGimmick(player, fireAction, 3);

        // Assert
        assertFalse(boss.isInvulnerable(), "Le boss aurait dû perdre son invulnérabilité au tour 3 avec une attaque FEU.");
    }

    @Test
    void testGimmick_InvulnerabilityRemoved_OnTurn5WithFire() {
        // Arrange
        AAction fireAction = new TestAction(Element.FEU);

        // Act
        boss.checkGimmick(player, fireAction, 5);

        // Assert
        assertFalse(boss.isInvulnerable(), "Le boss aurait dû perdre son invulnérabilité au tour 5 avec une attaque FEU.");
    }

    @Test
    void testGimmick_InvulnerabilityStays_OnTurn3WithWater() {
        // Arrange
        AAction waterAction = new TestAction(Element.EAU);

        // Act
        boss.checkGimmick(player, waterAction, 3);

        // Assert
        assertTrue(boss.isInvulnerable(), "Le boss doit rester invulnérable s'il n'est pas attaqué par FEU au tour 3.");
    }

    @Test
    void testGimmick_InvulnerabilityStays_OnTurn4WithFire() {
        // Arrange
        AAction fireAction = new TestAction(Element.FEU);

        // Act
        boss.checkGimmick(player, fireAction, 4); // Mauvais tour

        // Assert
        assertTrue(boss.isInvulnerable(), "Le boss doit rester invulnérable en dehors des tours 3 et 5.");
    }

    @Test
    void testGimmick_BecomesEnraged_OnTurn2WithWater() {
        // Arrange
        AAction waterAction = new TestAction(Element.EAU);

        // Act
        boss.checkGimmick(player, waterAction, 2);

        // Assert
        assertTrue(boss.isEnraged(), "Le boss aurait dû devenir enragé après une attaque EAU au tour 2.");
    }

    @Test
    void testGimmick_DoesNotBecomeEnraged_OnTurn5WithWater() {
        // Arrange
        AAction waterAction = new TestAction(Element.EAU);

        // Act
        boss.checkGimmick(player, waterAction, 5); // Le tour est trop tardif

        // Assert
        assertFalse(boss.isEnraged(), "Le boss ne doit pas devenir enragé par une attaque EAU après le tour 4.");
    }

    @Test
    void testGimmick_BecomesEnraged_WhenHpIsLow() {
        // Arrange
        AAction anyAction = new TestAction(Element.NEUTRAL);
        // On simule une attaque qui fait passer le boss sous le seuil de 20% (20 PV).
        // Le boss a 100 PV, on lui inflige 81 dégâts pour qu'il tombe à 19 PV.
        // On désactive temporairement l'invulnérabilité pour que le takeDamage fonctionne.
        boss.isInvulnerable = false;
        boss.takeDamage(81);
        // Act
        boss.checkGimmick(player, anyAction, 6); // N'importe quel tour

        // Assert
        assertTrue(boss.isEnraged(), "Le boss aurait dû devenir enragé quand ses PV sont sous 20%.");
        assertEquals(19, boss.getHp()); // Juste pour être sûr
    }

    @Test
    void testGimmick_StaysEnraged_AndDoesNotRetrigger() {
        // Arrange
        AAction waterAction = new TestAction(Element.EAU);
        boss.isEnraged = true; // On met le boss enragé manuellement pour le test

        // Act
        boss.checkGimmick(player, waterAction, 2); // On simule une condition qui devrait déclencher la rage

        // Assert
        // Le but est de s'assurer que la logique dans le `if (!this.isEnraged)` n'est pas exécutée.
        // Le test passera si aucune erreur n'est levée et l'état reste true.
        assertTrue(boss.isEnraged(), "Le boss doit rester enragé.");
    }
}