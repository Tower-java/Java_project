@echo off
REM Script de compilation pour Tower Battle (structure MVC)

echo ========================================
echo  Compilation de Tower Battle (MVC)
echo ========================================
echo.

set SRC_DIR=src\main\java
set OUT_DIR=target\classes
set RES_DIR=src\main\resources

REM Créer le répertoire de sortie
if not exist "%OUT_DIR%" mkdir "%OUT_DIR%"

echo [1/4] Compilation des Models...
javac -d "%OUT_DIR%" -cp "%OUT_DIR%" "%SRC_DIR%\towergame\model\actions\*.java"
if errorlevel 1 goto error

javac -d "%OUT_DIR%" -cp "%OUT_DIR%" "%SRC_DIR%\towergame\model\entities\*.java"
if errorlevel 1 goto error

javac -d "%OUT_DIR%" -cp "%OUT_DIR%" "%SRC_DIR%\towergame\model\status\*.java"
if errorlevel 1 goto error

javac -d "%OUT_DIR%" -cp "%OUT_DIR%" "%SRC_DIR%\towergame\model\managers\*.java"
if errorlevel 1 goto error

echo [2/4] Compilation des Views...
javac -d "%OUT_DIR%" -cp "%OUT_DIR%" "%SRC_DIR%\towergame\view\ConsoleView.java"
if errorlevel 1 goto error

javac -d "%OUT_DIR%" -cp "%OUT_DIR%" "%SRC_DIR%\towergame\view\GameView.java"
if errorlevel 1 goto error

echo [3/4] Compilation des Controllers...
javac -d "%OUT_DIR%" -cp "%OUT_DIR%" "%SRC_DIR%\towergame\controller\GameEngine.java"
if errorlevel 1 goto error

echo [4/4] Copie des ressources...
if not exist "%OUT_DIR%\fxml" mkdir "%OUT_DIR%\fxml"
if not exist "%OUT_DIR%\css" mkdir "%OUT_DIR%\css"
copy /Y "%RES_DIR%\fxml\*.fxml" "%OUT_DIR%\fxml\" >nul
copy /Y "%RES_DIR%\css\*.css" "%OUT_DIR%\css\" >nul

echo.
echo ========================================
echo  ✓ Compilation réussie !
echo ========================================
echo.
echo Pour exécuter le jeu en mode console :
echo   java -cp target\classes towergame.controller.GameEngine
echo.
goto end

:error
echo.
echo ========================================
echo  ✗ Erreur de compilation
echo ========================================
echo.
exit /b 1

:end
