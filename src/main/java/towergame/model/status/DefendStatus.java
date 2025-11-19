package towergame.model.status;

import towergame.model.entities.AEntity;


public class DefendStatus implements IStatusEffect {
    private int duration;
    private int damageBlockAmount;

    public DefendStatus(int duration, int damageBlockAmount) {
        this.duration = duration;
        this.damageBlockAmount = damageBlockAmount;
    }

    @Override
    public int modifyDamageDealt(int damage) {
        return damage;
    }

    @Override
    public int modifyDamageTaken(int damage) {
        int reduced = damage - damageBlockAmount;
        return Math.max(reduced, 0);
    }

    @Override
    public void onTurnEnd(AEntity target) {
        // Aucun effet à la fin du tour
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
        return "Defend";
    }
}
