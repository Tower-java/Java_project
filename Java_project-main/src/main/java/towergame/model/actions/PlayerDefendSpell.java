package towergame.model.actions;

import towergame.model.entities.AEntity;
import towergame.model.status.DefendStatus;


public class PlayerDefendSpell extends AAction {
    private int duration;

    public PlayerDefendSpell(String name, Element element, int duration, int coolDownDuration) {
        super(name, element, coolDownDuration);
        this.duration = duration;
    }

    @Override
    public void execute(AEntity user, AEntity target) {
        if (isReady()) {
            // La défense s'applique à l'utilisateur du sort (le joueur)
            DefendStatus defend = new DefendStatus(duration, 10);
            user.addStatus(defend);
            startCooldown();
        }
    }
}
