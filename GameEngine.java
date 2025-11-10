import java.util.List;

public class GameEngine {

    public static void main(String[] args) {
        ConsoleView view = new ConsoleView(System.in, System.out);
        view.displayWelcomeMessage();

        // 1. Initialisation
        StageManager stageManager = new StageManager();
        Player player = new Player("Héros");

        // On donne au joueur les actions débloquées.
        // Le GameEngine fait confiance au StageManager pour être correctement initialisé.
        // On s'assure qu'il y a au moins 4 actions ou toutes celles disponibles.
        List<AAction> availableActions = stageManager.getUnlockedActions();
        player.setEquippedActions(availableActions.subList(0, Math.min(4, availableActions.size())));

        ABoss boss = stageManager.getNextBoss();

        if (boss == null) {
            view.closeScanner();
            view.displayNoMoreBosses(); // Correction: Utiliser la vue
            return;
        }
