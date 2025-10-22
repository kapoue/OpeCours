# 📊 Analyse Détaillée : Finnhub API

## 🌐 **Vue d'ensemble**

**Finnhub** est une API financière professionnelle qui fournit des données boursières, des actualités financières et des analyses de marché. C'est une alternative sérieuse à Alpha Vantage et Yahoo Finance.

- **Site officiel** : https://finnhub.io/
- **Documentation** : https://finnhub.io/docs/api
- **Fondée** : 2017
- **Siège** : États-Unis
- **Spécialité** : Données financières temps réel et historiques

---

## 💰 **Plans Tarifaires**

### **Plan Gratuit (Free)**
- **Prix** : 0€/mois
- **Appels API** : 60 appels/minute
- **Délai des données** : **15 minutes** (pas temps réel)
- **Actions supportées** : ✅ Actions américaines et européennes
- **Données historiques** : ✅ Limitées
- **Support** : Community

### **Plan Basic**
- **Prix** : $59.99/mois (~55€)
- **Appels API** : 300 appels/minute
- **Délai des données** : **Temps réel**
- **Actions supportées** : ✅ Toutes les bourses mondiales
- **Données historiques** : ✅ Complètes
- **Support** : Email

### **Plan Pro**
- **Prix** : $299/mois (~275€)
- **Appels API** : 600 appels/minute
- **Fonctionnalités** : Données institutionnelles, WebSocket
- **Support** : Prioritaire

---

## 🔧 **Fonctionnalités Techniques**

### **Endpoints Disponibles**

#### 1. **Quote (Cours Actuel)**
```
GET https://finnhub.io/api/v1/quote?symbol=ORA.PA&token=YOUR_API_KEY
```

**Réponse exemple** :
```json
{
  "c": 14.16,     // Prix actuel (current)
  "d": -0.18,     // Variation absolue (change)
  "dp": -1.25,    // Variation en % (percent change)
  "h": 14.35,     // Plus haut du jour (high)
  "l": 14.10,     // Plus bas du jour (low)
  "o": 14.28,     // Prix d'ouverture (open)
  "pc": 14.34,    // Clôture précédente (previous close)
  "t": 1697731200 // Timestamp
}
```

#### 2. **Données Historiques**
```
GET https://finnhub.io/api/v1/stock/candle?symbol=ORA.PA&resolution=D&from=1697644800&to=1697731200&token=YOUR_API_KEY
```

#### 3. **Profil de l'Entreprise**
```
GET https://finnhub.io/api/v1/stock/profile2?symbol=ORA.PA&token=YOUR_API_KEY
```

#### 4. **Actualités**
```
GET https://finnhub.io/api/v1/company-news?symbol=ORA.PA&from=2023-10-01&to=2023-10-20&token=YOUR_API_KEY
```

---

## 🇪🇺 **Support des Actions Européennes**

### **Actions Françaises Supportées**
- ✅ **Orange** : `ORA.PA`
- ✅ **Bouygues** : `EN.PA`
- ✅ **Altice/SFR** : `ATC.PA`
- ✅ **Iliad/Free** : `ILD.PA`

### **Autres Bourses Européennes**
- ✅ **Euronext Paris** : `.PA`
- ✅ **London Stock Exchange** : `.L`
- ✅ **Frankfurt** : `.DE`
- ✅ **Milan** : `.MI`
- ✅ **Amsterdam** : `.AS`

---

## ⚡ **Avantages de Finnhub**

### **1. Délai Réduit (Plan Gratuit)**
- **15 minutes** vs **J-1** (Alpha Vantage)
- Meilleur compromis pour une app gratuite

### **2. API Professionnelle**
- Documentation complète et claire
- Réponses JSON structurées et cohérentes
- Support technique disponible

### **3. Fiabilité**
- Service stable et professionnel
- Uptime élevé (99.9%+)
- Pas de blocages inattendus

### **4. Fonctionnalités Riches**
- Données fondamentales des entreprises
- Actualités financières
- Indicateurs techniques
- Données de sentiment

### **5. Limites Généreuses (Gratuit)**
- **60 appels/minute** vs **5/minute** (Alpha Vantage)
- Permet un refresh plus fréquent

---

## ❌ **Inconvénients**

### **1. Pas de Temps Réel Gratuit**
- Plan gratuit : 15 minutes de délai
- Temps réel : $59.99/mois minimum

### **2. Complexité**
- Plus d'endpoints à gérer
- Structure JSON différente d'Alpha Vantage

### **3. Coût du Premium**
- $59.99/mois pour le temps réel
- Plus cher qu'Alpha Vantage ($49.99/mois)

---

## 🔄 **Comparaison avec Alpha Vantage**

| Aspect | Finnhub (Gratuit) | Alpha Vantage (Gratuit) |
|--------|-------------------|-------------------------|
| **Délai** | 15 minutes | J-1 |
| **Appels/minute** | 60 | 5 |
| **Actions EU** | ✅ Excellente | ✅ Bonne |
| **Documentation** | ✅ Excellente | ✅ Bonne |
| **Stabilité** | ✅ Excellente | ✅ Bonne |
| **JSON** | Simple | Complexe |
| **Temps réel** | $59.99/mois | $49.99/mois |

---

## 🛠️ **Implémentation pour OpéCours**

### **Interface Retrofit**
```kotlin
interface FinnhubApi {
    @GET("quote")
    suspend fun getQuote(
        @Query("symbol") symbol: String,
        @Query("token") token: String
    ): Response<FinnhubQuoteResponse>
    
    @GET("stock/candle")
    suspend fun getCandles(
        @Query("symbol") symbol: String,
        @Query("resolution") resolution: String = "D",
        @Query("from") from: Long,
        @Query("to") to: Long,
        @Query("token") token: String
    ): Response<FinnhubCandleResponse>
}
```

### **Modèles de Données**
```kotlin
data class FinnhubQuoteResponse(
    val c: Double,  // current price
    val d: Double,  // change
    val dp: Double, // percent change
    val h: Double,  // high
    val l: Double,  // low
    val o: Double,  // open
    val pc: Double, // previous close
    val t: Long     // timestamp
)

data class FinnhubCandleResponse(
    val c: List<Double>, // close prices
    val h: List<Double>, // high prices
    val l: List<Double>, // low prices
    val o: List<Double>, // open prices
    val t: List<Long>,   // timestamps
    val v: List<Long>,   // volumes
    val s: String        // status
)
```

### **Mapping vers Stock**
```kotlin
fun FinnhubQuoteResponse.toStock(operator: Operator): Stock {
    return Stock(
        symbol = operator.symbol,
        operatorName = operator.displayName,
        currentPrice = c,
        openPrice = o,
        previousClose = pc,
        change = d,
        changePercent = dp,
        lastUpdate = t * 1000, // Convert to milliseconds
        isMarketOpen = DateUtils.isMarketOpen(),
        historicalPrices = emptyList(), // À récupérer séparément
        volume = 0L // À récupérer séparément
    )
}
```

---

## 🎯 **Recommandation pour OpéCours**

### **✅ Avantages d'un Switch vers Finnhub**
1. **Délai réduit** : 15 minutes vs J-1 (Alpha Vantage)
2. **Plus d'appels** : 60/min vs 5/min
3. **API plus moderne** et mieux documentée
4. **Support des 4 opérateurs** français confirmé

### **⚠️ Points d'Attention**
1. **Refactoring nécessaire** : Changer toute la couche API
2. **Tests approfondis** : Vérifier la stabilité sur plusieurs jours
3. **Gestion d'erreurs** : Adapter aux nouvelles réponses

### **🚀 Plan de Migration**
1. **Phase 1** : Créer l'interface Finnhub en parallèle
2. **Phase 2** : Tester avec les 4 symboles français
3. **Phase 3** : Implémenter un système de fallback
4. **Phase 4** : Basculer progressivement

---

## 📝 **Conclusion**

**Finnhub représente un excellent compromis** pour OpéCours :
- **15 minutes de délai** (vs J-1 actuel)
- **API professionnelle** et stable
- **Support confirmé** des actions françaises
- **Plan gratuit généreux** (60 appels/minute)

C'est probablement la **meilleure option gratuite** disponible pour obtenir des données relativement récentes (15 min) avec une API fiable et bien documentée.

**Voulez-vous que je teste Finnhub** et implémente une version de démonstration ?