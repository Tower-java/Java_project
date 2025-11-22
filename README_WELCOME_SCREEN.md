# 🎮 ÉCRAN D'ACCUEIL TOWER BATTLE

## 📋 Description

Un écran d'accueil élégant a été ajouté au jeu Tower Battle, offrant une expérience utilisateur professionnelle avec :

### ✨ Caractéristiques

- **Design élégant** : Fond sombre avec gradient et effets d'étoiles
- **Texte clignotant** : "Press any key to start" qui clignote toutes les 800ms
- **Interface intuitive** : N'importe quelle touche démarre le jeu
- **Transitions fluides** : Passage automatique vers le jeu principal

### 🎨 Éléments visuels

- **Titre principal** : "⚔️ TOWER BATTLE ⚔️" en doré (48pt)
- **Sous-titre** : "Epic RPG Adventure" en blanc (24pt)
- **Description** : Courte présentation du gameplay
- **Instructions** : Texte clignotant pour démarrer
- **Crédits** : Signature en bas d'écran

## 🚀 Utilisation

### Lancement rapide

```batch
# Double-cliquer sur le fichier :
launch_tower_battle.bat
```

### Lancement manuel

```batch
cd "chemin\vers\le\projet"
javac -d target\classes -encoding UTF-8 [... fichiers java]
java -cp target\classes towergame.TowerBattleMain
```

## 📁 Nouveaux fichiers

### `WelcomeScreen.java`

- Classe principale de l'écran d'accueil
- Gère l'affichage et les interactions
- Implémente KeyListener pour détecter les touches

### `TowerBattleMain.java`

- Point d'entrée principal du jeu
- Lance l'écran d'accueil puis le jeu
- Configure le Look & Feel système

### `launch_tower_battle.bat`

- Script de lancement automatisé
- Compilation et exécution en une commande
- Gestion d'erreurs intégrée

## 🔧 Architecture technique

### Flux d'exécution

1. **TowerBattleMain** → Démarre l'application
2. **WelcomeScreen** → Affiche l'écran d'accueil
3. **Détection touche** → L'utilisateur appuie sur une touche
4. **Callback** → Ferme l'écran d'accueil
5. **GameWindow** → Lance le jeu principal

### Gestion des événements

- **KeyListener** : Détection des touches clavier
- **Timer** : Animation du texte clignotant
- **SwingUtilities** : Thread-safety pour l'interface

## 🎯 Expérience utilisateur

### Au lancement

1. L'écran d'accueil s'affiche immédiatement
2. Le texte "Press any key" clignote pour attirer l'attention
3. Les effets visuels créent une ambiance immersive

### Interaction

1. L'utilisateur appuie sur n'importe quelle touche
2. L'écran d'accueil se ferme instantanément
3. Le jeu principal démarre automatiquement

## 🛠️ Personnalisation

### Couleurs (dans WelcomeScreen.java)

```java
private static final Color BG_COLOR = new Color(20, 20, 35);        // Fond sombre
private static final Color GOLD_COLOR = new Color(255, 215, 0);     // Doré pour titres
private static final Color TEXT_WHITE = new Color(245, 245, 245);   // Blanc pour texte
```

### Timing du clignotement

```java
blinkTimer = new Timer(800, e -> { ... });  // 800ms entre chaque clignotement
```

### Textes

- Modifiables directement dans le constructeur de WelcomeScreen
- Support HTML pour formatting avancé

## 📊 Performance

- **Temps de lancement** : Instantané
- **Mémoire utilisée** : Minimale (composants Swing légers)
- **Compatibilité** : Java 8+ sur Windows/Linux/Mac

## 🔄 Intégration future

L'écran d'accueil peut facilement être étendu pour inclure :

- Menu des options
- Sélection de difficulté
- Sauvegarde/Chargement
- Paramètres audio/vidéo
- Crédits étendus

---

_Écran d'accueil créé pour améliorer l'expérience utilisateur de Tower Battle_ 🎮
