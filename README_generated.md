# 🎮 Tower Battle — jeu de combat tour par tour (Java)

Un petit projet Java de jeu de combat au tour par tour, inspiré des styles Pokémon / Final Fantasy.

Ce dépôt contient : la logique du jeu (modèle), une version console (`GameEngine` + `ConsoleView`),
une version graphique (JavaFX/Swing controllers) et une suite de tests unitaires couvrant la logique.

**Contenu clé :**
- Code source : `src/main/java/towergame/`
- Tests : `src/test/java/`
- Diagramme UML généré : `uml_from_project.puml`

**Status:** la suite de tests est verte localement (100% des tests passent).

## Prérequis

- Java 11+ installé (JDK)
- Maven (`mvn`) pour builder et lancer les tests
- (Optionnel) PlantUML / Docker si vous voulez générer le PNG du diagramme UML

## Compilation et tests

- Compiler le projet et exécuter les tests :

```powershell
mvn clean test
```

- Exécuter un test précis (ex. `BattleControllerTest`) :

```powershell
mvn -Dtest=towergame.controller.BattleControllerTest test
```

## Lancer le jeu

Version console (simple) — depuis le projet racine :

```powershell
mvn -DskipTests package
mvn exec:java -Dexec.mainClass="towergame.controller.GameEngine"
```

Version graphique (IDE recommandé)
- Ouvrez le projet dans IntelliJ IDEA ou Eclipse, puis lancez la classe UI principale (`towergame.view.GameWindow` ou `JavaFXMain` selon la configuration).

Remarque : certains éléments JavaFX/Swing peuvent demander une configuration spécifique selon votre JDK.

## UML

- Un fichier PlantUML synthétique est généré : `uml_from_project.puml`.
- Pour produire un PNG localement (PowerShell) :

```powershell
# Avec plantuml.jar
# Téléchargez plantuml.jar depuis https://plantuml.com/download
java -jar plantuml.jar -tpng uml_from_project.puml

# Ou avec Docker (si Docker Desktop installé)
docker run --rm -v ${PWD}:/workspace plantuml/plantuml:latest -tpng uml_from_project.puml
```

Si vous n'avez ni PlantUML ni Docker, je peux générer l'image via un service public PlantUML (envoi du contenu du `.puml`) sur demande.

## Ajouter un nouveau boss (ex. Plante)

Pour ajouter un boss personnalisé :

- Créez une nouvelle classe dans `src/main/java/towergame/model/entities/`, par exemple `PlantElementalBoss.java`, étendant `ABoss`.
- Implémentez le script d'actions via `setActionScript(List<AAction>)` et la logique `checkGimmick(Player, AAction, int)`.
- Enregistrez le boss dans `StageManager.loadAllBosses()` pour qu'il soit sélectionné par le moteur de jeu.

Astuce : regardez `FireElementalBoss.java` comme exemple complet (gimmicks, résistances/faiblesses, script).

## Notes pour les tests JavaFX

- Les tests qui manipulent des composants JavaFX utilisent `JFXPanel` / `Platform.runLater()` pour initialiser le toolkit et exécuter du code UI.
- Certaines méthodes du `BattleController` ont été rendues package-private dans le but de faciliter les tests (seams). Si vous préférez respecter l'encapsulation stricte, vous pouvez extraire un `AlertProvider`/`UIEffects` injectable pour remplacer les dialogues en tests.

## Fichiers importants

- `src/main/java/towergame/model/entities/` : `Player`, `ABoss`, `FireElementalBoss`, `PlantElementalBoss` (nouveau)
- `src/main/java/towergame/model/actions/` : actions du joueur et du boss
- `src/main/java/towergame/model/status/` : effets de statut (Weaken, Defend, Poison...)
- `src/main/java/towergame/controller/` : `GameEngine`, `BattleController`
- `src/test/java/` : tests unitaires (JUnit 5)
- `uml_from_project.puml` : diagramme PlantUML généré

## Contribution

- Fork & PR : bienvenue !
- Tests : ajoutez des tests unitaires pour toute nouvelle logique.

Si tu veux, je peux :
- ajouter un test unitaire dédié pour `PlantElementalBoss` ;
- restaurer l'encapsulation et ajouter une abstraction `AlertProvider` pour tester proprement ;
- générer le PNG du diagramme UML et te l'envoyer.

---

Si tu veux que j'ajoute autre chose au README (badges CI, instructions IDE, exemples de parties du jeu), dis-moi quoi et je l'ajoute.
