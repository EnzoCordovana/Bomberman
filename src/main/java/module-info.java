module Bomberman {
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.media;

    opens fr.amu.iut.bomberman to javafx.fxml;
    opens fr.amu.iut.bomberman.controller to javafx.fxml;
    exports fr.amu.iut.bomberman;
}