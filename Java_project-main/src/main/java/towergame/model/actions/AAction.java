
package towergame.model.actions;

import towergame.model.entities.AEntity;
import towergame.model.actions.Element;

/**
 * Classe abstraite représentant une action qu'une entité peut effectuer en
 * combat.
 * Gère le nom, l'élément et le système de cooldown de l'action.
 */
public abstract class AAction {
    protected String name;
    protected Element element;
    protected int cooldownDuration;
    protected int currentCooldown;

    /**
     * Constructeur pour une AAction.
     *
     * @param name             Le nom de l'action (ex: "Boule de Feu").
     * @param element          L'élément de l'action.
     * @param cooldownDuration Le nombre de tours à attendre avant de pouvoir la
     *                         réutiliser.
     */
    public AAction(String name, Element element, int cooldownDuration) {
        this.name = name;
        this.element = element;
        this.cooldownDuration = cooldownDuration;
        this.currentCooldown = 0; // Une action est toujours prête au début.
    }

    /**
     * Exécute la logique de l'action.
     * Doit être implémentée par chaque classe d'action concrète.
     *
     * @param user   L'entité qui utilise l'action.
     * @param target L'entité ciblée par l'action.
     */
    public abstract void execute(AEntity user, AEntity target);

    /**
     * Vérifie si l'action est prête à être utilisée.
     * 
     * @return true si le cooldown est à 0, false sinon.
     */
    public boolean isReady() {
        return this.currentCooldown <= 0;
    }

    /**
     * Démarre le cooldown de l'action après son utilisation.
     * Copie la durée totale (ex: 3) dans le compteur actuel.
     */
    public void startCooldown() {
        this.currentCooldown = this.cooldownDuration;
    }

    /**
     * Réduit le cooldown de 1.
     * (À appeler par le BattleManager à la fin de chaque tour pour chaque action).
     */
    public void updateCooldown() {
        if (this.currentCooldown > 0) {
            this.currentCooldown--;
        }
    }

    // GETTERS

    public String getName() {
        return this.name;
    }

    public Element getElement() {
        return this.element;
    }

    public int getCurrentCooldown() {
        return this.currentCooldown;
    }
}
