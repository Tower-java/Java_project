@echo off
title Tower Battle - Launcher

echo.
echo    ███████╗ ██████╗ ██╗    ██╗███████╗██████╗     ██████╗  █████╗ ████████╗████████╗██╗     ███████╗
echo    ╚══██╔══╝██╔═══██╗██║    ██║██╔════╝██╔══██╗    ██╔══██╗██╔══██╗╚══██╔══╝╚══██╔══╝██║     ██╔════╝
echo       ██║   ██║   ██║██║ █╗ ██║█████╗  ██████╔╝    ██████╔╝███████║   ██║      ██║   ██║     █████╗  
echo       ██║   ██║   ██║██║███╗██║██╔══╝  ██╔══██╗    ██╔══██╗██╔══██║   ██║      ██║   ██║     ██╔══╝  
echo       ██║   ╚██████╔╝╚███╔███╔╝███████╗██║  ██║    ██████╔╝██║  ██║   ██║      ██║   ███████╗███████╗
echo       ╚═╝    ╚═════╝  ╚══╝╚══╝ ╚══════╝╚═╝  ╚═╝    ╚═════╝ ╚═╝  ╚═╝   ╚═╝      ╚═╝   ╚══════╝╚══════╝
echo.
echo                                   Style Pokemon/Final Fantasy 7
echo                                  ==============================
echo.
echo    [1] Interface Graphique (Recommended)
echo    [2] Version Console (Classic)
echo    [3] Quitter
echo.
set /p choice="Votre choix (1-3): "

if "%choice%"=="1" goto gui
if "%choice%"=="2" goto console
if "%choice%"=="3" goto exit
goto invalid

:gui
echo.
echo 🎮 Lancement de l'interface graphique...
echo Style: Pokemon/Final Fantasy 7 avec effets visuels
echo.
cd "C:\Users\yugo-\OneDrive\Documents\Epitech\Project Epitech\turnbased-game"
java -cp target/classes towergame.view.GameWindow
goto end

:console
echo.
echo 💻 Lancement de la version console...
echo Style: Retro terminal avec couleurs ANSI
echo.
cd "C:\Users\yugo-\OneDrive\Documents\Epitech\Project Epitech\turnbased-game"
java -cp target/classes towergame.controller.GameEngine
goto end

:invalid
echo.
echo ❌ Choix invalide. Veuillez entrer 1, 2 ou 3.
echo.
pause
cls
goto :start

:exit
echo.
echo 👋 Merci d'avoir joué à Tower Battle !
timeout /t 2 /nobreak >nul
goto end

:end
echo.
echo =====================================
echo    Merci d'avoir joué à Tower Battle !
echo =====================================
pause