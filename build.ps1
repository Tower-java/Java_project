# Tower Battle - Script de compilation (structure MVC)

Write-Host "========================================"
Write-Host " Compilation de Tower Battle (MVC)"
Write-Host "========================================"
Write-Host ""

$SRC = "src\main\java"
$OUT = "target\classes"
$RES = "src\main\resources"

if (-not (Test-Path $OUT)) {
    New-Item -ItemType Directory -Path $OUT | Out-Null
}

Write-Host "[1/4] Collecte des fichiers source..."
$files = @()
$files += Get-ChildItem -Path "$SRC\towergame\model\actions" -Filter "*.java" | ForEach-Object { $_.FullName }
$files += Get-ChildItem -Path "$SRC\towergame\model\entities" -Filter "*.java" | ForEach-Object { $_.FullName }
$files += Get-ChildItem -Path "$SRC\towergame\model\status" -Filter "*.java" | ForEach-Object { $_.FullName }
$files += Get-ChildItem -Path "$SRC\towergame\model\managers" -Filter "*.java" | ForEach-Object { $_.FullName }
$files += Get-ChildItem -Path "$SRC\towergame\view" -Filter "*.java" | ForEach-Object { $_.FullName }
$files += Get-ChildItem -Path "$SRC\towergame\controller" -Filter "*.java" | ForEach-Object { $_.FullName }

Write-Host "  Trouve $($files.Count) fichiers Java"

Write-Host "[2/4] Compilation des fichiers Java..."
$quotedFiles = $files | ForEach-Object { "`"$_`"" }
$cmd = "javac -d `"$OUT`" -cp `"$OUT`" -encoding UTF-8 $($quotedFiles -join ' ')"
Invoke-Expression $cmd

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "Erreur de compilation"
    exit 1
}

Write-Host "[3/4] Copie des ressources FXML..."
$fxmlDir = "$OUT\fxml"
if (-not (Test-Path $fxmlDir)) {
    New-Item -ItemType Directory -Path $fxmlDir | Out-Null
}
Copy-Item "$RES\fxml\*.fxml" -Destination $fxmlDir -Force

Write-Host "[4/4] Copie des ressources CSS..."
$cssDir = "$OUT\css"
if (-not (Test-Path $cssDir)) {
    New-Item -ItemType Directory -Path $cssDir | Out-Null
}
Copy-Item "$RES\css\*.css" -Destination $cssDir -Force

Write-Host ""
Write-Host "Compilation reussie!"
Write-Host ""
Write-Host "Executer le jeu:"
Write-Host "  Console: java -cp target\classes towergame.controller.GameEngine"
Write-Host ""
