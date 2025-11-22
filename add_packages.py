#!/usr/bin/env python3
import os
import glob

def add_package_if_missing(file_path, package_name):
    """Ajoute la déclaration package si manquante"""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Si le fichier ne commence pas par package, l'ajouter
        if not content.strip().startswith('package '):
            new_content = f'package {package_name};\n\n{content}'
            
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(new_content)
            
            print(f"Added package to {os.path.basename(file_path)}")
    except Exception as e:
        print(f"Error processing {file_path}: {e}")

def main():
    print("Adding missing package declarations...")
    
    # Mapping des répertoires vers les packages
    mappings = [
        ("src/main/java/towergame/model/entities/*.java", "towergame.model.entities"),
        ("src/main/java/towergame/model/actions/*.java", "towergame.model.actions"),
        ("src/main/java/towergame/model/status/*.java", "towergame.model.status"),
        ("src/main/java/towergame/model/managers/*.java", "towergame.model.managers"),
        ("src/main/java/towergame/view/*.java", "towergame.view"),
        ("src/main/java/towergame/controller/*.java", "towergame.controller"),
        ("src/test/java/towergame/*.java", "towergame"),
    ]
    
    for pattern, package in mappings:
        files = glob.glob(pattern)
        for file_path in files:
            add_package_if_missing(file_path, package)
    
    print("Done!")

if __name__ == "__main__":
    main()