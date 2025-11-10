import java.util.List;
import java.util.Scanner;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * Gère l'affichage des informations du jeu et la lecture des entrées du joueur via la console.
 * C'est la "Vue" du jeu.
 */
public class ConsoleView {
    private final Scanner scanner;
    private final PrintStream out;

    // On injecte les dépendances pour les rendre remplaçables en test
    public ConsoleView(InputStream in, PrintStream out) {
        this.scanner = new Scanner(in);
        this.out = out;
    }

    public void displayWelcomeMessage() {
        out.println("--- Bienvenue dans Tower Battle ! ---");
    }

    public void displayBattleStart(ABoss boss) {
        out.println("Un terrible " + boss.getName() + " apparaît !");
    }

    public void displayTurnInfo(Player player, ABoss boss, int turn) {
        out.println("\n----------------- TOUR " + turn + " -----------------");
        out.println(player.getName() + ": " + player.getHp() + "/" + player.getMaxHp() + " PV");
        out.println(boss.getName() + ": " + boss.getHp() + "/" + boss.getMaxHp() + " PV");
    }

    public void displayPlayerTurn(Player player) {
        out.println("\nC'est au tour de " + player.getName() + ".");
    }

    public AAction getPlayerAction(Player player) {
        while (true) {
            out.println("Choisissez votre action :");
            List<AAction> actions = player.getEquippedActions();
            for (int i = 0; i < actions.size(); i++) {
                AAction action = actions.get(i);
                String cooldownStatus = action.isReady() ? "" : " (En recharge: " + action.currentCooldown + ")";
                out.println((i + 1) + ". " + action.getName() + cooldownStatus);
            }

            try {
                String input = scanner.nextLine();
                int choice = Integer.parseInt(input) - 1;

                if (choice >= 0 && choice < actions.size()) {
                    AAction selectedAction = actions.get(choice);
                    if (selectedAction.isReady()) {
                        return selectedAction;
                    } else {
                        out.println("Cette action est en cours de rechargement ! Veuillez en choisir une autre.");
                    }
                } else {
                    out.println("Choix invalide. Veuillez entrer un numéro entre 1 et " + actions.size() + ".");
                }
            } catch (NumberFormatException e) {
                out.println("Entrée invalide. Veuillez entrer un numéro.");
            }
        }
    }

    public void pressEnterToContinue() {
        out.println("\nAppuyez sur Entrée pour passer au tour suivant...");
        scanner.nextLine();
    }

    public void displayEndScreen(Player player, ABoss boss) {
        out.println("\n----------------- FIN DU COMBAT -----------------");
        if (player.isAlive()) {
            out.println(player.getName() + " a triomphé !");
        } else {
            out.println(boss.getName() + " a gagné...");
        }
    }

    public void displayNoMoreBosses() {
        out.println("Félicitations ! Vous avez vaincu tous les boss. Fin du jeu.");
    }

    public void closeScanner() {
        scanner.close();
    }
}