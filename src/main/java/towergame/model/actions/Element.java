package towergame.model.actions;

public enum Element {
    FIRE, WATER, PLANT, NEUTRAL;

    private Element strongAgainst;
    private Element weakAgainst;

    static {
        FIRE.strongAgainst = PLANT;
        FIRE.weakAgainst = WATER;

        WATER.strongAgainst = FIRE;
        WATER.weakAgainst = PLANT;

        PLANT.strongAgainst = WATER;
        PLANT.weakAgainst = FIRE;

        NEUTRAL.strongAgainst = null;
        NEUTRAL.weakAgainst = null;
    }

    /**
     * Calcule le multiplicateur de dégâts de cet élément contre un autre.
     * @param targetElement L'élément de la cible.
     * @return 2.0 si fort, 0.5 si faible, 1.0 sinon.
     */
    public double getMultiplierAgainst(Element targetElement){
        if (targetElement == null || this == NEUTRAL || targetElement == NEUTRAL){
            return 1.0;
        }
        if (targetElement == this.strongAgainst) return 2.0;
        if (targetElement == this.weakAgainst) return 0.5;
        return 1.0; // Cas neutre (ex: FIRE vs FIRE)
    }
}