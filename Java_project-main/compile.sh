#!/bin/bash

SRC_DIR=src/main/java
OUT_DIR=target/classes
RES_DIR=src/main/resources

# Créer le répertoire de sortie
mkdir -p "$OUT_DIR"

echo "[1/4] Compilation des Models..."
javac -d "$OUT_DIR" -cp "$OUT_DIR" $SRC_DIR/towergame/model/actions/*.java
if [ $? -ne 0 ]; then echo "Erreur de compilation"; exit 1; fi

javac -d "$OUT_DIR" -cp "$OUT_DIR" $SRC_DIR/towergame/model/entities/*.java
if [ $? -ne 0 ]; then echo "Erreur de compilation"; exit 1; fi

javac -d "$OUT_DIR" -cp "$OUT_DIR" $SRC_DIR/towergame/model/status/*.java
if [ $? -ne 0 ]; then echo "Erreur de compilation"; exit 1; fi

javac -d "$OUT_DIR" -cp "$OUT_DIR" $SRC_DIR/towergame/model/managers/*.java
if [ $? -ne 0 ]; then echo "Erreur de compilation"; exit 1; fi

echo "[2/4] Compilation des Views..."
javac -d "$OUT_DIR" -cp "$OUT_DIR" $SRC_DIR/towergame/view/ConsoleView.java
if [ $? -ne 0 ]; then echo "Erreur de compilation"; exit 1; fi

javac -d "$OUT_DIR" -cp "$OUT_DIR" $SRC_DIR/towergame/view/GameView.java
if [ $? -ne 0 ]; then echo "Erreur de compilation"; exit 1; fi

echo "[3/4] Compilation des Controllers..."
javac -d "$OUT_DIR" -cp "$OUT_DIR" $SRC_DIR/towergame/controller/GameEngine.java
if [ $? -ne 0 ]; then echo "Erreur de compilation"; exit 1; fi

echo "[4/4] Copie des ressources..."
mkdir -p "$OUT_DIR/fxml"
mkdir -p "$OUT_DIR/css"
cp -r $RES_DIR/fxml/*.fxml "$OUT_DIR/fxml/"
cp -r $RES_DIR/css/*.css "$OUT_DIR/css/"

echo "✓ Compilation réussie !"
