# 🏥 MedCare – JavaFX & Symfony Application

**MedCare** est une plateforme de gestion médicale qui permet aux utilisateurs de prendre soin de leur santé via une application desktop (JavaFX) connectée à une base de données MySQL, gérée par un backend Symfony.

## 💻 Frontend – JavaFX Desktop App

Cette application permet aux patients et aux médecins d’interagir facilement avec les services de santé.

### ✨ Fonctionnalités principales

- 🔍 Parcourir les médecins et spécialités
- 📅 Prendre des rendez-vous
- 📋 Voir et gérer ses consultations
- 💬 Laisser un avis sur les consultations
- 📄 Exporter une ordonnance en PDF
- 👩‍⚕️ Interface médecin : gérer les patients et les prescriptions
 - 📅 voir des evennements
   - 📅 voir des blog et faire des commentaires

### 🧰 Technologies utilisées

- Java 17+
- JavaFX 17
- JDBC (MySQL)
- MVC Architecture
- FXML (pour les interfaces)
- SceneBuilder (pour la conception visuelle)

### ⚙️ Installation et exécution

1. **Pré-requis** :
   - Java JDK 17+
   - SceneBuilder
   - Serveur MySQL avec la base `medicaldb`

2. **Connexion à la base de données** :
   Dans le fichier `DBConnection.java` :
   ```java
   String url = "jdbc:mysql://localhost:3306/medicaldb";
   String user = "root";
   String password = "tonmotdepasse";
