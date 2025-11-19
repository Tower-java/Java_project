package towergame.model.managers;

import towergame.model.actions.AAction;
import towergame.model.actions.Element;
import towergame.model.entities.ABoss;
import towergame.model.entities.Player;

import java.util.List;

public class SuccessTracker {

    /**
     * Vérifie les succès débloqués à la fin d'un combat.
     * Appelée par le BattleManager.
     *
     * @param player             L'instance du joueur (pour ses PV finaux)
     * @param boss               Le boss qui vient d'être vaincu (POUR LES SUCCÈS
     *                           SPÉCIFIQUES)
     * @param turnNumber         Le nombre total de tours du combat
     * @param actionsUsedHistory La liste de toutes les actions utilisées
     */
    public static void checkAchievements(Player player, ABoss boss, int turnNumber, List<AAction> actionsUsedHistory) {

        String bossName = boss.getName(); // On récupère le nom du boss

        // --- Succès Spécifiques au Golem ---
        if (bossName.equals("Golem de Pierre")) {

            if (turnNumber <= 8) {
                System.out.println("SUCCÈS : Briseur de Pierre (Golem battu en 8 tours ou moins !)");
            }

            // (Ici, on vérifie si le joueur a utilisé l'élément WATER)
            boolean usedWater = false;
            for (AAction action : actionsUsedHistory) {
                if (action.getElement() == Element.WATER) {
                    usedWater = true;
                    break;
                }
            }
            if (!usedWater) {
                System.out.println("SUCCÈS : Intouchable (Golem battu sans utiliser d'Eau !)");
            }
        }

        // --- Succès Spécifiques au Dragon ---
        if (bossName.equals("Dragon Ancien")) {
            if (turnNumber <= 15) {
                System.out.println("SUCCÈS : Dompteur de Dragon (Dragon battu en 15 tours ou moins !)");
            }
        }

        // --- Succès Globaux (toujours vérifiés) ---
        if (player.getHp() == 1) {
            System.out.println("SUCCÈS DÉBLOQUÉ : Au Bord du Gouffre (Gagné avec 1 PV restant !)");
        }
    }
}
