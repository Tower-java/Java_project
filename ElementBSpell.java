public class ElementBSpell extends Action {
    private int baseDamage;
    private int statusDuration;

    public ElementBSpell(String name, Element element, int baseDamage, int statusDuration, int coolDownDuration) {
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
            EntraveStatus entrave = new EntraveStatus(statusDuration);
            target.addStatusEffect(entrave);
            startCooldown();
        }
    }
}
