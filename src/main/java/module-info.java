module sapper {
    requires java.xml;

    requires javafx.base;
    requires javafx.graphics;
    requires javafx.controls;

    requires org.panteleyev.fx;
    requires org.panteleyev.freedesktop;

    exports org.panteleyev.sapper;
    exports org.panteleyev.sapper.score;
    exports org.panteleyev.sapper.game;
    exports org.panteleyev.sapper.settings;
}