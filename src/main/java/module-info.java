module sapper {
    requires javafx.graphics;
    requires java.desktop;

    requires org.panteleyev.fx;
    requires org.panteleyev.freedesktop;
    requires java.sql;

    exports org.panteleyev.sapper;
    exports org.panteleyev.sapper.score;
    exports org.panteleyev.sapper.game;
}