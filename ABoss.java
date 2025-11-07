import java.util.List;
import java.util.ArrayList;

/**
 * Classe abstraite pour un Boss.
 * Hérite de AEntity et ajoute la logique spécifique au boss :
 * - Stats de base (attackPoints, healPoints)
 * - Un script d'action (actionScript)
 * - Un gimmick de puzzle (checkGimmick)
 */
public abstract class ABoss extends AEntity {

    // Attributs protégés pour que les enfants (GolemBoss) puissent les voir
    protected int attackPoints;
    protected int healPoints;
    protected boolean isEnraged;
    
    protected List<AAction> actionScript;
    protected int scriptIndex;

    /**
     * Constructeur pour un Boss.
     *
     * @param name Nom du boss
     * @param maxHp Points de vie maximum
     * @param element Élément du boss
     * @param attackPoints Stat d'attaque de base
     * @param healPoints Stat de soin de base
     */
    public ABoss(String name, int maxHp, Element element, int attackPoints, int healPoints) {
        
        // 1. Appelle le constructeur de la classe mère (AEntity)
        super(name, maxHp, element);
        
        // 2. Initialise ses propres attributs
        this.attackPoints = attackPoints;
        this.healPoints = healPoints;
        this.isEnraged = false; // Par défaut, non enragé
        this.scriptIndex = 0;   // Commence au début du script
        this.actionScript = new ArrayList<>(); // Initialisation pour éviter NullPointerException
    }

    /**
     * C'EST LE PUZZLE.
     * Méthode abstraite que chaque boss concret (GolemBoss) DOIT implémenter.
     * C'est là que vous codez la logique unique du "gimmick" (ex: "si tour 3, deviens enragé").
     *
     * @param player L'entité joueur, pour vérifier ses actions ou statuts.
     * @param turnNumber Le numéro du tour actuel.
     */
    public abstract void checkGimmick(Player player, int turnNumber);

    /**
     * Charge le scénario d'attaques du boss.
     * Appelée par le constructeur du boss concret (ex: GolemBoss).
     *
     * @param script La liste des Actions que le boss exécutera.
     */
    public void setActionScript(List<AAction> script) {
        this.actionScript = script;
    }

    /**
     * Récupère la prochaine action du script.
     * Avance l'index et boucle à la fin.
     * 
     * @return L'action que le boss doit jouer ce tour-ci.
     */
    public AAction getNextAction() {
        // 1. Récupère l'action à l'index actuel
        AAction nextAction = this.actionScript.get(this.scriptIndex);
        
        // 2. Avance l'index pour le prochain tour (en utilisant le modulo pour boucler)
        this.scriptIndex = (this.scriptIndex + 1) % this.actionScript.size();
        
        // 3. Retourne l'action
        return nextAction;
    }

    // --- GETTERS ---
    // (Nécessaires pour que les BossAttackAction puissent lire ces stats)
    
    public int getAttackPoints() {
        return this.attackPoints;
    }

    public int getHealPoints() {
        return this.healPoints;
    }
    
    public boolean isEnraged() {
        return this.isEnraged;
    }
}