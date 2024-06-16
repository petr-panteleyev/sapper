/*
 Copyright © 2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.sapper.game;

public final class Cell {
    private static final int MAX_MINES = 8;

    static final int EMPTY          = 0b001_0000;
    static final int MINE           = 0b010_0000;
    static final int FLAG_MASK      = 0b100_0000;

    static final int EMPTY_WITH_FLAG = EMPTY | FLAG_MASK;
    static final int MINE_WITH_FLAG  = MINE | FLAG_MASK;

    public static boolean empty(int value) {
        return (value & EMPTY) == EMPTY;
    }

    public static boolean emptyWithFlag(int value) {
        return value == EMPTY_WITH_FLAG;
    }

    public static boolean mine(int value) {
        return (value & MINE) == MINE;
    }

    public static boolean mineNoFlag(int value) {
        return value == MINE;
    }

    public static boolean flag(int value) {
        return (value & FLAG_MASK) == FLAG_MASK;
    }

    public static boolean isExplored(int value) {
        return value <= MAX_MINES;
    }

    public static int toggleFlag(int value) {
        return value ^ FLAG_MASK;
    }

    public static int putMine(int value) {
        return (value ^ MINE) & (~EMPTY);
    }

    private Cell() {
    }
}
