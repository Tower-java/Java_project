# 🛠️ Guide d'Extension Rapide - Tower Battle

## 📋 Facilité d'Extension Actuelle

Le code est **parfaitement conçu pour l'extension facile** ! Voici comment ajouter de nouveaux éléments sans casser le code existant :

---

## ⚔️ Ajouter un Nouveau Sort en 2 Minutes

### 🎯 Exemple Concret : Sort "Tempête de Glace"

**1. Créer le sort dans StageManager :**

```java
// Dans loadAllActions() - UNE SEULE LIGNE !
this.unlockedActions.add(new ElementalSpell("Tempête de Glace", Element.WATER, 15, 3, 4,
    ElementalSpell.StatusType.POISON, 5.0));
```

**2. C'est tout !** ✅ Le sort apparaît automatiquement dans l'interface

### 🔧 Paramètres Expliqués

- **"Tempête de Glace"** : Nom affiché
- **Element.WATER** : Type élémentaire
- **15** : Dégâts de base
- **3** : Durée de l'effet de statut
- **4** : Cooldown en tours
- **POISON, 5.0** : Empoisonne pour 5 dégâts/tour

### 🎨 Support Automatique

✅ **Interface** : Bouton créé automatiquement  
✅ **Messages** : Descriptions générées  
✅ **Effets** : Animation et couleurs appliquées  
✅ **Équilibrage** : Système de cooldown intégré

---

## 👹 Ajouter un Nouveau Boss en 5 Minutes

### 🎯 Exemple Concret : "Golem de Pierre"

**1. Créer la classe Boss :**

```java
package towergame.model.entities;

public class StoneGolemBoss extends ABoss {

    public StoneGolemBoss() {
        // Nom, HP, Élément, AttackPoints, HealPoints
        super("Golem de Pierre", 150, Element.PLANT, 12, 8);

        // Gimmick spécial
        this.isInvulnerable = false; // Vulnérable dès le début

        // Script d'attaques
        List<AAction> script = List.of(
            new BossAttackAction("Coup de Poing Rocheux", Element.PLANT, 0, 1.2),
            new BossAttackAction("Jet de Pierres", Element.NEUTRAL, 0, 0.9),
            new BossDefendAction("Armure de Roche", 3, 2, 15),
            new BossHealAction("Régénération Terrestre", Element.PLANT, 0, 1.5)
        );

        this.setActionScript(script);
    }

    @Override
    public void checkGimmick(Player player, AAction playerAction, int turnNumber) {
        // Gimmick : Devient enragé si attaqué par FIRE
        if (!this.isEnraged && playerAction.getElement() == Element.FIRE) {
            this.isEnraged = true;
            // Message automatique d'enrage géré par GameWindow
        }
    }
}
```

**2. L'ajouter dans StageManager :**

```java
// Dans loadAllBosses() - UNE SEULE LIGNE !
this.bossList.add(new StoneGolemBoss());
```

**3. C'est tout !** ✅ Le boss apparaît dans la séquence

### 🎯 Fonctionnalités Automatiques

✅ **Interface** : Affichage HP, nom, statuts  
✅ **Combat** : Système de tour, résistances  
✅ **Gimmicks** : Mécaniques spéciales personnalisables  
✅ **Animations** : Effets visuels intégrés

---

## 🌟 Ajouter un Nouvel Élément (Avancé)

### 🎯 Exemple : Element.FOUDRE

**1. Modifier l'enum Element :**

```java
public enum Element {
    FIRE, WATER, PLANT, FOUDRE, NEUTRAL; // Ajouter FOUDRE

    public Element getStrongAgainst() {
        switch (this) {
            case FOUDRE: return WATER; // La foudre bat l'eau
            // ... autres cas
        }
    }

    public Element getWeakAgainst() {
        switch (this) {
            case FOUDRE: return PLANT; // La foudre faible contre plante
            // ... autres cas
        }
    }
}
```

**2. Créer des sorts foudre :**

```java
this.unlockedActions.add(new ElementalSpell("Éclair Foudroyant", Element.FOUDRE, 20, 2, 3,
    ElementalSpell.StatusType.ENTRAVE, 0.0));
```

**3. Boss foudre :**

```java
public class ThunderDragonBoss extends ABoss {
    public ThunderDragonBoss() {
        super("Dragon Tonnerre", 120, Element.FOUDRE, 14, 6);
        // ... reste identique
    }
}
```

---

## 🎭 Ajouter un Nouvel Effet de Statut

### 🎯 Exemple : "Burn" (Brûlure)

**1. Créer la classe d'effet :**

```java
package towergame.model.status;

public class BurnStatus implements IStatusEffect {
    private int duration;
    private int damagePerTurn;

    public BurnStatus(int duration, int damagePerTurn) {
        this.duration = duration;
        this.damagePerTurn = damagePerTurn;
    }

    @Override
    public void onTurnEnd(AEntity target) {
        target.takeDamage(damagePerTurn);
        System.out.println(target.getName() + " brûle et subit " + damagePerTurn + " dégâts !");
    }

    // ... autres méthodes IStatusEffect
}
```

**2. L'ajouter à ElementalSpell :**

```java
public enum StatusType {
    WEAKEN, ENTRAVE, POISON, BOOST, BURN // Ajouter BURN
}

private IStatusEffect createStatusEffect() {
    switch (statusType) {
        case BURN:
            return new BurnStatus(statusDuration, (int) statusStrength);
        // ... autres cas
    }
}
```

**3. Utiliser dans les sorts :**

```java
this.unlockedActions.add(new ElementalSpell("Boule de Feu", Element.FIRE, 12, 4, 3,
    ElementalSpell.StatusType.BURN, 3.0)); // Brûle pour 3 dégâts/tour
```

---

## 🏗️ Architecture Extensible - Résumé

### ✅ Points Forts Actuels

| Composant             | Facilité d'Extension | Effort Requis             |
| --------------------- | -------------------- | ------------------------- |
| **Nouveaux Sorts**    | ⭐⭐⭐⭐⭐           | 1 ligne de code           |
| **Nouveaux Boss**     | ⭐⭐⭐⭐⭐           | 1 classe + 1 ligne        |
| **Nouveaux Éléments** | ⭐⭐⭐⭐             | Modification enum + sorts |
| **Nouveaux Statuts**  | ⭐⭐⭐⭐             | 1 classe + ajout enum     |
| **Interface**         | ⭐⭐⭐⭐⭐           | Automatique (0 code)      |

### 🎯 Patterns de Conception Utilisés

1. **Factory Pattern** : `createStatusEffect()` crée les effets
2. **Strategy Pattern** : `checkGimmick()` pour mécaniques boss
3. **Template Method** : `AAction.execute()` structure commune
4. **Builder Pattern** : Constructeurs ElementalSpell flexibles

### 📦 Composants Découplés

✅ **StageManager** : Point central d'ajout  
✅ **ElementalSpell** : Classe générique extensible  
✅ **ABoss** : Base réutilisable pour tous les boss  
✅ **GameWindow** : Interface auto-adaptive

---

## 🎮 Test d'Extension en Live

**Pour la démonstration, vous pouvez :**

1. **Ajouter un sort en 30 secondes** (live coding)
2. **Montrer un nouveau boss** (préparé à l'avance)
3. **Expliquer l'architecture** (diagrammes si besoin)

### 💡 Arguments de Vente

> _"Notre architecture permet d'ajouter **un nouveau sort en 1 ligne** et **un nouveau boss en 5 minutes** sans casser le code existant. L'interface s'adapte automatiquement, et tous les systèmes (effets, animations, messages) fonctionnent immédiatement."_

---

**Conclusion :** Le code est **parfaitement préparé pour l'extension rapide** ! 🚀 L'architecture modulaire permet d'impressionner les correcteurs avec des ajouts en temps réel.
