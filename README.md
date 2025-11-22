# 🎮 Tower Battle - Java RPG Game

Un jeu de combat au tour par tour en Java avec interface graphique dans le style **Pokémon/Final Fantasy 7**.

## ✨ Fonctionnalités

- 🎨 **Interface graphique Swing** avec effets visuels gaming
- 🎯 **Système de combat tactique** avec éléments et résistances
- 🔥 **Boss avec mécaniques d'enrage** et animations
- ⚡ **4 sorts diversifiés** : soin, défense, attaques élémentaires
- 🌈 **Version console** avec couleurs ANSI
- 🎪 **Effets visuels** style JRPG classique

## 🚀 Lancement du Jeu

### 📦 Compilation
```bash
cd turnbased-game
javac -cp target/classes -d target/classes src/main/java/towergame/view/GameWindow.java
```

### 🎮 Interface Graphique (Recommandé)
```bash
java -cp target/classes towergame.view.GameWindow
```

### 💻 Version Console
```bash
java -cp target/classes towergame.controller.GameEngine
```

### 🚀 Lanceur Automatique
Double-cliquez sur `TowerBattle_Launcher.bat` pour un menu de choix.

## 🎯 Gameplay

- **🤺 Héros** (à gauche) vs **👺 Boss** (à droite)
- **Éléments** : Feu 🔥, Eau ❄️, Plante 🌿, Neutre ⚪
- **Résistances** : Feu résiste à Feu/Plante, faible à Eau
- **Enrage** : Le boss devient plus dangereux à 20% PV
- **Cooldowns** : Gestion tactique des sorts

## 🎨 Actions Disponibles

| Sort | Type | Élément | Effet | Cooldown |
|------|------|---------|-------|----------|
| 💚 Soin Léger | Soin | Neutre | +25 PV | 2 tours |
| 🛡️ Barrière | Défense | Neutre | Protection | 3 tours |
| 🌿 Fragiliser | Debuff | Plante | 5 dégâts + Weaken | 3 tours |
| ❄️ Jet de Glace | Attaque | Eau | 5 dégâts + Entrave | 2 tours |

## 🏗️ Architecture

```
src/main/java/towergame/
├── model/
│   ├── entities/     # Player, Boss, FireElementalBoss
│   ├── actions/      # Spells, Elements
│   ├── status/       # Status effects
│   └── managers/     # Game logic
├── view/
│   ├── GameWindow.java    # Interface graphique Swing
│   └── ConsoleView.java   # Interface console
└── controller/
    ├── GameEngine.java    # Console controller
    └── BattleController.java # JavaFX controller
```

## 🎊 Effets Visuels

- **Résistance** : 🛡️ "L'attaque n'est pas très efficace..."
- **Faiblesse** : ⚡ "C'est super efficace !"
- **Enrage** : 🔥 Animation rouge clignotante
- **Actions** : Animations de scale et couleurs

---

*Développé avec ❤️ en Java - Style Pokemon/Final Fantasy 7*
