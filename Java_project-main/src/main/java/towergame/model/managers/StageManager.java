package towergame.model.managers;

import towergame.model.actions.*;
import towergame.model.entities.ABoss;
import towergame.model.entities.FireElementalBoss;

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

    // Crée toutes les 9 actions du joueur ("grimoire")
    private void loadAllActions() {

        // On charge 4 sorts existants pour que le joueur puisse jouer.
        this.unlockedActions.add(new PlayerHealSpell("Soin Léger", Element.NEUTRAL, 25, 2));
        this.unlockedActions.add(new PlayerDefendSpell("Barrière", Element.NEUTRAL, 2, 3));
        this.unlockedActions.add(new FireSpell("Fragiliser", Element.PLANT, 5, 3, 3)); // Applique Weaken
        this.unlockedActions.add(new WaterSpell("Jet de Glace", Element.WATER, 5, 2, 2)); // Applique Entrave

        // L'ancienne logique de fallback n'est plus nécessaire.
        if (this.unlockedActions.isEmpty()) {
            System.out.println("StageManager: ERREUR CRITIQUE - Aucun sort n'a pu être chargé !");
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
