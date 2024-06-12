/*
 Copyright © 2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.sapper;

import javafx.application.Application;
import javafx.stage.Stage;

public class SapperApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        new SapperWindowController(stage);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
