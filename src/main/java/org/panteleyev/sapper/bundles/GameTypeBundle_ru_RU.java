/*
 Copyright © 2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.sapper.bundles;

import org.panteleyev.sapper.game.GameType;

import java.util.ListResourceBundle;

public class GameTypeBundle_ru_RU extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][] {
                {GameType.BIG.name(), "30 x 16, 99 мин"},
                {GameType.MEDIUM.name(), "16 x 16, 40 мин"},
                {GameType.SMALL.name(), "8 x 8, 10 мин"}
        };
    }
}
