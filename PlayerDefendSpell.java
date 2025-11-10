public class PlayerDefendSpell extends Action {
    private int duration;

    public PlayerDefendSpell(String name, Element element, int duration, int coolDownDuration) {
        this.name = name;
        this.element = element;
        this.duration = duration;
        this.coolDownDuration = coolDownDuration;
        this.currentCooldown = 0;
    }

    @Override
    public void execute(Entity user, Entity target) {
        if (isReady()) {
            DefendStatus defend = new DefendStatus(duration, 10);
            target.addStatusEffect(defend);
            startCooldown();
        }
    }
}
