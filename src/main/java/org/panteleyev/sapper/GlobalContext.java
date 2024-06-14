/*
 Copyright © 2022 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.sapper;

import org.panteleyev.sapper.score.Scoreboard;

public final class GlobalContext {
    private static final ApplicationFiles FILES = new ApplicationFiles();
    private static final Scoreboard SCOREBOARD = new Scoreboard();
//    private static final Settings SETTINGS = new Settings(FILES);

    private GlobalContext() {
    }

//    public static Settings settings() {
//        return SETTINGS;
//    }

    public static ApplicationFiles files() {
        return FILES;
    }

    public static Scoreboard scoreboard() {
        return SCOREBOARD;
    }
}
