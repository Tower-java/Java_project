package towergame.model.actions;

import towergame.model.entities.AEntity;
import towergame.model.entities.Player;

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
        if (isReady() && user instanceof Player && target != null) {
            Player player = (Player) user;

            // Récupère le multiplicateur de dégâts actuel du joueur
            double multiplier = player.getDamageMultiplier();

            // Calcule les dégâts finaux en appliquant le boost
            int finalDamage = (int) (this.damage * multiplier);

            // Inflige les dégâts à la cible
            target.takeDamage(finalDamage);
            startCooldown();
        }
    }
}