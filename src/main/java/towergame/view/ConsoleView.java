package towergame.view;

import java.util.List;
import java.util.Scanner;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * Gère l'affichage des informations du jeu et la lecture des entrées du joueur
 * via la console avec support ANSI pour les couleurs.
 * C'est la "Vue" du jeu.
 */
import towergame.model.actions.AAction;
import towergame.model.entities.ABoss;
import towergame.model.entities.Player;

public class ConsoleView {
    private final Scanner scanner;
    private final PrintStream out;

    // Codes couleur ANSI
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String BOLD = "\u001B[1m";
    private static final String GRAY = "\u001B[90m";
    private static final String WHITE = "\u001B[37m";
    private static final String BRIGHT_RED = "\u001B[91m";
    private static final String BRIGHT_GREEN = "\u001B[92m";
    private static final String BRIGHT_YELLOW = "\u001B[93m";
    private static final String BLINK = "\u001B[5m";
    private static final String UNDERLINE = "\u001B[4m";

    // On injecte les dépendances pour les rendre remplaçables en test
    public ConsoleView(InputStream in, PrintStream out) {
        this.scanner = new Scanner(in);
        this.out = out;
    }

    public void displayWelcomeMessage() {
        out.println(BOLD + CYAN + "\n╔════════════════════════════════════════╗" + RESET);
        out.println(BOLD + CYAN + "║   Bienvenue dans Tower Battle !   ║" + RESET);
        out.println(BOLD + CYAN + "╚════════════════════════════════════════╝" + RESET);
    }

    public void displayBattleStart(ABoss boss) {
        out.println(BOLD + RED + "\n⚔ Un terrible " + boss.getName() + " apparaît ! ⚔" + RESET);
    }

    public void displayTurnInfo(Player player, ABoss boss, int turn) {
        out.println(BOLD + YELLOW + "\n┌─────────────── TOUR " + turn + " ───────────────┐" + RESET);
        out.println(getHealthBar(player.getName(), player.getHp(), player.getMaxHp(), GREEN));
        out.println(getHealthBar(boss.getName(), boss.getHp(), boss.getMaxHp(), RED));
        out.println(BOLD + YELLOW + "└────────────────────────────────────────┘" + RESET);
    }

    /**
     * Génère une barre de santé colorée avec ASCII.
     */
    private String getHealthBar(String name, int currentHp, int maxHp, String color) {
        int barLength = 20;
        double hpPercent = (double) currentHp / maxHp;
        int filledBars = (int) (hpPercent * barLength);

        // Couleur dynamique selon le pourcentage
        String hpColor = color;
        if (hpPercent < 0.3) {
            hpColor = RED;
        } else if (hpPercent < 0.6) {
            hpColor = YELLOW;
        }

        StringBuilder bar = new StringBuilder(BOLD + name + RESET + " [");
        for (int i = 0; i < barLength; i++) {
            if (i < filledBars) {
                bar.append(hpColor).append("█").append(RESET);
            } else {
                bar.append(GRAY).append("░").append(RESET);
            }
        }
        bar.append("] ").append(hpColor).append(currentHp).append("/").append(maxHp).append(" PV").append(RESET);

        return bar.toString();
    }

    public void displayPlayerTurn(Player player) {
        out.println(BOLD + CYAN + "\n⇒ C'est au tour de " + player.getName() + " !" + RESET);
    }

    public AAction getPlayerAction(Player player) {
        List<AAction> actions = player.getEquippedActions();
        if (actions.isEmpty()) {
            out.println("Le joueur n'a aucune action disponible.");
            return null;
        }

        while (true) {
            out.println(BOLD + "\nChoisissez votre action :" + RESET);
            for (int i = 0; i < actions.size(); i++) {
                AAction action = actions.get(i);
                String cooldownStatus = action.isReady() ? ""
                        : GRAY + " (Recharge: " + action.getCurrentCooldown() + ")" + RESET;
                String actionColor = getActionColor(action);
                String readyIndicator = action.isReady() ? GREEN + "✓" : RED + "✗";
                out.println(readyIndicator + RESET + " " + (i + 1) + ". " + actionColor + BOLD + action.getName()
                        + RESET + cooldownStatus);
            }

            try {
                out.print(CYAN + "Votre choix : " + RESET);
                String input = scanner.nextLine();
                int choice = Integer.parseInt(input) - 1;

                if (choice >= 0 && choice < actions.size()) {
                    AAction selectedAction = actions.get(choice);
                    if (selectedAction.isReady()) {
                        return selectedAction;
                    } else {
                        out.println(RED + "✗ Cette action est en cours de rechargement !" + RESET);
                    }
                } else {
                    out.println(RED + "✗ Choix invalide. Veuillez entrer un numéro entre 1 et " + actions.size() + "."
                            + RESET);
                }
            } catch (NumberFormatException e) {
                out.println(RED + "✗ Entrée invalide. Veuillez entrer un numéro." + RESET);
            }
        }
    }

    /**
     * Retourne une couleur en fonction du type d'action.
     */
    private String getActionColor(AAction action) {
        String name = action.getName().toLowerCase();
        if (name.contains("heal") || name.contains("soin")) {
            return GREEN;
        } else if (name.contains("defend") || name.contains("défense")) {
            return BLUE;
        } else if (name.contains("boost")) {
            return PURPLE;
        } else if (name.contains("attack") || name.contains("attaque") || name.contains("spell")
                || name.contains("sort")) {
            return RED;
        }
        return RESET;
    }

    public void pressEnterToContinue() {
        out.println(GRAY + "\n[Appuyez sur Entrée pour continuer...]" + RESET);
        scanner.nextLine();
    }

    public void displayEndScreen(Player player, ABoss boss) {
        out.println(BOLD + YELLOW + "\n╔════════════════════════════════════════╗" + RESET);
        out.println(BOLD + YELLOW + "║       FIN DU COMBAT                ║" + RESET);
        out.println(BOLD + YELLOW + "╚════════════════════════════════════════╝" + RESET);
        if (player.isAlive()) {
            out.println(BOLD + GREEN + "🎉 " + player.getName() + " a triomphé ! 🎉" + RESET);
        } else {
            out.println(BOLD + RED + "💀 " + boss.getName() + " a gagné... 💀" + RESET);
        }
    }

    public void displayNoMoreBosses() {
        out.println(BOLD + GREEN + "\n╔════════════════════════════════════════╗" + RESET);
        out.println(BOLD + GREEN + "║  Félicitations ! Tous les boss sont vaincus ! ║" + RESET);
        out.println(BOLD + GREEN + "╚════════════════════════════════════════╝" + RESET);
    }

    /**
     * Affiche un message d'enrage du boss dans le style Pokémon/FF7
     */
    public void displayBossEnrage(ABoss boss) {
        out.println();
        displayAnimatedLine(BRIGHT_RED + BOLD + "═══════════════════════════════════════" + RESET);
        out.println(BRIGHT_RED + BOLD + BLINK + "   ⚡ " + boss.getName() + " ENTRE EN RAGE ! ⚡   " + RESET);
        out.println(BRIGHT_RED + BOLD + "   Ses attaques deviennent plus puissantes !" + RESET);
        displayAnimatedLine(BRIGHT_RED + BOLD + "═══════════════════════════════════════" + RESET);
        out.println();

        // Petit délai dramatique
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Affiche un message de résistance dans le style Pokémon/FF7
     */
    public void displayResistanceMessage(ABoss boss, String attackName) {
        out.println();
        displayBoxMessage(
                BLUE + BOLD + boss.getName() + " résiste à " + attackName + " !" + RESET,
                BLUE + "L'attaque n'est pas très efficace...",
                "🛡️");
        animateText(GRAY + "*Tink* *Tink*" + RESET);
        out.println();
    }

    /**
     * Affiche un message de faiblesse dans le style Pokémon/FF7
     */
    public void displayWeaknessMessage(ABoss boss, String attackName) {
        out.println();
        displayBoxMessage(
                BRIGHT_GREEN + BOLD + boss.getName() + " craint " + attackName + " !" + RESET,
                BRIGHT_GREEN + "C'est super efficace !",
                "💥");
        animateText(BRIGHT_YELLOW + BLINK + "*CRITICAL HIT*" + RESET);
        out.println();
    }

    /**
     * Affiche un message d'action du boss dans le style FF7
     */
    public void displayBossAction(ABoss boss, String actionName) {
        out.println();
        displayBoxMessage(
                PURPLE + BOLD + boss.getName() + " utilise " + actionName + " !" + RESET,
                "",
                "🔥");
    }

    /**
     * Affiche un message d'action du joueur dans le style FF7
     */
    public void displayPlayerAction(Player player, String actionName) {
        out.println();
        displayBoxMessage(
                CYAN + BOLD + player.getName() + " utilise " + actionName + " !" + RESET,
                "",
                "⚔️");
    }

    /**
     * Affiche un message de dégâts dans le style Pokémon
     */
    public void displayDamageMessage(String targetName, int damage) {
        if (damage > 0) {
            animateText(RED + BOLD + targetName + " subit " + damage + " dégâts !" + RESET);
        }
    }

    /**
     * Affiche un message de soin dans le style Pokémon
     */
    public void displayHealMessage(String targetName, int healAmount) {
        if (healAmount > 0) {
            animateText(GREEN + BOLD + targetName + " récupère " + healAmount + " PV !" + RESET);
        }
    }

    /**
     * Affiche une boîte de message stylisée
     */
    private void displayBoxMessage(String mainText, String subText, String icon) {
        String border = "▄▀▄▀▄▀▄▀▄▀▄▀▄▀▄▀▄▀▄▀▄▀▄▀▄▀▄▀▄▀▄▀▄▀";

        out.println(WHITE + BOLD + border + RESET);
        out.println(WHITE + BOLD + "▌ " + icon + " " + mainText + WHITE + BOLD + " ▐" + RESET);
        if (!subText.isEmpty()) {
            out.println(WHITE + BOLD + "▌   " + subText + WHITE + BOLD + "   ▐" + RESET);
        }
        out.println(WHITE + BOLD + border + RESET);

        // Petit délai pour l'effet
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Anime une ligne de texte caractère par caractère
     */
    private void animateText(String text) {
        for (char c : text.toCharArray()) {
            out.print(c);
            out.flush();
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        out.println();
    }

    /**
     * Affiche une ligne avec animation
     */
    private void displayAnimatedLine(String line) {
        out.println(line);
        out.flush();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void closeScanner() {
        scanner.close();
    }
}
