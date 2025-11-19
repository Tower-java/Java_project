package towergame.model.actions;

import towergame.model.entities.AEntity;
import towergame.model.status.BoostStatus;


public class PlayerBoostSpell extends AAction {
    private int duration;

    public PlayerBoostSpell(String name, Element element, int duration, int coolDownDuration) {
        super(name, element, coolDownDuration);
        this.duration = duration;
    }

    @Override
    public void execute(AEntity user, AEntity target) {
        if (isReady()) {
            // Le boost s'applique à l'utilisateur du sort (le joueur)
            BoostStatus boost = new BoostStatus(duration, 1.5);
            user.addStatus(boost);
            startCooldown();
        }
    }
}
