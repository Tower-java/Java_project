public class BossAttackAction extends AAction {
    // Attributs
    private double multiplier;

    // Constructeur
    /**
     * Crée une action d'attaque pour un boss.
     * @param name Le nom de l'attaque (ex: "Morsure")
     * @param element L'élément de l'attaque
     * @param multiplier Le multiplicateur de dégâts selon l'attaque, different de la roue elementaire (ex: 0.5, 1.0, 2.0)
     * @param cooldown Le temps de recharge en tours
     */
    public BossAttackAction(String name, Element element, int cooldownDuration, double multiplier) {
        super(name, element, cooldownDuration);
        this.multiplier = multiplier;
    }

    // Méthodes héritées 

    @Override
    public void execute(AEntity user, AEntity target) {
        
        // 3a. Vérifier que l'utilisateur est bien un Boss
        // (Sinon, on ne peut pas getAttackPoints())
        if (!(user instanceof ABoss)) {
            System.out.println("Erreur : BossAttackAction utilisée par un non-Boss !");
            return;
        }
        
        // "Cast" de user en ABoss pour accéder à ses méthodes
        ABoss boss = (ABoss) user;

        // 3b. Récupérer les stats de base du boss
        int attackPoints = boss.getAttackPoints();
        
        // 3c. Calculer les dégâts
        // Dégâts de base = (Stat * Multiplicateur de l'action)
        double baseDamage = attackPoints * this.multiplier;
        
        // Dégâts finaux = Dégâts de base * Multiplicateur élémentaire
        double finalDamage = baseDamage * this.element.getMultiplierAgainst(target.getElement());

        // 3d. Afficher l'action et appliquer les dégâts
        System.out.println(user.getName() + " utilise " + this.name + " !");
        
        // La méthode takeDamage s'occupera d'afficher les dégâts subis.
        target.takeDamage((int)finalDamage);
        
        this.startCooldown();
    }
}