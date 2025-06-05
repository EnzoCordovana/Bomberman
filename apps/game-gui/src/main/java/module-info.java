module game.gui {
    requires javafx.controls;
    requires javafx.fxml;

    requires core;
    requires engine;
    requires ui;

    exports fr.amu.iut.bomberman.gui;

    opens fr.amu.iut.bomberman.gui.controller to javafx.fxml;
    opens fr.amu.iut.bomberman.gui.view to javafx.fxml;
}