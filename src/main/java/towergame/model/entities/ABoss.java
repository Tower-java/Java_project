package towergame.model.entities;

import towergame.model.actions.AAction;
import towergame.model.actions.Element;
import towergame.model.entities.Player;

import java.util.List;
import java.util.ArrayList;

/**
 * Classe abstraite pour un Boss.
 */
public abstract class ABoss extends AEntity {

    private int attackPoints;
    private int healPoints;
    protected boolean isEnraged;
    protected boolean isInvulnerable;

    protected List<AAction> actionScript;
    protected int scriptIndex;

    public ABoss(String name, int maxHp, Element element, int attackPoints, int healPoints) {
        super(name, maxHp, element);
        this.attackPoints = attackPoints;
        this.healPoints = healPoints;
        this.isEnraged = false;
        this.isInvulnerable = false;
        this.scriptIndex = 0;
        this.actionScript = new ArrayList<>();
    }

    @Override
    public void takeDamage(int amount) {
        if (this.isInvulnerable) {
            System.out.println(this.name + " est invulnérable et ne subit aucun dégât !");
            return;
        }
        super.takeDamage(amount);
    }

    public void setActionScript(List<AAction> script) {
        this.actionScript = script;
    }

    public AAction getNextAction() {
        AAction nextAction = this.actionScript.get(this.scriptIndex);
        this.scriptIndex = (this.scriptIndex + 1) % this.actionScript.size();
        return nextAction;
    }

    public List<AAction> getActionScript() {
        return this.actionScript;
    }

    public int getAttackPoints() {
        return this.attackPoints;
    }

    public int getHealPoints() {
        return this.healPoints;
    }

    public boolean isEnraged() {
        return this.isEnraged;
    }

    public boolean isInvulnerable() {
        return this.isInvulnerable;
    }

    // Méthode ajoutée pour les besoins des tests
    public void setInvulnerable(boolean isInvulnerable) {
        this.isInvulnerable = isInvulnerable;
    }

    // Méthode ajoutée pour les besoins des tests
    public void setEnraged(boolean isEnraged) {
        this.isEnraged = isEnraged;
    }
}
