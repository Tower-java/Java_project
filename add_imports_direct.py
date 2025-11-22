#!/usr/bin/env python3
import os
import re

def main():
    print("=== AJOUT D'IMPORTS DIRECT ===")
    
    # Mapping direct des imports par fichier
    imports_by_file = {
        "src/main/java/towergame/model/entities/Player.java": [
            "import towergame.model.actions.AAction;",
            "import towergame.model.actions.Element;"
        ],
        "src/main/java/towergame/model/entities/AEntity.java": [
            "import towergame.model.actions.Element;",
            "import towergame.model.status.IStatusEffect;"
        ],
        "src/main/java/towergame/model/entities/ABoss.java": [
            "import towergame.model.actions.AAction;",
            "import towergame.model.actions.Element;",
            "import towergame.model.entities.Player;"
        ],
        "src/main/java/towergame/model/entities/FireElementalBoss.java": [
            "import towergame.model.actions.*;",
            "import towergame.model.entities.Player;"
        ],
        "src/main/java/towergame/model/entities/Entity.java": [
            "import towergame.model.actions.Element;",
            "import towergame.model.status.StatusEffect;"
        ],
        # Actions
        "src/main/java/towergame/model/actions/BossAttackAction.java": [
            "import towergame.model.entities.AEntity;"
        ],
        "src/main/java/towergame/model/actions/BossDefendAction.java": [
            "import towergame.model.entities.AEntity;",
            "import towergame.model.status.DefendStatus;"
        ],
        "src/main/java/towergame/model/actions/BossHealAction.java": [
            "import towergame.model.entities.AEntity;"
        ],
        "src/main/java/towergame/model/actions/FireHardSpell.java": [
            "import towergame.model.entities.AEntity;",
            "import towergame.model.status.*;"
        ],
        "src/main/java/towergame/model/actions/FireSpell.java": [
            "import towergame.model.entities.AEntity;",
            "import towergame.model.status.*;"
        ],
        "src/main/java/towergame/model/actions/WaterHardSpell.java": [
            "import towergame.model.entities.AEntity;",
            "import towergame.model.status.*;"
        ],
        "src/main/java/towergame/model/actions/WaterSpell.java": [
            "import towergame.model.entities.AEntity;",
            "import towergame.model.status.*;"
        ],
        "src/main/java/towergame/model/actions/PlantHardSpell.java": [
            "import towergame.model.entities.AEntity;",
            "import towergame.model.status.*;"
        ],
        "src/main/java/towergame/model/actions/PlantSpell.java": [
            "import towergame.model.entities.AEntity;",
            "import towergame.model.status.*;"
        ],
        "src/main/java/towergame/model/actions/PlayerBoostSpell.java": [
            "import towergame.model.entities.AEntity;",
            "import towergame.model.status.BoostStatus;"
        ],
        "src/main/java/towergame/model/actions/PlayerDefendSpell.java": [
            "import towergame.model.entities.AEntity;",
            "import towergame.model.status.DefendStatus;"
        ],
        "src/main/java/towergame/model/actions/PlayerHealSpell.java": [
            "import towergame.model.entities.AEntity;"
        ],
        # Status
        "src/main/java/towergame/model/status/BoostStatus.java": [
            "import towergame.model.entities.AEntity;"
        ],
        "src/main/java/towergame/model/status/DefendStatus.java": [
            "import towergame.model.entities.AEntity;"
        ],
        "src/main/java/towergame/model/status/EntraveStatus.java": [
            "import towergame.model.entities.AEntity;"
        ],
        "src/main/java/towergame/model/status/IStatusEffect.java": [
            "import towergame.model.entities.AEntity;"
        ],
        "src/main/java/towergame/model/status/PoisonStatus.java": [
            "import towergame.model.entities.AEntity;"
        ],
        "src/main/java/towergame/model/status/StatusEffect.java": [
            "import towergame.model.entities.Entity;",
            "import towergame.model.entities.AEntity;"
        ],
        "src/main/java/towergame/model/status/WeakenStatus.java": [
            "import towergame.model.entities.AEntity;"
        ],
        # Managers
        "src/main/java/towergame/model/managers/BattleManager.java": [
            "import towergame.model.actions.AAction;",
            "import towergame.model.entities.ABoss;",
            "import towergame.model.entities.Player;"
        ],
        "src/main/java/towergame/model/managers/StageManager.java": [
            "import towergame.model.actions.AAction;",
            "import towergame.model.entities.ABoss;",
            "import towergame.model.entities.FireElementalBoss;"
        ],
        "src/main/java/towergame/model/managers/SuccessTracker.java": [
            "import towergame.model.actions.AAction;",
            "import towergame.model.entities.ABoss;",
            "import towergame.model.entities.Player;"
        ],
        # View
        "src/main/java/towergame/view/ConsoleView.java": [
            "import towergame.model.actions.AAction;",
            "import towergame.model.entities.ABoss;",
            "import towergame.model.entities.Player;"
        ],
        # Controller
        "src/main/java/towergame/controller/GameEngine.java": [
            "import towergame.model.actions.AAction;",
            "import towergame.model.entities.ABoss;",
            "import towergame.model.entities.Player;",
            "import towergame.model.managers.*;",
            "import towergame.view.ConsoleView;"
        ]
    }
    
    for file_path, imports in imports_by_file.items():
        if os.path.exists(file_path):
            try:
                with open(file_path, 'r', encoding='utf-8') as f:
                    content = f.read()
                
                # Trouve la position après la déclaration package
                lines = content.split('\n')
                new_lines = []
                package_found = False
                imports_added = False
                
                for line in lines:
                    new_lines.append(line)
                    
                    # Après la ligne package, ajouter les imports
                    if line.startswith('package ') and not imports_added:
                        new_lines.append('')  # ligne vide
                        for imp in imports:
                            if imp not in content:  # Éviter les doublons
                                new_lines.append(imp)
                        new_lines.append('')  # ligne vide après imports
                        imports_added = True
                
                # Écrire le fichier mis à jour
                new_content = '\n'.join(new_lines)
                with open(file_path, 'w', encoding='utf-8') as f:
                    f.write(new_content)
                
                print(f"✓ Ajouté imports à {os.path.basename(file_path)}")
                
            except Exception as e:
                print(f"✗ Erreur avec {file_path}: {e}")
        else:
            print(f"✗ Fichier non trouvé: {file_path}")
    
    print("=== TERMINÉ ===")

if __name__ == "__main__":
    main()