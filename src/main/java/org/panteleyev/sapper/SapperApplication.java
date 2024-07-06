/*
 Copyright © 2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.sapper;

import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import static org.panteleyev.sapper.GlobalContext.files;
import static org.panteleyev.sapper.GlobalContext.scoreboard;
import static org.panteleyev.sapper.GlobalContext.settings;

public class SapperApplication extends Application {
    @Override
    public void start(Stage stage) {
        files().initialize();
        scoreboard().load();
        settings().load();

        Font.loadFont(
                SapperApplication.class.getResource("/fonts/mine-sweeper.ttf").toString(),
                14
        );
        Font.loadFont(
                SapperApplication.class.getResource("/fonts/neat-lcd.ttf").toString(),
                14
        );


        new SapperWindowController(stage);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
