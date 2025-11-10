public class BossHealAction extends AAction {
    // Attributs
    private double multiplier;

    // Constructeur
    /**
     * Crée une action de soin pour un boss.
     * @param name Le nom du soin (ex: "Régénération")
     * @param element L'élément de l'action (généralement NEUTRAL pour un soin)
     * @param cooldownDuration Le temps de recharge en tours
     * @param multiplier Le multiplicateur de soin (ex: 1.0 pour un soin normal, 2.0 pour un grand soin)
     */
    public BossHealAction(String name, Element element, int cooldownDuration, double multiplier) {
        super(name, element, cooldownDuration);
        this.multiplier = multiplier;
    }

    // Méthodes héritées 

    @Override
    public void execute(AEntity user, AEntity target) {
        
        // 3a. Vérifier que l'utilisateur est bien un Boss
        // (Sinon, on ne peut pas getHealPoints())
        if (!(user instanceof ABoss)) {
            System.out.println("Erreur : BossHealAction utilisée par un non-Boss !");
            return;
        }
        
        // "Cast" de user en ABoss pour accéder à ses méthodes
        ABoss boss = (ABoss) user;

        // 3b. Récupérer les stats de base du boss
        int healPoints = boss.getHealPoints();
        
        // 3c. Calculer le montant du soin
        // Soin final = (Stat de soin * Multiplicateur de l'action)
        double finalHeal = healPoints * this.multiplier;
        
        // 3d. Afficher l'action et appliquer le soin à l'utilisateur (le boss lui-même)
        System.out.println(user.getName() + " utilise " + this.name + " !");
        
        // Utilisation de la méthode heal()
        user.heal((int)finalHeal);
        
        this.startCooldown();
    }
}