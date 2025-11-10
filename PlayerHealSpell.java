public class PlayerHealSpell extends Action {
    private int healAmount;

    public PlayerHealSpell(String name, Element element, int healAmount, int coolDownDuration) {
        this.name = name;
        this.element = element;
        this.healAmount = healAmount;
        this.coolDownDuration = coolDownDuration;
        this.currentCooldown = 0;
    }

    @Override
    public void execute(Entity user, Entity target) {
        if (isReady()) {
            target.heal(healAmount);
            startCooldown();
        }
    }
}
