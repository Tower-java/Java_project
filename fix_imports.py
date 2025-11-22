#!/usr/bin/env python3
"""
Script pour corriger tous les imports manquants dans la structure MVC
"""
import os
import re
from pathlib import Path

# Définir le chemin de base
BASE_PATH = Path("src/main/java/towergame")

# Mapping des classes vers leurs packages
CLASS_TO_PACKAGE = {
    # Entities
    'AEntity': 'towergame.model.entities.AEntity',
    'Entity': 'towergame.model.entities.Entity',
    'Player': 'towergame.model.entities.Player',
    'ABoss': 'towergame.model.entities.ABoss',
    'FireElementalBoss': 'towergame.model.entities.FireElementalBoss',
    
    # Actions
    'AAction': 'towergame.model.actions.AAction',
    'Action': 'towergame.model.actions.Action',
    'Element': 'towergame.model.actions.Element',
    'BossAttackAction': 'towergame.model.actions.BossAttackAction',
    'BossDefendAction': 'towergame.model.actions.BossDefendAction',
    'BossHealAction': 'towergame.model.actions.BossHealAction',
    'PlayerAttackAction': 'towergame.model.actions.PlayerAttackAction',
    'PlayerBoostSpell': 'towergame.model.actions.PlayerBoostSpell',
    'PlayerDefendSpell': 'towergame.model.actions.PlayerDefendSpell',
    'PlayerHealSpell': 'towergame.model.actions.PlayerHealSpell',
    'FireSpell': 'towergame.model.actions.FireSpell',
    'WaterSpell': 'towergame.model.actions.WaterSpell',
    'PlantSpell': 'towergame.model.actions.PlantSpell',
    'FireHardSpell': 'towergame.model.actions.FireHardSpell',
    'WaterHardSpell': 'towergame.model.actions.WaterHardSpell',
    'PlantHardSpell': 'towergame.model.actions.PlantHardSpell',
    
    # Status
    'IStatusEffect': 'towergame.model.status.IStatusEffect',
    'StatusEffect': 'towergame.model.status.StatusEffect',
    'BoostStatus': 'towergame.model.status.BoostStatus',
    'DefendStatus': 'towergame.model.status.DefendStatus',
    'EntraveStatus': 'towergame.model.status.EntraveStatus',
    'PoisonStatus': 'towergame.model.status.PoisonStatus',
    'WeakenStatus': 'towergame.model.status.WeakenStatus',
    
    # Managers
    'BattleManager': 'towergame.model.managers.BattleManager',
    'StageManager': 'towergame.model.managers.StageManager',
    'SuccessTracker': 'towergame.model.managers.SuccessTracker',
    
    # View
    'ConsoleView': 'towergame.view.ConsoleView',
    'GameView': 'towergame.view.GameView',
    'JavaFXMain': 'towergame.view.JavaFXMain',
    
    # Controller
    'BattleController': 'towergame.controller.BattleController',
    'GameEngine': 'towergame.controller.GameEngine'
}

def get_package_from_path(file_path):
    """Détermine le package à partir du chemin du fichier"""
    if "model/entities" in str(file_path):
        return "towergame.model.entities"
    elif "model/actions" in str(file_path):
        return "towergame.model.actions"
    elif "model/status" in str(file_path):
        return "towergame.model.status"
    elif "model/managers" in str(file_path):
        return "towergame.model.managers"
    elif "view" in str(file_path):
        return "towergame.view"
    elif "controller" in str(file_path):
        return "towergame.controller"
    return ""

def find_missing_imports(content, current_package):
    """Trouve les imports manquants dans le contenu"""
    imports_needed = set()
    
    # Chercher toutes les références aux classes
    for class_name, full_import in CLASS_TO_PACKAGE.items():
        package_name = full_import.rsplit('.', 1)[0]
        
        # Si la classe est utilisée et pas dans le même package
        if class_name in content and package_name != current_package:
            # Vérifier si l'import n'existe pas déjà
            if f"import {full_import}" not in content:
                imports_needed.add(full_import)
    
    return imports_needed

def process_java_file(file_path):
    """Traite un fichier Java pour corriger le package et les imports"""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        current_package = get_package_from_path(file_path)
        if not current_package:
            return False
        
        lines = content.split('\n')
        new_lines = []
        
        # Ajouter le package si manquant
        if not content.strip().startswith('package'):
            content = f'package {current_package};\n\n{content}'
        
        lines = content.split('\n')

        
        # Trouver les imports manquants
        missing_imports = find_missing_imports(content, current_package)
        
        if missing_imports:
            # Ajouter les imports après la déclaration du package
            lines = content.split('\n')
            new_content = []
            package_found = False
            imports_added = False
            
            for line in lines:
                new_content.append(line)
                
                if line.startswith('package ') and not package_found:
                    package_found = True
                elif package_found and not imports_added and (line.strip() == '' or line.startswith('import') or line.startswith('public')):
                    if not line.startswith('import') and line.strip() != '':
                        # Ajouter les imports avant la déclaration de classe
                        for import_stmt in sorted(missing_imports):
                            new_content.insert(-1, f'import {import_stmt};')
                        new_content.insert(-1, '')
                        imports_added = True
            
            if not imports_added and missing_imports:
                # Ajouter les imports après le package si pas encore fait
                package_line_index = -1
                for i, line in enumerate(new_content):
                    if line.startswith('package '):
                        package_line_index = i
                        break
                
                if package_line_index >= 0:
                    insert_position = package_line_index + 1
                    new_content.insert(insert_position, '')
                    for import_stmt in sorted(missing_imports, reverse=True):
                        new_content.insert(insert_position + 1, f'import {import_stmt};')
                    new_content.insert(insert_position + len(missing_imports) + 1, '')
            
            content = '\n'.join(new_content)
        
        # Écrire le fichier mis à jour
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)
        
        print(f"  ✓ {file_path.name} updated")
        return True
        
    except Exception as e:
        print(f"  ✗ Error processing {file_path.name}: {e}")
        return False

def main():
    print("Starting import fix...")
    print("=" * 50)
    
    # Traiter tous les fichiers Java dans la structure MVC
    for directory in ["model/entities", "model/actions", "model/status", "model/managers", "view", "controller"]:
        dir_path = BASE_PATH / directory
        print(f"\nChecking directory: {dir_path}")
        if dir_path.exists():
            print(f"Processing {directory}/")
            java_files = list(dir_path.glob("*.java"))
            print(f"Found {len(java_files)} Java files: {[f.name for f in java_files]}")
            for java_file in java_files:
                process_java_file(java_file)
        else:
            print(f"Directory does not exist: {dir_path}")
    
    # Traiter aussi les tests
    test_path = Path("src/test/java/towergame")
    if test_path.exists():
        print(f"\nProcessing tests/")
        for java_file in test_path.glob("*.java"):
            process_java_file(java_file)
    
    print("\n" + "=" * 50)
    print("Import fix complete!")

if __name__ == "__main__":
    main()