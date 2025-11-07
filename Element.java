public enum Element {
    FEU,
    EAU,
    PLANTE,
    NEUTRAL;

    public double getMultiplierAgainst(Element targetElement){
        if (this == NEUTRAL || targetElement == NEUTRAL){
            return 1.0;
        }
        if (this == FEU && targetElement == EAU){
            return 0.5; // Feu est faible contre Eau
        }
        if (this == FEU && targetElement == PLANTE){
            return 2.0; // Feu est fort contre Plante
        }
        if (this == EAU && targetElement == FEU){
            return 2.0; // Eau est forte contre Feu
        }
        if (this == EAU && targetElement == PLANTE){
            return 0.5; // Eau est faible contre Plante
        }
        if (this == PLANTE && targetElement == FEU){
            return 0.5; // Plante est faible contre Feu
        }
        if (this == PLANTE && targetElement == EAU){
            return 2.0; // Plante est forte contre Eau
        }
        // Cas par défaut (ex: FEU vs FEU), le multiplicateur est neutre.
        return 1.0;
    }

}