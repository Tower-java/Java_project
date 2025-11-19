package towergame.model.status;

import towergame.model.entities.AEntity;


public class WeakenStatus implements IStatusEffect {
    private int duration;
    private double weakenMultiplier;

    public WeakenStatus(int duration, double weakenMultiplier) {
        this.duration = duration;
        this.weakenMultiplier = weakenMultiplier;
    }

    @Override
    public int modifyDamageDealt(int damage) {
        return damage;
    }

    @Override
    public int modifyDamageTaken(int damage) {
        // La cible est affaiblie, elle subit donc le double des dégâts.
        // Le weakenMultiplier n'est plus utilisé ici, on pourrait le supprimer.
        return damage * 2;
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
        return "Weaken";
    }
}
