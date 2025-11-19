package towergame.controller;

import towergame.model.managers.*;

import java.util.List;
import towergame.model.actions.AAction;
import towergame.model.entities.ABoss;
import towergame.model.entities.Player;
import towergame.model.managers.BattleManager;
import towergame.model.managers.StageManager;
import towergame.model.managers.SuccessTracker;
import towergame.view.ConsoleView;

public class GameEngine {

    public static void main(String[] args) {
        ConsoleView view = new ConsoleView(System.in, System.out);
        view.displayWelcomeMessage();

        // 1. Initialisation des managers et du joueur
        StageManager stageManager = new StageManager();
        Player player = new Player("Héros");

        // 2. Boucle de jeu principale : un tour de boucle par boss
        while (true) {
            ABoss boss = stageManager.getNextBoss();

            // S'il n'y a plus de boss, le jeu est terminé
            if (boss == null) {
                view.displayNoMoreBosses();
                break;
            }

            // 3. Préparation du combat
            view.displayBattleStart(boss);
            // On donne au joueur les 4 premiers sorts disponibles
            List<AAction> availableActions = stageManager.getUnlockedActions();
            player.setEquippedActions(availableActions.subList(0, Math.min(4, availableActions.size())));

            BattleManager battleManager = new BattleManager(player, boss);

            // 4. Boucle de combat : un tour de boucle par tour de jeu
            while (!battleManager.isBattleOver()) {
                view.displayTurnInfo(player, boss, battleManager.getTurnNumber());
                view.displayPlayerTurn(player);

                // Demander au joueur de choisir une action
                AAction playerAction = view.getPlayerAction(player);

                // Exécuter la logique du tour
                battleManager.executeTurn(playerAction);

                // Petite pause pour que le joueur puisse lire ce qui s'est passé
                if (!battleManager.isBattleOver()) {
                    view.pressEnterToContinue();
                }
            }

            // 5. Fin du combat
            view.displayEndScreen(player, boss);

            // Si le joueur a perdu, on arrête le jeu
            if (!player.isAlive()) {
                break;
            }

            // Si le joueur a gagné, on vérifie les succès
            SuccessTracker.checkAchievements(player, boss, battleManager.getTurnNumber(),
                    battleManager.getActionsUsedHistory());
            // view.pressEnterToContinue(); // Pause avant le prochain combat (désactivé
            // pour éviter l'exception)
        }

        // 6. Nettoyage
        view.closeScanner();
    }
}