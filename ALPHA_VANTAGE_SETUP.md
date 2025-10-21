# 🔑 Configuration Alpha Vantage pour OpéCours

## Pourquoi avez-vous besoin d'une vraie clé API ?

La clé "demo" d'Alpha Vantage a des limitations sévères :
- ❌ Ne fonctionne que pour quelques symboles américains (IBM, MSFT, etc.)
- ❌ Ne supporte PAS les symboles européens (ORA.PA, EN.PA, ILD.PA, ATC.PA)
- ❌ Limite de 5 requêtes par minute
- ❌ Données souvent obsolètes ou inexistantes

## 📝 Étapes pour obtenir une clé gratuite

### 1. Créer un compte Alpha Vantage

1. Allez sur : **https://www.alphavantage.co/support/#api-key**
2. Cliquez sur **"Get your free API key today"**
3. Remplissez le formulaire :
   - **First Name** : Votre prénom
   - **Last Name** : Votre nom
   - **Email** : Votre email
   - **Organization** : Vous pouvez mettre "Personal" ou "Individual"
   - **Intended API usage** : "Personal stock tracking app"

### 2. Vérifier votre email

1. Consultez votre boîte email
2. Cliquez sur le lien de confirmation
3. Votre clé API sera affichée sur la page

### 3. Configurer l'application

1. Ouvrez le fichier : `OpeCours/app/src/main/java/com/kapoue/opecours/util/Constants.kt`
2. Remplacez la ligne :
   ```kotlin
   const val API_KEY = "demo"
   ```
   Par :
   ```kotlin
   const val API_KEY = "VOTRE_CLE_ICI"
   ```

### 4. Exemple de configuration

```kotlin
object Constants {
    const val BASE_URL = "https://www.alphavantage.co/"
    const val API_KEY = "ABC123XYZ789" // Votre vraie clé ici
    // ... reste du fichier
}
```

## 🎯 Avantages de la clé gratuite

- ✅ **500 requêtes par jour** (largement suffisant)
- ✅ **5 requêtes par minute** (OK pour 4 opérateurs)
- ✅ **Support des symboles européens** (ORA.PA, EN.PA, etc.)
- ✅ **Données temps réel** (délai ~15-20 minutes)
- ✅ **Données historiques** pour les graphiques

## 🚀 Test de votre configuration

Après avoir configuré votre clé :

1. **Compilez l'application** : `./gradlew assembleDebug`
2. **Installez sur votre appareil**
3. **Ouvrez l'app** et tirez vers le bas pour rafraîchir
4. **Vérifiez les logs** dans Android Studio pour voir les vraies données

## 🔍 Vérification des logs

Dans Android Studio, filtrez les logs avec "OpeCours" pour voir :
```
🔑 Clé API utilisée: ABC123XYZ789
📞 Appel Alpha Vantage pour Orange (ORA.PA)
📡 Réponse HTTP 200 pour Orange
✅ Données Alpha Vantage récupérées pour Orange: 10.45€
```

## ⚠️ Limitations à connaître

### Clé gratuite :
- 500 requêtes/jour
- 5 requêtes/minute
- Délai de ~15-20 minutes

### Clé premium (optionnel) :
- Plus de requêtes
- Données plus fréquentes
- Support prioritaire

## 🛠️ Dépannage

### Problème : "Invalid API call"
- ✅ Vérifiez que votre clé est correcte
- ✅ Vérifiez que vous n'avez pas dépassé les limites

### Problème : "Symbol not found"
- ✅ Les symboles européens nécessitent une vraie clé
- ✅ Vérifiez que les symboles sont corrects (ORA.PA, EN.PA, etc.)

### Problème : "Quota exceeded"
- ✅ Attendez 1 minute entre les rafraîchissements
- ✅ L'auto-refresh est configuré pour 5 minutes (OK)

## 📞 Support

Si vous rencontrez des problèmes :
1. Vérifiez les logs Android Studio
2. Testez votre clé sur le site Alpha Vantage
3. Contactez le support Alpha Vantage si nécessaire

---

**Une fois configuré, vous aurez des vraies données boursières en temps réel ! 📈**