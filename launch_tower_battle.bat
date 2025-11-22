@echo off
echo =========================================
echo        TOWER BATTLE - LAUNCHER
echo =========================================
echo.
echo Compilation du jeu...

cd /d "c:\Users\yugo-\OneDrive\Documents\Epitech\Project Epitech\turnbased-game"

REM Nettoyer les anciens fichiers
rmdir /s /q target\classes 2>nul
mkdir target\classes 2>nul

REM Compiler tous les fichiers Java (exclusion des fichiers JavaFX)
javac -d target\classes -encoding UTF-8 -cp target\classes src\main\java\towergame\model\actions\*.java src\main\java\towergame\model\entities\*.java src\main\java\towergame\model\status\*.java src\main\java\towergame\model\managers\*.java src\main\java\towergame\view\GameWindow.java src\main\java\towergame\view\GameView.java src\main\java\towergame\view\ConsoleView.java src\main\java\towergame\view\WelcomeScreen.java src\main\java\towergame\controller\GameEngine.java src\main\java\towergame\TowerBattleMain.java

if %ERRORLEVEL% neq 0 (
    echo.
    echo ❌ ERREUR DE COMPILATION!
    pause
    exit /b 1
)

echo.
echo ✅ Compilation réussie!
echo.
echo Lancement de Tower Battle...
echo.

REM Lancer le jeu avec l'écran d'accueil
java -cp target\classes towergame.TowerBattleMain

echo.
echo Merci d'avoir joué à Tower Battle!
pause