package towergame.model.actions;

import towergame.model.entities.AEntity;
import towergame.model.status.DefendStatus;


public class BossDefendAction extends AAction {
    private int duration;
    private int blockAmount;

    public BossDefendAction(String name, int cooldownDuration, int duration, int blockAmount) {
        super(name, Element.NEUTRAL, cooldownDuration);
        this.duration = duration;
        this.blockAmount = blockAmount;
    }

    public int getBlockAmount(){
        return this.blockAmount;
    }

    @Override
    public void execute(AEntity user, AEntity target) {
        if (isReady()) {
            user.addStatus(new DefendStatus(this.duration, this.blockAmount));
            System.out.println(user.getName() + " utilise " + this.name + " !");
            this.startCooldown();
        }
    }
}
