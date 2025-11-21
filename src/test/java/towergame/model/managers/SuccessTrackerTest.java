package towergame.model.managers;

import org.junit.jupiter.api.*;
import towergame.model.actions.AAction;
import towergame.model.actions.Element;
import towergame.model.entities.ABoss;
import towergame.model.entities.Player;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests pour SuccessTracker")
class SuccessTrackerTest {

    private ByteArrayOutputStream outContent;
    private final PrintStream originalOut = System.out;

    // --- Classes "Stubs" pour simuler des entités contrôlables ---

    private static class TestPlayer extends Player {
        public TestPlayer(int hp) {
            super("Test Player");
            // On ajuste les PV pour le test
            if (hp < this.getHp()) {
                this.takeDamage(this.getHp() - hp);
            }
        }
    }

    private static class TestBoss extends ABoss {
        public TestBoss(String name) {
            super(name, 100, Element.NEUTRAL, 10, 10);
        }
    }

    private static class TestAction extends AAction {
        public TestAction(Element element) {
            super("Test Action", element, 0);
        }
        @Override
        public void execute(towergame.model.entities.AEntity user, towergame.model.entities.AEntity target) {
            // Ne fait rien
        }
    }

    @BeforeEach
    public void setUpStreams() {
        // Redirige System.out vers notre buffer avant chaque test
        // Il est crucial de créer une nouvelle instance ici pour isoler les tests.
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        // Restaure System.out à sa valeur originale après chaque test
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Succès 'Au Bord du Gouffre' doit se débloquer avec 1 PV")
    void checkAchievements_shouldUnlockNearDeathAchievement() {
        // Arrange
        Player player = new TestPlayer(1);
        ABoss boss = new TestBoss("Any Boss");

        // Act
        SuccessTracker.checkAchievements(player, boss, 10, new ArrayList<>());

        // Assert
        assertTrue(outContent.toString().contains("Au Bord du Gouffre"), "Le succès pour 1 PV restant doit être débloqué.");
    }

    @Test
    @DisplayName("Succès 'Extincteur' pour l'Élémentaire de Feu en 8 tours")
    void checkAchievements_shouldUnlockFireElementalSpeedrunAchievement() {
        // Arrange
        Player player = new TestPlayer(100);
        ABoss boss = new TestBoss("Fire Elemental");

        // Act
        SuccessTracker.checkAchievements(player, boss, 8, new ArrayList<>());

        // Assert
        assertTrue(outContent.toString().contains("Extincteur"), "Le succès pour avoir battu l'Élémentaire de Feu rapidement doit être débloqué.");
    }

    @Test
    @DisplayName("Succès 'Combat loyal' pour l'Élémentaire de Feu sans utiliser d'Eau")
    void checkAchievements_shouldUnlockFireElementalNoWaterAchievement() {
        // Arrange
        Player player = new TestPlayer(100);
        ABoss boss = new TestBoss("Fire Elemental");
        List<AAction> actions = List.of(new TestAction(Element.FIRE), new TestAction(Element.PLANT));

        // Act
        SuccessTracker.checkAchievements(player, boss, 10, actions);

        // Assert
        assertTrue(outContent.toString().contains("Combat loyal"), "Le succès pour avoir battu l'Élémentaire de Feu sans Eau doit être débloqué.");
    }

    @Test
    @DisplayName("Aucun succès 'Combat loyal' si de l'Eau est utilisée contre l'Élémentaire de Feu")
    void checkAchievements_shouldNotUnlockFireElementalNoWaterAchievement_whenWaterIsUsed() {
        // Arrange
        Player player = new TestPlayer(100);
        ABoss boss = new TestBoss("Fire Elemental");
        List<AAction> actions = List.of(new TestAction(Element.WATER));

        // Act
        SuccessTracker.checkAchievements(player, boss, 10, actions);

        // Assert
        assertFalse(outContent.toString().contains("Combat loyal"), "Le succès ne doit pas se débloquer si de l'Eau est utilisée.");
    }

}