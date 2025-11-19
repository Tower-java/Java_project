package towergame.model.entities;

import towergame.model.actions.*;
import towergame.model.entities.Player;
import towergame.view.ConsoleView;

import java.util.List;

public class FireElementalBoss extends ABoss {
    // Constructeur
    public FireElementalBoss() {
        super("Élémentaire de feu", 100, Element.FIRE, 10, 10);
        // On active invulnérabilité
        this.isInvulnerable = true;

        // 2. On crée la liste d'actions (le script) avec List.of() pour les 12 tours
        List<AAction> script = List.of(
                // === CYCLE 1 ===
                // Tour 1: Attaque de feu (100% de 10 ATK = 10 dégâts)
                new BossAttackAction("Jet de Flammes", Element.FIRE, 0, 1.0),
                // Tour 2: Morsure (80% de 10 ATK = 8 dégâts)
                new BossAttackAction("Morsure", Element.NEUTRAL, 0, 0.8),
                // Tour 3: Défense (Bloque 10 PV, dure 1 tour, 2 tours de CD)
                new BossDefendAction("Carapace de Magma", 2, 1, 10),
                // Tour 4: Grosse attaque (150% de 10 ATK = 15 dégâts)
                new BossAttackAction("Explosion", Element.FIRE, 0, 1.5),

                // === CYCLE 2 ===
                // Tour 5: Attaque de feu
                new BossAttackAction("Jet de Flammes", Element.FIRE, 0, 1.0),
                // Tour 6: Morsure
                new BossAttackAction("Morsure", Element.NEUTRAL, 0, 0.8),
                // Tour 7: Défense
                new BossDefendAction("Carapace de Magma", 2, 1, 10),
                // Tour 8: Grosse attaque
                new BossAttackAction("Explosion", Element.FIRE, 0, 1.5),

                // === CYCLE 3 ===
                // Tour 9: Attaque de feu
                new BossAttackAction("Jet de Flammes", Element.FIRE, 0, 1.0),
                // Tour 10: Morsure
                new BossAttackAction("Morsure", Element.NEUTRAL, 0, 0.8),
                // Tour 11: Défense
                new BossDefendAction("Carapace de Magma", 2, 1, 10),
                // Tour 12: Grosse attaque
                new BossAttackAction("Explosion", Element.FIRE, 0, 1.5));

        // 3. On charge ce script dans le boss
        this.setActionScript(script);
    }

    /**
     * Calcule le modificateur de dégâts basé sur les résistances/faiblesses
     * élémentaires
     * 
     * @param attackElement L'élément de l'attaque
     * @return Le modificateur de dégâts (0.5 = résistance, 1.0 = normal, 1.5 =
     *         faiblesse)
     */

    /**
     * Vérifie si l'attaque déclenche une résistance
     */
    public boolean isResistant(Element attackElement) {
        return attackElement == Element.FIRE || attackElement == Element.PLANT;
    }

    /**
     * Vérifie si l'attaque déclenche une faiblesse
     */
    public boolean isWeak(Element attackElement) {
        return attackElement == Element.WATER;
    }

    @Override
    /**
     * Gimmick : L'Élémentaire est invulnérable. Il perd son invulnérabilité s'il
     * est touché par une action de FIRE au Tour 3 OU au Tour 5.
     * Il devient enragé si il est touché par une attaque WATER avant le tour 4, ou si
     * ses PV tombent sous 20%.
     */
    public void checkGimmick(Player player, AAction playerAction, int turnNumber) {
        ConsoleView view = new ConsoleView(System.in, System.out);

        // --- GIMMICK 1 : Perdre l'invulnérabilité ---
        // On ne vérifie cette logique que si le boss est encore invulnérable.
        if (this.isInvulnerable) {
            // 1a. Vérifier si c'est le bon tour (3 ou 5)
            if (turnNumber == 3 || turnNumber == 5) {
                // 1b. Si c'est le bon tour, VÉRIFIER L'ÉLÉMENT
                if (playerAction.getElement() == Element.FIRE) {
                    // Gimmick réussi !
                    System.out.println(this.getName() + " rugit alors que son bouclier magique se brise !");
                    this.isInvulnerable = false; // Désactive l'invulnérabilité
                }
            }
        }

        // --- GIMMICK 2 : Devenir Enragé ---
        // On vérifie cette logique à chaque tour, tant que le boss n'est pas déjà
        // enragé.
        if (!this.isEnraged) {
            // Condition 1: Touché par une attaque WATER au tour 4 ou avant
            if (turnNumber <= 4 && playerAction.getElement() == Element.WATER) {
                view.displayBossEnrage(this);
                this.isEnraged = true;
            }
            // Condition 2: PV en dessous de 20%
            // Note: Cette vérification se fait APRES que le joueur a attaqué, donc les PV
            // sont à jour.
            else if (this.getHp() <= this.getMaxHp() * 0.20) {
                view.displayBossEnrage(this);
                this.isEnraged = true;
            }
        }
    }
}