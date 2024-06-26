/*
 Copyright © 2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.sapper.score;

import org.panteleyev.sapper.game.BoardSize;

import java.time.LocalDate;
import java.time.LocalTime;

public record GameScore(
        BoardSize boardSize,
        LocalDate date,
        LocalTime time
) {
}
