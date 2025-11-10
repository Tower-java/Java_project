public class ElementCSpell extends Action {
    private int baseDamage;
    private int statusDuration;

    public ElementCSpell(String name, Element element, int baseDamage, int statusDuration, int coolDownDuration) {
        this.name = name;
        this.element = element;
        this.baseDamage = baseDamage;
        this.statusDuration = statusDuration;
        this.coolDownDuration = coolDownDuration;
        this.currentCooldown = 0;
    }

    @Override
    public void execute(Entity user, Entity target) {
        if (isReady()) {
            target.takeDamage(baseDamage);
            PoisonStatus poison = new PoisonStatus(statusDuration, 7);
            target.addStatusEffect(poison);
            startCooldown();
        }
    }
}
