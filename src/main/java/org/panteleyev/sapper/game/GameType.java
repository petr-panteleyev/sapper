/*
 Copyright © 2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.sapper.game;

import org.panteleyev.sapper.bundles.GameTypeBundle;

import java.util.ResourceBundle;

import static java.util.ResourceBundle.getBundle;

public enum GameType {
    BIG(30, 16, 99),
    MEDIUM(16, 16, 40),
    SMALL(8, 8, 10);

    private static final ResourceBundle BUNDLE = getBundle(GameTypeBundle.class.getCanonicalName());

    private final int width;
    private final int height;
    private final int mines;

    GameType(int width, int height, int mines) {
        this.width = width;
        this.height = height;
        this.mines = mines;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getMines() {
        return mines;
    }

    @Override
    public String toString() {
        return BUNDLE.getString(name());
    }
}
