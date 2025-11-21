package towergame.model.entities;

import towergame.model.actions.AAction;
import towergame.model.actions.Element;


import java.util.List;
import java.util.ArrayList;

public class Player extends AEntity {
    // Attributs
    private List<AAction> equippedActions;
    private double damageMultiplier = 1.0;
    private int damageBoostDuration = 0;

    // Constructeur
    /**
     * Crée un joueur. Le joueur a toujours 100 PV max et est de type NEUTRE.
     * 
     * @param name Le nom du joueur.
     */
    public Player(String name) {
        // Les statistiques du joueur sont fixes : 100 PV, élément NEUTRE.
        super(name, 100, Element.NEUTRAL);
        this.equippedActions = new ArrayList<>();
    }

    // Méthodes
    /**
     * Définit les 4 actions que le joueur utilisera pour ce combat.
     * (Appelé par le StageManager avant le début du combat).
     *
     * @param actions La liste des 4 actions choisies.
     */
    public void setEquippedActions(List<AAction> actions) {
        this.equippedActions = actions;
    }

    /**
     * Récupère la liste des actions équipées.
     * (Appelé par le BattleManager pour afficher l'interface).
     *
     * @return La liste des 4 actions.
     */
    public List<AAction> getEquippedActions() {
        return this.equippedActions;
    }

    /**
     * Applique un boost de dégâts au joueur pour une certaine durée.
     * Si un boost est déjà actif, celui-ci est remplacé par le nouveau.
     * @param multiplier Le multiplicateur de dégâts (ex: 1.5 pour +50%).
     * @param duration Le nombre de tours pendant lequel le boost est actif.
     */
    public void applyDamageBoost(double multiplier, int duration) {
        this.damageMultiplier = multiplier;
        this.damageBoostDuration = duration;
    }

    /**
     * Récupère le multiplicateur de dégâts actuel du joueur.
     * @return Le multiplicateur de dégâts.
     */
    public double getDamageMultiplier() {
        return this.damageMultiplier;
    }

    /**
     * Met à jour les statuts du joueur à la fin de son tour.
     * (Par exemple, réduit la durée des boosts).
     */
    public void updateStatusEffects() {
        if (this.damageBoostDuration > 0) {
            this.damageBoostDuration--;
            if (this.damageBoostDuration == 0) {
                this.damageMultiplier = 1.0; // Réinitialise le boost
            }
        }
    }
}
