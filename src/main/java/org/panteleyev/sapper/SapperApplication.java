// Copyright © 2024-2025 Petr Panteleyev
// SPDX-License-Identifier: BSD-2-Clause
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
        files().initialize().throwIfFailure();
        scoreboard().load();
        settings().load();

        Font.loadFont(
                SapperApplication.class.getResource("/fonts/mine-sweeper.ttf").toString(),
                14
        );
        Font.loadFont(
                SapperApplication.class.getResource("/fonts/Pixel-LCD-7.ttf").toString(),
                14
        );

        new SapperWindowController(stage);
        stage.show();
    }

    static void main(String[] args) {
        launch(args);
    }
}
