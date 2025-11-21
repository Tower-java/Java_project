package towergame.model.actions;

import towergame.model.entities.AEntity;


public class PlayerHealSpell extends AAction {
    private int healAmount;

    public PlayerHealSpell(String name, Element element, int healAmount, int coolDownDuration) {
        // Appel au constructeur de la classe mère AAction
        super(name, element, coolDownDuration);
        
        this.healAmount = healAmount;
    }

    @Override
    public void execute(AEntity user, AEntity target) {
        if (isReady()) {
            target.heal(healAmount);
            startCooldown();
        }
    }
}
