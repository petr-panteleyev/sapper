/*
 Copyright © 2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.sapper.score;

import org.panteleyev.sapper.GameType;

import java.time.LocalDate;
import java.time.LocalTime;

public record GameScore(
        GameType gameType,
        LocalDate date,
        LocalTime time
) {
}
