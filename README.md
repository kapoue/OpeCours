# 📱 OpéCours - Suivi des Cours Boursiers Télécoms

Application Android moderne pour suivre en temps réel les cours boursiers des principaux opérateurs télécoms français.

## 🎯 Fonctionnalités

- 📊 **Affichage en temps réel** des cours de 4 opérateurs télécoms français
- 🔄 **Auto-refresh** toutes les 5 minutes + pull-to-refresh manuel
- 📈 **Mini-graphiques** montrant l'évolution sur 5 jours
- 💾 **Mode hors ligne** avec cache local
- 🌙 **Thème adaptatif** (clair/sombre)
- ⚡ **Animations fluides** et interface moderne

## 📱 Opérateurs Suivis

- 🟠 **Orange** (ORA.PA) - 14,16 € (+0,78%)
- 🔵 **Bouygues Telecom** (EN.PA) - 41,16 € (+0,76%)
- ⚫ **Free/Iliad** (ILD.PA) - 182,00 € (+0,83%)
- 🔴 **SFR/Altice** (ATC.PA) - Données non disponibles

## 🛠️ Technologies Utilisées

- **Kotlin** - Langage principal
- **Jetpack Compose** - Interface utilisateur moderne
- **Architecture MVVM** - Clean Architecture
- **Hilt/Dagger** - Injection de dépendances
- **Retrofit 2** - Appels API REST
- **Room Database** - Cache local
- **Coroutines & Flow** - Programmation asynchrone
- **Alpha Vantage API** - Source des données boursières

## 📦 Installation

### Prérequis
- Android Studio Hedgehog | 2023.1.1+
- JDK 17
- Android SDK 26+ (Android 8.0+)

### Étapes
1. Cloner le repository
```bash
git clone https://github.com/kapoue/OpeCours.git
cd OpeCours
```

2. Ouvrir dans Android Studio

3. Synchroniser Gradle

4. Lancer sur émulateur ou appareil physique
```bash
./gradlew installDebug
```

## 🏗️ Architecture

```
app/src/main/java/com/kapoue/opecours/
├── data/                    # Couche données
│   ├── local/              # Base de données Room
│   ├── remote/             # API Alpha Vantage
│   ├── repository/         # Repository pattern
│   └── mapper/             # Mappers de données
├── domain/                 # Logique métier
│   ├── model/              # Modèles domaine
│   └── usecase/            # Use cases
├── presentation/           # Interface utilisateur
│   ├── components/         # Composants Compose
│   └── theme/              # Thème Material 3
├── di/                     # Injection dépendances
└── util/                   # Utilitaires
```

## 🔧 Configuration

L'application utilise l'API Alpha Vantage avec une clé de démonstration. Pour une utilisation en production, remplacez la clé dans `Constants.kt` :

```kotlin
const val API_KEY = "VOTRE_CLE_API_ALPHA_VANTAGE"
```

## 📊 Source des Données

Les données boursières proviennent d'**Alpha Vantage** via leur API publique. En cas d'indisponibilité, l'application utilise un système de fallback intelligent avec des données mock.

## 🔒 Permissions

- `INTERNET` - Récupération des données
- `ACCESS_NETWORK_STATE` - Vérification de la connexion

## 📱 Captures d'Écran

*À venir - Screenshots de l'application*

## 🤝 Contribution

Ce projet est à usage personnel et éducatif. Les contributions ne sont pas acceptées.

## ⚠️ Disclaimer

Cette application est fournie à titre informatif uniquement. Les données ne constituent pas des conseils d'investissement. L'auteur décline toute responsabilité quant aux décisions prises sur la base de ces informations.

## 📄 License

Ce projet est sous licence MIT. Voir le fichier [LICENSE](LICENSE) pour plus de détails.

## 👤 Auteur

**kapoue**  
GitHub: [@kapoue](https://github.com/kapoue)

---

*Développé avec ❤️ en Kotlin & Jetpack Compose*