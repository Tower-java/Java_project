package towergame.model.actions;

import towergame.model.entities.AEntity;
import towergame.model.status.*;

public class WaterHardSpell extends AAction {
    private int baseDamage;
    private int statusDuration;

    public WaterHardSpell(String name, Element element, int baseDamage, int statusDuration, int coolDownDuration) {
        super(name, element, coolDownDuration);
        this.baseDamage = baseDamage;
        this.statusDuration = statusDuration;
    }

    @Override
    public void execute(AEntity user, AEntity target) {
        if (isReady()) {
            double finalDamage = baseDamage * this.element.getMultiplierAgainst(target.getElement());
            target.takeDamage((int) finalDamage);
            EntraveStatus entrave = new EntraveStatus(statusDuration, 0.8);
            target.addStatus(entrave);
            startCooldown();
        }
    }
}
