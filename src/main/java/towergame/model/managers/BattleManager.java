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

    public BattleManager(Player player, ABoss boss) {
        this.player = player;
        this.boss = boss;
        this.turnNumber = 1;
        this.actionsUsedHistory = new ArrayList<>();
    }

    public void executeTurn(AAction playerAction) {
        this.actionsUsedHistory.add(playerAction);

        // Vérifier les résistances/faiblesses avant d'exécuter l'action
        if (boss instanceof towergame.model.entities.FireElementalBoss) {
            towergame.model.entities.FireElementalBoss fireBoss = (towergame.model.entities.FireElementalBoss) boss;
            towergame.view.ConsoleView view = new towergame.view.ConsoleView(System.in, System.out);

            if (fireBoss.isResistant(playerAction.getElement())) {
                view.displayResistanceMessage(fireBoss, playerAction.getName());
            } else if (fireBoss.isWeak(playerAction.getElement())) {
                view.displayWeaknessMessage(fireBoss, playerAction.getName());
            }
        }

        playerAction.execute(player, boss);
        boss.checkGimmick(player, playerAction, turnNumber);

        if (!boss.isAlive())
            return;

        AAction bossAction = boss.getNextAction();
        if (bossAction.isReady()) {
            this.actionsUsedHistory.add(bossAction);
            bossAction.execute(boss, player);
        }

        if (!player.isAlive())
            return;

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
}
