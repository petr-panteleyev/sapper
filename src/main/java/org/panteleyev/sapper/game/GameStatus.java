/*
 Copyright © 2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.sapper.game;

public enum GameStatus {
    INITIAL(false),
    IN_PROGRESS(false),
    SUCCESS(true),
    FAILURE(true);

    private final boolean isFinal;

    GameStatus(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public boolean isFinal() {
        return isFinal;
    }
}
