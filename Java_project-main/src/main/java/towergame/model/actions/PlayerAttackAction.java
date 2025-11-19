package towergame.model.actions;

import towergame.model.entities.AEntity;

/**
 * Action d'attaque basique du joueur.
 * Inflige des dégâts directs à l'ennemi.
 */
public class PlayerAttackAction extends AAction {
    private int damage;

    public PlayerAttackAction(String name, Element element, int cooldownDuration, int damage) {
        super(name, element, cooldownDuration);
        this.damage = damage;
    }

    @Override
    public void execute(AEntity user, AEntity target) {
        if (isReady() && target != null) {
            target.takeDamage(damage);
            startCooldown();
        }
    }
}