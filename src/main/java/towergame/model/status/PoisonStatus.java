package towergame.model.status;

import towergame.model.entities.AEntity;


public class PoisonStatus implements IStatusEffect {
    private int duration;
    private int poisonDamage;

    public PoisonStatus(int duration, int poisonDamage) {
        this.duration = duration;
        this.poisonDamage = poisonDamage;
    }

    @Override
    public int modifyDamageDealt(int damage) {
        return damage;
    }

    @Override
    public int modifyDamageTaken(int damage) {
        return damage;
    }

    @Override
    public void onTurnEnd(AEntity target) {
        target.takeDamage(poisonDamage);
        // L'appel à updateDuration() est supprimé d'ici car il est déjà géré par AEntity.updateStatusEffects
    }

    @Override
    public void onTurnStart(AEntity target) {
        // Aucun effet au début du tour
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public void updateDuration() {
        duration--;
    }

    @Override
    public boolean isDone() {
        return duration <= 0;
    }

    @Override
    public String getName() {
        return "Poison";
    }
}
