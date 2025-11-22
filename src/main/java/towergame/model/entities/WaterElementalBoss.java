package towergame.model.entities;

import java.util.List;

import towergame.model.actions.AAction;
import towergame.model.actions.BossAttackAction;
import towergame.model.actions.BossDefendAction;
import towergame.model.actions.Element;

/**
 * Représente le boss "Élémentaire d'eau".
 * Ce boss possède une gimmick spécifique liée à son invulnérabilité et à un
 * état enragé.
 * Son script d'actions est basé sur des cycles de 4 tours.
 */
public class WaterElementalBoss extends ABoss {
    /**
     * Construit un nouveau boss Élémentaire d'eau.
     * Initialise ses statistiques, son état d'invulnérabilité et son script
     * d'actions pour le combat.
     */
    public WaterElementalBoss() {
        super("Élémentaire d'eau", 100, Element.WATER, 10, 10);
        // On active invulnérabilité
        this.isInvulnerable = true;

        // 2. On crée la liste d'actions (le script) avec List.of() pour les 12 tours
        List<AAction> script = List.of(
                // === CYCLE 1 ===
                // Tour 1: Attaque d'eau (0% de 10 ATK = 0 dégâts)
                new BossAttackAction("Trempette", Element.WATER, 0, 0),
                // Tour 2: Morsure (80% de 10 ATK = 8 dégâts)
                new BossAttackAction("Morsure", Element.NEUTRAL, 0, 0.8),
                // Tour 3: Défense (Bloque 1 PV, dure 1 tour, 2 tours de CD)
                new BossDefendAction("Carapace d'eau liquide", 2, 1, 1),
                // Tour 4: Grosse attaque (200% de 10 ATK = 20 dégâts)
                new BossAttackAction("Éclaboussure mortelle", Element.WATER, 0, 2),

                // === CYCLE 2 ===
                // Tour 5: Attaque d'eau
                new BossAttackAction("Trempette", Element.WATER, 0, 0),
                // Tour 6: Morsure (80% de 10 ATK = 8 dégâts)
                new BossAttackAction("Morsure", Element.NEUTRAL, 0, 0.8),
                // Tour 7: Défense (Bloque 1 PV, dure 1 tour, 2 tours de CD)
                new BossDefendAction("Carapace d'eau liquide", 2, 1, 1),
                // Tour 8: Grosse attaque (200% de 10 ATK = 20 dégâts)
                new BossAttackAction("Éclaboussure mortelle", Element.WATER, 0, 2),

                // === CYCLE 3 ===
                // Tour 9: Attaque d'eau
                new BossAttackAction("Trempette", Element.WATER, 0, 0),
                // Tour 10: Morsure (80% de 10 ATK = 8 dégâts)
                new BossAttackAction("Morsure", Element.NEUTRAL, 0, 0.8),
                // Tour 11: Défense (Bloque 1 PV, dure 1 tour, 2 tours de CD)
                new BossDefendAction("Carapace d'eau liquide", 2, 1, 1),
                // Tour 12: Grosse attaque (200% de 10 ATK = 20 dégâts)
                new BossAttackAction("Éclaboussure mortelle", Element.WATER, 0, 2));

        // 3. On charge ce script dans le boss
        this.setActionScript(script);
    }

    /**
     * Énumération des événements spéciaux pouvant être déclenchés par la gimmick du
     * boss.
     */
    public enum GimmickEvent {
        /**
         * Se déclenche lorsque le bouclier d'invulnérabilité du boss est brisé.
         */
        SHIELD_BROKEN,
        /**
         * Se déclenche lorsque le boss entre en état de rage.
         */
        ENRAGED,
        /**
         * Indique qu'aucun événement spécial ne s'est produit.
         */
        NONE // Aucun événement spécial
    }

    /**
     * Gimmick : L'Élémentaire est invulnérable. Il perd son invulnérabilité s'il
     * est touché par une action de FIRE au Tour 1 OU au Tour 2.
     * Il devient enragé s'il est touché par une attaque FIRE après le tour 4, ou
     * si ses PV tombent sous 40%.
     * 
     * @param player       Le joueur qui exécute l'action.
     * @param playerAction L'action effectuée par le joueur ce tour-ci.
     * @param turnNumber   Le numéro du tour actuel.
     * @return GimmickEvent pour signaler si un événement spécial a eu lieu.
     */
    public GimmickEvent checkGimmick(Player player, AAction playerAction, int turnNumber) {
        // DEBUG: Log les informations du gimmick
        System.out.println("DEBUG GIMMICK: Tour=" + turnNumber + ", Invulnérable=" + this.isInvulnerable +
                ", ActionName=" + playerAction.getName() + ", Element=" + playerAction.getElement() +
                ", BossHP=" + this.hp + "/" + this.maxHp);

        // --- GIMMICK 1 : Perdre l'invulnérabilité ---
        // On ne vérifie cette logique que si le boss est encore invulnérable.
        if (this.isInvulnerable) {
            // 1a. Vérifier si c'est le bon tour (1 ou 2)
            if (turnNumber == 1 || turnNumber == 2) {
                System.out.println("DEBUG: Tour 1-2 détecté. Vérification élément...");
                // 1b. Si c'est le bon tour, VÉRIFIER L'ÉLÉMENT
                if (playerAction.getElement() == Element.FIRE) {
                    // Gimmick réussi !
                    System.out.println(this.getName() + " rugit alors que sa fine couche de glace protectrice fond !");
                    this.isInvulnerable = false; // Désactive l'invulnérabilité
                    return GimmickEvent.SHIELD_BROKEN;
                } else {
                    System.out.println("DEBUG: Élément incorrect pour briser l'invulnérabilité. Attendu: FIRE, Reçu: "
                            + playerAction.getElement());
                }
            } else {
                System.out.println("DEBUG: Pas le bon tour pour briser l'invulnérabilité (tours 1-2 uniquement)");
            }
        }

        // --- GIMMICK 2 : Devenir Enragé ---
        // On vérifie cette logique à chaque tour, tant que le boss n'est pas déjà
        // enragé.
        if (!this.isEnraged && !this.isInvulnerable) {
            // Condition 1: Touché par une attaque FIRE après le tour 4
            if (turnNumber > 4 && playerAction.getElement() == Element.FIRE) {
                System.out.println("DEBUG: Enrage déclenché par attaque FIRE après tour 4!");
                this.isEnraged = true;
                return GimmickEvent.ENRAGED;
            }
            // Condition 2: PV en dessous de 40%
            // Note: Cette vérification se fait APRES que le joueur a attaqué, donc les PV
            // sont à jour.
            else if (this.getHp() <= this.getMaxHp() * 0.40) {
                System.out.println("DEBUG: Enrage déclenché par PV < 40% (HP=" + this.hp + ", Limite="
                        + (this.maxHp * 0.40) + ")");
                this.isEnraged = true;
                return GimmickEvent.ENRAGED;
            } else {
                System.out.println("DEBUG: Pas d'enrage (HP=" + this.hp + "/" + this.maxHp + ", Limite=40%)");
            }
        }

        return GimmickEvent.NONE;
    }
}