package towergame.model.actions;

import towergame.model.entities.AEntity;
import towergame.model.entities.ABoss;

public class BossAttackAction extends AAction {
    private double multiplier;

    public BossAttackAction(String name, Element element, int cooldownDuration, double multiplier) {
        super(name, element, cooldownDuration);
        this.multiplier = multiplier;
    }

    public BossAttackAction(String name, int cooldownDuration, double multiplier) {
        this(name, Element.NEUTRAL, cooldownDuration, multiplier);
    }

    @Override
    public void execute(AEntity user, AEntity target) {
        if (!(user instanceof ABoss)) {
            System.out.println("Erreur : BossAttackAction utilisée par un non-Boss !");
            return;
        }
        ABoss boss = (ABoss) user;
        int attackPoints = boss.getAttackPoints();
        double baseDamage = attackPoints * this.multiplier;
        double finalDamage = baseDamage * this.element.getMultiplierAgainst(target.getElement());
        System.out.println(user.getName() + " utilise " + this.name + " !");
        target.takeDamage((int) finalDamage);
        this.startCooldown();
    }
}
