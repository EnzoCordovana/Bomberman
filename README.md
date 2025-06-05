# Bomberman

## Architecture
Le projet est organisé en plusieurs modules Maven dans une structure monorepo pour séparer clairement les responsabilités et faciliter la maintenance.
```bash
├── apps
│   └── game-gui
│       ├── pom.xml
│       └── src
│           ├── main
│           │   └── java
│           │       └── fr.amu.iut.bomberman.gui
│           │           ├── controller
│           │           │   ├── GuiController.java           # Contrôleur principal MVC
│           │           │   ├── ProfileController.java       # Gestion profils joueurs
│           │           │   ├── ThemeController.java         # Gestion des thèmes graphiques
│           │           │   ├── BotController.java           # IA des bots
│           │           │   └── LevelEditorController.java    # Editeur de niveaux
│           │           ├── view
│           │           │   ├── components
│           │           │   │   ├── BombView.java
│           │           │   │   ├── PlayerView.java
│           │           │   │   └── FlagView.java             # Vue pour drapeau (mode Capture the Flag)
│           │           │   ├── GameView.java                  # Vue principale du jeu
│           │           │   ├── ProfileView.fxml               # FXML pour gestion profils
│           │           │   ├── ThemeView.fxml                 # FXML pour choix des thèmes
│           │           │   ├── LevelEditorView.fxml           # FXML pour éditeur de niveaux
│           │           │   └── styles
│           │           │       ├── default.css                # Feuilles de style CSS par thème
│           │           │       ├── pokemon.css
│           │           │       └── super-bomberman.css
│           │           ├── GameApplication.java                # Classe principale JavaFX
│           │           └── resources                             # Ressources (images, sons, etc.)
│           └── test
│               └── java
│                   └── fr.amu.iut.bomberman.gui
│                       └── GameApplicationTest.java
├── libs
│   ├── core
│   │   ├── pom.xml
│   │   └── src
│   │       └── main
│   │           └── java
│   │               └── fr.amu.iut.bomberman.core
│   │                   ├── controller
│   │                   │   ├── GameController.java            # Logique métier, gestion des règles
│   │                   │   ├── PlayerController.java          # Gestion des joueurs, profils
│   │                   │   └── FlagController.java            # Gestion mode Capture the Flag
│   │                   └── model
│   │                       ├── Bomb.java
│   │                       ├── Cell.java
│   │                       ├── GameMap.java
│   │                       ├── Player.java
│   │                       └── Profile.java                     # Modèle profil joueur (nom, avatar, stats)
│   ├── engine
│   │   ├── pom.xml
│   │   └── src
│   │       └── main
│   │           └── java
│   │               └── fr.amu.iut.bomberman.engine
│   │                   ├── CollisionManager.java                # Gestion des collisions
│   │                   ├── GameLoop.java                        # Boucle principale du jeu
│   │                   └── BotAI.java                           # Intelligence artificielle avancée
│   └── ui
│       ├── pom.xml
│       └── src
│           └── main
│               └── java
│                   └── fr.amu.iut.bomberman.ui
│                       ├── assets
│                       │   └── SpriteLoader.java                # Chargement des sprites graphiques
│                       ├── fx
│                       │   └── FXUtils.java                      # Utilitaires JavaFX (chargement FXML, CSS)
│                       └── theme
│                           └── ThemeManager.java                 # Gestion dynamique des thèmes (CSS, ressources)
├── tools                                                      # Outils, scripts divers
├── LICENSE
├── pom.xml                                                   # POM parent gérant le multi-module
├── README.md
├── src                                                       # Module racine, peut contenir des tests ou classes utilitaires
│   ├── main
│   │   └── java
│   │       └── fr.amu.iut
│   │           └── Main.java
│   └── test
│       └── java
│           └── fr.amu.iut
│               └── MainTest.java
```

1. `apps` : </br>
   Contient les applications finales, les exécutables du projet.
   -  **game-gui** : Version graphique du jeu, basée sur JavaFX.
      Inclut la gestion de l’interface graphique avec des classes de contrôleur (`GuiController.java`), des vues (`GameView.java`, `BombView.java`, `PlayerView.java`) et l’entrée point d’application (`GameApplication.java`).
2. `libs` :</br>
   Modules réutilisables qui contiennent la logique métier, le moteur de jeu, et l’interface utilisateur.
    - **core** : Contient le cœur du jeu, la logique métier et les modèles de données.
      Exemples : gestion des joueurs, des bombes, de la carte de jeu.
      Contrôleurs principaux du jeu comme `GameController.java`.
    - **engine** : Gère la mécanique du jeu, comme la boucle principale (`GameLoop.java`) et la gestion des collisions (`CollisionManager.java`).
    - **ui** : Composants liés à l’interface utilisateur, comme le chargement des assets graphiques (`SpriteLoader.java`), les utilitaires JavaFX (`FXUtils.java`) et la gestion des thèmes visuels (`ThemeManager.java`).
3. `tools` :</br> Dossier réservé pour les outils annexes, scripts ou utilitaires pouvant aider au développement ou à la maintenance.

## En résumé

- **`apps`** : applications finales à exécuter (GUI).
- **`libs/core`** : logique métier, modèles du jeu.
- **`libs/engine`** : moteur du jeu, boucle, collisions.
- **`libs/ui`** : gestion de l’interface, ressources graphiques et thèmes.
- **`tools`** : outils et scripts d’aide.