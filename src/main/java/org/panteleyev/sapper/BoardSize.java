/*
 Copyright © 2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.sapper;

public record BoardSize(
        int width,
        int height,
        int mineCount
) {
    public static final BoardSize BIG = new BoardSize(30, 16, 99);
    public static final BoardSize MEDIUM = new BoardSize(16, 16, 40);
    public static final BoardSize SMALL = new BoardSize(8, 8, 10);
}
