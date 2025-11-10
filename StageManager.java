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
        // (Nom, Élément, Cooldown, Montant du soin)
        // TODO: Décommenter ces lignes lorsque les classes correspondantes seront créées.
        /*
        this.unlockedActions.add(new PlayerHealSpell("Soin Léger", Element.NEUTRAL, 2, 25));
        
        // (Nom, Élément, Cooldown, Durée du statut)
        this.unlockedActions.add(new PlayerBoostSpell("Inspiration", Element.NEUTRAL, 4, 2));
        
        // (Nom, Élément, Cooldown, Durée du statut)
        this.unlockedActions.add(new PlayerWeakenSpell("Fragiliser", Element.NEUTRAL, 3, 3));

        // --- Famille des Sorts Élémentaires (Feu/Eau/Plante) ---
        
        // (Nom, Élément, Cooldown, Dégâts, Durée du statut)
        this.unlockedActions.add(new ElementFeuSpell("Brûlure", Element.FEU, 0, 5, 3)); // Statut B (Poison)
        this.unlockedActions.add(new ElementFeuHardSpell("Incinérer", Element.FEU, 3, 15, 3)); // Statut B (Poison)
        
        this.unlockedActions.add(new ElementEauSpell("Jet de Glace", Element.EAU, 0, 5, 2)); // Statut A (Entrave)
        this.unlockedActions.add(new ElementEauHardSpell("Blizzard", Element.EAU, 3, 15, 2)); // Statut A (Entrave)
        
        this.unlockedActions.add(new ElementPlanteSpell("Fouet Végétal", Element.PLANTE, 0, 5, 3)); // Statut C (Weaken)
        this.unlockedActions.add(new ElementPlanteHardSpell("Tempête de Lames", Element.PLANTE, 3, 15, 3)); // Statut C (Weaken)
        this.unlockedActions.add(new ElementFeuSpell("Brûlure", Element.FEU, 0, 5, 3));
        // ... etc pour toutes les actions
        */
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
            return bossList.get(currentStage);
            // Plus tard, vous ajouterez : currentStage++;
        }
        return null; // Plus de boss
    }

    public List<AAction> getUnlockedActions() {
        return this.unlockedActions;
    }
}
