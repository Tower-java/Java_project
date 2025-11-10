import java.util.List;
import java.util.ArrayList;

/**
 * Gère la logique d'un combat entre un joueur et un boss.
 * Cette classe ne fait aucune entrée/sortie (pas de System.in ou System.out).
 * Elle est entièrement contrôlée par le GameEngine.
 */
public class BattleManager {

    private Player player;
    private ABoss boss;
    private int turnNumber;
    private List<AAction> actionsUsedHistory;

    public BattleManager(Player player, ABoss boss) {
        this.player = player;
        this.boss = boss;
        this.turnNumber = 1;
        this.actionsUsedHistory = new ArrayList<>();
    }

    /**
     * Exécute un tour complet de combat.
     * @param playerAction L'action choisie par le joueur pour ce tour.
     */
    public void executeTurn(AAction playerAction) {
        // 1. Tour du joueur
        this.actionsUsedHistory.add(playerAction);
        playerAction.execute(player, boss);
        boss.checkGimmick(player, playerAction, turnNumber);

        if (!boss.isAlive()) return; // Combat terminé

        // 2. Tour du Boss
        AAction bossAction = boss.getNextAction();
        if (bossAction.isReady()) {
            this.actionsUsedHistory.add(bossAction);
            bossAction.execute(boss, player);
        }

        if (!player.isAlive()) return; // Combat terminé

        // 3. Fin du tour
        updateTurnEndEffects();
        turnNumber++;
    }

    private void updateTurnEndEffects() {
        // Met à jour les cooldowns
        player.getEquippedActions().forEach(AAction::updateCooldown);
        boss.actionScript.forEach(AAction::updateCooldown);

        // Met à jour les effets de statut (Poison, Boost, etc.)
        player.updateStatusEffects();
        boss.updateStatusEffects();
    }

    public boolean isBattleOver() {
        return !player.isAlive() || !boss.isAlive();
    }

    // Getters pour que le GameEngine puisse afficher les informations
    public Player getPlayer() {
        return player;
    }

    public ABoss getBoss() {
        return boss;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public List<AAction> getActionsUsedHistory() {
        return actionsUsedHistory;
    }
}