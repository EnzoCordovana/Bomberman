module MVP {
    // Dépendances JavaFX
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.media;

    // Dépendances Java standard
    requires java.desktop;

    exports fr.amu.iut.bomberman.model.entities to javafx.fxml, javafx.graphics;
    opens fr.amu.iut.bomberman.model.entities to javafx.fxml;
    exports fr.amu.iut.bomberman.model.game to javafx.fxml, javafx.graphics;
    opens fr.amu.iut.bomberman.model.game to javafx.fxml;
    exports fr.amu.iut.bomberman.model.map to javafx.fxml, javafx.graphics;
    opens fr.amu.iut.bomberman.model.map to javafx.fxml;
    exports fr.amu.iut.bomberman.model.common to javafx.fxml, javafx.graphics;
    opens fr.amu.iut.bomberman.model.common to javafx.fxml;
    exports fr.amu.iut.bomberman.view to javafx.fxml, javafx.graphics;
    opens fr.amu.iut.bomberman.view to javafx.fxml;
    exports fr.amu.iut.bomberman to javafx.fxml, javafx.graphics;
    opens fr.amu.iut.bomberman to javafx.fxml;
    exports fr.amu.iut.bomberman.controller to javafx.fxml, javafx.graphics;
    opens fr.amu.iut.bomberman.controller to javafx.fxml;
}