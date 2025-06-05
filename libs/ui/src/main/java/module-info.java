module ui {
    requires javafx.graphics;
    requires javafx.fxml;

    exports fr.amu.iut.bomberman.ui.assets;
    exports fr.amu.iut.bomberman.ui.fx;
    exports fr.amu.iut.bomberman.ui.theme;

    opens fr.amu.iut.bomberman.ui.fx to javafx.fxml;
}