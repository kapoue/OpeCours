# 🔄 Migration vers Finnhub API - OpéCours

## 📅 Date : 22 octobre 2025

### 🎯 **Objectif de la Migration**
Passer d'Alpha Vantage (délai J-1) vers Finnhub (délai 15 minutes) pour obtenir des données boursières plus récentes, répondant à la définition du "temps réel" de l'utilisateur (≤ 1 heure).

---

## ✅ **Changements Implémentés**

### 1. **Nouvelle API Finnhub**
- **Interface** : [`FinnhubApi.kt`](app/src/main/java/com/kapoue/opecours/data/remote/FinnhubApi.kt)
- **DTOs** : 
  - [`FinnhubQuoteResponse.kt`](app/src/main/java/com/kapoue/opecours/data/remote/dto/FinnhubQuoteResponse.kt)
  - [`FinnhubCandleResponse.kt`](app/src/main/java/com/kapoue/opecours/data/remote/dto/FinnhubCandleResponse.kt)
- **Mappers** : [`FinnhubMappers.kt`](app/src/main/java/com/kapoue/opecours/data/remote/dto/FinnhubMappers.kt)

### 2. **Configuration API**
```kotlin
// Constants.kt
const val FINNHUB_BASE_URL = "https://finnhub.io/api/v1/"
const val FINNHUB_API_KEY = "d3se7o9r01qvii72sk0gd3se7o9r01qvii72sk10"
```

### 3. **Injection de Dépendances**
- **Dual Retrofit** : Finnhub + Alpha Vantage (fallback)
- **Qualifiers** : `@FinnhubRetrofit` et `@AlphaVantageRetrofit`

### 4. **Repository Modifié**
- **Méthode principale** : `fetchStockFromFinnhub()`
- **Deux appels API** :
  1. `getQuote()` : Cours actuel
  2. `getCandles()` : Données historiques pour graphique
- **Logs détaillés** : Traçabilité complète

### 5. **Opérateurs Supportés**
- ✅ **Orange (ORA.PA)** : Finnhub
- ✅ **Bouygues (EN.PA)** : Finnhub  
- ❌ **SFR (ATC.PA)** : Désactivé (isActive = false)
- ❌ **Free (ILD.PA)** : Désactivé (isActive = false)

---

## 📊 **Comparaison des APIs**

| Aspect | Alpha Vantage | Finnhub |
|--------|---------------|---------|
| **Délai** | J-1 (24h+) | 15 minutes |
| **Appels/minute** | 5 | 60 |
| **Actions EU** | ✅ Limitées | ✅ Complètes |
| **JSON** | Complexe | Simple |
| **Stabilité** | ✅ Bonne | ✅ Excellente |
| **Documentation** | ✅ Correcte | ✅ Excellente |

---

## 🔧 **Détails Techniques**

### **Endpoints Finnhub Utilisés**

#### 1. Quote (Cours Actuel)
```
GET https://finnhub.io/api/v1/quote?symbol=ORA.PA&token=YOUR_KEY
```
**Réponse** :
```json
{
  "c": 14.16,    // Prix actuel
  "d": -0.18,    // Variation €
  "dp": -1.25,   // Variation %
  "o": 14.28,    // Ouverture
  "pc": 14.34,   // Clôture précédente
  "t": 1697731200 // Timestamp (secondes)
}
```

#### 2. Candles (Historique)
```
GET https://finnhub.io/api/v1/stock/candle?symbol=ORA.PA&resolution=D&from=START&to=END&token=YOUR_KEY
```
**Réponse** :
```json
{
  "c": [14.16, 14.25, ...], // Prix de clôture
  "t": [1697673600, ...],   // Timestamps
  "s": "ok"                 // Statut
}
```

### **Mapping des Données**
```kotlin
fun FinnhubQuoteResponse.toStock(operator: Operator): Stock {
    return Stock(
        symbol = operator.symbol,
        operatorName = operator.displayName,
        currentPrice = currentPrice,
        openPrice = openPrice,
        previousClose = previousClose,
        change = change,
        changePercent = changePercent,
        lastUpdate = timestamp * 1000, // ⚠️ Conversion secondes → millisecondes
        isMarketOpen = DateUtils.isMarketOpen(),
        historicalPrices = historicalPrices,
        volume = 0L
    )
}
```

---

## 🕐 **Gestion de l'Heure**

### **Point Important : Heure des Données**
- **Avant** : `lastUpdate` = heure de refresh de l'app
- **Maintenant** : `lastUpdate` = timestamp des données Finnhub
- **Affichage** : "MAJ: 14:30" = heure réelle des données (pas de l'app)

### **Conversion Timestamp**
```kotlin
// Finnhub renvoie des timestamps en SECONDES
val timestamp = quoteResponse.timestamp * 1000 // Conversion en millisecondes
```

---

## 🚀 **Avantages de la Migration**

### **1. Délai Réduit**
- **Avant** : Données de la veille (J-1)
- **Maintenant** : Données de 15 minutes maximum
- **Perception** : "Temps réel" selon critère utilisateur

### **2. Fréquence d'Appels**
- **Avant** : 5 appels/minute (Alpha Vantage)
- **Maintenant** : 60 appels/minute (Finnhub)
- **Bénéfice** : Refresh plus fréquent possible

### **3. Qualité des Données**
- **JSON plus simple** : Parsing plus fiable
- **API professionnelle** : Documentation complète
- **Support technique** : Équipe dédiée

---

## ⚠️ **Points d'Attention**

### **1. Clé API**
- **Gratuite** : 60 appels/minute
- **Limite** : À surveiller en production
- **Upgrade** : $59.99/mois pour temps réel complet

### **2. Opérateurs Désactivés**
- **SFR et Free** : Restent grisés
- **Raison** : Données non fiables ou obsolètes
- **Solution future** : Rechercher d'autres symboles

### **3. Fallback**
- **Alpha Vantage** : Conservé en secours
- **Mock Data** : Désactivé par défaut
- **Gestion d'erreurs** : Robuste

---

## 🧪 **Tests à Effectuer**

### **1. Tests Fonctionnels**
- [ ] Vérifier les cours Orange et Bouygues
- [ ] Contrôler l'heure d'affichage des données
- [ ] Tester le pull-to-refresh
- [ ] Valider l'auto-refresh (5 minutes)

### **2. Tests de Robustesse**
- [ ] Coupure réseau
- [ ] Erreur API Finnhub
- [ ] Données invalides
- [ ] Limite de quota atteinte

### **3. Tests de Performance**
- [ ] Temps de réponse API
- [ ] Consommation mémoire
- [ ] Fluidité de l'interface

---

## 📝 **Logs de Débogage**

### **Nouveaux Logs Finnhub**
```
🔄 Début de fetchFromApi() avec Finnhub API
🔑 Clé Finnhub utilisée: d3se7o9r01qvii72sk0g...
📞 Appel Finnhub pour Orange (ORA.PA)
📡 Réponse HTTP 200 pour Orange
📦 Quote reçu pour Orange: 14.16€ (timestamp: 1697731200)
📈 Données historiques pour Orange: 5 points
✅ Données Finnhub récupérées pour Orange: 14.16€ (-1.25%)
🕐 Heure des données: 22/10/2025 14:30
```

---

## 🎯 **Résultat Attendu**

### **Avant Migration (Alpha Vantage)**
```
Orange: 14.125€ (-1.22%) - MAJ: 21/10/2025 (J-1)
Bouygues: 40.77€ (-0.27%) - MAJ: 21/10/2025 (J-1)
```

### **Après Migration (Finnhub)**
```
Orange: 14.16€ (-1.25%) - MAJ: 14:30 (15 min de délai)
Bouygues: 40.85€ (+0.15%) - MAJ: 14:30 (15 min de délai)
```

---

## ✅ **Validation de la Migration**

La migration sera considérée comme réussie si :

1. ✅ **Données récentes** : Délai ≤ 15 minutes
2. ✅ **Heure correcte** : Timestamp des données (pas de refresh)
3. ✅ **Orange et Bouygues** : Fonctionnels
4. ✅ **SFR et Free** : Grisés correctement
5. ✅ **Interface fluide** : Pas de régression
6. ✅ **Logs détaillés** : Traçabilité complète

---

## 🚀 **Prochaines Étapes**

1. **Test avec clé demo** : Validation technique
2. **Test avec vraie clé** : Validation fonctionnelle
3. **Monitoring** : Surveillance des quotas
4. **Documentation** : Mise à jour README
5. **Optimisation** : Réduction des appels si nécessaire

---

**Migration réalisée le 22 octobre 2025**  
**Objectif** : Transformer OpéCours en app "temps réel" (15 minutes)