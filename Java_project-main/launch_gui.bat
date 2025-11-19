@echo off
echo 🎮 Lancement de Tower Battle - Interface Graphique 🎮
echo Style: Pokémon/Final Fantasy 7
echo ===============================================

cd "C:\Users\yugo-\OneDrive\Documents\Epitech\Project Epitech\turnbased-game"

REM Copier les ressources si nécessaire
if not exist "target\classes\css" (
    xcopy "src\main\resources\css" "target\classes\css" /E /I /Y
)
if not exist "target\classes\fxml" (
    xcopy "src\main\resources\fxml" "target\classes\fxml" /E /I /Y
)

REM Lancer l'interface graphique JavaFX
java -cp target/classes towergame.JavaFXLauncher

echo.
echo Merci d'avoir joué à Tower Battle !
pause