package towergame.model.entities;

import towergame.model.actions.*;
import towergame.view.ConsoleView;

import java.util.List;

/**
 * Boss de type Plante.
 */
public class PlantElementalBoss extends ABoss {

    public PlantElementalBoss() {
        super("Géant Végétal", 120, Element.PLANT, 8, 12);

        // Par défaut, pas d'invulnérabilité
        this.isInvulnerable = false;

        // Script d'actions : cycle simple
        List<AAction> script = List.of(
                // Tour 1 : Attaque racines (PLANT)
                new BossAttackAction("Racines Enserrantes", Element.PLANT, 0, 1.0),
                // Tour 2 : Piqure (neutre)
                new BossAttackAction("Piqûre", Element.NEUTRAL, 0, 0.8),
                // Tour 3 : Défense (bloque une quantité)
                new BossDefendAction("Carapace de Lierre", 2, 1, 12),
                // Tour 4 : Barrage d'épines (grosse attaque)
                new BossAttackAction("Barrage d'Épines", Element.PLANT, 0, 1.5),

                // Cycle répété
                new BossAttackAction("Racines Enserrantes", Element.PLANT, 0, 1.0),
                new BossAttackAction("Piqûre", Element.NEUTRAL, 0, 0.8),
                new BossDefendAction("Carapace de Lierre", 2, 1, 12),
                new BossAttackAction("Barrage d'Épines", Element.PLANT, 0, 1.5),

                new BossAttackAction("Racines Enserrantes", Element.PLANT, 0, 1.0),
                new BossAttackAction("Piqûre", Element.NEUTRAL, 0, 0.8),
                new BossDefendAction("Carapace de Lierre", 2, 1, 12),
                new BossAttackAction("Barrage d'Épines", Element.PLANT, 0, 1.5)
        );

        this.setActionScript(script);
    }

    /**
     * Le boss Plante est résistant aux attaques de type PLANT (auto-résistance)
     * et vulnérable au FEU.
     */
    public boolean isResistant(Element attackElement) {
        return attackElement == Element.PLANT;
    }

    public boolean isWeak(Element attackElement) {
        return attackElement == Element.FIRE;
    }

    @Override
    public void checkGimmick(Player player, AAction playerAction, int turnNumber) {
        ConsoleView view = new ConsoleView(System.in, System.out);

        // Gimmick : Si touché par une attaque FEU tôt (tour <=3), le boss devient enragé
        if (!this.isEnraged) {
            if (turnNumber <= 3 && playerAction.getElement() == Element.FIRE) {
                view.displayBossEnrage(this);
                this.isEnraged = true;
            } else if (this.getHp() <= this.getMaxHp() * 0.25) {
                // Si PV en dessous de 25%, le boss s'enracine (enrage)
                view.displayBossEnrage(this);
                this.isEnraged = true;
            }
        }
    }
}
