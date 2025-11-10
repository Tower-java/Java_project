import java.util.List;
import java.util.ArrayList;

public class StageManager {

    // Attributs
    // La liste complète de tous les sorts/actions qui existent dans le jeu
    private List<AAction> unlockedActions; 
    // La liste de tous les boss à affronter, dans l'ordre
    private List<ABoss> bossList; 
    // Un index pour savoir où on en est (quel boss est le prochain)
    private int currentStage;

    // Constructeur
    public StageManager() {
        // Initialiser les listes
        this.unlockedActions = new ArrayList<>();
        this.bossList = new ArrayList<>();
        this.currentStage = 0; // On commence au premier boss

        // Appeler les méthodes privées pour charger le contenu : 
        // C'EST ICI QUE TOUT EST INSTANCIE
        loadAllActions();
        loadAllBosses();
    }

    //Crée toutes les 9 actions du joueur ("grimoire")
    private void loadAllActions() {
        
        // --- Famille des Sorts Neutres ---
        // TODO: Décommenter ces lignes lorsque les classes correspondantes seront créées.
        // this.unlockedActions.add(new PlayerHealSpell("Soin Léger", Element.NEUTRAL, 2, 25));
        // this.unlockedActions.add(new PlayerBoostSpell("Inspiration", Element.NEUTRAL, 4, 2));
        // this.unlockedActions.add(new PlayerWeakenSpell("Fragiliser", Element.NEUTRAL, 3, 3));

        // --- Famille des Sorts Élémentaires (Feu/Eau/Plante) ---
        // this.unlockedActions.add(new ElementFeuSpell("Brûlure", Element.FEU, 0, 5, 3)); // Statut B (Poison)
        // this.unlockedActions.add(new ElementFeuHardSpell("Incinérer", Element.FEU, 3, 15, 3)); // Statut B (Poison)
        // this.unlockedActions.add(new ElementEauSpell("Jet de Glace", Element.EAU, 0, 5, 2)); // Statut A (Entrave)
        // this.unlockedActions.add(new ElementEauHardSpell("Blizzard", Element.EAU, 3, 15, 2)); // Statut A (Entrave)
        // this.unlockedActions.add(new ElementPlanteSpell("Fouet Végétal", Element.PLANTE, 0, 5, 3)); // Statut C (Weaken)
        // this.unlockedActions.add(new ElementPlanteHardSpell("Tempête de Lames", Element.PLANTE, 3, 15, 3)); // Statut C (Weaken)

        // Logique de "fallback" : Si aucune action n'est chargée (par exemple, les classes n'existent pas encore),
        // on ajoute des actions de test pour que le jeu puisse démarrer.
        if (this.unlockedActions.isEmpty()) {
            System.out.println("StageManager: Aucune action réelle chargée. Ajout d'actions de test.");
            this.unlockedActions.add(new PlayerAttackAction("Attaque de FEU", Element.FEU, 0, 10));
            this.unlockedActions.add(new PlayerAttackAction("Attaque d'EAU", Element.EAU, 0, 10));
            this.unlockedActions.add(new PlayerAttackAction("Attaque NEUTRE", Element.NEUTRAL, 0, 8));
        }
    }

    /**
     * Crée tous les boss du jeu, dans l'ordre
     */
    private void loadAllBosses() {
        // On instancie et on ajoute notre premier boss à la liste !
        this.bossList.add(new FireElementalBoss());
        // this.bossList.add(new WaterSerpentBoss()); // etc.
    }

    // --- Méthodes Publiques (pour le GameEngine) ---

    public ABoss getNextBoss() {
        if (currentStage < bossList.size()) {
            ABoss nextBoss = bossList.get(currentStage);
            currentStage++; // Correction: Incrémenter le stage pour le prochain appel
            return nextBoss;
        }
        return null; // Plus de boss
    }

    public List<AAction> getUnlockedActions() {
        return this.unlockedActions;
    }
}
