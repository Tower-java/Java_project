package towergame.model.actions;

import towergame.model.entities.AEntity;
import towergame.model.entities.Player;

public class PlayerBoostSpell extends AAction {
    private final int boostDuration;
    private static final double DAMAGE_MULTIPLIER = 1.5; // Boost de 50%

    /**
     * Crée un sort qui booste les dégâts du joueur.
     * @param name Le nom du sort.
     * @param element L'élément du sort.
     * @param boostDuration La durée du boost en nombre de tours.
     * @param cooldown Le temps de rechargement du sort en nombre de tours.
     */
    public PlayerBoostSpell(String name, Element element, int boostDuration, int cooldown) {
        super(name, element, cooldown);
        this.boostDuration = boostDuration;
    }

    @Override
    public void execute(AEntity user, AEntity target) {
        if (isReady() && user instanceof Player) {
            Player player = (Player) user;
            player.applyDamageBoost(DAMAGE_MULTIPLIER, this.boostDuration);
            startCooldown();
        }
    }
}