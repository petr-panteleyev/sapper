/*
 Copyright © 2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.sapper.game;

import java.util.Comparator;
import java.util.List;

public record BoardSize(int width, int height, int mines) {
    public static final int MIN_WIDTH = 8;
    public static final int MAX_WIDTH = 30;
    public static final int MIN_HEIGHT = 8;
    public static final int MAX_HEIGHT = 24;

    public static final BoardSize BIG = new BoardSize(30, 16, 99);
    public static final BoardSize MEDIUM = new BoardSize(16, 16, 40);
    public static final BoardSize SMALL = new BoardSize(8, 8, 10);

    public static final List<BoardSize> STANDARD_SIZES = List.of(SMALL, MEDIUM, BIG);

    public static final Comparator<BoardSize> COMPARATOR =
            Comparator.comparingInt((BoardSize o) -> o.width * o.height)
                    .thenComparing(BoardSize::mines);

    public BoardSize {
        if (width < MIN_WIDTH || width > MAX_WIDTH || height < MIN_HEIGHT || height > MAX_HEIGHT) {
            throw new IllegalArgumentException("Board dimensions are out of bounds");
        }
    }

    @Override
    public String toString() {
        return width() + " * " + height() + " : " + mines();
    }
}
