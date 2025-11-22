package towergame.model.status;

import towergame.model.entities.AEntity;

/**
 * Statut qui entrave une entité, réduisant les dégâts qu'elle inflige.
 */
public class EntraveStatus implements IStatusEffect {
    private int duration;
    private double damageMultiplier; // e.g., 0.8 pour une réduction de 20% des dégâts

    public EntraveStatus(int duration, double damageMultiplier) {
        this.duration = duration;
        this.damageMultiplier = damageMultiplier;
    }

    @Override
    public int modifyDamageDealt(int damage) {
        // Réduit les dégâts infligés par l'entité
        return (int) (damage * damageMultiplier);
    }

    @Override
    public int modifyDamageTaken(int damage) {
        // N'affecte pas les dégâts subis
        return damage;
    }

    @Override
    public void onTurnEnd(AEntity target) {
        // Aucun effet
    }

    @Override
    public void onTurnStart(AEntity target) {
        // Aucun effet
    }

    @Override
    public int getDuration() { return duration; }

    @Override
    public void updateDuration() { duration--; }

    @Override
    public boolean isDone() { return duration <= 0; }

    @Override
    public String getName() { return "Entrave"; }
}