**# Bomberman

## Architecture
````bash
.
├── Bomberman.iml
├── LICENSE
├── pom.xml
├── README.md
├── src
│   └── main
│       ├── java
│       │   ├── fr
│       │   │   └── amu
│       │   │       └── iut
│       │   │           └── bomberman
│       │   │               ├── controller
│       │   │               │   ├── MenuController.java
│       │   │               │   ├── PlayController.java
│       │   │               │   ├── ProfileController.java
│       │   │               │   └── SettingsController.java
│       │   │               ├── Main.java
│       │   │               ├── model
│       │   │               │   ├── common
│       │   │               │   │   └── Position.java
│       │   │               │   ├── entities
│       │   │               │   │   ├── Bomb.java
│       │   │               │   │   ├── Explosion.java
│       │   │               │   │   └── Player.java
│       │   │               │   ├── game
│       │   │               │   │   ├── GameEngine.java
│       │   │               │   │   └── GameState.java
│       │   │               │   └── map
│       │   │               │       ├── GameMap.java
│       │   │               │       ├── IMap.java
│       │   │               │       └── Tile.java
│       │   │               └── view
│       │   │                   ├── IViewManager.java
│       │   │                   ├── MapView.java
│       │   │                   └── ViewManager.java
│       │   └── module-info.java
│       └── resources
│           ├── assets
│           │   ├── default_avatar.jpg
│           │   └── wallpaper.jpg
│           ├── styles
│           │   └── styles.css
│           └── view
│               ├── MenuView.fxml
│               ├── PlayView.fxml
│               ├── ProfileView.fxml
│               └── SettingsView.fxml
````

### Architecture MVC

📂 **Controller** (src/main/java/.../controller/)
**Responsabilité** : Logique de contrôle et gestion des interactions utilisateur
MenuController.java

**Rôle** : Contrôleur du menu principal
**Fonctionnalités** :

Navigation entre les vues (Jouer, Paramètres, Profil)
Gestion des événements boutons
Configuration de l'arrière-plan



PlayController.java

**Rôle** : Contrôleur principal du jeu
**Architecture multithreadée** :

Thread logique (120Hz) : Calculs, collisions, état du jeu
Thread rendu (60 FPS) : Affichage et animations
Thread input (temps réel) : Gestion des entrées clavier


**Fonctionnalités** :

Gestion des mouvements joueurs (ZQSD, Flèches)
Placement des bombes (Espace, Entrée)
Pause/Reprise du jeu



ProfileController.java

**Rôle** : Gestion des profils utilisateur
**Fonctionnalités** :

Affichage des statistiques joueur
Modification des avatars
Sauvegarde des profils



SettingsController.java

**Rôle** : Configuration des paramètres
**Fonctionnalités** :

Configuration des contrôles
Paramètres audio/vidéo
Sauvegarde des préférences



📂 **Model** (src/main/java/.../model/)
**Responsabilité** : Logique métier et données du jeu
common/Position.java

**Rôle** : Représentation d'une position 2D
**Fonctionnalités** :

Coordonnées x, y immutables
Calculs de distance (Manhattan)
Méthodes de déplacement (north, south, east, west)



entities/ - Entités du jeu
Player.java

**Rôle** : Représentation d'un joueur
**Propriétés** :

Position (pixels et grille)
Vies, score, couleur, nom
Capacités bombes (nombre max, portée)
État (vivant, invulnérable)


**Fonctionnalités** :

Déplacement et placement de bombes
Gestion des dégâts et invulnérabilité
Système de score



Bomb.java

**Rôle** : Représentation d'une bombe
**Propriétés** :

Position, propriétaire, portée d'explosion
Timer d'explosion (3 secondes par défaut)
État d'explosion


**Fonctionnalités** :

Décompte automatique
Vérification d'explosion
Animation de progression



Explosion.java

**Rôle** : Représentation d'une explosion
**Propriétés** :

Position, durée de vie (1 seconde)
État actif/inactif


**Fonctionnalités** :

Gestion de la durée de vie
Animation progressive



game/ - Logique de jeu
GameEngine.java

**Rôle** : Moteur principal du jeu
**Architecture thread-safe** :

Collections concurrentes (CopyOnWriteArrayList)
Verrous lecture/écriture (ReadWriteLock)
États atomiques (AtomicBoolean)


**Fonctionnalités** :

Initialisation des parties
Mise à jour de l'état de jeu (120Hz)
Gestion des bombes et explosions
Détection des collisions
Conditions de victoire



GameState.java

**Rôle** : État global de la partie
**Propriétés thread-safe** :

État (en cours, pause, terminé)
Gagnant, temps de jeu
Durée de partie


**Fonctionnalités** :

Gestion du timing
Vérification des conditions de fin



map/ - Système de carte
IMap.java

**Rôle** : Interface pour les opérations de carte
**Contrat** :

Gestion des tuiles (get/set)
Placement/explosion des bombes
Validation des positions



GameMap.java

**Rôle** : Implémentation de la carte de jeu
**Fonctionnalités** :

Génération de carte (15x13)
Pattern Bomberman classique (murs fixes + destructibles)
Zones de départ protégées
Gestion des explosions en croix



Tile.java

**Rôle** : Représentation d'une tuile
**Types** : Sol, Mur, Mur destructible, Bombe, Explosion, Power-up
**Propriétés** : Traversable, destructible, timer d'explosion

📂 **View** (src/main/java/.../view/)
**Responsabilité** : Interface utilisateur et affichage
IViewManager.java

**Rôle** : Interface de gestion des vues
**Contrat** : Navigation entre menu, jeu, paramètres, profil

ViewManager.java

**Rôle** : Gestionnaire de navigation
**Pattern** : Singleton
**Fonctionnalités** :

Changement de vues via Main.changeView()
Gestion des contrôleurs associés



MapView.java

**Rôle** : Rendu graphique de la carte
**Héritage** : Étend JavaFX Canvas
**Fonctionnalités** :

Rendu des tuiles (sol vert, murs gris/marron)
Animation des bombes (clignotement, timer)
Explosions en 3 phases (intense → flammes → fumée)
Affichage des joueurs avec effets**