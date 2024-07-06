module sapper {
    requires javafx.graphics;
    requires java.desktop;
    requires java.xml;

    requires org.panteleyev.fx;
    requires org.panteleyev.freedesktop;
    requires java.sql;

    exports org.panteleyev.sapper;
    exports org.panteleyev.sapper.score;
    exports org.panteleyev.sapper.game;
    exports org.panteleyev.sapper.settings;
}