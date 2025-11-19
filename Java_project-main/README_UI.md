JavaFX UI and Scene Builder instructions

Overview

- This project was scaffolded to run a JavaFX UI on Java 21 using Maven.
- The JavaFX FXML file `src/main/resources/fxml/battle.fxml` can be opened in Scene Builder.

Requirements

- JDK 21 installed and `JAVA_HOME` set.
- Maven installed (or use the JavaFX Maven plugin run command below).

Open FXML in Scene Builder

1. Open Scene Builder and choose `Open File`.
2. Select `src/main/resources/fxml/battle.fxml`.
3. The controller class is `BattleController` (located at project root). Because the project uses the default package for older source files, Scene Builder will show the controller as `BattleController`.

Run the JavaFX app (PowerShell)

```powershell
cd "C:\Users\yugo-\OneDrive\Documents\Epitech\Project Epitech\turnbased-game"
# Compile and run via the javafx-maven-plugin
mvn javafx:run
```

Notes

- The project currently keeps the original .java sources at the project root. The `pom.xml` config uses the project root as the compile source directory so you don't need to move files.
- If you prefer a standard Maven layout, I can move files into `src/main/java` and adjust package names.
- If you want a Pokemon-like art style, add sprite images into `src/main/resources/images/` as `player.png` and `enemy.png`.
