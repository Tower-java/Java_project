package towergame.model.actions;

import towergame.model.entities.AEntity;
import towergame.model.status.*;


public class PlantSpell extends AAction {
    private int baseDamage;
    private int statusDuration;

    public PlantSpell(String name, Element element, int baseDamage, int statusDuration, int coolDownDuration) {
        super(name, element, coolDownDuration);
        this.baseDamage = baseDamage;
        this.statusDuration = statusDuration;
    }

    @Override
    public void execute(AEntity user, AEntity target) {
        if (isReady()) {
            double finalDamage = baseDamage * this.element.getMultiplierAgainst(target.getElement());
            target.takeDamage((int) finalDamage);
            WeakenStatus weaken = new WeakenStatus(statusDuration, 0.2);
            target.addStatus(weaken);
            startCooldown();
        }
    }
}
