package towergame.model.actions;

import towergame.model.entities.AEntity;
import towergame.model.entities.ABoss;

public class BossHealAction extends AAction {
    private double multiplier;

    public BossHealAction(String name, Element element, int cooldownDuration, double multiplier) {
        super(name, element, cooldownDuration);
        this.multiplier = multiplier;
    }

    @Override
    public void execute(AEntity user, AEntity target) {
        if (!(user instanceof ABoss)) {
            System.out.println("Erreur : BossHealAction utilisée par un non-Boss !");
            return;
        }
        ABoss boss = (ABoss) user;
        int healPoints = boss.getHealPoints();
        double finalHeal = healPoints * this.multiplier;
        System.out.println(user.getName() + " utilise " + this.name + " !");
        user.heal((int) finalHeal);
        this.startCooldown();
    }
}
