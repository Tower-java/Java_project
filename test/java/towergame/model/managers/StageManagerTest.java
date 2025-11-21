package towergame.model.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import towergame.model.actions.AAction;
import towergame.model.entities.ABoss;
import towergame.model.entities.FireElementalBoss;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de test pour StageManager.
 * Assurez-vous d'avoir JUnit 5 (Jupiter) dans les dépendances de votre projet.
 */
@DisplayName("Tests pour StageManager")
class StageManagerTest {

    private StageManager stageManager;

    @BeforeEach
    void setUp() {
        // Cette méthode est exécutée avant chaque test,
        // garantissant une instance fraîche de StageManager.
        stageManager = new StageManager();
    }

    @Test
    @DisplayName("Le constructeur doit initialiser les listes d'actions et de boss")
    void constructor_ShouldInitializeLists() {
        // 1. Vérifier que la liste des actions a été chargée
        List<AAction> actions = stageManager.getUnlockedActions();
        assertNotNull(actions, "La liste des actions ne devrait pas être nulle.");
        assertFalse(actions.isEmpty(), "La liste des actions ne devrait pas être vide après initialisation.");
        assertEquals(4, actions.size(), "Devrait charger 4 actions au départ.");

        // 2. Vérifier que la liste des boss a été chargée (indirectement)
        // On ne peut pas accéder à la liste directement, mais on peut appeler getNextBoss()
        ABoss firstBoss = stageManager.getNextBoss();
        assertNotNull(firstBoss, "Le premier boss ne devrait pas être nul, ce qui signifie que la liste des boss a été chargée.");
    }

    @Test
    @DisplayName("getUnlockedActions doit retourner la liste correcte des actions")
    void getUnlockedActions_ShouldReturnCorrectActions() {
        List<AAction> actions = stageManager.getUnlockedActions();

        // Vérifications de base
        assertNotNull(actions);
        assertEquals(4, actions.size());

        // Vérifications plus spécifiques sur le contenu
        assertEquals("Soin Léger", actions.get(0).getName());
        assertEquals("Barrière", actions.get(1).getName());
        assertEquals("Fragiliser", actions.get(2).getName());
        assertEquals("Jet de Glace", actions.get(3).getName());
    }

    @Test
    @DisplayName("getNextBoss doit retourner les boss dans l'ordre puis null")
    void getNextBoss_ShouldReturnBossesInOrderAndThenNull() {
        // Premier appel : doit retourner le premier boss
        ABoss firstBoss = stageManager.getNextBoss();
        assertNotNull(firstBoss, "Le premier appel à getNextBoss() devrait retourner un boss.");
        assertTrue(firstBoss instanceof FireElementalBoss, "Le premier boss devrait être une instance de FireElementalBoss.");

        // Deuxième appel : comme il n'y a qu'un boss chargé, doit retourner null
        ABoss secondBoss = stageManager.getNextBoss();
        assertNull(secondBoss, "Le deuxième appel devrait retourner null car il n'y a plus de boss.");

        // Troisième appel : doit aussi retourner null
        ABoss thirdBoss = stageManager.getNextBoss();
        assertNull(thirdBoss, "Les appels suivants devraient continuer à retourner null.");
    }

    @Test
    @DisplayName("loadAllBosses doit initialiser la liste des boss")
    void loadAllBosses_ShouldInitializeBossList() throws Exception {
        // Arrange
        // On utilise la réflexion pour accéder à la liste privée des boss
        Field bossListField = StageManager.class.getDeclaredField("bossList");
        bossListField.setAccessible(true);

        // Act
        @SuppressWarnings("unchecked")
        List<ABoss> bossList = (List<ABoss>) bossListField.get(stageManager);

        // Assert
        assertNotNull(bossList, "La liste des boss ne devrait pas être nulle.");
        assertFalse(bossList.isEmpty(), "La liste des boss ne devrait pas être vide après initialisation.");
        assertEquals(1, bossList.size(), "La liste devrait contenir 1 boss.");
        assertTrue(bossList.get(0) instanceof FireElementalBoss, "Le premier boss doit être un FireElementalBoss.");
    }

    @Test
    @DisplayName("getNextBoss doit incrémenter l'index du stage interne")
    void getNextBoss_ShouldIncrementInternalStageIndex() throws Exception {
        // Arrange
        // On utilise la réflexion pour accéder au champ privé 'currentStage'
        Field currentStageField = StageManager.class.getDeclaredField("currentStage");
        currentStageField.setAccessible(true);

        // Assert : Vérification de l'état initial
        assertEquals(0, currentStageField.get(stageManager), "L'index du stage doit être à 0 au début.");

        // Act : On appelle la méthode une première fois
        stageManager.getNextBoss();

        // Assert : Vérification après le premier appel
        assertEquals(1, currentStageField.get(stageManager), "L'index du stage doit être à 1 après avoir récupéré le premier boss.");

        stageManager.getNextBoss(); // On prend le dernier boss
        assertEquals(1, currentStageField.get(stageManager), "L'index ne doit plus changer une fois la liste de boss épuisée.");
    }
}