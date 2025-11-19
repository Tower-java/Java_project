#!/usr/bin/env python3
import os
import re

def clean_duplicates():
    """Nettoie les déclarations package dupliquées"""
    
    for root, dirs, files in os.walk("src"):
        for file in files:
            if file.endswith('.java'):
                filepath = os.path.join(root, file)
                
                try:
                    with open(filepath, 'r', encoding='utf-8') as f:
                        content = f.read()
                    
                    # Supprimer les déclarations package dupliquées
                    lines = content.split('\n')
                    new_lines = []
                    package_found = False
                    
                    for line in lines:
                        if line.startswith('package ') and not package_found:
                            new_lines.append(line)
                            package_found = True
                        elif line.startswith('package ') and package_found:
                            # Ignorer les déclarations package dupliquées
                            continue
                        else:
                            new_lines.append(line)
                    
                    new_content = '\n'.join(new_lines)
                    
                    if new_content != content:
                        with open(filepath, 'w', encoding='utf-8') as f:
                            f.write(new_content)
                        print(f"Cleaned {filepath}")
                
                except Exception as e:
                    print(f"Error processing {filepath}: {e}")

if __name__ == "__main__":
    clean_duplicates()
    print("Done!")