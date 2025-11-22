@echo off
REM Test les écrans de fin du jeu Tower Battle
setlocal enabledelayedexpansion
set MAVEN_HOME=%USERPROFILE%\.tools\apache-maven-3.9.6
set PATH=%MAVEN_HOME%\bin;%PATH%

cd /d "c:\Users\yugo-\OneDrive\Documents\Epitech\Project Epitech\turnbased-game"

echo Compilation du projet...
call mvn clean compile -q -DskipTests

if errorlevel 1 (
    echo Erreur lors de la compilation
    exit /b 1
)

echo.
echo Lancement du test des écrans de fin...
echo.

REM Utiliser javafx:run avec la classe de test
call mvn javafx:run -Djavafx.mainClass=towergame.view.TestEndScreens -DskipTests

pause
