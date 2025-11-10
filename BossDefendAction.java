public class BossDefendAction extends AAction {
    
    // Attributs
    private int duration; // Combien de tours dure le bouclier
    private int blockAmount; // Combien de dégâts le bouclier bloque

    /**
     * Crée une action de défense pour un boss.
     * @param name Le nom de l'action (ex: "Carapace")
     * @param cooldownDuration Le temps de recharge en tours
     * @param duration Le nombre de tours pendant lesquels le bouclier est actif
     * @param blockAmount Le montant de dégâts bloqués par le bouclier
     */

    public BossDefendAction(String name, int cooldownDuration, int duration, int blockAmount) {

        // 1. Envoyer les infos communes au parent (AAction)
        super(name, Element.NEUTRAL, cooldownDuration);
        
        // 2. Initialiser les attributs spécifiques à cette classe
        this.duration = duration;
        this.blockAmount = blockAmount;
    }

    @Override
    public void execute(AEntity user, AEntity target) {
        
        // 3a. Appliquer le statut de défense à l'utilisateur (le boss lui-même)
        // On utilise les attributs de cette classe pour configurer le DefendStatus.
        user.addStatus(new DefendStatus(this.duration, this.blockAmount)); 
        
        System.out.println(user.getName() + " utilise " + this.name + " !");

        // 3b. Démarrer le minuteur de cooldown
        this.startCooldown();
    }
}