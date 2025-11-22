# Guide de Test - Fire Attack Sprite

## 🎯 Objectif

Vérifier que le sprite **fire_attack.png** s'affiche **UNIQUEMENT** lorsque le boss attaque le héros.

## 📋 Comportement Correct

### ✅ CAS OÙ fire_attack DOIT apparaître :

- **Boss attaque Héros** → Le sprite `fire_attack.png` s'affiche en superposition sur le cadre du héros
- **Timing** : L'effet apparaît pendant l'animation d'attaque du boss (à partir de la 2ème frame)
- **Durée** : L'overlay reste visible 1 seconde puis disparaît

### ❌ CAS OÙ fire_attack NE DOIT PAS apparaître :

- **Héros attaque Boss** → Aucun effet fire_attack
- **Boss se défend** → Aucun effet fire_attack
- **Boss se soigne** → Aucun effet fire_attack
- **Boss fait d'autres actions non-dommageables** → Aucun effet fire_attack

## 🔍 Messages de Debug

Surveillez ces messages dans la console :

```
🔥 FIRE_ATTACK: Overlay ajouté au cadre du HÉROS (boss attaque héros) !
✅ FIRE_ATTACK: Overlay supprimé du cadre du héros !
```

## 🚀 Comment Tester

1. **Lancez le jeu :**

   ```bash
   java -cp "target/classes;lib/*" towergame.view.GameWindow
   ```

2. **Jouez quelques tours et observez :**

   - L'effet fire_attack apparaît-il seulement quand le boss attaque ?
   - L'effet se superpose-t-il bien sur le cadre du héros ?
   - L'effet disparaît-il correctement après 1 seconde ?

3. **Actions de test spécifiques :**
   - Faites attaquer le héros → Vérifiez qu'il n'y a PAS d'effet fire_attack
   - Laissez le boss attaquer → Vérifiez qu'il y a l'effet fire_attack sur le héros
   - Si le boss fait autre chose → Vérifiez qu'il n'y a PAS d'effet

## 🔧 Modifications Apportées

- **Variable de tracking :** `bossAttackedThisTurn` pour détecter si le boss a vraiment attaqué
- **Condition d'affichage :** L'effet ne s'affiche que si `playerDamage > 0` (boss a infligé des dégâts)
- **Messages de debug :** Clarifiés pour faciliter le débogage

## 📝 Code Clé Modifié

```java
// Détecter si le boss a attaqué
int playerDamage = playerHpBefore - player.getHp();
bossAttackedThisTurn = (playerDamage > 0);

// Déclencher l'animation SEULEMENT s'il a attaqué
if (bossAttackedThisTurn) {
    playBossAttackAnimation();
}

// Dans l'animation, afficher l'effet SEULEMENT si boss a attaqué
if (currentFrame >= 1 && bossAttackedThisTurn) {
    showAttackOverlayOnPlayer();
}
```

## ✅ Résultat Attendu

Le sprite **fire_attack.png** doit maintenant être **exclusivement** réservé aux attaques du boss vers le héros, créant l'effet visuel souhaité : "le boss attaque et le héros se prend fire_attack de plein fouet" !
