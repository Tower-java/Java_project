package towergame.model.entities;

import towergame.model.actions.AAction;
import towergame.model.actions.Element;


import java.util.List;
import java.util.ArrayList;

public class Player extends AEntity {
    // Attributs
    private List<AAction> equippedActions;

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

}
