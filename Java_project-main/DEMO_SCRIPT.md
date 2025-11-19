# 🎯 Scripts de Démonstration Live - Extension Facile

## ⚡ Nouveau Sort en 30 Secondes

**Ouvrez :** `StageManager.java` → méthode `loadAllActions()`

**Ajoutez cette ligne :**

```java
// Nouveau sort puissant pour la démo
this.unlockedActions.add(new ElementalSpell("Nova de Feu", Element.FIRE, 25, 4, 5,
    ElementalSpell.StatusType.POISON, 8.0));
```

**Résultat :**

- ✅ Sort "Nova de Feu" apparaît dans l'interface
- ✅ 25 dégâts + empoisonnement 8 dégâts/tour pendant 4 tours
- ✅ Cooldown de 5 tours pour équilibrer
- ✅ Interface s'adapte automatiquement

---

## 👹 Nouveau Boss en 2 Minutes

**1. Copiez le fichier :** `DEMO_IceGolemBoss.java` → `src/main/java/towergame/model/entities/IceGolemBoss.java`

**2. Dans StageManager.java, ajoutez :**

```java
// Dans loadAllBosses()
this.bossList.add(new IceGolemBoss()); // UNE SEULE LIGNE !
```

**Résultat :**

- ✅ Boss "Golem de Glace" ajouté après le boss de feu
- ✅ 130 HP, attaques glaciales, gimmicks uniques
- ✅ Mécaniques : Enrage avec feu + invulnérabilité temporaire
- ✅ Interface gère tout automatiquement

---

## 📊 Points à Souligner Pendant la Démo

### 🏗️ Architecture Modulaire

> "Regardez, j'ajoute un sort complexe en **1 ligne de code**. L'interface, les effets, les animations - tout fonctionne immédiatement sans modification."

### 🔧 Extensibilité Boss

> "Notre système ABoss permet de créer des mécaniques uniques. Ce boss de glace a des gimmicks complètement différents, mais s'intègre parfaitement."

### 🎨 Auto-Adaptation Interface

> "L'interface GameWindow s'adapte automatiquement. Nouveaux boutons, nouvelles couleurs, nouveaux messages - aucun code GUI à modifier."

### ⚖️ Équilibrage Intégré

> "Le système de cooldown et de multiplicateurs permet un équilibrage fin sans refonte du code."

---

## 🎭 Scénario de Présentation

**Étape 1 :** Montrer le jeu actuel (1-2 minutes)  
**Étape 2 :** "Et si on veut ajouter du contenu ?"  
**Étape 3 :** Live coding nouveau sort (30 sec)  
**Étape 4 :** Compilation et test (30 sec)  
**Étape 5 :** "Un boss ? Aussi simple !" (1 minute)  
**Étape 6 :** Test du nouveau boss (1 minute)

### 💡 Phrases d'Impact

- _"1 ligne de code = 1 nouveau sort complet"_
- _"Architecture découplée = ajouts sans risques"_
- _"Factory Pattern = création automatisée d'effets"_
- _"Template Method = garantie de cohérence"_

---

## ⚠️ Préparation Technique

1. **Backup** : Sauvegardez `StageManager.java` avant démo
2. **Test** : Vérifiez que les exemples compilent
3. **Timing** : Chronométrez vos ajouts
4. **Recovery** : Préparez un rollback rapide si problème

**Temps total estimé :** 3-4 minutes pour impressionner les correcteurs ! 🚀
