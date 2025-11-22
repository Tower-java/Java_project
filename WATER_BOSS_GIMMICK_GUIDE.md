# Guide du Gimmick du Boss d'Eau 💧

## Résumé Rapide

Le boss d'eau a **2 phases de gimmick** à comprendre :

### **Phase 1 - Briser l'Invulnérabilité (Tours 1-2)**

Le boss commence **invulnérable** et résiste à tous les dégâts !

**Pour briser son bouclier :**

- **Tours 1 ou 2 UNIQUEMENT**
- **Attaquez avec un sort de FEU** → `Coup de Feu` ou `Inferno`
- Si réussi → message : "Élémentaire d'eau rugit alors que sa fine couche de glace protectrice fond !"
- Sinon, il redevient invulnérable au tour suivant

### **Phase 2 - Déclencher l'Enrage (après Tour 4)**

Une fois l'invulnérabilité cassée, il peut devenir enragé par **2 conditions** :

**Option 1 - Attaque FEU après tour 4** :

- Tours 5, 6, 7, 8+ : attaquez avec `Coup de Feu` ou `Inferno`
- Le boss rentre en rage → utilise `Boss_d'eau_enragé.png`

**Option 2 - Réduire ses PV sous 40%** :

- Dégâts cumulés : 100 PV × 0.40 = **40 PV**
- Si boss HP ≤ 40 : enrage automatique

## Debug Logs à Regarder

Pendant le combat, vous verrez des messages `DEBUG: GIMMICK:` qui vous indiqueront :

```
DEBUG GIMMICK: Tour=1, Invulnérable=true, ActionName=Coup de Feu, Element=FIRE, BossHP=100/100
→ Cela signifie: C'est bon, tu as attaqué avec du FEU au tour 1 !
```

## Stratégie Recommandée

1. **Tour 1 ou 2** : Utilise `Coup de Feu` ou `Inferno` pour briser le bouclier
2. **Tours 3-4** : Prépare tes attaques (boost, heal, etc)
3. **Tours 5+** :
   - Soit : Utilise `Coup de Feu` pour enrager le boss
   - Soit : Réduis ses PV sous 40% pour l'enrager automatiquement
4. **Phase Enragée** : Le sprite du boss change, il est plus puissant !

## Actions Disponibles du Joueur

### Attaques FEU 🔥

- `Coup de Feu` (5 dégâts, pas de cooldown)
- `Inferno` (10 dégâts, cooldown 2 tours)

### Autres Actions

- `Attaque` (9999 dégâts, élément NEUTRE)
- `Soin Léger` (25 HP récupérés)
- `Barrière` (2 PV de défense)
- `Fureur` (boost 1.5x les dégâts pour 3 tours)
- `Jet de Glace` (5 dégâts, EAU)
- `Lianes` (5 dégâts, PLANT)
- `Blizzard` (10 dégâts, EAU, cooldown 2)
- `Encracinement` (10 dégâts, PLANT, cooldown 2)

## Problèmes Courants

**Q: Je n'arrive pas à briser l'invulnérabilité**

- R: Vérifie que tu attaques avec un sort **FEU** (Coup de Feu ou Inferno) au **tour 1 ou 2**

**Q: Le boss ne devient pas enragé**

- R: Après avoir cassé l'invulnérabilité, attaque avec du FEU après le tour 4, OU réduis ses PV sous 40%

**Q: Comment je vois si c'est en rage ?**

- R: Le sprite du boss change en `Boss_d'eau_enragé.png`

**Q: Pourquoi l'invulnérabilité revient ?**

- R: L'invulnérabilité ne revient PAS une fois cassée. Regarde les logs DEBUG pour voir l'état actuel.
