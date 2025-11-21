package towergame.model.status;

import towergame.model.entities.AEntity;


public class BoostStatus implements IStatusEffect {
    private int duration;
    private double boostMultiplier;

    public BoostStatus(int duration, double boostMultiplier) {
        this.duration = duration;
        this.boostMultiplier = boostMultiplier;
    }

    @Override
    public int modifyDamageDealt(int damage) {
        return (int) (damage * boostMultiplier);
    }

    @Override
    public int modifyDamageTaken(int damage) {
        return damage;
    }

    @Override
    public void onTurnEnd(AEntity target) {
        // Aucun effet particulier à la fin du tour
    }

    @Override
    public void onTurnStart(AEntity target) {
        // Aucun effet particulier au début du tour
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
        return "Boost";
    }
}
