# 🔍 Recherche d'APIs Gratuites pour Données Boursières Temps Réel

## 📅 Date de recherche : 22 octobre 2025

### 🎯 **Objectif**
Trouver une API gratuite qui fournit les cours boursiers **en temps réel** (et non avec un délai d'un jour) pour les actions européennes (Orange, Bouygues, SFR, Free).

---

## 📊 **APIs Financières Gratuites Analysées**

### 1. **Yahoo Finance API (Unofficial)**
- **URL** : `https://query1.finance.yahoo.com/`
- **Délai** : 15-20 minutes (pas temps réel)
- **Limite** : Pas de clé API, mais peut être bloquée
- **Actions européennes** : ✅ Supportées
- **Verdict** : ❌ Pas temps réel

### 2. **Alpha Vantage** (Actuel)
- **URL** : `https://www.alphavantage.co/`
- **Délai** : Données de la veille (J-1)
- **Limite** : 5 appels/minute, 500/jour
- **Actions européennes** : ✅ Supportées
- **Verdict** : ❌ Pas temps réel

### 3. **IEX Cloud**
- **URL** : `https://iexcloud.io/`
- **Plan gratuit** : 500 000 appels/mois
- **Délai** : 15 minutes pour plan gratuit
- **Actions européennes** : ❌ Principalement US
- **Verdict** : ❌ Pas d'actions européennes

### 4. **Finnhub**
- **URL** : `https://finnhub.io/`
- **Plan gratuit** : 60 appels/minute
- **Délai** : 15 minutes pour plan gratuit
- **Actions européennes** : ✅ Supportées
- **Temps réel** : 💰 Payant uniquement
- **Verdict** : ❌ Temps réel payant

### 5. **Twelve Data**
- **URL** : `https://twelvedata.com/`
- **Plan gratuit** : 800 appels/jour
- **Délai** : 15 minutes pour plan gratuit
- **Actions européennes** : ✅ Supportées
- **Temps réel** : 💰 Payant uniquement
- **Verdict** : ❌ Temps réel payant

### 6. **Polygon.io**
- **URL** : `https://polygon.io/`
- **Plan gratuit** : 5 appels/minute
- **Délai** : 15 minutes pour plan gratuit
- **Actions européennes** : ❌ Principalement US
- **Verdict** : ❌ Pas d'actions européennes

### 7. **Marketstack**
- **URL** : `https://marketstack.com/`
- **Plan gratuit** : 1000 appels/mois
- **Délai** : Fin de journée uniquement
- **Actions européennes** : ✅ Supportées
- **Verdict** : ❌ Pas temps réel

### 8. **Quandl (Nasdaq Data Link)**
- **URL** : `https://data.nasdaq.com/`
- **Plan gratuit** : Limité
- **Délai** : Fin de journée
- **Actions européennes** : ✅ Limitées
- **Verdict** : ❌ Pas temps réel

---

## 🔍 **APIs Alternatives Explorées**

### 1. **Yahoo Finance (Scraping)**
- **Méthode** : Scraping direct du site web
- **Délai** : Potentiellement temps réel
- **Problèmes** : 
  - Violation des ToS
  - Peut être bloqué
  - Structure HTML changeante
- **Verdict** : ❌ Non recommandé

### 2. **Google Finance (Scraping)**
- **Méthode** : Scraping du site Google Finance
- **Délai** : Potentiellement temps réel
- **Problèmes** :
  - Violation des ToS
  - Protection anti-bot
  - Pas d'API officielle
- **Verdict** : ❌ Non recommandé

### 3. **Boursorama API**
- **Statut** : Pas d'API publique
- **Méthode** : Scraping uniquement
- **Verdict** : ❌ Non disponible

### 4. **Euronext API**
- **URL** : `https://live.euronext.com/`
- **Statut** : API payante uniquement
- **Plan gratuit** : ❌ Inexistant
- **Verdict** : ❌ Payant

---

## 💰 **APIs Payantes avec Temps Réel**

### 1. **Alpha Vantage Premium**
- **Prix** : $49.99/mois
- **Délai** : Temps réel
- **Actions européennes** : ✅

### 2. **IEX Cloud Premium**
- **Prix** : $9/mois (plan de base)
- **Délai** : Temps réel
- **Actions européennes** : ❌

### 3. **Finnhub Premium**
- **Prix** : $59.99/mois
- **Délai** : Temps réel
- **Actions européennes** : ✅

### 4. **Bloomberg API**
- **Prix** : $2000+/mois
- **Délai** : Temps réel
- **Actions européennes** : ✅

---

## 🚫 **Contraintes du Marché Financier**

### **Pourquoi Pas de Temps Réel Gratuit ?**

1. **Coût des Données** : Les bourses facturent l'accès aux données temps réel
2. **Licences** : Euronext, NYSE, etc. ont des accords de distribution payants
3. **Réglementation** : Les données financières sont strictement contrôlées
4. **Valeur Commerciale** : Les données temps réel ont une valeur marchande élevée

### **Délais Standards Gratuits**
- **15-20 minutes** : Standard pour la plupart des APIs gratuites
- **Fin de journée** : Pour les APIs de base
- **J-1** : Pour certaines APIs comme Alpha Vantage

---

## 🎯 **Recommandations**

### **Option 1 : Accepter le Délai (Recommandé)**
- Garder Alpha Vantage avec délai J-1
- Ajouter un disclaimer "Données différées"
- Comportement normal pour une app gratuite

### **Option 2 : Essayer Yahoo Finance Unofficial**
```
Endpoint: https://query1.finance.yahoo.com/v8/finance/chart/ORA.PA
Délai: 15-20 minutes
Risque: Peut être bloqué
```

### **Option 3 : Combinaison d'APIs**
- Yahoo Finance pour données récentes (15 min)
- Alpha Vantage en fallback
- Rotation automatique en cas de blocage

### **Option 4 : Upgrade Payant**
- Alpha Vantage Premium : $49.99/mois
- Finnhub Premium : $59.99/mois
- Pour une app commerciale

---

## 📋 **Conclusion**

### ❌ **Réalité du Marché**
**Il n'existe PAS d'API gratuite fiable pour les données boursières temps réel des actions européennes.**

### ✅ **Meilleures Options Gratuites**
1. **Yahoo Finance** : 15-20 minutes de délai
2. **Alpha Vantage** : Données J-1 (actuel)
3. **Finnhub** : 15 minutes de délai

### 🎯 **Recommandation Finale**
**Garder Alpha Vantage** et ajouter un disclaimer explicite :
- "Données différées - Dernière mise à jour : [date]"
- "Pour usage informatif uniquement"
- "Données non contractuelles"

L'application **OpéCours** reste techniquement excellente, mais les contraintes du marché financier limitent l'accès gratuit aux données temps réel.