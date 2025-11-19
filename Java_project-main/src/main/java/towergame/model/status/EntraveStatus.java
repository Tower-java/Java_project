package towergame.model.status;

import towergame.model.entities.AEntity;


public class EntraveStatus implements IStatusEffect {
    private int duration;

    public EntraveStatus(int duration) {
        this.duration = duration;
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
        // Aucun effet à la fin du tour
    }

    @Override
    public void onTurnStart(AEntity target) {
        // Ici tu peux gérer la restriction ou immobilisation
        // L'appel à updateDuration() est supprimé pour centraliser la logique.
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
        return "Entrave";
    }
}
