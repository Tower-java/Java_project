package towergame.model.entities;

import towergame.model.actions.Element;
import towergame.model.status.IStatusEffect;


import java.util.List;
import java.util.ArrayList;


/**
 * Classe abstraite représentant une entité de combat (joueur, boss, etc.).
 * Gère les statistiques de base et les effets de statut.
 */
public abstract class AEntity{
    protected String name;
    protected int hp;
    protected int maxHp;
    protected Element element;
    protected List<IStatusEffect> activeStatus;

    // Constructeur
    public AEntity(String name, int maxHp, Element element){
        this.name = name;
        this.maxHp = maxHp;
        this.hp = this.maxHp; 
        this.element = element;
        this.activeStatus = new ArrayList<>();
    }

    // Méthodes

    /**
     * Applique des dégâts à l'entité, en prenant en compte les effets de statut.
     * @param amount La quantité de dégâts bruts.
     */
    public void takeDamage(int amount){
        int finalDamage = amount;
        // On parcourt les effets pour modifier les dégâts subis
        for (IStatusEffect effect : activeStatus) {
            finalDamage = effect.modifyDamageTaken(finalDamage);
        }
        this.hp -= finalDamage;
        // On s'assure que les PV ne tombent pas en dessous de 0
        if (this.hp < 0) {
            this.hp = 0;
        }
        System.out.println(this.name + " subi " + finalDamage + " dégâts!");
    }

    public void heal(int amount){
        this.hp += amount;
        if (this.hp > this.maxHp){
            this.hp = this.maxHp;
        }
        System.out.println(this.name + " se soigne de " + amount + " PV!");
    }

    public void addStatus(IStatusEffect status){
        this.activeStatus.add(status);
        System.out.println(this.name + " est maintenant affecté par " + status.getName() + "!");
    }

    /**
     * Met à jour tous les effets de statut actifs sur l'entité.
     * Appelle onTurnEnd() et updateDuration() sur chaque effet.
     */
    public void updateStatusEffects() {
        System.out.println("DEBUG: --- Début de AEntity.updateStatusEffects ---");
        activeStatus.removeIf(status -> {
            System.out.println("DEBUG: Traitement du statut '" + status.getName() + "'");
            
            System.out.println("DEBUG: Appel de onTurnEnd...");
            status.onTurnEnd(this);
            
            System.out.println("DEBUG: Appel de updateDuration...");
            status.updateDuration();
            
            boolean done = status.isDone();
            System.out.println("DEBUG: Le statut est terminé (isDone) ? " + done);
            return done;
        });
        System.out.println("DEBUG: --- Fin de AEntity.updateStatusEffects ---");
    }

    public boolean isAlive(){
        return this.hp > 0;
    }

    public boolean hasEffect(String effectName){
        for (IStatusEffect status : this.activeStatus){
            if (status.getName().equalsIgnoreCase(effectName)){
                return true;
            }
        }
        return false;
    }

    // Getters
    public String getName(){
        return this.name;
    }
    public Element getElement(){
        return this.element;
    }
    public int getHp(){
        return this.hp;
    }
    public int getMaxHp(){
        return this.maxHp;
    }
    public List<IStatusEffect> getActiveStatus(){
        return this.activeStatus;
    }
}