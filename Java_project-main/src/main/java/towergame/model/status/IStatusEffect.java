package towergame.model.status;

import towergame.model.entities.AEntity;


//  Interface pour tous les effets de statut (bonus/malus) qui peuvent affecter une entité.
public interface IStatusEffect {

    /**
     * Modifie les dégâts infligés par une entité.
     * 
     * @param damage Les dégâts de base.
     * @return Les dégâts modifiés.
     */
    int modifyDamageDealt(int damage);

    /**
     * Modifie les dégâts subis par une entité.
     * 
     * @param damage Les dégâts de base.
     * @return Les dégâts modifiés.
     */
    int modifyDamageTaken(int damage);

    /**
     * Action à exécuter à la fin du tour de l'entité affectée (ex: dégâts de
     * poison).
     * 
     * @param target L'entité affectée.
     */
    void onTurnEnd(AEntity target);

    /**
     * Action à exécuter au début du tour de l'entité affectée (ex: entrave).
     * 
     * @param target L'entité affectée.
     */
    void onTurnStart(AEntity target);

    int getDuration();

    void updateDuration();

    boolean isDone();

    String getName();

}
