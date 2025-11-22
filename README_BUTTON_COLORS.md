# 🎨 SYSTÈME DE COULEURS DES BOUTONS

## 📋 Description

Le système de boutons colorés de Tower Battle offre une expérience visuelle intuitive avec des couleurs spécifiques selon le type d'action.

## 🎯 Palette de couleurs

### 💚 **Actions de Soin**

- **Couleur** : Vert éclatant `#22C55E`
- **Bordure** : Vert foncé `#16A34A`
- **Déclencheur** : Actions contenant "soin" ou "heal"

### 🛡️ **Actions de Défense**

- **Couleur** : Bleu royal `#3B82F6`
- **Bordure** : Bleu foncé `#2563EB`
- **Déclencheur** : Actions contenant "barrière", "défense", "protection"

### ⚡ **Actions de Boost**

- **Couleur** : Violet magique `#9333EA`
- **Bordure** : Violet foncé `#7C3AED`
- **Déclencheur** : Actions contenant "boost", "amélioration"

### 🔥 **Attaques de Feu**

- **Couleur** : Rouge feu `#EF4444`
- **Bordure** : Rouge foncé `#DC2626`
- **Déclencheur** : Actions contenant "feu", "fire"

### ❄️ **Attaques de Glace**

- **Couleur** : Cyan glacé `#06B6D4`
- **Bordure** : Cyan foncé `#0891B2`
- **Déclencheur** : Actions contenant "glace", "ice"

### 🌿 **Attaques Nature**

- **Couleur** : Vert nature `#65A30D`
- **Bordure** : Vert foncé `#4D7C0F`
- **Déclencheur** : Actions contenant "plante", "nature"

### 🎯 **Attaques par Défaut**

- **Couleur** : Orange `#F97316`
- **Bordure** : Orange foncé `#EA580C`
- **Déclencheur** : Toutes les autres attaques

## ✨ Effets interactifs

### 🖱️ **Effet de survol (Hover)**

- **Comportement** : Éclaircissement de +30 RGB
- **Animation** : Transition fluide instantanée
- **Condition** : Uniquement si le bouton est activé

### 🚫 **Boutons désactivés**

- **Couleur** : Version assombrie (RGB ÷ 3)
- **Transparence** : Alpha à 150
- **Indication** : Texte avec cooldown affiché

## 🔧 Propriétés techniques

### Dimensions

- **Taille** : 170×55 pixels (augmentée de 160×50)
- **Police** : Arial Bold 14pt (augmentée de 12pt)
- **Bordure** : 2px solide avec couleur foncée

### Rendu

- **Opaque** : `setOpaque(true)` pour couleurs pleines
- **Bordure peinte** : `setBorderPainted(true)`
- **Focus** : `setFocusPainted(false)` pour un look propre

## 🎮 Avantages utilisateur

### 🧠 **Reconnaissance intuitive**

- Identification rapide des types d'actions
- Cohérence visuelle avec les éléments RPG
- Réduction du temps de décision

### 👁️ **Confort visuel**

- Couleurs vibrantes mais non éblouissantes
- Contraste optimal avec le texte blanc
- Feedback visuel immédiat sur survol

### 🚀 **Performance**

- Rendu optimisé avec couleurs prédéfinies
- Gestion mémoire efficace des couleurs
- Animations fluides sans lag

---

_Système de couleurs conçu pour une expérience RPG immersive_ 🎨✨
