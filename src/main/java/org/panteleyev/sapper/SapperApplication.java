// Copyright © 2024-2026 Petr Panteleyev
// SPDX-License-Identifier: BSD-2-Clause
package org.panteleyev.sapper;

import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import static java.util.Objects.requireNonNull;
import static org.panteleyev.sapper.GlobalContext.files;
import static org.panteleyev.sapper.GlobalContext.scoreboard;
import static org.panteleyev.sapper.GlobalContext.settings;

public class SapperApplication extends Application {
    private static final int FONT_SIZE = 14;

    @Override
    public void start(Stage stage) {
        files().initialize().throwIfFailure();
        scoreboard().load();
        settings().load();

        Font.loadFont(getResourceUrl("/fonts/mine-sweeper.ttf"), FONT_SIZE);
        Font.loadFont(getResourceUrl("/fonts/Pixel-LCD-7.ttf"), FONT_SIZE);

        new SapperWindowController(stage);
        stage.show();
    }

    static void main(String[] args) {
        launch(args);
    }

    private static String getResourceUrl(String name) {
        return requireNonNull(SapperApplication.class.getResource(name), "Resource " + name + " not found").toString();
    }
}
