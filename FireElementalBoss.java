import java.util.List;
import java.util.ArrayList;

public class FireElementalBoss extends ABoss {
    // Constructeur
    public FireElementalBoss(){
        super("Élémentaire de feu", 100, Element.FEU, 10, 10);
        // On active invulnérabilité
        this.isInvulnerable = true; 
    // 2. On crée la liste d'actions (le script) avec List.of()
        List<AAction> script = List.of(
           
        // Tour 1: Attaque de feu (100% de 10 ATK = 10 dégâts)
            new BossAttackAction("Jet de Flammes", Element.FEU, 0, 1.0),
            
        // Tour 2: Morsure (80% de 10 ATK = 8 dégâts)
            new BossAttackAction("Morsure", Element.NEUTRAL, 0, 0.8),
            
        // Tour 3: Défense (Bloque 10 PV, dure 1 tour, 2 tours de CD)
            new BossDefendAction("Carapace de Magma", 2, 1, 10),
            
        // Tour 4: Grosse attaque (150% de 10 ATK = 15 dégâts)
            new BossAttackAction("Explosion", Element.FEU, 0, 1.5),

        // Tour 5: Attaque de feu (100% de 10 ATK = 10 dégâts)
            new BossAttackAction("Jet de Flammes", Element.FEU, 0, 1.0),
            
        // Tour 6: Morsure (80% de 10 ATK = 8 dégâts)
            new BossAttackAction("Morsure", Element.NEUTRAL, 0, 0.8),
            
        // Tour 7: Défense (Bloque 10 PV, dure 1 tour, 2 tours de CD)
            new BossDefendAction("Carapace de Magma", 2, 1, 10),
            
        // Tour 8: Grosse attaque (150% de 10 ATK = 15 dégâts)
            new BossAttackAction("Explosion", Element.FEU, 0, 1.5),

        // Tour 9: Attaque de feu (100% de 10 ATK = 10 dégâts)
            new BossAttackAction("Jet de Flammes", Element.FEU, 0, 1.0),
            
        // Tour 10: Morsure (80% de 10 ATK = 8 dégâts)
            new BossAttackAction("Morsure", Element.NEUTRAL, 0, 0.8),
            
        // Tour 11: Défense (Bloque 10 PV, dure 1 tour, 2 tours de CD)
            new BossDefendAction("Carapace de Magma", 2, 1, 10),
            
        // Tour 12: Grosse attaque (150% de 10 ATK = 15 dégâts)
            new BossAttackAction("Explosion", Element.FEU, 0, 1.5)
            );

        // 3. On charge ce script dans le boss
        this.setActionScript(script);
        
    }
    
/**

     * Gimmick : L'Élémentaire est invulnérable. (On suppose qu'il a un attribut 'isInvulnerable').
     * Il perd son invulnérabilité s'il est touché par une action de FEU
     * au Tour 3 OU au Tour 5.
     *
     * @param player Le joueur (au cas où vous en auriez besoin)
     * @param playerAction L'action que le joueur vient d'utiliser CE tour-ci
     * @param turnNumber Le numéro du tour actuel (quand le joueur joue)
     */
    @Override
    /**
     * Gimmick : L'Élémentaire est invulnérable. Il perd son invulnérabilité s'il est touché par une action de FEU au Tour 3 OU au Tour 5.
     * Il devient enragé si il est touché par une attaque EAU avant le tour 4, ou si ses PV tombent sous 20%.
     */
    public void checkGimmick(Player player, AAction playerAction, int turnNumber) {
        
        // --- GIMMICK 1 : Perdre l'invulnérabilité ---
        // On ne vérifie cette logique que si le boss est encore invulnérable.
        if (this.isInvulnerable) {
            // 1a. Vérifier si c'est le bon tour (3 ou 5)
            if (turnNumber == 3 || turnNumber == 5) {
                // 1b. Si c'est le bon tour, VÉRIFIER L'ÉLÉMENT
                if (playerAction.getElement() == Element.FEU) {
                    // Gimmick réussi !
                    System.out.println(this.name + " rugit alors que son bouclier magique se brise !");
                    this.isInvulnerable = false; // Désactive l'invulnérabilité
                }
            }
        }

        // --- GIMMICK 2 : Devenir Enragé ---
        // On vérifie cette logique à chaque tour, tant que le boss n'est pas déjà enragé.
        if (!this.isEnraged) { 
            // Condition 1: Touché par une attaque EAU au tour 4 ou avant
            if (turnNumber <= 4 && playerAction.getElement() == Element.EAU) {
                System.out.println(this.name + " est aspergé d'eau et entre dans une rage folle !");
                this.isEnraged = true;
            }
            // Condition 2: PV en dessous de 20%
            // Note: Cette vérification se fait APRES que le joueur a attaqué, donc les PV sont à jour.
            else if (this.getHp() <= this.getMaxHp() * 0.20) {
                System.out.println(this.name + ", sentant la défaite approcher, déchaîne sa fureur !");
                this.isEnraged = true;
            }
        }
    }
}