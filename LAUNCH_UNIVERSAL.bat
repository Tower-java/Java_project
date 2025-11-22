@echo off
title Tower Battle - Launcher Universel
color 0A

echo.
echo ========================================
echo   🎮 TOWER BATTLE - LAUNCHER UNIVERSEL 🎮
echo ========================================
echo.
echo [1] Lancement DIRECT (Sans Maven)
echo [2] Compilation + Lancement 
echo [3] Mode DEBUG (avec infos)
echo [4] Test des ecrans
echo [Q] Quitter
echo.
set /p choice="Votre choix: "

if /i "%choice%"=="1" goto direct
if /i "%choice%"=="2" goto compile
if /i "%choice%"=="3" goto debug  
if /i "%choice%"=="4" goto test
if /i "%choice%"=="q" goto quit
goto invalid

:direct
echo.
echo 🚀 Lancement DIRECT (evite Maven/JavaFX)...
echo =========================================
java -cp "target/classes;lib/*" towergame.view.GameWindow
if %errorlevel% neq 0 (
    echo ❌ Echec du lancement direct
    echo Essayez l'option 2 pour compiler d'abord
    pause
    goto menu
)
goto end

:compile
echo.
echo 🔨 Compilation manuelle (evite Maven)...
echo ======================================
echo Compilation des fichiers Java...
javac -cp "lib/*" -d target/classes src/main/java/towergame/*/*.java src/main/java/*.java 2>nul

echo Copie des ressources...
if not exist "target\classes\css" xcopy "src\main\resources\css" "target\classes\css" /E /I /Y /Q >nul 2>&1
if not exist "target\classes\fxml" xcopy "src\main\resources\fxml" "target\classes\fxml" /E /I /Y /Q >nul 2>&1
if not exist "target\classes\sprites" xcopy "src\main\resources\sprites" "target\classes\sprites" /E /I /Y /Q >nul 2>&1

echo 🚀 Lancement du jeu...
java -cp "target/classes;lib/*" towergame.view.GameWindow
goto end

:debug
echo.
echo 🔍 MODE DEBUG - Diagnostic complet...
echo ==================================
echo.
echo 📁 Verification des dossiers:
if exist "target\classes" (echo ✅ target/classes existe) else (echo ❌ target/classes manquant)
if exist "lib" (echo ✅ lib/ existe) else (echo ❌ lib/ manquant)
if exist "src\main\java" (echo ✅ src/main/java existe) else (echo ❌ src/main/java manquant)

echo.
echo 📋 Version Java:
java -version

echo.
echo 🔍 Contenu target/classes:
if exist "target\classes" dir "target\classes" /B

echo.
echo 🚀 Tentative de lancement avec debug...
java -cp "target/classes;lib/*" -Djava.util.logging.config.file=logging.properties towergame.view.GameWindow
goto end

:test
echo.
echo 🧪 Test des ecrans...
echo ===================
echo.
echo [V] Ecran Victoire
echo [D] Ecran Defaite  
echo [W] Ecran d'accueil
echo [R] Retour
echo.
set /p testchoice="Test: "

if /i "%testchoice%"=="v" (
    echo 🏆 Test ecran victoire...
    javac -cp "target/classes;lib/*" TestVictoryScreen.java
    java -cp "target/classes;lib/*;." TestVictoryScreen
)
if /i "%testchoice%"=="d" (
    echo 💀 Test ecran defaite...
    javac -cp "target/classes;lib/*" TestDefeatScreen.java  
    java -cp "target/classes;lib/*;." TestDefeatScreen
)
if /i "%testchoice%"=="w" (
    echo 🌟 Test ecran d'accueil...
    java -cp "target/classes;lib/*" towergame.view.WelcomeScreen
)
if /i "%testchoice%"=="r" goto menu
goto test

:invalid
echo ❌ Choix invalide!
pause
goto menu

:quit
echo 👋 Au revoir!
timeout /t 2 /nobreak >nul
exit

:end
echo.
echo =====================================
echo    Merci d'avoir joue a Tower Battle!
echo =====================================
pause

:menu
cls
goto :eof