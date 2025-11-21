package towergame.model.managers;

import towergame.model.actions.AAction;
import towergame.model.entities.ABoss;
import towergame.model.entities.Player;

import java.util.List;
import java.util.ArrayList;

public class BattleManager {

    private Player player;
    private ABoss boss;
    private int turnNumber;
    private List<AAction> actionsUsedHistory;
    private AAction lastBossAction; // Track la dernière action du boss

    public BattleManager(Player player, ABoss boss) {
        this.player = player;
        this.boss = boss;
        this.turnNumber = 1;
        this.actionsUsedHistory = new ArrayList<>();
        this.lastBossAction = null;
    }

    public void executeTurn(AAction playerAction) {
        this.actionsUsedHistory.add(playerAction);

        playerAction.execute(player, boss);

        AAction bossAction = boss.getNextAction();
        // Le boss ne riposte pas s'il est déjà mort.
        if (boss.isAlive() && bossAction.isReady()) {
            this.actionsUsedHistory.add(bossAction);
            this.lastBossAction = bossAction; // Track l'action du boss
            bossAction.execute(boss, player);
        } else {
            this.lastBossAction = null; // Pas d'action du boss
        }

        // La logique de Gimmick doit être appelée après les deux actions
        if (boss instanceof towergame.model.entities.FireElementalBoss) {
            ((towergame.model.entities.FireElementalBoss) boss).checkGimmick(player, playerAction, turnNumber);
        }

        // Les effets de fin de tour (poison, cooldowns) doivent TOUJOURS être
        // appliqués.
        updateTurnEndEffects();
        turnNumber++;
    }

    private void updateTurnEndEffects() {
        player.getEquippedActions().forEach(AAction::updateCooldown);
        // Mettre à jour les cooldowns des actions du boss (accès via getter)
        for (AAction action : boss.getActionScript()) {
            action.updateCooldown();
        }

        player.updateStatusEffects();
        boss.updateStatusEffects();
    }

    public boolean isBattleOver() {
        return !player.isAlive() || !boss.isAlive();
    }

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

    public AAction getLastBossAction() {
        return lastBossAction;
    }
}
