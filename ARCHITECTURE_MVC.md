# Architecture MVC - Tower Battle

## Vue d'ensemble

Le projet Tower Battle suit le pattern **Model-View-Controller (MVC)** pour une séparation claire des responsabilités et une meilleure maintenabilité du code.

```
src/main/java/towergame/
├── model/              # Couche Modèle (données et logique métier)
│   ├── entities/       # Entités du jeu
│   ├── actions/        # Actions et sorts
│   ├── status/         # Effets de statut
│   └── managers/       # Gestionnaires de jeu
├── view/               # Couche Vue (interface utilisateur)
└── controller/         # Couche Contrôleur (logique de contrôle)
```

---

## 📦 Model (Modèle)

Le modèle contient toute la **logique métier** et les **données** du jeu.

### `model/entities/` - Entités du jeu

Classes représentant les personnages et créatures :

- **`AEntity.java`** - Classe abstraite de base pour toutes les entités
- **`Entity.java`** - Interface pour les entités
- **`Player.java`** - Le joueur contrôlé par l'utilisateur
- **`ABoss.java`** - Classe abstraite pour les boss
- **`FireElementalBoss.java`** - Boss Élémentaire de Feu (implémentation concrète)

**Responsabilités :**

- Gestion des points de vie (HP)
- Application et calcul des dégâts
- Gestion des effets de statut actifs
- Mécaniques spécifiques aux entités (invulnérabilité, rage, etc.)

### `model/actions/` - Actions et sorts

Classes représentant toutes les actions disponibles dans le jeu :

- **`AAction.java`** - Classe abstraite de base pour toutes les actions
- **`Action.java`** - Interface pour les actions
- **`Element.java`** - Enum définissant les éléments (FIRE, WATER, TERRE, etc.)
- **Boss Actions :**
  - `BossAttackAction.java`
  - `BossDefendAction.java`
  - `BossHealAction.java`
- **Player Spells :**
  - `PlayerBoostSpell.java`
  - `PlayerDefendSpell.java`
  - `PlayerHealSpell.java`
  - `FireSpell.java`, `WaterSpell.java`, `PlantSpell.java`
  - `FireHardSpell.java`, `WaterHardSpell.java`, `PlantHardSpell.java`

**Responsabilités :**

- Définition des effets des actions
- Gestion des cooldowns
- Calcul des dégâts et effets
- Application des modificateurs élémentaires

### `model/status/` - Effets de statut

Classes gérant les buffs, debuffs et effets temporaires :

- **`IStatusEffect.java`** - Interface pour tous les effets
- **`StatusEffect.java`** - Classe abstraite de base
- **Implémentations :**
  - `BoostStatus.java` - Augmentation de puissance
  - `DefendStatus.java` - Réduction de dégâts
  - `EntraveStatus.java` - Ralentissement
  - `PoisonStatus.java` - Dégâts sur la durée
  - `WeakenStatus.java` - Réduction de puissance

**Responsabilités :**

- Application des effets à chaque tour
- Gestion de la durée des effets
- Modification des statistiques temporaires

### `model/managers/` - Gestionnaires

Classes gérant la logique de haut niveau du jeu :

- **`BattleManager.java`** - Gère le déroulement des combats
- **`StageManager.java`** - Gère la progression et les stages
- **`SuccessTracker.java`** - Système d'achievements/succès

**Responsabilités :**

- Orchestration des tours de combat
- Gestion de la progression du joueur
- Déblocage d'actions et de boss
- Suivi des statistiques de jeu

---

## 👁️ View (Vue)

La vue s'occupe de l'**affichage** et de l'**interaction utilisateur**.

### Classes de Vue

- **`ConsoleView.java`** - Interface console avec ANSI colors
- **`JavaFXMain.java`** - Point d'entrée de l'interface graphique
- **`GameView.java`** - Interface abstraite pour les vues

### Ressources FXML

- **`src/main/resources/fxml/battle.fxml`** - Layout JavaFX pour l'écran de combat
  - Compatible SceneBuilder
  - Tous les éléments ont des fx:id
  - Structure optimisée pour l'édition visuelle

### Ressources CSS

- **`src/main/resources/css/style.css`** - Feuille de style complète
  - Thème moderne avec dark mode
  - Styles pour boutons d'action (attack, heal, defend, boost)
  - Effets visuels (ombres, transitions)

**Responsabilités :**

- Affichage des informations de combat
- Rendu des barres de vie
- Affichage des actions disponibles
- Collecte des entrées utilisateur
- Notifications visuelles (victoire, défaite)

---

## 🎮 Controller (Contrôleur)

Le contrôleur gère la **logique de contrôle** entre le modèle et la vue.

### Classes de Contrôleur

- **`GameEngine.java`** - Contrôleur principal pour le mode console
  - Initialisation du jeu
  - Boucle de jeu principale
  - Gestion du mode console vs GUI (`--gui` flag)
- **`BattleController.java`** - Contrôleur FXML pour l'interface graphique
  - Binding entre FXML et logique Java
  - Gestion des événements UI (clics sur boutons)
  - Mise à jour de l'affichage en temps réel
  - Gestion de la fin de combat

**Responsabilités :**

- Réception des entrées utilisateur (console ou GUI)
- Appel des méthodes du modèle appropriées
- Mise à jour de la vue selon l'état du modèle
- Gestion du flux du jeu

---

## 🔄 Flux de données MVC

### Mode Console

```
┌─────────────┐
│ GameEngine  │ (Controller)
└──────┬──────┘
       │ demande action
       ↓
┌─────────────┐
│ ConsoleView │ (View)
└──────┬──────┘
       │ entrée utilisateur
       ↓
┌─────────────┐
│ GameEngine  │ (Controller)
└──────┬──────┘
       │ exécute action
       ↓
┌──────────────────┐
│ BattleManager    │ (Model)
│ Player/ABoss     │
│ AAction          │
└──────┬───────────┘
       │ résultat
       ↓
┌─────────────┐
│ ConsoleView │ (View)
└─────────────┘
   affiche résultat
```

### Mode JavaFX

```
┌──────────────────┐
│ BattleController │ (Controller)
└────────┬─────────┘
         │ @FXML initialize
         ↓
┌──────────────────┐
│ battle.fxml      │ (View)
│ + style.css      │
└────────┬─────────┘
         │ événement bouton
         ↓
┌──────────────────┐
│ BattleController │ (Controller)
└────────┬─────────┘
         │ exécute action
         ↓
┌──────────────────┐
│ BattleManager    │ (Model)
│ Player/ABoss     │
│ AAction          │
└────────┬─────────┘
         │ résultat
         ↓
┌──────────────────┐
│ BattleController │ (Controller)
└────────┬─────────┘
         │ met à jour UI
         ↓
┌──────────────────┐
│ battle.fxml      │ (View)
└──────────────────┘
   affiche changements
```

---

## 🛠️ Utilisation avec SceneBuilder

### Configuration SceneBuilder

1. **Ouvrir le fichier FXML** : `src/main/resources/fxml/battle.fxml`
2. **Vérifier le contrôleur** : `towergame.controller.BattleController`
3. **Tous les éléments avec fx:id sont éditables** :
   - `turnLabel` - Label du numéro de tour
   - `messageLabel` - Messages de statut
   - `enemySprite` - Image du boss
   - `enemyName` - Nom du boss
   - `enemyHp` - Points de vie du boss
   - `playerSprite` - Image du joueur
   - `playerName` - Nom du joueur
   - `playerHp` - Points de vie du joueur
   - `actionsBox` - Conteneur des boutons d'action

### Ajout de nouveaux éléments

1. Dans SceneBuilder, ajouter l'élément visuel
2. Lui donner un **fx:id** unique
3. Dans `BattleController.java`, ajouter le champ correspondant :
   ```java
   @FXML
   private Label monNouveauLabel;
   ```
4. Utiliser ce champ dans les méthodes du contrôleur

---

## 🚀 Exécution du projet

### Mode Console

```bash
# Compilation
mvn clean compile

# Exécution console
mvn exec:java
```

### Mode JavaFX (GUI)

```bash
# Exécution GUI
mvn javafx:run

# Ou via GameEngine avec flag
java -cp target/classes towergame.controller.GameEngine --gui
```

---

## 📋 Avantages de l'architecture MVC

### ✅ Séparation des préoccupations

- Le **modèle** ne connaît pas la vue
- La **vue** ne connaît pas la logique métier
- Le **contrôleur** orchestre les deux

### ✅ Testabilité

- Chaque couche peut être testée indépendamment
- Mock facile des dépendances
- Tests unitaires simplifiés

### ✅ Maintenabilité

- Modifications d'UI sans toucher à la logique
- Changements de logique métier sans impacter l'affichage
- Code mieux organisé et documenté

### ✅ Réutilisabilité

- Le modèle peut être réutilisé avec différentes vues (console, GUI)
- Les vues peuvent être interchangées facilement
- Ajout de nouveaux contrôleurs simplifié

### ✅ Scalabilité

- Facile d'ajouter de nouveaux boss, actions, effets
- Extension du système sans modification du code existant
- Respect du principe Open/Closed (SOLID)

---

## 📝 Bonnes pratiques

1. **Ne jamais mélanger les couches**

   - Pas de `System.out.println()` dans le modèle
   - Pas de logique métier dans la vue
   - Le contrôleur ne fait que coordonner

2. **Utiliser l'injection de dépendances**

   - Passer les instances nécessaires via constructeur
   - Facilite les tests et le découplage

3. **Respecter le Single Responsibility Principle**

   - Chaque classe a une responsabilité unique et bien définie

4. **Documenter les interfaces publiques**
   - JavaDoc pour toutes les méthodes publiques
   - Commentaires pour les algorithmes complexes

---

## 🔮 Évolutions futures

- **Multiples vues** : Ajout d'une vue web avec Spring Boot
- **Persistence** : Sauvegarde de progression avec base de données
- **Multiplayer** : Architecture client-serveur en conservant le modèle
- **IA avancée** : Boss avec comportements adaptatifs
- **Système d'inventaire** : Gestion d'items et d'équipements

---

## 📚 Ressources

- [Design Pattern MVC](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller)
- [JavaFX Documentation](https://openjfx.io/)
- [SceneBuilder](https://gluonhq.com/products/scene-builder/)
- [SOLID Principles](https://en.wikipedia.org/wiki/SOLID)
