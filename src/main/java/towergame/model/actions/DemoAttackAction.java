package towergame.model.actions;

import towergame.model.entities.AEntity;
import towergame.model.entities.Player;
import towergame.model.entities.ABoss;

/**
 * Action d'attaque pour la démonstration.
 * Inflige des dégâts massifs et IGNORE l'invulnérabilité du boss.
 * Permet de one-shot n'importe quel boss de manière fluide pour la
 * présentation.
 */
public class DemoAttackAction extends AAction {
    private int damage;

    public DemoAttackAction(String name, Element element, int cooldownDuration, int damage) {
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

            // Si la cible est un boss invulnérable, désactiver temporairement
            // l'invulnérabilité
            if (target instanceof ABoss) {
                ABoss boss = (ABoss) target;
                boolean wasInvulnerable = boss.isInvulnerable();
                boss.setInvulnerable(false); // Désactiver l'invulnérabilité temporairement

                // Inflige les dégâts à la cible
                target.takeDamage(finalDamage);

                // Restaurer l'état d'invulnérabilité si nécessaire
                boss.setInvulnerable(wasInvulnerable);
            } else {
                // Pour les entités non-boss, inflige simplement les dégâts
                target.takeDamage(finalDamage);
            }

            startCooldown();
        }
    }
}
