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

    // Crée toutes les actions du joueur
    private void loadAllActions() {
        // Charger TOUS les sorts disponibles (10 actions)
        this.unlockedActions.add(new PlayerAttackAction("Attaque", Element.NEUTRAL, 0, 10));
        this.unlockedActions.add(new PlayerHealSpell("Soin Léger", Element.NEUTRAL, 25, 2));
        this.unlockedActions.add(new PlayerDefendSpell("Barrière", Element.NEUTRAL, 2, 3));
        this.unlockedActions.add(new PlayerBoostSpell("Fureur", Element.NEUTRAL, 3, 3));
        this.unlockedActions.add(new FireSpell("Coup de Feu", Element.FIRE, 5, 0, 0));
        this.unlockedActions.add(new WaterSpell("Jet de Glace", Element.WATER, 5, 0, 0));
        this.unlockedActions.add(new PlantSpell("Lianes", Element.PLANT, 5, 0, 0));
        this.unlockedActions.add(new FireHardSpell("Inferno", Element.FIRE, 10, 3, 2));
        this.unlockedActions.add(new WaterHardSpell("Blizzard", Element.WATER, 10, 3, 2));
        this.unlockedActions.add(new PlantHardSpell("Encracinement", Element.PLANT, 10, 3, 2));

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
            // On récupère le boss à l'index actuel, PUIS on incrémente pour le prochain
            // appel.
            ABoss nextBoss = bossList.get(currentStage++);
            return nextBoss;
        }
        return null; // Plus de boss
    }

    public List<AAction> getUnlockedActions() {
        return this.unlockedActions;
    }
}
