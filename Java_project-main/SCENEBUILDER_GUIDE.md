# Guide SceneBuilder - Tower Battle

## 🎨 Guide de démarrage rapide pour SceneBuilder

Ce document explique comment utiliser SceneBuilder pour personnaliser l'interface graphique du jeu Tower Battle.

---

## 📥 Installation de SceneBuilder

### Windows

1. Télécharger depuis [gluonhq.com/products/scene-builder](https://gluonhq.com/products/scene-builder/)
2. Installer la version 21 (compatible JavaFX 21)
3. Lancer SceneBuilder

### Configuration initiale

1. Ouvrir SceneBuilder
2. `File` → `Open` → Naviguer vers :
   ```
   src/main/resources/fxml/battle.fxml
   ```

---

## 🏗️ Structure actuelle du fichier FXML

### Hiérarchie des composants

```
AnchorPane (root)
└── VBox (container principal)
    ├── Label (turnLabel) - Numéro de tour
    ├── Label (messageLabel) - Messages de statut
    ├── HBox (zone de bataille)
    │   ├── VBox (section ennemi)
    │   │   ├── Label ("ENNEMI")
    │   │   ├── ImageView (enemySprite)
    │   │   ├── Label (enemyName)
    │   │   └── Label (enemyHp)
    │   ├── Label ("VS")
    │   └── VBox (section joueur)
    │       ├── Label ("JOUEUR")
    │       ├── ImageView (playerSprite)
    │       ├── Label (playerName)
    │       └── Label (playerHp)
    ├── Separator
    └── VBox (section actions)
        ├── Label ("ACTIONS DISPONIBLES")
        └── HBox (actionsBox) - Boutons générés dynamiquement
```

---

## 🔧 Éléments modifiables

### Labels avec fx:id (contrôlés par Java)

| fx:id          | Type  | Description                            | Mis à jour par                          |
| -------------- | ----- | -------------------------------------- | --------------------------------------- |
| `turnLabel`    | Label | Affiche "Tour X"                       | `BattleController.updateDisplay()`      |
| `messageLabel` | Label | Statuts du boss (Invulnérable, Enragé) | `BattleController.updateDisplay()`      |
| `enemyName`    | Label | Nom du boss                            | `BattleController.initialize()`         |
| `enemyHp`      | Label | HP du boss                             | `BattleController.updateDisplay()`      |
| `playerName`   | Label | Nom du joueur                          | `BattleController.initialize()`         |
| `playerHp`     | Label | HP du joueur                           | `BattleController.updateDisplay()`      |
| `actionsBox`   | HBox  | Conteneur des boutons d'action         | `BattleController.setupActionButtons()` |

### ImageViews avec fx:id

| fx:id          | Type      | Description     | Source image         |
| -------------- | --------- | --------------- | -------------------- |
| `enemySprite`  | ImageView | Image du boss   | `/images/enemy.png`  |
| `playerSprite` | ImageView | Image du joueur | `/images/player.png` |

---

## 🎨 Personnalisation dans SceneBuilder

### 1. Modifier les couleurs

**Via SceneBuilder :**

- Sélectionner un élément
- Panneau de droite → `Properties` → `Style`
- Modifier `-fx-background-color`, `-fx-text-fill`, etc.

**Via CSS (recommandé) :**
Éditer `src/main/resources/css/style.css`

### 2. Changer la taille des éléments

- Sélectionner un composant
- `Layout` → Modifier `Pref Width` / `Pref Height`
- Ou utiliser `fitWidth` / `fitHeight` pour les ImageView

### 3. Ajouter des effets visuels

- Sélectionner un Label ou autre
- `Properties` → `Effect`
- Ajouter : DropShadow, Glow, InnerShadow, etc.

### 4. Modifier l'espacement

- Sélectionner un VBox ou HBox
- `Properties` → `Spacing` : ajuster la valeur
- `Padding` : ajuster les marges internes

---

## 🆕 Ajouter de nouveaux éléments

### Exemple : Ajouter une barre de progression HP

#### Étape 1 : Dans SceneBuilder

1. Glisser un `ProgressBar` depuis la bibliothèque
2. Le placer sous `enemyHp`
3. Lui donner un fx:id : `enemyHpBar`
4. Ajuster les propriétés :
   - Pref Width: `200.0`
   - Max: `1.0` (représente 100%)
   - Style: `-fx-accent: #ff6b6b;`

#### Étape 2 : Dans BattleController.java

```java
@FXML
private ProgressBar enemyHpBar;

// Dans updateDisplay()
double hpPercent = (double) boss.getHp() / boss.getMaxHp();
enemyHpBar.setProgress(hpPercent);
```

#### Étape 3 : Compiler et tester

```bash
mvn clean compile javafx:run
```

---

## 🎯 Classes CSS disponibles

### Pour les Labels

```css
.entity-name     /* Nom des entités (gras, blanc) */
/* Nom des entités (gras, blanc) */
.entity-hp; /* Points de vie (cyan) */
```

### Pour les Buttons

```css
.attack-button   /* Bouton d'attaque (rouge) */
/* Bouton d'attaque (rouge) */
.heal-button     /* Bouton de soin (vert) */
.defend-button   /* Bouton de défense (bleu) */
.boost-button; /* Bouton de boost (violet) */
```

### Utilisation dans SceneBuilder

1. Sélectionner un élément
2. `Properties` → `Style Class`
3. Ajouter le nom de la classe (sans le point)

---

## 🖼️ Ajouter des images

### Structure des ressources

```
src/main/resources/
└── images/
    ├── player.png      (200x200 recommandé)
    ├── enemy.png       (200x200 recommandé)
    ├── background.jpg  (optionnel)
    └── icons/          (pour boutons)
```

### Dans SceneBuilder

1. Sélectionner une ImageView
2. `Properties` → `Image`
3. Entrer le chemin : `/images/nom_fichier.png`

### Propriétés importantes

- `Preserve Ratio` : ☑️ (maintient les proportions)
- `Smooth` : ☑️ (lissage de l'image)
- `Pick On Bounds` : ☑️ (pour la détection de clic)

---

## 🔀 Layouts avancés

### AnchorPane (actuel)

- Positionne les éléments avec des ancres
- Bon pour le responsive
- Propriétés : leftAnchor, rightAnchor, topAnchor, bottomAnchor

### VBox / HBox

- Organisation verticale / horizontale
- Propriétés utiles :
  - `spacing` : espace entre éléments
  - `alignment` : alignement (CENTER, TOP_LEFT, etc.)
  - `fillWidth` : étendre les enfants

### GridPane (alternative)

Pour un layout en grille :

```
┌─────────┬─────────┐
│ Ennemi  │ Joueur  │
├─────────┴─────────┤
│    Actions        │
└───────────────────┘
```

---

## 🎬 Animations (futures)

### Ajouter une FadeTransition

```java
// Dans BattleController.java
FadeTransition fade = new FadeTransition(Duration.millis(500), messageLabel);
fade.setFromValue(0.0);
fade.setToValue(1.0);
fade.play();
```

### Préparation dans SceneBuilder

- Donner des fx:id aux éléments à animer
- Les animer depuis le code Java

---

## 🐛 Débogage

### Problème : FXML ne se charge pas

**Causes possibles :**

1. Chemin incorrect dans JavaFXMain :
   ```java
   getClass().getResource("/fxml/battle.fxml")
   ```
2. Package du contrôleur incorrect dans FXML :
   ```xml
   fx:controller="towergame.controller.BattleController"
   ```
3. fx:id manquant dans FXML mais présent dans le contrôleur

### Problème : CSS ne s'applique pas

**Vérifier :**

1. Chemin dans le FXML ou JavaFXMain :
   ```xml
   <stylesheets>
       <String fx:value="/css/style.css" />
   </stylesheets>
   ```
2. Syntaxe CSS valide (points-virgules, accolades)
3. Sélecteurs corrects (`.class`, `#id`, `Type`)

### Problème : Image ne s'affiche pas

**Solutions :**

1. Vérifier que l'image existe dans `src/main/resources/images/`
2. Utiliser un slash initial : `/images/player.png`
3. Format supporté : PNG, JPEG, GIF
4. Reconstruire le projet : `mvn clean compile`

---

## 📊 Bonnes pratiques

### ✅ DO

- Utiliser des fx:id descriptifs (`playerNameLabel` plutôt que `label1`)
- Préférer le CSS au style inline pour la cohérence
- Tester régulièrement avec `mvn javafx:run`
- Conserver une hiérarchie claire des composants
- Utiliser les StyleClass pour la réutilisabilité

### ❌ DON'T

- Ne pas mélanger style inline et CSS
- Éviter les positions en dur (utiliser les layouts)
- Ne pas oublier de sauvegarder le FXML après modification
- Éviter trop d'imbrication (max 4-5 niveaux)

---

## 🚀 Workflow recommandé

### 1. Design dans SceneBuilder

- Placer les éléments visuellement
- Ajuster les tailles et espacements
- Définir les fx:id

### 2. Lier au contrôleur

- Ajouter les champs @FXML dans BattleController.java
- Implémenter la logique dans les méthodes

### 3. Styliser avec CSS

- Créer/modifier les classes CSS
- Appliquer aux éléments via styleClass

### 4. Tester et itérer

```bash
mvn clean compile javafx:run
```

---

## 📚 Ressources utiles

### Documentation

- [JavaFX CSS Reference](https://openjfx.io/javadoc/21/javafx.graphics/javafx/scene/doc-files/cssref.html)
- [SceneBuilder User Guide](https://docs.gluonhq.com/scenebuilder/)
- [JavaFX Layouts](https://docs.oracle.com/javase/8/javafx/layout-tutorial/index.html)

### Outils

- [ColorPicker](https://www.google.com/search?q=color+picker) - Pour choisir des couleurs
- [Iconfinder](https://www.iconfinder.com/) - Icônes gratuites
- [CSS Gradient Generator](https://cssgradient.io/) - Pour les dégradés

---

## 🎓 Exercices pratiques

### Exercice 1 : Changer le thème

Modifier `style.css` pour créer un thème "clair" :

- Background : `#f5f5f5`
- Texte : `#333333`
- Boutons : couleurs pastels

### Exercice 2 : Ajouter un timer

1. Ajouter un Label `timerLabel` dans SceneBuilder
2. Dans BattleController, utiliser un Timeline pour compter
3. Afficher le temps écoulé

### Exercice 3 : Icônes sur les boutons

1. Trouver/créer des icônes pour chaque type d'action
2. Les ajouter dans `resources/images/icons/`
3. Modifier `setupActionButtons()` pour ajouter les ImageView

---

**Bon design ! 🎨**
