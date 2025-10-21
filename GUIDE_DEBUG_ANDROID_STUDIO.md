# 🔍 Guide de Débogage avec Android Studio

## 📱 **Étapes pour Tester avec Smartphone USB**

### 1. **Préparer le Smartphone**
- Connectez votre smartphone au PC avec un câble USB
- Assurez-vous que le **débogage USB** est activé :
  - `Paramètres > À propos du téléphone`
  - Tapez 7 fois sur "Numéro de build" (active le mode développeur)
  - `Paramètres > Options pour les développeurs`
  - Activez `Débogage USB`

### 2. **Dans Android Studio**

#### A. Ouvrir le Projet
1. Lancez **Android Studio**
2. `File > Open` → Sélectionnez le dossier `OpeCours`
3. Attendez que Gradle se synchronise

#### B. Vérifier la Connexion
1. En bas d'Android Studio, cliquez sur l'onglet **"Logcat"**
2. En haut à droite du Logcat, vérifiez que votre smartphone apparaît dans la liste déroulante
3. Si votre téléphone n'apparaît pas :
   - Débranchez/rebranchez le câble USB
   - Autorisez le débogage USB sur votre téléphone (popup)

#### C. Installer et Lancer l'App
1. Cliquez sur le bouton **▶️ "Run"** (ou `Shift + F10`)
2. Sélectionnez votre smartphone dans la liste
3. L'application s'installe et se lance automatiquement

### 3. **Voir les Logs**

#### A. Configurer le Filtre Logcat
1. Dans l'onglet **"Logcat"** (en bas d'Android Studio)
2. Dans le champ de recherche, tapez : `OpeCours`
3. Ou utilisez le filtre par tag : `tag:OpeCours`

#### B. Tester l'Application
1. **Ouvrez l'app** OpéCours sur votre téléphone
2. **Tirez vers le bas** pour déclencher le pull-to-refresh
3. **Observez les logs** dans Android Studio

#### C. Logs à Rechercher
Vous devriez voir des logs comme :
```
🚀 MainViewModel initialisé
📱 MainViewModel.loadStocks() appelé
🎯 GetStocksUseCase.invoke() appelé
🔄 Début de la collecte des stocks via UseCase
🔑 Clé API utilisée: AB8FB9V1ZDFC6KDS
📞 Appel Alpha Vantage pour Orange (ORA.PA)
💥 [ERREUR] - avec détails de l'exception
```

### 4. **Copier les Logs**

#### A. Sélectionner les Logs
1. Dans le Logcat, **sélectionnez tous les logs** pertinents
2. `Ctrl + A` pour tout sélectionner ou sélection manuelle

#### B. Copier
1. `Ctrl + C` pour copier
2. Collez dans un fichier texte ou directement dans votre réponse

### 5. **Alternative : Export des Logs**
1. Dans Logcat, clic droit → **"Save As"**
2. Sauvegardez en fichier `.txt`
3. Envoyez le fichier

## 🔧 **Dépannage**

### Problème : Smartphone non détecté
- **Solution** : Installez les drivers USB de votre fabricant
- **Alternative** : Utilisez `adb devices` en ligne de commande

### Problème : Pas d'autorisation de débogage
- **Solution** : Révoqué les autorisations USB dans les options développeur
- Débranchez/rebranchez pour redemander l'autorisation

### Problème : App ne se lance pas
- **Solution** : Vérifiez que l'app se compile sans erreur
- Regardez les erreurs dans l'onglet "Build" d'Android Studio

## 📋 **Checklist Rapide**
- [ ] Smartphone connecté en USB
- [ ] Débogage USB activé
- [ ] Android Studio ouvert avec projet OpeCours
- [ ] Smartphone visible dans Logcat
- [ ] App installée et lancée via Run ▶️
- [ ] Filtre "OpeCours" activé dans Logcat
- [ ] Pull-to-refresh testé
- [ ] Logs copiés et envoyés

**Une fois les logs récupérés, je pourrai identifier et corriger l'erreur rapidement ! 🚀**