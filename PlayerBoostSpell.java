public class PlayerBoostSpell extends Action {
    private int duration;

    public PlayerBoostSpell(String name, Element element, int duration, int coolDownDuration) {
        this.name = name;
        this.element = element;
        this.duration = duration;
        this.coolDownDuration = coolDownDuration;
        this.currentCooldown = 0;
    }

    @Override
    public void execute(Entity user, Entity target) {
        if (isReady()) {
            BoostStatus boost = new BoostStatus(duration, 1.5);
            target.addStatusEffect(boost);
            startCooldown();
        }
    }
}
