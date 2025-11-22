#!/usr/bin/env python3
"""
Script pour mettre à jour tous les packages et imports après restructuration MVC
"""
import os
import re
from pathlib import Path

# Définir le chemin de base
BASE_PATH = Path(r"/home/tanguy/Java_project/src/main/java/towergame")

# Mapping des packages
PACKAGE_MAPPING = {
    'model/entities': 'towergame.model.entities',
    'model/actions': 'towergame.model.actions',
    'model/status': 'towergame.model.status',
    'model/managers': 'towergame.model.managers',
    'view': 'towergame.view',
    'controller': 'towergame.controller',
}

# Mapping des classes vers leurs packages
CLASS_TO_PACKAGE = {
    # Entities
    'AEntity': 'towergame.model.entities',
    'Entity': 'towergame.model.entities',
    'Player': 'towergame.model.entities',
    'ABoss': 'towergame.model.entities',
    'FireElementalBoss': 'towergame.model.entities',
    
    # Actions
    'AAction': 'towergame.model.actions',
    'Action': 'towergame.model.actions',
    'Element': 'towergame.model.actions',
    'BossAttackAction': 'towergame.model.actions',
    'BossDefendAction': 'towergame.model.actions',
    'BossHealAction': 'towergame.model.actions',
    'PlayerBoostSpell': 'towergame.model.actions',
    'PlayerDefendSpell': 'towergame.model.actions',
    'PlayerHealSpell': 'towergame.model.actions',
    'FireSpell': 'towergame.model.actions',
    'WaterSpell': 'towergame.model.actions',
    'PlantSpell': 'towergame.model.actions',
    'FireHardSpell': 'towergame.model.actions',
    'WaterHardSpell': 'towergame.model.actions',
    'PlantHardSpell': 'towergame.model.actions',
    
    # Status
    'IStatusEffect': 'towergame.model.status',
    'StatusEffect': 'towergame.model.status',
    'BoostStatus': 'towergame.model.status',
    'DefendStatus': 'towergame.model.status',
    'EntraveStatus': 'towergame.model.status',
    'PoisonStatus': 'towergame.model.status',
    'WeakenStatus': 'towergame.model.status',
    
    # Managers
    'BattleManager': 'towergame.model.managers',
    'StageManager': 'towergame.model.managers',
    'SuccessTracker': 'towergame.model.managers',
    
    # Views
    'ConsoleView': 'towergame.view',
    'JavaFXMain': 'towergame.view',
    'GameView': 'towergame.view',
    
    # Controllers
    'BattleController': 'towergame.controller',
    'GameEngine': 'towergame.controller',
}

def update_package_declaration(content, expected_package):
    """Met à jour la déclaration de package"""
    return re.sub(r'package towergame;', f'package {expected_package};', content)

def update_imports(content):
    """Met à jour tous les imports pour utiliser la nouvelle structure"""
    lines = content.split('\n')
    updated_lines = []
    
    for line in lines:
        # Skip les lignes qui ne sont pas des imports
        if not line.strip().startswith('import towergame'):
            updated_lines.append(line)
            continue
        
        # Extraire le nom de classe de l'import
        match = re.search(r'import towergame\.([A-Za-z0-9]+);', line)
        if match:
            class_name = match.group(1)
            if class_name in CLASS_TO_PACKAGE:
                new_import = f'import {CLASS_TO_PACKAGE[class_name]}.{class_name};'
                updated_lines.append(new_import)
            else:
                # Garder tel quel si on ne connaît pas la classe
                updated_lines.append(line)
        else:
            updated_lines.append(line)
    
    return '\n'.join(updated_lines)

def process_directory(dir_path, expected_package):
    """Traite tous les fichiers Java dans un répertoire"""
    java_files = list(dir_path.glob('*.java'))
    
    for java_file in java_files:
        print(f"Processing {java_file.name}...")
        
        # Lire le contenu
        with open(java_file, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Mettre à jour le package
        content = update_package_declaration(content, expected_package)
        
        # Mettre à jour les imports
        content = update_imports(content)
        
        # Écrire le résultat
        with open(java_file, 'w', encoding='utf-8') as f:
            f.write(content)
        
        print(f"  ✓ {java_file.name} updated")

def main():
    print("Starting MVC package update...")
    print("=" * 50)
    
    # Traiter chaque répertoire
    for subdir, package in PACKAGE_MAPPING.items():
        dir_path = BASE_PATH / subdir
        if dir_path.exists():
            print(f"\nProcessing {subdir}/")
            process_directory(dir_path, package)
    
    print("\n" + "=" * 50)
    print("Package update complete!")

if __name__ == '__main__':
    main()
