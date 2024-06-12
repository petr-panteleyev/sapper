/*
 Copyright © 2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.sapper;

public record HitResult(
        boolean ignore,
        GameStatus gameStatus,
        int cellStatus
) {
    public HitResult(boolean ignore) {
        this(ignore, GameStatus.IN_PROGRESS, 0);
    }

    public HitResult(GameStatus gameStatus) {
        this(false, gameStatus, 0);
    }
}
