#!/usr/bin/env python3
import os
import re

def fix_file(filepath):
    """Corrige un fichier Java avec son package et ses imports"""
    
    # Déterminer le package selon le répertoire
    if "model/entities" in filepath:
        package = "towergame.model.entities"
    elif "model/actions" in filepath:
        package = "towergame.model.actions"
    elif "model/status" in filepath:
        package = "towergame.model.status"
    elif "model/managers" in filepath:
        package = "towergame.model.managers"
    elif "view" in filepath:
        package = "towergame.view"
    elif "controller" in filepath:
        package = "towergame.controller"
    elif "test" in filepath:
        package = "towergame"
    else:
        return False
    
    # Mapping des imports nécessaires
    imports_map = {
        'AEntity': 'import towergame.model.entities.AEntity;',
        'Entity': 'import towergame.model.entities.Entity;',
        'Player': 'import towergame.model.entities.Player;',
        'ABoss': 'import towergame.model.entities.ABoss;',
        'FireElementalBoss': 'import towergame.model.entities.FireElementalBoss;',
        'AAction': 'import towergame.model.actions.AAction;',
        'Action': 'import towergame.model.actions.Action;',
        'Element': 'import towergame.model.actions.Element;',
        'BossAttackAction': 'import towergame.model.actions.BossAttackAction;',
        'BossDefendAction': 'import towergame.model.actions.BossDefendAction;',
        'BossHealAction': 'import towergame.model.actions.BossHealAction;',
        'PlayerAttackAction': 'import towergame.model.actions.PlayerAttackAction;',
        'PlayerBoostSpell': 'import towergame.model.actions.PlayerBoostSpell;',
        'PlayerDefendSpell': 'import towergame.model.actions.PlayerDefendSpell;',
        'PlayerHealSpell': 'import towergame.model.actions.PlayerHealSpell;',
        'FireSpell': 'import towergame.model.actions.FireSpell;',
        'WaterSpell': 'import towergame.model.actions.WaterSpell;',
        'PlantSpell': 'import towergame.model.actions.PlantSpell;',
        'FireHardSpell': 'import towergame.model.actions.FireHardSpell;',
        'WaterHardSpell': 'import towergame.model.actions.WaterHardSpell;',
        'PlantHardSpell': 'import towergame.model.actions.PlantHardSpell;',
        'IStatusEffect': 'import towergame.model.status.IStatusEffect;',
        'StatusEffect': 'import towergame.model.status.StatusEffect;',
        'BoostStatus': 'import towergame.model.status.BoostStatus;',
        'DefendStatus': 'import towergame.model.status.DefendStatus;',
        'EntraveStatus': 'import towergame.model.status.EntraveStatus;',
        'PoisonStatus': 'import towergame.model.status.PoisonStatus;',
        'WeakenStatus': 'import towergame.model.status.WeakenStatus;',
        'BattleManager': 'import towergame.model.managers.BattleManager;',
        'StageManager': 'import towergame.model.managers.StageManager;',
        'SuccessTracker': 'import towergame.model.managers.SuccessTracker;',
        'ConsoleView': 'import towergame.view.ConsoleView;',
        'GameView': 'import towergame.view.GameView;',
        'JavaFXMain': 'import towergame.view.JavaFXMain;',
        'BattleController': 'import towergame.controller.BattleController;',
        'GameEngine': 'import towergame.controller.GameEngine;'
    }
    
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Ajouter le package si manquant
        if not content.startswith('package'):
            content = f'package {package};\n\n{content}'
        
        # Trouver les imports nécessaires
        needed_imports = []
        for class_name, import_stmt in imports_map.items():
            # Vérifier si la classe est utilisée dans le fichier
            if re.search(rf'\b{class_name}\b', content) and import_stmt not in content:
                # Ne pas importer une classe de son propre package
                import_package = import_stmt.split()[1].rsplit('.', 1)[0]
                if import_package != package:
                    needed_imports.append(import_stmt)
        
        # Ajouter les imports manquants après le package
        if needed_imports:
            lines = content.split('\n')
            new_lines = []
            
            for line in lines:
                new_lines.append(line)
                if line.startswith('package '):
                    new_lines.append('')
                    for import_stmt in sorted(needed_imports):
                        new_lines.append(import_stmt)
                    new_lines.append('')
                    break
            
            # Ajouter le reste des lignes (en sautant les imports déjà traités)
            in_imports = False
            for line in lines[len([l for l in new_lines if not l.startswith('import')]):]:
                if line.startswith('import'):
                    in_imports = True
                elif in_imports and not line.startswith('import') and line.strip() != '':
                    in_imports = False
                
                if not in_imports or not line.startswith('import'):
                    new_lines.append(line)
            
            content = '\n'.join(new_lines)
        
        # Écrire le fichier corrigé
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        
        print(f"✓ Fixed {os.path.basename(filepath)}")
        return True
        
    except Exception as e:
        print(f"✗ Error fixing {filepath}: {e}")
        return False

def main():
    print("Fixing all Java files...")
    
    # Traiter tous les fichiers Java
    base_dir = "src/main/java/towergame"
    
    for root, dirs, files in os.walk(base_dir):
        for file in files:
            if file.endswith('.java'):
                filepath = os.path.join(root, file)
                fix_file(filepath)
    
    # Traiter les tests
    test_dir = "src/test/java/towergame"
    if os.path.exists(test_dir):
        for root, dirs, files in os.walk(test_dir):
            for file in files:
                if file.endswith('.java'):
                    filepath = os.path.join(root, file)
                    fix_file(filepath)
    
    print("Done!")

if __name__ == "__main__":
    main()